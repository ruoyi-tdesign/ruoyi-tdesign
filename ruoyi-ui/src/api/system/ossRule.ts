import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysOssRuleForm, SysOssRuleQuery, SysOssRuleVo } from '@/api/system/model/ossRuleModel';
import { request } from '@/utils/request';

// 查询OSS处理规则列表
export function listOssRule(query?: SysOssRuleQuery) {
  return request.get<TableDataInfo<SysOssRuleVo>>({
    url: '/system/ossRule/list',
    params: query,
  });
}

// 查询OSS处理规则详细
export function getOssRule(ossRuleId: number) {
  return request.get<R<SysOssRuleVo>>({
    url: `/system/ossRule/${ossRuleId}`,
  });
}

// 新增OSS处理规则
export function addOssRule(data: SysOssRuleForm) {
  return request.post<R>({
    url: '/system/ossRule',
    data,
  });
}

// 修改OSS处理规则
export function updateOssRule(data: SysOssRuleForm) {
  return request.put<R>({
    url: '/system/ossRule',
    data,
  });
}

// 删除OSS处理规则
export function delOssRule(ossRuleIds: number | Array<number>) {
  return request.delete<R>({
    url: `/system/ossRule/${ossRuleIds}`,
  });
}

// 刷新OSS处理规则缓存
export function refreshOssRuleCache() {
  return request.delete<R>({
    url: 'system/ossRule/refreshCache',
  });
}

// OSS处理规则覆盖字段值修改
export function changeOssRuleOverwrite(ossRuleId: number, isOverwrite: string) {
  return request.put<R>({
    url: 'system/ossRule/changeOverwrite',
    params: {
      ossRuleId,
      isOverwrite,
    },
  });
}
