import { ref, toRefs } from 'vue';

import { getDicts } from '@/api/system/dict/data';
import type { SysDictDataVo } from '@/api/system/model/dictModel';
import useDictStore from '@/store/modules/dict';

export interface DictModel {
  label: string;
  value: string | number;
  tagType?: 'default' | 'warning' | 'danger' | 'success' | 'primary' | 'text';
  tagClass?: string;
  tagStyle?: 'light' | 'outline' | 'dark' | 'light-outline';
}
export interface StringDictModel extends DictModel {
  value: string;
}

/**
 * 系统字典对象转为字典组件数据
 * @param data
 */
export function dictConvert(data: Array<SysDictDataVo>): StringDictModel[] {
  return data.map((p) => ({
    label: p.dictLabel,
    value: p.dictValue,
    tagType: p.listClass,
    tagClass: p.cssClass,
    tagStyle: p.tagStyle,
  }));
}

/**
 * 获取字典数据
 */
export function useDict(...args: string[]) {
  const res = ref<Record<string, Array<StringDictModel>>>({});
  return (() => {
    args.forEach((dictType) => {
      res.value[dictType] = [];
      const dicts = useDictStore().getDict(dictType);
      if (dicts) {
        res.value[dictType] = dicts;
      } else {
        getDicts(dictType).then((resp) => {
          res.value[dictType] = dictConvert(resp.data);
          useDictStore().setDict(dictType, res.value[dictType]);
        });
      }
    });
    return toRefs(res.value);
  })();
}
