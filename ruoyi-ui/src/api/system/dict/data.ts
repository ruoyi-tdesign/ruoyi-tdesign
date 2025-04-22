import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysDictDataForm, SysDictDataQuery, SysDictDataVo } from '@/api/system/model/dictModel';
import { request } from '@/utils/request';

// 查询字典数据列表
export function listData(query: SysDictDataQuery) {
  return request.get<TableDataInfo<SysDictDataVo>>({
    url: '/system/dict/data/list',
    params: query,
  });
}

// 查询字典数据详细
export function getData(dictCode: string | number) {
  return request.get<R<SysDictDataVo>>({
    url: `/system/dict/data/${dictCode}`,
  });
}

// 根据字典类型查询字典数据信息
export function getDicts(dictType: string) {
  return request.get<R<Array<SysDictDataVo>>>({
    url: `/system/dict/data/type/${dictType}`,
  });
}

// 新增字典数据
export function addData(data: SysDictDataForm) {
  return request.post<R>({
    url: '/system/dict/data',
    data,
  });
}

// 修改字典数据
export function updateData(data: SysDictDataForm) {
  return request.put<R>({
    url: '/system/dict/data',
    data,
  });
}

// 删除字典数据
export function delData(dictCode: number | number[]) {
  return request.delete<R>({
    url: `/system/dict/data/${dictCode}`,
  });
}
