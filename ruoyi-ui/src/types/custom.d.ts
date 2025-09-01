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

declare module '@vue/runtime-core' {
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
    /**
     * i18n $t方法支持ts类型提示
     * @param key i18n key
     */
    $t(key: ObjKeysToUnion<any>): string;
  }
}
/**
 * { a: 1, b: { ba: { baa: 1, bab: 2 }, bb: 2} } ---> a | b.ba.baa | b.ba.bab | b.bb
 * https://juejin.cn/post/7280062870670606397
 */
export type ObjKeysToUnion<T, P extends string = ''> = T extends object
  ? {
      [K in keyof T]: ObjKeysToUnion<T[K], P extends '' ? `${K & string}` : `${P}.${K & string}`>;
    }[keyof T]
  : P;

export {};
