import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysTenantForm, SysTenantQuery, SysTenantVo } from '@/api/system/model/tenantModel';
import { request } from '@/utils/request';

// 查询租户列表
export function listTenant(query: SysTenantQuery) {
  return request.get<TableDataInfo<SysTenantVo>>({
    url: '/system/tenant/list',
    params: query,
  });
}

// 查询租户详细
export function getTenant(id: number) {
  return request.get<R<SysTenantVo>>({
    url: `/system/tenant/${id}`,
  });
}

// 新增租户
export function addTenant(data: SysTenantForm) {
  return request.post<R>(
    {
      url: '/system/tenant',
      data,
    },
    {
      withEncrypt: true,
      repeatSubmit: false,
    },
  );
}

// 修改租户
export function updateTenant(data: SysTenantForm) {
  return request.put<R>({
    url: '/system/tenant',
    data,
  });
}

// 租户状态修改
export function changeTenantStatus(id: number, tenantId: string, status: string) {
  const data = {
    id,
    tenantId,
    status,
  };
  return request.put<R>({
    url: '/system/tenant/changeStatus',
    data,
  });
}

// 删除租户
export function delTenant(id: number | number[]) {
  return request.delete<R>({
    url: `/system/tenant/${id}`,
  });
}

// 动态切换租户
export function dynamicTenant(tenantId: string | number) {
  return request.get<R>({
    url: `/system/tenant/dynamic/${tenantId}`,
  });
}

// 清除动态租户
export function dynamicClear() {
  return request.get<R>({
    url: '/system/tenant/dynamic/clear',
  });
}

// 同步租户套餐
export function syncTenantPackage(tenantId: string, packageId: number) {
  const data = {
    tenantId,
    packageId,
  };
  return request.get<R>({
    url: '/system/tenant/syncTenantPackage',
    params: data,
  });
}
