<template>
  <div>
    <!--  文档:https://www.tiny.cloud/docs/tinymce/6/-->
    <editor ref="editorRef" :model-value="modelValue" :init="conf" :disabled="disabled" v-bind="$attrs" />
    <upload-select
      v-if="selectFile"
      v-model:visible="open"
      :title="title"
      :support-url="false"
      :query-param="query"
      :multiple="false"
      :image-upload="imageUpload"
      :file-upload="fileUpload"
      :image-upload-props="{
        accept: ['image/*'],
        fileSize: imageMaxSize,
      }"
      :file-upload-props="{
        accept: fileAccept,
        fileSize: fileMaxSize,
      }"
      :on-submit="handleSelectSubmit"
    />
  </div>
</template>

<script lang="ts" setup>
// eslint-disable-next-line
import tinymce from 'tinymce/tinymce';
import 'tinymce/models/dom';
import 'tinymce/icons/default/icons.min.js'; // 引入编辑器图标icon，不引入则不显示对应图标
import 'tinymce/themes/silver/theme.min.js'; // 引入编辑器主题
// Skins
import 'tinymce/skins/ui/oxide/skin.min.css';
import 'tinymce/skins/ui/oxide/content.min.css';
// Plugins 组件根据init中调用情况自行加载
import 'tinymce/plugins/preview'; // 预览
import 'tinymce/plugins/importcss'; // 引入css
import 'tinymce/plugins/searchreplace'; // 查找替换
import 'tinymce/plugins/autolink'; // 自动链接
// import 'tinymce/plugins/autosave'; // 自动存稿
import 'tinymce/plugins/save'; // 保存
import 'tinymce/plugins/directionality'; // 文字方向
import 'tinymce/plugins/code'; // 编辑源码
import 'tinymce/plugins/visualblocks'; // 显示元素范围
import 'tinymce/plugins/visualchars'; // 显示不可见字符
import 'tinymce/plugins/fullscreen'; // 全屏
import 'tinymce/plugins/image'; // 插入编辑图片
import 'tinymce/plugins/link'; // 超链接
import 'tinymce/plugins/media'; // 插入编辑媒体
import 'tinymce/plugins/codesample'; // 代码示例
import 'tinymce/plugins/table'; // 表格
import 'tinymce/plugins/charmap'; // 特殊字符
import 'tinymce/plugins/pagebreak'; // 插入分页符
import 'tinymce/plugins/nonbreaking'; // 插入不间断空格
import 'tinymce/plugins/anchor'; // 锚点
import 'tinymce/plugins/insertdatetime'; // 插入日期时间
import 'tinymce/plugins/advlist'; // 高级列表
import 'tinymce/plugins/lists'; // 列表插件
import 'tinymce/plugins/wordcount'; // 字数统计
import 'tinymce/plugins/help'; // 帮助
import 'tinymce/plugins/quickbars'; // 快速工具栏
import 'tinymce/plugins/emoticons'; // 表情
import 'tinymce/plugins/help/js/i18n/keynav/zh_CN.js'; // 导入这个help才能够显示

import 'prismjs'; // 代码高亮
import Editor from '@tinymce/tinymce-vue';
import type { RawEditorOptions } from 'tinymce';
import emojisURL from 'tinymce/plugins/emoticons/js/emojis.js?url';
import contentDarkURL from 'tinymce/skins/content/tinymce-5-dark/content.min.css?url';
import contentURL from 'tinymce/skins/content/default/content.min.css?url';
import { computed, getCurrentInstance, onMounted, ref, watch } from 'vue';

import { uploader } from '@/api/system/oss';
import { useSettingStore } from '@/store';
import type { MyOssProps } from '@/pages/system/ossCategory/components/myOss.vue';
import type { SelectFile } from '@/components/upload-select/index.vue';

export type TinyEditor = Parameters<RawEditorOptions['setup']>[0];

interface EditorProps {
  modelValue?: string;
  // 插件
  plugins?: string | string[];
  // 工具栏
  toolbar?:
    | boolean
    | string
    | string[]
    | Array<{
        name?: string;
        items: string[];
      }>;
  menubar?: string | boolean;
  // 自定义菜单栏
  menu?: Record<string, { title: string; items: string }>;
  // 宽度
  width?: string | number;
  // 高度
  height?: string | number;
  // 是否禁用
  disabled?: boolean;
  // 调整大小.该选项可以是true,false或字符串'both'。false禁用调整大小、true仅启用垂直调整大小、'both'可以在水平和垂直方向上调整大小。
  resize?: boolean | 'both';
  // 图片大小限制(MB)
  imageMaxSize?: number;
  // 文件大小限制(MB)
  fileMaxSize?: number;
  // 媒体文件大小限制(MB)
  mediaMaxSize?: number;
  // 启用图片上传
  imageEnableUpload?: boolean;
  // 启用文件上传
  fileEnableUpload?: boolean;
  // 启用媒体上传
  mediaEnableUpload?: boolean;
  // 启用文件选择功能
  selectFile?: boolean;
  // 富文本setup
  setup?: (editor: TinyEditor) => void;
}

const props = withDefaults(defineProps<EditorProps>(), {
  modelValue: '',
  plugins:
    'preview importcss searchreplace autolink save directionality code visualblocks visualchars fullscreen image link media codesample table charmap pagebreak nonbreaking anchor insertdatetime advlist lists wordcount help charmap quickbars emoticons',
  toolbar: `undo redo | bold italic underline strikethrough | fontfamily fontsize blocks | alignleft aligncenter alignright alignjustify | outdent indent |  numlist bullist | forecolor backcolor removeformat | pagebreak | charmap emoticons | code fullscreen  preview save print | insertfile image media link anchor codesample | ltr rtl`,
  menubar: 'file edit view insert format tools table help',
  width: '100%',
  height: 600,
  disabled: false,
  resize: 'both',
  imageMaxSize: 5,
  fileMaxSize: 5,
  mediaMaxSize: 10,
  imageEnableUpload: true,
  fileEnableUpload: true,
  mediaEnableUpload: true,
  selectFile: true,
});

const baseURL = '/tinymce';
const settingStore = useSettingStore();
const useDarkMode = computed(() => settingStore.displayMode === 'dark');
const editorRef = ref<any>();
const title = ref('');
const fileAccept = ref<MyOssProps['fileUploadProps']['accept']>([]);
const query = ref<MyOssProps['queryParam']>({
  maxSize: 5 * 1024 * 1024,
});
const open = ref(false);
const fileUpload = ref(false);
const imageUpload = ref(false);
const { proxy } = getCurrentInstance();
const filePickerCallback = ref<Parameters<RawEditorOptions['file_picker_callback']>[0]>();

// 选择文件后的回调处理
function handleSelectSubmit(values: SelectFile[]) {
  filePickerCallback.value?.call(this, values[0].url, { title: values[0].name });
  filePickerCallback.value = null;
  return true;
}

/** 统一上传处理 */
function uploadHandle(file: File, fileType: 'file' | 'image' | 'media') {
  return new Promise<string>((resolve, reject) => {
    let error = '';
    if (fileType === 'file') {
      if (file.size / 1024 / 1024 > props.fileMaxSize) {
        error = `上传失败，大小请控制在 ${props.fileMaxSize}M 以内`;
        reject(new Error(error));
        proxy.$modal.msgWarning(error, 5000);
        return;
      }
      if (!props.fileEnableUpload) {
        error = '不允许上传文件';
        reject(new Error(error));
        proxy.$modal.msgWarning(error, 5000);
        return;
      }
    } else if (fileType === 'image') {
      if (file.size / 1024 / 1024 > props.imageMaxSize) {
        error = `上传失败，大小请控制在 ${props.imageMaxSize}M 以内`;
        reject(new Error(error));
        proxy.$modal.msgWarning(error, 5000);
        return;
      }
      if (!props.imageEnableUpload) {
        error = '不允许上传图片';
        reject(new Error(error));
        proxy.$modal.msgWarning(error, 5000);
        return;
      }
    } else if (fileType === 'media') {
      if (file.size / 1024 / 1024 > props.mediaMaxSize) {
        error = `上传失败，大小请控制在 ${props.mediaMaxSize}M 以内`;
        reject(new Error(error));
        proxy.$modal.msgWarning(error, 5000);
        return;
      }
      if (!props.mediaEnableUpload) {
        error = '不允许上传媒体文件';
        reject(new Error(error));
        proxy.$modal.msgWarning(error, 5000);
        return;
      }
    } else {
      error = '不支持的上传文件类型';
      reject(new Error(error));
      proxy.$modal.msgWarning(error, 5000);
      return;
    }
    const formData = new FormData();
    formData.append('file', file);
    const msgLoading = proxy.$modal.msgLoading(`正在上传【${file.name}】文件...`);
    uploader(formData)
      .then((res) => {
        proxy.$modal.msgSuccess(`文件【${file.name}】上传成功！`);
        resolve(res.data.url);
      })
      .catch((reason) => reject(reason))
      .finally(() => proxy.$modal.msgClose(msgLoading));
  });
}

// 上传处理
const imageUploadHandler: RawEditorOptions['images_upload_handler'] = (blobInfo) => {
  const blob = blobInfo.blob();
  return uploadHandle(blob as File, 'image');
};

const filePickerTypes = computed(() => {
  const arr = [];
  if (props.fileEnableUpload) {
    arr.push('file');
  }
  if (props.imageEnableUpload) {
    arr.push('image');
  }
  if (props.mediaEnableUpload) {
    arr.push('media');
  }
  return arr.length > 0 ? arr.join(' ') : '';
});

// 配置项
const conf = computed<RawEditorOptions>(() => {
  const config: RawEditorOptions = {
    setup: props.setup,
    promotion: false,
    license_key: 'gpl',
    // 语言包下载：https://www.tiny.cloud/get-tiny/language-packages/
    language_url: `${baseURL}/langs/zh_CN.js`,
    base_url: baseURL,
    language: 'zh_CN',
    plugins: props.plugins,
    menubar: props.menubar,
    menu: props.menu,
    toolbar: props.toolbar,
    codesample_languages: [
      { text: 'HTML/XML', value: 'markup' },
      { text: 'Vue', value: 'vue' },
      { text: 'Bash', value: 'bash' },
      { text: 'PlainText', value: 'plaintext' },
      { text: 'JavaScript', value: 'javascript' },
      { text: 'Java', value: 'java' },
      { text: 'Json', value: 'json' },
      { text: 'CSS', value: 'css' },
      { text: 'C', value: 'c' },
      { text: 'C#', value: 'csharp' },
      { text: 'C++', value: 'cpp' },
      { text: 'Diff', value: 'diff' },
      { text: 'Dart', value: 'dart' },
      { text: 'PHP', value: 'php' },
      { text: 'Python', value: 'python' },
      { text: 'R', value: 'r' },
      { text: 'Rust', value: 'rust' },
      { text: 'Ruby', value: 'ruby' },
      { text: 'Go', value: 'go' },
      { text: 'Graphql', value: 'graphql' },
      { text: 'Ini', value: 'ini' },
      { text: 'Less', value: 'less' },
      { text: 'Lua', value: 'lua' },
      { text: 'Kotlin', value: 'kotlin' },
      { text: 'Objective-C', value: 'objectivec' },
      { text: 'Scss', value: 'scss' },
      { text: 'Shell', value: 'shell' },
      { text: 'SQL', value: 'sql' },
      { text: 'Swift', value: 'swift' },
      { text: 'Yaml', value: 'yaml' },
      { text: 'Typescript', value: 'typescript' },
    ],
    codesample_global_prismjs: true, // 使用全局加载
    // autosave_ask_before_unload: true,
    // autosave_interval: '30s',
    // autosave_prefix: '{path}{query}-{id}-',
    // autosave_restore_when_empty: false,
    // autosave_retention: '2m',
    image_advtab: true,
    image_caption: true,
    image_uploadtab: props.imageEnableUpload,
    images_upload_handler: imageUploadHandler,
    // 文件选择器类型
    file_picker_types: filePickerTypes.value, // 指定所需的文件选择器类型
    file_picker_callback: (callback, _value, meta) => {
      const mimeList = [
        'text/plain',
        'application/x-gzip',
        'application/zip',
        'application/x-rar-compressed',
        'application/x-7z-compressed',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/vnd.ms-excel',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-powerpoint',
        'application/vnd.openxmlformats-officedocument.presentationml.presentation',
        'application/pdf',
      ];
      if (props.selectFile) {
        filePickerCallback.value = callback;
        imageUpload.value = false;
        fileUpload.value = false;
        if (meta.filetype === 'image') {
          title.value = '选择图片';
          imageUpload.value = true;
          query.value.maxSize = props.imageMaxSize * 1024 * 1024;
          query.value.contentTypes = ['image/*'];
        } else if (meta.filetype === 'media') {
          title.value = '选择媒体文件';
          fileAccept.value = ['video/*', 'audio/*'];
          fileUpload.value = true;
          query.value.maxSize = props.mediaMaxSize * 1024 * 1024;
          query.value.contentTypes = fileAccept.value;
        } else if (meta.filetype === 'file') {
          title.value = '选择文件';
          fileAccept.value = mimeList;
          fileUpload.value = true;
          query.value.maxSize = props.fileMaxSize * 1024 * 1024;
          query.value.contentTypes = mimeList;
        }
        open.value = true;
      } else {
        const input = document.createElement('input'); // 创建一个隐藏的input
        input.setAttribute('type', 'file');
        switch (meta.filetype) {
          case 'image':
            input.setAttribute('accept', 'image/*');
            break;
          case 'media':
            input.setAttribute('accept', 'video/*,audio/*');
            break;
          case 'file': {
            input.setAttribute('accept', mimeList.join(','));
            break;
          }
          default:
            return;
        }
        input.addEventListener('change', () => {
          const file = input.files[0];

          const reader = new FileReader();
          reader.addEventListener('load', async () => {
            /*
          注意：现在我们需要在 TinyMCE 图像 blob 中注册 blob注册表。
          在下一个版本中，这部分希望不会必要的，因为我们希望在内部处理它。
        */
            // const id = `blobid${new Date().getTime()}`;
            // const blobCache = tinymce.activeEditor.editorUpload.blobCache;
            // const base64 = (reader.result as string).split(',')[1];
            // const blobInfo = blobCache.create(id, file, base64);
            // blobCache.add(blobInfo);

            /* 调用回调并使用文件名填充标题字段 */
            uploadHandle(file, meta.filetype).then((url) => {
              callback(url, { title: file.name });
            });
            input.remove();
          });
          reader.readAsDataURL(file);
        });
        // 触发点击
        input.click();
      }
    },
    resize: props.resize, // 编辑器调整大小
    branding: false, // 是否显示 “Powered by TinyMCE”
    paste_webkit_styles: 'none', // 指定在 WebKit 中粘贴时要保留的样式
    importcss_append: true,
    height: props.height,
    width: props.width,
    quickbars_selection_toolbar: `bold italic | quicklink h2 h3 blockquote quickimage quicktable`,
    noneditable_class: 'mceNonEditable',
    toolbar_mode: 'sliding',
    contextmenu: `link lists table image`,
    skin_url: useDarkMode.value ? `${baseURL}/skins/ui/oxide-dark` : `${baseURL}/skins/ui/oxide`,
    content_css: useDarkMode.value ? contentDarkURL : contentURL,
    // content_style: `body { background-color: ${useDarkMode.value ? '#242424' : '#fff'};`,
    emoticons_database_url: emojisURL,
    // emoticons_images_url: 'http://my.server/images/emoticons/',
    // 此选项使您能够控制 TinyMCE 是否智能并将 URL 恢复为其原始值。默认情况下，URL 会自动转换（混乱），因为浏览器的内置逻辑就是这样工作的。除非您将其存储起来，否则无法获取真实的 URL。如果将此选项设置为 false，它会尝试保持这些 URL 不变。此选项默认设置为 true，这意味着 URL 被强制为绝对或相对，具体取决于 relative_urls 的状态。
    convert_urls: false, // 这个参数加上去就可以了
  };
  return config;
});

watch(conf, () => {
  editorRef.value.rerender(conf.value);
});
onMounted(() => {
  tinymce.init({});
});

// 暴露组件实例
const exposed = {
  editor: () => editorRef.value,
};
export type EditorInstance = typeof exposed;
defineExpose<EditorInstance>(exposed);
</script>
