<template>
  <t-dialog
    v-model:visible="visible"
    :close-on-overlay-click="false"
    :header="title"
    :width="width"
    :z-index="4000"
    attach="body"
    :confirm-btn="{
      content: '确 定',
      theme: 'primary',
      loading: buttonLoading,
    }"
    placement="center"
    @confirm="onConfirm"
  >
    <t-form
      v-if="visible"
      ref="formRef"
      label-align="right"
      :data="form"
      :rules="rules"
      label-width="calc(4em + 31px)"
      scroll-to-first-error="smooth"
      @submit="submitForm"
    >
      <t-tabs v-model="activeTab">
        <t-tab-panel v-if="supportSelectFile" label="我的文件" value="myFile" :destroy-on-hide="false">
          <file-category
            :query-param="formatQueryParam"
            :multiple="multiple"
            :image-upload="imageUpload"
            :file-upload="fileUpload"
            :file-upload-props="fileUploadProps"
            :image-upload-props="imageUploadProps"
            :thumbnail-size="thumbnailSize"
            :rect-max-height="rectMaxHeight"
            @change="handleSelectChange"
          />
        </t-tab-panel>
        <t-tab-panel v-if="supportUrl" label="外链地址" value="url">
          <t-form-item
            v-show="activeTab === 'url'"
            label="外链地址"
            name="url"
            help="支持多个外链地址，每个链接地址占一行"
            style="margin: var(--td-comp-margin-xl) 0; word-break: break-all"
          >
            <t-textarea v-model="form.url" autosize />
          </t-form-item>
        </t-tab-panel>
      </t-tabs>
    </t-form>
  </t-dialog>
</template>
<script setup lang="ts">
import type { CustomValidateResolveType, FormInstanceFunctions, FormRule, SubmitContext } from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref, watch } from 'vue';

import type { SysFileVo } from '@/api/system/model/fileModel';
import type { FileCategoryProps } from '@/pages/system/myFile/index.vue';
import FileCategory from '@/pages/system/myFile/index.vue';
import { getHttpFileName, getHttpFileSuffix, getVisitUrl } from '@/utils/ruoyi';

export interface SelectFile {
  url: string;
  name: string;
  fileId?: number;
  size?: number;
  contentType?: string;
}

defineOptions({
  name: 'XUploadSelect',
});

export interface UploadSelectProps extends FileCategoryProps {
  /** 标题 */
  title?: string;
  /** 支持选择文件 */
  supportSelectFile?: boolean;
  /** 支持手动输入url */
  supportUrl?: boolean;
  /** 提交时调用，返回false可以阻止关闭窗口 */
  onSubmit?: (values: SelectFile[]) => boolean;
}
const props = withDefaults(defineProps<UploadSelectProps>(), {
  imageUpload: true,
  fileUpload: true,
  supportSelectFile: true,
  supportUrl: false,
  multiple: true,
  thumbnailSize: 120,
  rectMaxHeight: 'calc(100vh - 444px)',
});

const formatQueryParam = computed(() => {
  const query = { ...props.queryParam };
  query.contentTypes = props.queryParam?.contentTypes?.map((value) => value.replaceAll('*', '%'));
  return query;
});

// 显示隐藏窗口
const visible = defineModel('visible', {
  type: Boolean,
  default: false,
});
const { proxy } = getCurrentInstance();

const form = ref({
  url: '',
});
const buttonLoading = ref(false);
const formRef = ref<FormInstanceFunctions>();
const activeTab = ref('myFile');
const selectValues = ref<SysFileVo[]>([]);
watch(
  () => [props.supportSelectFile, props.supportUrl],
  () => {
    if (!props.supportSelectFile) {
      activeTab.value = 'url';
    }
    if (!props.supportUrl) {
      activeTab.value = 'myFile';
    }
  },
  { immediate: true },
);

const rules = ref<Record<string, Array<FormRule>>>({
  url: [
    { required: true, message: '外链地址不能为空' },
    { pattern: /^(http(s)?:\/\/[^.\\/]+\.[^\n]+\/[^\n]+\n?)+$/, message: '地址格式错误' },
    { validator: UrlValidator },
  ],
});

/** 手动输入url验证器 */
function UrlValidator(val: string): CustomValidateResolveType {
  const arr = [false, false];
  function valid(fileType: string[]) {
    const urls = val.split('\n');
    return urls.some((value) => {
      const suffix = getHttpFileSuffix(value);
      return fileType.includes(suffix);
    });
  }
  if (props.imageUpload && props.imageUploadProps?.fileType) {
    arr[0] = true;
    if (valid(props.imageUploadProps?.fileType)) return true;
  } else if (props.fileUpload && props.fileUploadProps?.fileType) {
    arr[1] = true;
    if (valid(props.fileUploadProps?.fileType)) return true;
  }
  if (arr[0] && arr[1]) {
    const fileType = props.imageUploadProps.fileType.concat(props.fileUploadProps.fileType);
    return {
      result: false,
      message: `请上传${fileType.join('/')}格式文件!`,
    };
  }
  if (arr[0]) {
    return {
      result: false,
      message: `请上传${props.imageUploadProps.fileType.join('/')}图片格式文件!`,
    };
  }
  if (arr[1]) {
    return {
      result: false,
      message: `请上传${props.fileUploadProps.fileType.join('/')}格式文件!`,
    };
  }
  return true;
}

const width = computed(() => {
  if (activeTab.value === 'myFile') {
    return 'min(1400px, 100%)';
  }
  return '600px';
});

function handleSelectChange(values: SysFileVo[]) {
  selectValues.value = values;
}
/** 表单提交按钮 */
function onConfirm() {
  formRef.value.submit();
}
/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    if (activeTab.value === 'myFile') {
      if (selectValues.value.length === 0) {
        proxy.$modal.msgError('未选择文件');
        return;
      }
      const values = selectValues.value.map((value) => ({
        url: getVisitUrl(value.previewUrl),
        name: value.originalFilename,
        fileId: value.fileId,
        size: value.size,
        contentType: value.contentType,
      }));
      const result = props.onSubmit.call(this, values);
      if (result) {
        visible.value = false;
        selectValues.value = [];
      }
    } else {
      const values = form.value.url
        .split('\n')
        .filter((value) => value)
        .map((url) => {
          const name = getHttpFileName(url);
          return { url, name };
        });
      const result = props.onSubmit.call(this, values);
      if (result) {
        visible.value = false;
        selectValues.value = [];
      }
    }
  } else {
    proxy.$modal.msgError(firstError);
  }
}
</script>
