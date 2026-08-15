import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SpelForm, SpelQuery, SpelVo } from '@/api/workflow/model/spelModel';
import { request } from '@/utils/request';

/**
 * 查询流程spel表达式定义列表
 * @param query
 */
export function listSpel(query?: SpelQuery) {
  return request.get<TableDataInfo<SpelVo>>({
    url: '/workflow/spel/list',
    params: query,
  });
}

/**
 * 查询流程spel表达式定义详细
 * @param id
 */
export function getSpel(id: string | number) {
  return request.get<R<SpelVo>>({
    url: `/workflow/spel/${id}`,
  });
}

/**
 * 新增流程spel表达式定义
 * @param data
 */
export function addSpel(data: SpelForm) {
  return request.post<R>({
    url: '/workflow/spel',
    data,
  });
}

/**
 * 修改流程spel表达式定义
 * @param data
 */
export function updateSpel(data: SpelForm) {
  return request.put<R>({
    url: '/workflow/spel',
    data,
  });
}

/**
 * 删除流程spel表达式定义
 * @param id
 */
export function delSpel(id: string | number | Array<string | number>) {
  return request.delete<R>({
    url: `/workflow/spel/${id}`,
  });
}
