import type { R, TableDataInfo } from '@/api/model/resultModel';
import type {
  SysSensitiveWordForm,
  SysSensitiveWordQuery,
  SysSensitiveWordTest,
  SysSensitiveWordTestVo,
  SysSensitiveWordVo,
} from '@/api/system/model/sensitiveWordModel';
import { request } from '@/utils/request';

// 查询敏感词列表
export function listSensitiveWord(query?: SysSensitiveWordQuery) {
  return request.get<TableDataInfo<SysSensitiveWordVo>>({
    url: '/system/sensitiveWord/list',
    params: query,
  });
}

// 查询敏感词详细
export function getSensitiveWord(wordId: number) {
  return request.get<R<SysSensitiveWordVo>>({
    url: `/system/sensitiveWord/${wordId}`,
  });
}

// 新增敏感词
export function addSensitiveWord(data: SysSensitiveWordForm) {
  return request.post<R>({
    url: '/system/sensitiveWord',
    data,
  });
}

// 修改敏感词
export function updateSensitiveWord(data: SysSensitiveWordForm) {
  return request.put<R>({
    url: '/system/sensitiveWord',
    data,
  });
}

// 删除敏感词
export function delSensitiveWord(wordIds: number | Array<number>) {
  return request.delete<R>({
    url: `/system/sensitiveWord/${wordIds}`,
  });
}

// 刷新缓存
export function refreshSensitiveWordCache() {
  return request.delete<R>({
    url: '/system/sensitiveWord/refreshCache',
  });
}

/**
 * 测试敏感词
 */
export function sensitiveWordTest(params: SysSensitiveWordTest) {
  return request.get<R<SysSensitiveWordTestVo>>({
    url: '/system/sensitiveWord/test',
    params,
  });
}
