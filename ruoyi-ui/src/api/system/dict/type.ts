import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysDictTypeForm, SysDictTypeQuery, SysDictTypeVo } from '@/api/system/model/dictModel';
import { request } from '@/utils/request';

// 查询字典类型列表
export function listType(query: SysDictTypeQuery) {
  return request.get<TableDataInfo<SysDictTypeVo>>({
    url: '/system/dict/type/list',
    params: query,
  });
}

// 查询字典类型详细
export function getType(dictId: number) {
  return request.get<R<SysDictTypeVo>>({
    url: `/system/dict/type/${dictId}`,
  });
}

// 新增字典类型
export function addType(data: SysDictTypeForm) {
  return request.post<R>({
    url: '/system/dict/type',
    data,
  });
}

// 修改字典类型
export function updateType(data: SysDictTypeForm) {
  return request.put<R>({
    url: '/system/dict/type',
    data,
  });
}

// 删除字典类型
export function delType(dictId: number | number[]) {
  return request.delete<R>({
    url: `/system/dict/type/${dictId}`,
  });
}

// 刷新字典缓存
export function refreshCache() {
  return request.delete<R>({
    url: '/system/dict/type/refreshCache',
  });
}

// 获取字典选择框列表
export function optionselect() {
  return request.get<R<Array<SysDictTypeVo>>>({
    url: '/system/dict/type/optionselect',
  });
}
