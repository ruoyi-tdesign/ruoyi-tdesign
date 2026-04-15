<template>
  <t-dialog
    v-model:visible="visible"
    :close-on-overlay-click="false"
    width="min(450px, 100%)"
    :header="title"
    attach="body"
    :footer="null"
    dialog-class-name="upload-excel"
    @close="handleClose"
  >
    <t-space direction="vertical" size="4px" style="width: 100%">
      <t-upload
        ref="uploadRef"
        v-model="files"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="`${upload.url}?updateSupport=${upload.updateSupport}`"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        theme="file"
        draggable
        tips="仅允许导入xls、xlsx格式文件。"
      >
      </t-upload>
      <div><t-checkbox v-model="upload.updateSupport" />是否更新已经存在的敏感词数据</div>
      <t-link theme="primary" hover="color" style="font-size: 12px; vertical-align: baseline" @click="importTemplate">
        下载模板
      </t-link>
    </t-space>
  </t-dialog>
</template>
<script setup lang="tsx">
import { storeToRefs } from 'pinia';
import type { SuccessContext, UploadInstanceFunctions, UploadProps } from 'tdesign-vue-next';
import { getCurrentInstance, reactive, ref } from 'vue';

import MyScrollbar from '@/components/my-scrollbar/index.vue';
import { useUserStore } from '@/store';

const props = defineProps({
  title: {
    type: String,
    default: '',
  },
  // 上传api地址
  uploadApi: {
    type: String,
    required: true,
  },
  // 下载模板api地址
  downloadTemplateApi: {
    type: String,
    required: true,
  },
  templateFilename: {
    type: String,
    default: 'template',
  },
});
const emit = defineEmits<{
  (e: 'refresh'): void;
}>();
const { token } = storeToRefs(useUserStore());
const { proxy } = getCurrentInstance();
const visible = defineModel('visible', {
  type: Boolean,
  default: false,
});
const files = ref<UploadProps['value']>([]);

const uploadRef = ref<UploadInstanceFunctions>();

/** 导入参数 */
const upload = reactive({
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的敏感词数据
  updateSupport: false,
  // 设置上传的请求头部
  headers: { Authorization: `Bearer ${token.value}` },
  // 上传的地址
  url: `${import.meta.env.VITE_APP_BASE_API}/${props.uploadApi}`,
});

const handleClose = () => {
  files.value = [];
};

/** 下载模板操作 */
function importTemplate() {
  proxy.download(props.downloadTemplateApi, {}, `${props.templateFilename}_${Date.now()}.xlsx`);
}
/** 文件上传中处理 */
const handleFileUploadProgress = () => {
  upload.isUploading = true;
};
/** 文件上传成功处理 */
const handleFileSuccess = (context: SuccessContext) => {
  visible.value = false;
  handleClose();
  upload.isUploading = false;
  proxy.$modal.alert({
    placement: 'center',
    body: (_) => (
      <MyScrollbar class="dialog-content-max-height" style="padding: 10px 20px 0">
        <div v-html={context.response.msg} />
      </MyScrollbar>
    ),
    footer: null,
    header: '导入结果',
  });
  emit('refresh');
};
</script>
<style lang="less" scoped>
:global(.upload-excel .t-upload__dragger) {
  width: 100%;
}
</style>
