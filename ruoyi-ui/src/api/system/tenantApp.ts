import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysTenantAppForm, SysTenantAppQuery, SysTenantAppVo } from '@/api/system/model/tenantAppModel';
import { request } from '@/utils/request';

// 查询租户应用管理列表
export function listTenantApp(query?: SysTenantAppQuery) {
  return request.get<TableDataInfo<SysTenantAppVo>>({
    url: '/system/tenantApp/list',
    params: query,
  });
}

// 查询租户应用管理详细
export function getTenantApp(appid: number) {
  return request.get<R<SysTenantAppVo>>({
    url: `/system/tenantApp/${appid}`,
  });
}

// 新增租户应用管理
export function addTenantApp(data: SysTenantAppForm) {
  return request.post<R>({
    url: '/system/tenantApp',
    data,
  });
}

// 修改租户应用管理
export function updateTenantApp(data: SysTenantAppForm) {
  return request.put<R>({
    url: '/system/tenantApp',
    data,
  });
}

// 删除租户应用管理
export function delTenantApp(appids: number | Array<number>) {
  return request.delete<R>({
    url: `/system/tenantApp/${appids}`,
  });
}
