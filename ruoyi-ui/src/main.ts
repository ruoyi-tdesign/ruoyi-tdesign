/* eslint-disable simple-import-sort/imports */
// import TDesign from 'tdesign-vue-next';
import 'virtual:uno.css';
import 'tdesign-vue-next/es/style/index.css';
import '@/style/index.less';
import './permission';

import { createApp } from 'vue';

import { getConfigKey, updateConfigByKey } from '@/api/system/config';
// 字典标签组件
import DictTag from '@/components/dict-tag/index.vue';
// 文件上传组件
import FileUpload from '@/components/file-upload/index.vue';
// 图片预览组件
import ImagePreview from '@/components/image-preview/index.vue';
// 图片上传组件
import ImageUpload from '@/components/image-upload/index.vue';
import { useDict } from '@/utils/dict';
import { download } from '@/utils/request';
import {
  addDateRange,
  bytesToSize,
  handleTree,
  parseTime,
  resetForm,
  selectDictLabel,
  selectDictLabels,
} from '@/utils/ruoyi';

import App from './App.vue';
import directive from './directive'; // directive
// 注册指令
import plugins from './plugins'; // plugins
import router from './router';
import { store } from './store';
import i18n from './locales';

const app = createApp(App);

// 全局方法挂载
app.config.globalProperties.useDict = useDict;
app.config.globalProperties.getConfigKey = getConfigKey;
app.config.globalProperties.updateConfigByKey = updateConfigByKey;
app.config.globalProperties.download = download;
app.config.globalProperties.parseTime = parseTime;
app.config.globalProperties.bytesToSize = bytesToSize;
app.config.globalProperties.resetForm = resetForm;
app.config.globalProperties.handleTree = handleTree;
app.config.globalProperties.addDateRange = addDateRange;
app.config.globalProperties.selectDictLabel = selectDictLabel;
app.config.globalProperties.selectDictLabels = selectDictLabels;

// 全局组件挂载
app.component('DictTag', DictTag);
app.component('FileUpload', FileUpload);
app.component('ImageUpload', ImageUpload);
app.component('ImagePreview', ImagePreview);

app.use(i18n);
app.use(store);
app.use(router);
app.use(plugins);
// app.use(TDesign);

directive(app);

app.mount('#app');
