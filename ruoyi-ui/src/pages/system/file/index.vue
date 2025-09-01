<template>
  <div>
    <t-space direction="vertical" style="width: 100%">
      <t-space>
        <t-button
          v-if="imageUpload || fileUpload"
          v-hasPermi="['system:file:upload']"
          theme="primary"
          @click="handleUpload"
        >
          <template #icon> <cloud-upload-icon /></template>
          上传
        </t-button>
        <t-button v-hasPermi="['system:file:remove']" theme="danger" :disabled="!ids.length" @click="handleDelete()">
          <template #icon> <delete-icon /> </template>
          删除
        </t-button>
        <t-button v-hasPermi="['system:file:download']" :disabled="ids.length !== 1" @click="handleDownload()">
          <template #icon> <download-icon /> </template>
          下载
        </t-button>
        <t-image-viewer v-model:index="imagePreviewIndex" :images="previewList" :z-index="5000">
          <template #trigger="{ open }">
            <t-button v-hasPermi="['system:file:query']" :disabled="previewList.length === 0" @click="open">
              <template #icon> <browse-icon /> </template>
              预览
            </t-button>
          </template>
        </t-image-viewer>
        <t-button v-hasPermi="['system:file:edit']" :disabled="ids.length !== 1" @click="handleUpdate()">
          <template #icon> <info-circle-icon /> </template>
          属性
        </t-button>
        <t-button v-hasPermi="['system:file:lock']" :disabled="ids.length === 0" @click="handleUnlock()">
          <template #icon> <lock-off-icon /> </template>
          解锁
        </t-button>
        <t-button v-hasPermi="['system:file:lock']" :disabled="ids.length === 0" @click="handleLock()">
          <template #icon> <lock-on-icon /> </template>
          加锁
        </t-button>
        <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline">
          <t-form-item label-width="0px" name="originalFilename">
            <t-input v-model="queryParams.originalFilename" placeholder="搜索文件名" @enter="handleQuery">
              <template #suffix-icon>
                <search-icon :style="{ cursor: 'pointer' }" size="var(--td-comp-size-xxxs)" @click="handleQuery" />
              </template>
            </t-input>
          </t-form-item>
        </t-form>
      </t-space>
      <t-loading :loading="loading">
        <div v-if="fileList.length === 0" class="t-table__empty">暂无数据</div>
        <rect-select
          v-else
          class="list-card-gallery"
          :disabled="!multiple"
          :box-max-height="rectMaxHeight"
          @change="handleRectChange"
          @keydown.ctrl.a.stop.prevent="handleCheckedAll"
        >
          <div
            v-for="(file, index) in effectiveFileList"
            :key="file.fileId"
            :aria-checked="file.active ? 'true' : 'false'"
            class="list-card-gallery-item"
            role="checkbox"
            :tabindex="index"
            :title="file.originalFilename"
            @mousedown.stop
            @dblclick.stop="handleUpdate(file)"
            @click.exact.stop="handleClick(file)"
            @click.ctrl.exact.stop="handleCtrlClick(file)"
            @click.shift.exact.stop="handleShiftClick(file)"
            @click.ctrl.shift.stop="handleShiftClick(file, true)"
          >
            <div
              class="list-card-gallery-item__label"
              :class="{ 'list-card-gallery-item__label--active': file.active }"
            >
              <figure class="list-card-gallery-figure" data-visible="true">
                <figcaption class="list-card-gallery-figure__caption">
                  {{ file.ext }}
                  <template v-if="thumbnailSize >= 100"> ({{ bytesToSize(file.size).replace(' ', '') }}) </template>
                </figcaption>
                <div class="list-card-gallery-figure__content">
                  <div
                    v-if="getMediaType(file) !== 'image'"
                    :class="`gallery-doc-icon gallery-doc-icon--${getMediaType(file)}`"
                  >
                    <component :is="getMediaType(file) + '-svg'" class="gallery-doc-icon__icon" />
                  </div>
                  <picture v-else class="list-card-gallery-responsive-image list-card-gallery-responsive-image--fit">
                    <img
                      draggable="false"
                      class="list-card-gallery-responsive-image__img--fit list-card-gallery-responsive-image__img--cover"
                      :src="
                        getVisitUrl(
                          file.previewUrl,
                          'x-oss-process=image/auto-orient,1/resize,m_lfit,w_300/quality,q_90&w=300&q=0.9',
                        )
                      "
                      :alt="file.originalFilename"
                    />
                  </picture>
                </div>
              </figure>
              <div class="list-card-gallery-item__details">
                <span class="list-card-gallery-item__name">
                  <lock-on-icon v-if="file.isLock === 1" /><span>{{ file.originalFilename }}</span>
                </span>
                <button
                  class="gallery-btn gallery-btn--neutral gallery-btn--plain gallery-btn--small gallery-btn--icon-only-small list-card-gallery-item__checkmark"
                  :class="{ 'list-card-gallery-item__checkmark--active': file.active }"
                  type="button"
                  @click.stop="buttonChecked(file)"
                >
                  <span>
                    <check-icon class="gallery-icon gallery-icon--base gallery-btn__icon" />
                  </span>
                </button>
              </div>
            </div>
          </div>
        </rect-select>
      </t-loading>
      <div class="list-card-pagination">
        <t-pagination
          v-model="pagination.current"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :show-jumper="pagination.showJumper"
          :page-size-options="[10, 20, 50]"
          @change="pagination.onChange"
        >
          <template #totalContent>
            <div class="t-pagination__total">
              共 {{ pagination.total }} 项数据
              <template v-if="ids.length !== 0">&nbsp;&nbsp;选中 {{ ids.length }} 项</template>
            </div>
          </template>
        </t-pagination>
      </div>
    </t-space>

    <!-- 上传文件存储对话框 -->
    <t-dialog
      v-model:visible="openUpload"
      :header="uploadTitle"
      destroy-on-close
      :close-on-overlay-click="false"
      placement="center"
      width="700px"
      @confirm="submitUploadForm"
    >
      <t-form :data="uploadForm" label-align="right" label-width="80px">
        <t-form-item label="上传类型">
          <t-radio-group v-model="rowType" variant="default-filled">
            <t-radio-button v-if="fileUpload" :value="0">上传文件</t-radio-button>
            <t-radio-button v-if="imageUpload" :value="1">上传图片</t-radio-button>
          </t-radio-group>
        </t-form-item>
        <t-form-item v-if="rowType === 1" label="压缩图片">
          <t-switch v-model="isCompress" />
        </t-form-item>
        <t-form-item :label="rowType === 0 ? '选择文件' : '选择图片'">
          <x-file-upload
            v-if="rowType === 0 && fileUpload"
            v-model="uploadForm.file"
            :limit="0"
            theme="file-flow"
            :support-select-file="false"
            :file-type="fileUploadProps?.fileType"
            :file-size="fileUploadProps?.fileSize"
            :accept="fileUploadProps?.accept"
            :file-category-id="categoryId"
          />
          <x-image-upload
            v-if="rowType === 1 && imageUpload"
            v-model="uploadForm.file"
            :limit="0"
            theme="image-flow"
            :support-select-file="false"
            :file-size="imageUploadProps?.fileSize"
            :file-type="imageUploadProps?.fileType"
            :accept="imageUploadProps?.accept"
            :file-category-id="categoryId"
            :compress-support="isCompress"
          />
        </t-form-item>
      </t-form>
    </t-dialog>
    <!-- 文件存储详情 -->
    <t-dialog v-model:visible="openView" header="文件存储详情" width="700px" :footer="false">
      <my-descriptions :loading="buttonLoading">
        <t-descriptions-item label="文件id">{{ form.fileId }}</t-descriptions-item>
        <t-descriptions-item label="文件名">{{ form.filename }}</t-descriptions-item>
        <t-descriptions-item label="原名">{{ form.originalFilename }}</t-descriptions-item>
        <t-descriptions-item label="锁定状态">{{ form.isLock }}</t-descriptions-item>
        <t-descriptions-item label="URL">
          <t-space direction="vertical" size="2px">
            <div
              style="
                word-wrap: break-word;
                border: 1px solid #dedede;
                line-height: 1.4;
                padding: 8px;
                white-space: normal;
                word-break: break-all;
              "
            >
              {{ getVisitUrl(form.previewUrl) }}
            </div>
            <t-space>
              <my-link @click="handleDownload(form.downloadUrl, form.originalFilename)">下载</my-link>
              <my-link @click="copyText(getVisitUrl(form.previewUrl))">复制链接URL</my-link>
            </t-space>
          </t-space>
        </t-descriptions-item>
        <t-descriptions-item label="分类路径" :span="2">{{ form.categoryPath ?? '/' }}</t-descriptions-item>
        <t-descriptions-item label="文件类型">{{ form.contentType }}</t-descriptions-item>
        <t-descriptions-item label="大小">{{ bytesToSize(form.size).replace(' ', '') }}</t-descriptions-item>
        <t-descriptions-item label="修改时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
    <t-dialog
      v-model:visible="openView"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      placement="center"
      width="550px"
      :confirm-btn="{
        loading: buttonLoading,
      }"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="fileRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(4em + 31px)"
          scroll-to-first-error="smooth"
        >
          <t-form-item label="fileId" name="fileId">
            {{ form.fileId }}
          </t-form-item>
          <t-form-item label="文件名" name="filename">
            {{ form.filename }}
          </t-form-item>
          <t-form-item label="原名" name="originalFilename">
            <t-input v-model="form.originalFilename" placeholder="请输入原名" clearable />
          </t-form-item>
          <t-form-item label="锁定状态" name="isLock">
            <t-switch v-model="form.isLock" :custom-value="[1, 0]" />
          </t-form-item>
          <t-form-item label="URL" name="url">
            <t-space direction="vertical" size="2px">
              <div
                style="
                  word-wrap: break-word;
                  border: 1px solid #dedede;
                  line-height: 1.4;
                  padding: 8px;
                  white-space: normal;
                  word-break: break-all;
                "
              >
                {{ getVisitUrl(form.previewUrl) }}
              </div>
              <t-space>
                <my-link @click="handleDownload(form.downloadUrl, form.originalFilename)">下载</my-link>
                <my-link @click="copyText(getVisitUrl(form.previewUrl))">复制链接URL</my-link>
              </t-space>
            </t-space>
          </t-form-item>
          <t-form-item label="分类路径" name="categoryPath">
            {{ form.categoryPath ?? '/' }}
          </t-form-item>
          <t-form-item label="文件类型" name="contentType" style="word-break: break-all">
            {{ form.contentType }}
          </t-form-item>
          <t-form-item label="大小" name="size">
            {{ bytesToSize(form.size).replace(' ', '') }}
          </t-form-item>
          <t-form-item label="修改时间" name="updateTime">
            {{ parseTime(form.updateTime) }}
          </t-form-item>
          <t-form-item label="创建时间" name="createTime">
            {{ parseTime(form.createTime) }}
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>
  </div>
</template>

<script lang="ts" setup>
import { useClipboard } from '@vueuse/core';
import {
  BrowseIcon,
  CheckIcon,
  CloudUploadIcon,
  DeleteIcon,
  DownloadIcon,
  InfoCircleIcon,
  LockOffIcon,
  LockOnIcon,
  SearchIcon,
} from 'tdesign-icons-vue-next';
import type { FormInstanceFunctions, FormRule, PageInfo } from 'tdesign-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref, watch } from 'vue';

import { delFile, getFile, listFile, lockFile, unlockFile } from '@/api/system/file';
import type { SysFileActiveVo, SysFileForm, SysFileQuery, SysFileVo } from '@/api/system/model/fileModel';
import ArchiveSvg from '@/assets/file-type/archive.svg?component';
import AudioSvg from '@/assets/file-type/audio.svg?component';
import ExcelSvg from '@/assets/file-type/excel.svg?component';
import PdfSvg from '@/assets/file-type/pdf.svg?component';
import PptSvg from '@/assets/file-type/ppt.svg?component';
import TextSvg from '@/assets/file-type/text.svg?component';
import UnknownSvg from '@/assets/file-type/unknown.svg?component';
import VideoSvg from '@/assets/file-type/video.svg?component';
import WordSvg from '@/assets/file-type/word.svg?component';
import type { FileUploadProps } from '@/components/file-upload/index.vue';
import type { ImageUploadProps } from '@/components/image-upload/index.vue';
import RectSelect from '@/components/rect-select/index.vue';
import { getVisitUrl } from '@/utils/ruoyi';

defineOptions({
  name: 'FileList',
  components: { ArchiveSvg, ExcelSvg, PdfSvg, PptSvg, AudioSvg, TextSvg, UnknownSvg, VideoSvg, WordSvg },
});

export interface FileListProps {
  /** 分类id */
  categoryId?: number;
  /** 启用多选 */
  multiple?: boolean;
  /** 缩略图大小，px */
  thumbnailSize?: number;
  /** 开启图片上传 */
  imageUpload?: boolean;
  /** 开启文件上传 */
  fileUpload?: boolean;
  /** 查询参数 */
  queryParam?: Pick<SysFileQuery, 'maxSize' | 'contentTypes' | 'suffixes'>;
  /** 图片上传组件参数 */
  imageUploadProps?: Pick<ImageUploadProps, 'fileType' | 'fileSize' | 'accept'>;
  /** 文件上传组件参数 */
  fileUploadProps?: Pick<FileUploadProps, 'fileType' | 'fileSize' | 'accept'>;
  /** 选区最大高度 */
  rectMaxHeight?: string;
}

const props = withDefaults(defineProps<FileListProps>(), {
  imageUpload: true,
  fileUpload: true,
  multiple: true,
  thumbnailSize: 120,
});
// const fileUploadProps = computed(() => props.fileUploadProps);
const imageUploadProps = computed(() => props.imageUploadProps);
watch(
  () => [props.categoryId, props.queryParam],
  () => getList(),
);
watch(
  () => props.multiple,
  (value, oldValue) => {
    if (oldValue) {
      // 从多选变更多单选，需要清理多余的选项
      ids.value.splice(1);
    }
  },
);
const emit = defineEmits<{
  (e: 'change', selectValues: SysFileVo[]): void;
  (e: 'update'): void;
}>();

const { proxy } = getCurrentInstance();

const fileRef = ref<FormInstanceFunctions>();
const fileList = ref<SysFileVo[]>([]);
const openUpload = ref(false);
const openView = ref(false);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<number[]>([]);
const total = ref(0);
const type = ref(0);
const title = ref('');
const shiftId = ref<number>();
// 预览图下标
const imagePreviewIndex = ref(0);
// 缩略图大小尺寸
const thumbnailSizePixel = computed(() => `${props.thumbnailSize}px`);
// 文件格式
const fileType = {
  image: 'jpg,jpeg,bmp,gif,png,svg,ico,tif,tiff,webp,pic,tga,jng,mng,jbg,jpe,heic,lbm'.split(','),
  archive: 'zip,7z,rar,gz,jar,lzh,taz,arj,z,ace,tar,xz,bz2,win'.split(','),
  excel: ['xls', 'xlsx', 'csv'],
  pdf: ['pdf'],
  ppt: ['ppt', 'pptx'],
  audio: ['wav', 'aif', 'au', 'mp3', 'ram', 'wma', 'mmf', 'amr', 'aac', 'flac', 'aifc', 'awr', 'cda', 'cdr', 'dig'],
  text: 'txt,sql,ini,xml,html,htm,css,scss,less,js,ts,java,inf,py,json'.split(','),
  video: 'avi,mp4,m4v,mkv,webm,flv,mov,wmv,3gp,3g2,mpg,vob,ogg,swf,dxr,fla,rm,mpe,mpeg,ts'.split(','),
  word: ['doc', 'docx', 'wps', 'rtf', 'odt'],
};
const isCompress = ref(false);
const rowType = computed({
  get() {
    if (props.imageUpload === false) {
      return 0;
    }
    if (props.fileUpload === false) {
      return 1;
    }
    return type.value;
  },
  set: (val) => (type.value = val),
});

const rules = ref<Record<string, Array<FormRule>>>({
  originalFilename: [
    { required: true, message: '原名不能为空' },
    { pattern: /^[^.][^\\/<>:?"|*]*$/, message: '文件名不能包含下列任何字符：\\/<>:?"|*' },
  ],
  fileCategoryId: [{ required: true, message: '分类不能为空' }],
  isLock: [{ required: true, message: '是否锁定状态不能为空' }],
});

const uploadForm = ref<{ file?: any }>({});
// 提交表单对象
const form = ref<SysFileVo & SysFileForm>({
  isLock: 0,
});
const queryParams = ref<SysFileQuery>({
  pageNum: 1,
  pageSize: 20,
  originalFilename: undefined,
});

const uploadTitle = computed(() => {
  if (rowType.value === 0) {
    return '上传文件';
  }
  return '上传图片';
});
// 保存了选中状态的文件列表
const effectiveFileList = computed(() => {
  const fileCollection: SysFileActiveVo[] = [];
  fileList.value.forEach((file) => {
    fileCollection.push({
      ...file,
      active: ids.value.includes(file.fileId),
    });
  });
  return fileCollection;
});
// 图片预览列表
const previewList = computed(() => {
  return effectiveFileList.value
    .filter((file) => {
      return file.active && getMediaType(file) === 'image';
    })
    .map((value) => getVisitUrl(value.previewUrl));
});

// 分页
const pagination = computed(() => {
  return {
    current: queryParams.value.pageNum,
    pageSize: queryParams.value.pageSize,
    total: total.value,
    showJumper: true,
    onChange: (pageInfo: PageInfo) => {
      queryParams.value.pageNum = pageInfo.current;
      queryParams.value.pageSize = pageInfo.pageSize;
      getList();
    },
  };
});

/** 获取媒体类型 */
function getMediaType(file: SysFileVo) {
  const suffix = file.ext?.toLowerCase();
  if (suffix) {
    const entries = Object.entries(fileType);
    const type = entries.find((value) => {
      return value[1].includes(suffix);
    });
    if (type) {
      return type[0];
    }
  }
  const contentType = file.contentType?.toLowerCase();
  if (contentType) {
    if (contentType.startsWith('image/')) {
      return 'image';
    }
    if (contentType.startsWith('video/')) {
      return 'video';
    }
    if (contentType.startsWith('audio/')) {
      return 'audio';
    }
  }
  return 'unknown';
}

/** 查询文件对象存储列表 */
function getList() {
  loading.value = true;
  queryParams.value.fileCategoryId = props.categoryId;
  queryParams.value.maxSize = props.queryParam?.maxSize;
  queryParams.value.contentTypes = props.queryParam?.contentTypes;
  listFile(queryParams.value)
    .then((response) => {
      ids.value = [];
      shiftId.value = null;
      fileList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    })
    .finally(() => (loading.value = false));
}
/** 表单重置 */
function reset() {
  uploadForm.value = {
    file: undefined,
  };
  form.value = {
    isLock: 0,
    fileCategoryId: 0,
  };
  proxy.resetForm('fileRef');
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 上传按钮操作 */
function handleUpload() {
  reset();
  openUpload.value = true;
}
/** 修改按钮操作 */
async function handleUpdate(row?: SysFileVo) {
  buttonLoading.value = true;
  reset();
  openView.value = true;
  title.value = '修改文件存储';
  const fileId = row?.fileId || ids.value.at(0);
  getFile(fileId).then((response) => {
    buttonLoading.value = false;
    form.value = response.data;
  });
}
/** 解锁按钮操作 */
function handleUnlock() {
  const fileIds = ids.value;
  const files = fileList.value.filter((value) => ids.value.includes(value.fileId));
  let content;
  if (files.length === 1) {
    content = `是否确认解锁文件【"${files.at(0).originalFilename}"】?`;
  } else {
    content = `是否确认解锁选中的${fileIds.length}项文件?`;
  }
  proxy.$modal.confirm(content, () => {
    const msgLoading = proxy.$modal.msgLoading('正在解锁中...');
    return unlockFile(fileIds)
      .then(() => {
        ids.value = [];
        getList();
        emit('update');
        proxy.$modal.msgSuccess('解锁成功');
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  });
}
/** 加锁按钮操作 */
function handleLock() {
  const fileIds = ids.value;
  const files = fileList.value.filter((value) => ids.value.includes(value.fileId));
  let content;
  if (files.length === 1) {
    content = `是否确认加锁文件【"${files.at(0).originalFilename}"】?`;
  } else {
    content = `是否确认加锁选中的${fileIds.length}项文件?`;
  }
  proxy.$modal.confirm(content, () => {
    const msgLoading = proxy.$modal.msgLoading('正在加锁中...');
    return lockFile(fileIds)
      .then(() => {
        ids.value = [];
        getList();
        emit('update');
        proxy.$modal.msgSuccess('加锁成功');
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  });
}
/** 上传提交按钮 */
function submitUploadForm() {
  openUpload.value = false;
  reset();
  getList();
}
/** 复制成功 */
function copyText(text: string) {
  const { copy } = useClipboard({ source: text });
  copy()
    .then(() => {
      MessagePlugin.closeAll();
      proxy.$modal.msgSuccess('复制成功');
    })
    .catch(() => {
      MessagePlugin.closeAll();
      proxy.$modal.msgError('复制失败');
    });
}
/** 下载按钮操作 */
function handleDownload(downloadUrl?: string, filename?: string) {
  const fileId = ids.value.at(0);
  const file = fileList.value.find((value) => fileId === value.fileId);
  proxy.$download.file(downloadUrl ?? file.downloadUrl, filename ?? file.originalFilename);
}
/** 删除按钮操作 */
function handleDelete() {
  const fileIds = ids.value;
  const files = fileList.value.filter((value) => ids.value.includes(value.fileId));
  let content;
  if (files.length === 1) {
    content = `是否确认删除文件【"${files.at(0).originalFilename}"】?`;
  } else {
    content = `是否确认删除选中的${fileIds.length}项文件?`;
  }
  proxy.$modal.confirm(content, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delFile(fileIds)
      .then(() => {
        ids.value = [];
        getList();
        emit('update');
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  });
}

/** 处理鼠标区域选择 */
function handleRectChange(checkedIndexes: number[]) {
  imagePreviewIndex.value = 0;
  ids.value = fileList.value.filter((value, index) => checkedIndexes.includes(index)).map((value) => value.fileId);
  handleChangeEmit();
}

/** 处理单击事件 */
function handleClick(file: SysFileVo) {
  imagePreviewIndex.value = 0;
  shiftId.value = file.fileId;
  ids.value = [file.fileId];
  handleChangeEmit();
}

/** 处理ctrl + 单击事件 */
function handleCtrlClick(file: SysFileVo) {
  shiftId.value = file.fileId;
  imagePreviewIndex.value = 0;
  if (props.multiple) {
    const index = ids.value.indexOf(file.fileId);
    if (index !== -1) {
      ids.value.splice(index, 1);
    } else {
      ids.value.push(file.fileId);
    }
    handleChangeEmit();
  } else {
    handleClick(file);
  }
}

/** 处理shift + 单击 和 ctrl + shift + 单击事件 */
function handleShiftClick(file: SysFileVo, append = false) {
  imagePreviewIndex.value = 0;
  if (props.multiple) {
    const arr = [];
    const fileId = shiftId.value ?? fileList.value.at(0).fileId;
    const startIndex = fileList.value.findIndex((value) => value.fileId === fileId) ?? 0;
    const endIndex = fileList.value.findIndex((value) => value.fileId === file.fileId);
    let i = Math.min(startIndex, endIndex);
    const max = Math.max(startIndex, endIndex);
    for (; i <= max; i++) {
      const value = fileList.value[i];
      if (!append || (append && !ids.value.includes(value.fileId))) {
        arr.push(value.fileId);
      }
    }
    if (append) {
      ids.value = ids.value.concat(arr);
    } else {
      ids.value = arr;
    }
    handleChangeEmit();
  } else {
    handleClick(file);
  }
}

/** 按钮选中 */
function buttonChecked(file: SysFileVo) {
  imagePreviewIndex.value = 0;
  const index = ids.value.indexOf(file.fileId);
  if (index !== -1) {
    ids.value.splice(index, 1);
  } else if (props.multiple) {
    ids.value.push(file.fileId);
  } else {
    ids.value = [file.fileId];
  }
  handleChangeEmit();
}

/** 选择全部 */
function handleCheckedAll() {
  ids.value = fileList.value.map((value) => value.fileId);
  handleChangeEmit();
}

/** 触发变更提交 */
function handleChangeEmit() {
  const values = fileList.value.filter((value) => ids.value.includes(value.fileId));
  emit('change', values);
}

getList();
</script>
<style lang="less" scoped>
@neutral-h: 220;
@neutral-s: 10%;

.list-card {
  height: 100%;

  &-gallery {
    padding: var(--td-pop-padding-xl);
    background-color: var(--td-bg-color-container-hover);
    display: grid;
    grid-gap: 13.5px;
    grid-template-columns: repeat(auto-fill, minmax(v-bind(thumbnailSizePixel), 1fr));
    -webkit-user-select: none;
    user-select: none;
    outline: none;

    &-responsive-image--fit {
      width: 100%;
      height: 100%;
    }

    &-responsive-image {
      position: absolute;
      opacity: 1;
    }

    &-responsive-image__img--cover {
      object-fit: cover;
    }

    &-responsive-image__img--fit {
      width: 100%;
      height: 100%;
    }

    &-figure {
      height: v-bind(thumbnailSizePixel);
      cursor: pointer;
      margin: 0;
      overflow: hidden;
      position: relative;

      &__caption {
        background-color: hsl(@neutral-h @neutral-s @neutral-s);
        color: hsl(@neutral-h @neutral-s 100%);
        font-size: 12px;
        left: 0;
        padding: 4px;
        position: absolute;
        text-transform: uppercase;
        line-height: 1;
        top: 0;
        z-index: 1;
      }

      &__content {
        margin: -1px;
        height: calc(100% + 1px);
        width: calc(100% + 1px);
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        position: relative;
      }
    }

    &-item {
      color: var(--td-text-color-primary);
      box-sizing: border-box;
      min-width: v-bind(thumbnailSizePixel);

      &:focus-visible {
        outline: none;
      }

      &__label {
        background-color: var(--td-bg-color-container);
        box-shadow: var(--td-shadow-1);
        border-radius: var(--td-radius-default);
        //box-shadow: 0 0 4px 2px var(--td-border-level-1-color);
        display: block;
        overflow: hidden;
        position: relative;
        outline: 1px solid rgb(0 0 0 / 0%);
        transition: outline 200ms;

        &--active {
          outline: 1px solid var(--td-brand-color);
        }
      }

      &__details {
        border-top: 1px solid hsl(@neutral-h @neutral-s 90%);
        cursor: pointer;
        display: flex;
        align-items: center;
        padding: calc(8px * 0.7) 8px;
        position: relative;
        gap: 4px;
      }

      &__name {
        font-size: 11.2px;
        overflow: hidden;
        position: relative;
        text-overflow: ellipsis;
        white-space: nowrap;
        flex-grow: 1;
      }

      &__checkmark {
        flex-shrink: 0;
        opacity: 0;
        color: hsl(@neutral-h @neutral-s 70%);
        transition:
          color 200ms,
          opacity 200ms;
        isolation: isolate;

        &--active {
          color: var(--td-brand-color);
          opacity: 1;
        }
      }

      &:hover &__checkmark {
        opacity: 1;
      }
    }
  }
}

.gallery-btn--plain:hover {
  --btn-background-color: hsl(@neutral-h @neutral-s 60% / 10%);
}

.gallery-btn--plain:active {
  --btn-background-color: hsl(@neutral-h @neutral-s 60% /20%);
}

.gallery-btn--neutral {
  --btn-background-color-plain-hover: hsl(@neutral-h @neutral-s 60% / 10%);
}

.gallery-btn {
  --btn-border-color: transparent;
  --btn-padding-vertical: 8px;
  --btn-padding-horizontal: 12px;
  --btn-font-size: 13.6px;
  --btn-line-height: 20px;

  border-radius: 7px;
  cursor: pointer;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  -webkit-user-select: none;
  user-select: none;
  gap: 4px;
  transition:
    background-color 200ms,
    opacity 200ms;
  font-weight: 500;
  padding: 2.4px;
  background-color: var(--btn-background-color);
  border: 1px solid transparent;
  max-width: 100%;
}

.gallery-icon--base {
  height: 16px;
  min-width: 16px;
}

.gallery-icon {
  display: block;
  fill: currentColor;
  max-width: initial;
  transition: color 200ms;
}

.gallery-doc-icon--unknown {
  background-color: #f5f5f5;
}

.gallery-doc-icon--archive {
  background-color: #f4e9f5;
}

.gallery-doc-icon--empty {
  background-color: #fff;
}

.gallery-doc-icon--excel {
  background-color: #e8f5ef;
}

.gallery-doc-icon--image {
  background-color: #f5f5f5;
}

.gallery-doc-icon--video {
  background-color: #e5f5f7;
}

.gallery-doc-icon--audio {
  background-color: #fff6ea;
}

.gallery-doc-icon--pdf {
  background-color: #ffeae8;
}

.gallery-doc-icon--ppt {
  background-color: #f9ebe8;
}

.gallery-doc-icon--text {
  background-color: #f8f8fb;
}

.gallery-doc-icon--word {
  background-color: #e9eff7;
}

.gallery-doc-icon {
  height: 100%;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.gallery-doc-icon__icon {
  width: 56px;
  height: 66px;
}
</style>
