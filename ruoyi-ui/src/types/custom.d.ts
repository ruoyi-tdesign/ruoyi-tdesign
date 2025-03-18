import type { DialogPlugin } from 'tdesign-vue-next';

import type { getConfigKey, updateConfigByKey } from '@/api/system/config';
import type auth from '@/plugins/auth';
import type cache from '@/plugins/cache';
import type $download from '@/plugins/download';
import type modal from '@/plugins/modal';
import type { useDict } from '@/utils/dict';
import type { download } from '@/utils/request';
import type {
  addDateRange,
  bytesToSize,
  handleTree,
  parseTime,
  resetForm,
  selectDictLabel,
  selectDictLabels,
} from '@/utils/ruoyi';

declare module 'vue' {
  interface ComponentCustomProperties {
    useDict: typeof useDict;
    getConfigKey: typeof getConfigKey;
    updateConfigByKey: typeof updateConfigByKey;
    download: typeof download;
    parseTime: typeof parseTime;
    resetForm: typeof resetForm;
    bytesToSize: typeof bytesToSize;
    handleTree: typeof handleTree;
    addDateRange: typeof addDateRange;
    selectDictLabel: typeof selectDictLabel;
    selectDictLabels: typeof selectDictLabels;
    $auth: typeof auth;
    $cache: typeof cache;
    $modal: typeof modal;
    $download: typeof $download;
    $dialog: typeof DialogPlugin;
  }
}

export {};
