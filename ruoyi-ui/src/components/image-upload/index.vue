<template>
  <div class="component-upload-image">
    <t-upload
      ref="imageUpload"
      v-model="fileList"
      multiple
      :abridge-name="abridgeName"
      :theme="theme"
      :accept="rawAccept"
      :action="uploadImgUrl"
      :before-upload="handleBeforeUpload"
      :max="limit"
      :on-fail="handleUploadError"
      :headers="headers"
      :draggable="draggable"
      :size-limit="{ size: fileSize, unit: 'MB', message: '上传图片大小不能超过 {sizeLimit} MB!' }"
      :disabled="disabled"
      :allow-upload-duplicate-file="allowUploadDuplicateFile"
      :data="{ ossCategoryId: ossCategoryId || '' }"
      @one-file-success="handleOneUploadSuccess"
      @success="handleUploadSuccess"
      @remove="handleDelete"
      @validate="onValidate"
      @click="handleOpenUpload($event)"
    >
      <template v-if="isShowTip" #tips>
        请上传大小不超过 {{ fileSize }}MB 的图片，
        <t-tooltip>
          <template #content>
            <p v-for="item in rawAccept?.split(',')" :key="item" style="word-break: break-all">
              {{ item }}
            </p>
          </template>
          <t-link theme="primary">查看格式要求</t-link>
        </t-tooltip>
      </template>
      <!-- 上传按钮 -->
      <slot />
      <t-button v-if="!$slots.default" variant="outline">
        <template #icon>
          <cloud-upload-icon />
        </template>
        选取文件
      </t-button>
    </t-upload>
    <upload-select
      v-if="supportSelectFile || effectiveSupportUrl"
      v-model:visible="open"
      title="选择图片"
      :support-url="effectiveSupportUrl"
      :support-select-file="supportSelectFile"
      :query-param="queryParam"
      :multiple="limit > 1"
      :file-upload="false"
      :image-upload-props="{
        accept: accept,
        fileSize: fileSize,
        fileType: fileType,
      }"
      :on-submit="handleSelectSubmit"
    />
  </div>
</template>

<script lang="ts" setup>
import { compressAccurately } from 'image-conversion';
import { storeToRefs } from 'pinia';
import { CloudUploadIcon } from 'tdesign-icons-vue-next';
import type { SuccessContext, UploadFile, UploadRemoveContext, UploadValidateType } from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref, watch } from 'vue';

import { delOss, listByIds, listByUrls } from '@/api/system/oss';
import type { SelectFile } from '@/components/upload-select/index.vue';
import type { MyOssProps } from '@/pages/system/ossCategory/components/myOss.vue';
import { useUserStore } from '@/store';
import { blobToFile } from '@/utils/file';
import { getHttpFileName, getHttpFileSuffix } from '@/utils/ruoyi';

export interface ImageUploadProps {
  modelValue?: string | string[];
  // 文件名过长时，需要省略中间的文本，保留首尾文本。示例：[10, 7]，表示首尾分别保留的文本长度。
  abridgeName?: Array<number>;
  // 图片数量限制,为0不限制
  limit?: number;
  // 是否支持拖拽上传
  draggable?: boolean;
  // 大小限制(MB)
  fileSize?: number;
  // 接受上传的文件类型
  accept?: Array<
    | 'image/gif'
    | 'image/jpeg'
    | 'image/png'
    | 'image/svg+xml'
    | 'image/tiff'
    | 'image/vnd.wap.wbmp'
    | 'image/webp'
    | 'image/x-icon'
    | 'image/x-jng'
    | 'image/x-ms-bmp'
    | 'image/*'
    | string
  >;
  // 文件类型, 例如['png', 'jpg', 'jpeg']
  fileType?: string[];
  // 是否显示提示
  isShowTip?: boolean;
  theme?: 'custom' | 'image' | 'image-flow';
  // 模式，id模式返回ossId，url模式返回url链接
  mode?: 'id' | 'url';
  // 禁用组件
  disabled?: boolean;
  // 是否允许重复上传相同文件名的文件
  allowUploadDuplicateFile?: boolean;
  // 支持选择文件
  supportSelectFile?: boolean;
  // 支持手动输入url，需要mode="url"才有效
  supportUrl?: boolean;
  // oss分类id
  ossCategoryId?: number;
  // 是否支持压缩，默认否
  compressSupport?: boolean;
  // 压缩目标大小，单位KB。默认300KB以上文件才压缩，并压缩至300KB以内
  compressTargetSize: number;
}

const props = withDefaults(defineProps<ImageUploadProps>(), {
  limit: 5,
  fileSize: 5,
  fileType: () => ['png', 'jpg', 'jpeg', 'gif', 'webp', 'bmp'],
  isShowTip: true,
  theme: 'image',
  mode: 'url',
  disabled: false,
  allowUploadDuplicateFile: false,
  supportSelectFile: true,
  supportUrl: true,
  compressSupport: false,
  compressTargetSize: 300,
});

const queryParam = computed<MyOssProps['queryParam']>(() => {
  const maxSize = props.fileSize * 1024 * 1024;
  if (props.accept) {
    return {
      contentTypes: props.accept,
      maxSize,
    };
  }
  return {
    suffixes: props.fileType,
    maxSize,
  };
});

const { token } = storeToRefs(useUserStore());
const { proxy } = getCurrentInstance();
const emit = defineEmits(['update:modelValue', 'change']);
const baseUrl = import.meta.env.VITE_APP_BASE_API;
const uploadImgUrl = ref(`${baseUrl}/resource/oss/upload`); // 上传的图片服务器地址
const headers = ref({ Authorization: `Bearer ${token.value}` });
const fileList = ref<UploadFile[]>([]);
const rawAccept = computed(() => {
  return props.accept?.join(',') || props.fileType.map((value) => `.${value}`).join(',');
});
const effectiveSupportUrl = computed(() => {
  return props.supportUrl && props.mode === 'url';
});
const open = ref(false);

watch(
  () => props.modelValue,
  async (val) => {
    if (val) {
      // 首先将值转为数组
      let list: any[];
      if (Array.isArray(val)) {
        list = val;
      } else if (props.mode === 'url') {
        await listByUrls(val as string).then((res) => {
          const tempMap = new Map<string, object>();
          res.data.forEach((oss) => {
            tempMap.set(oss.url, {
              name: oss.originalName,
              status: 'success',
              size: oss.size,
              uploadTime: oss.createTime,
              url: oss.url,
              ossId: oss.ossId,
            });
          });
          list = val.split(/,(?=http)/).map((url: string) => {
            return (
              tempMap.get(url) || {
                name: getHttpFileName(url),
                status: 'success',
                size: 0,
                url,
              }
            );
          });
        });
      } else if (props.mode === 'id') {
        await listByIds(val as string).then((res) => {
          list = res.data.map((oss) => {
            return {
              name: oss.originalName,
              status: 'success',
              size: oss.size,
              uploadTime: oss.createTime,
              url: oss.url,
              ossId: oss.ossId,
            };
          });
        });
      }
      // 然后将数组转为对象数组
      fileList.value = list.map((item: any) => {
        // 字符串回显处理 如果此处存的是url可直接回显 如果存的是id需要调用接口查出来
        if (typeof item === 'string') {
          item = { name: getHttpFileName(item), status: 'success', url: item };
        } else {
          // 此处name使用ossId 防止删除出现重名
          item = {
            name: item.name,
            status: item.status,
            size: item.size,
            uploadTime: item.uploadTime,
            url: item.url,
            ossId: item.ossId,
          };
        }
        return item;
      });
    } else {
      fileList.value = [];
    }
  },
  { deep: true, immediate: true },
);

// 有文件数量超出时会触发，文件大小超出限制、文件同名时会触发等场景。注意如果设置允许上传同名文件，则此事件不会触发
const onValidate = (params: { type: UploadValidateType; files: UploadFile[] }) => {
  const { files, type } = params;
  if (type === 'FILE_OVER_SIZE_LIMIT') {
    files.map((t) => t.name).join('、');
    proxy.$modal.msgWarning(`${files.map((t) => t.name).join('、')} 等文件大小超出限制，已自动过滤`, 5000);
  } else if (type === 'FILES_OVER_LENGTH_LIMIT') {
    proxy.$modal.msgWarning('文件数量超出限制，仅上传未超出数量的文件');
  } else if (type === 'FILTER_FILE_SAME_NAME') {
    // 如果希望支持上传同名文件，请设置 allowUploadDuplicateFile={true}
    proxy.$modal.msgWarning('不允许上传同名文件');
  }
};

// 拦截默认事件，打开选择窗口
function handleOpenUpload(event: PointerEvent) {
  if (!event.pointerType && (effectiveSupportUrl.value || props.supportSelectFile)) {
    event.preventDefault();
    open.value = true;
  }
}

// 处理选中的图片
function handleSelectSubmit(values: SelectFile[]) {
  let validate: { type: UploadValidateType; files: UploadFile[] };
  let rowValues = values;
  // 做一些校验
  // 检查文件大小
  const maxSize = props.fileSize * 1024 * 1024;
  if (rowValues.some((value) => value.size > maxSize)) {
    const arr1: SelectFile[] = [];
    const arr2: SelectFile[] = [];
    rowValues.forEach((value) => {
      if (value.size > maxSize) {
        arr2.push(value);
      } else {
        arr1.push(value);
      }
    });
    rowValues = arr1;
    validate = {
      type: 'FILE_OVER_SIZE_LIMIT',
      files: arr2,
    };
    onValidate(validate);
  }
  // 检查文件类型
  if (props.fileType?.length) {
    const arr1: SelectFile[] = [];
    const arr2: SelectFile[] = [];
    rowValues.forEach((value) => {
      const suffix = getHttpFileSuffix(value.name);
      if (props.fileType.includes(suffix?.toLowerCase())) {
        arr1.push(value);
      } else {
        arr2.push(value);
      }
    });
    if (arr2.length) {
      proxy.$modal.msgWarning(`文件格式不正确, 已自动过滤非${props.fileType.join('/')}图片格式文件!`);
    } else {
      rowValues = arr1;
    }
  }
  // 检查重复名称
  if (props.allowUploadDuplicateFile) {
    const map = new Map<string, SelectFile>();
    rowValues.forEach((value) => map.set(value.name, value));
    let exist = false;
    if (map.size !== rowValues.length) {
      exist = true;
    }
    const keys = [...map.keys()];
    if (fileList.value.some((value) => keys.includes(value.name))) {
      exist = true;
    }
    if (exist) {
      validate = {
        type: 'FILE_OVER_SIZE_LIMIT',
        files: [],
      };
      onValidate(validate);
      return false;
    }
  }
  // 检查文件数量
  if (props.limit !== 0 && fileList.value.length + rowValues.length > props.limit) {
    rowValues = rowValues.slice(0, props.limit - fileList.value.length);
    validate = {
      type: 'FILES_OVER_LENGTH_LIMIT',
      files: rowValues.slice(props.limit - fileList.value.length),
    };
    onValidate(validate);
  }
  uploadedSuccessfully(rowValues);
  return true;
}

// 上传前loading加载
async function handleBeforeUpload(file: UploadFile) {
  let isImg: boolean;
  if (props.fileType.length) {
    let fileExtension = '';
    if (file.name.lastIndexOf('.') > -1) {
      fileExtension = getHttpFileSuffix(file.name);
    }
    isImg = props.fileType.some((type) => {
      if (file.type.indexOf(type) > -1) return true;
      return fileExtension && fileExtension.indexOf(type) > -1;
    });
  } else {
    isImg = file.type.indexOf('image') > -1;
  }
  if (!isImg) {
    proxy.$modal.msgError(`文件格式不正确, 请上传${props.fileType.join('/')}图片格式文件!`);
    return false;
  }
  if (file.name.includes(',')) {
    proxy?.$modal.msgError('文件名不正确，不能包含英文逗号!');
    return false;
  }
  // 压缩图片，开启压缩并且大于指定的压缩大小时才压缩
  if (props.compressSupport && file.size / 1024 > props.compressTargetSize) {
    const blob = await compressAccurately(file.raw, props.compressTargetSize);
    file.raw = blobToFile(blob, file.name, file.type);
    file.size = file.raw.size;
  }
  return true;
}

// 单上传成功回调，多选回调多次
function handleOneUploadSuccess(context: Pick<SuccessContext, 'e' | 'file' | 'response' | 'XMLHttpRequest'>) {
  if (context.response.code !== 200) {
    proxy.$modal.msgError(context.response.msg);
  } else {
    proxy.$modal.msgSuccess(`文件【${context.response.data.fileName}】上传成功！`);
  }
}

// 上传成功回调
function handleUploadSuccess(context: SuccessContext) {
  const uploadList = context.results
    .filter((value) => value.response.code === 200)
    .map((value) => {
      return { name: value.response.data.fileName, url: value.response.data.url, ossId: value.response.data.ossId };
    });
  uploadedSuccessfully(uploadList);
}

// 删除图片
function handleDelete({ file }: UploadRemoveContext) {
  const { ossId, name } = file;
  // 直接上传时，删除旧数据
  if (!effectiveSupportUrl.value && !props.supportSelectFile) {
    delOss(ossId).then(() => {
      proxy.$modal.msgSuccess(`文件【${name}】删除成功！`);
    });
  }
  const value = listToString(fileList.value);
  emit('update:modelValue', value);
  emit('change', value);
}

// 上传结束处理
function uploadedSuccessfully(uploadList: UploadFile[]) {
  fileList.value = fileList.value.filter((f) => f.url !== undefined).concat(uploadList);
  const value = listToString(fileList.value);
  emit('update:modelValue', value);
  emit('change', value);
}

// 上传失败
function handleUploadError() {
  proxy.$modal.msgError('上传图片失败');
}

// 对象转成指定字符串分隔
function listToString(list: UploadFile[], separator?: string) {
  let strs = '';
  separator = separator || ',';
  for (const i in list) {
    if (props.mode === 'url') {
      if (list[i].url && list[i].url.indexOf('blob:') !== 0) {
        strs += list[i].url + separator;
      }
    } else if (props.mode === 'id') {
      if (list[i].ossId && list[i].url.indexOf('blob:') !== 0) {
        strs += list[i].ossId + separator;
      }
    }
  }
  return strs !== '' ? strs.substring(0, strs.length - 1) : '';
}
</script>
