import type { R, TableDataInfo } from '@/api/model/resultModel';
import type {
  SysTenantPackageForm,
  SysTenantPackageQuery,
  SysTenantPackageVo,
} from '@/api/system/model/tenantPackageModel';
import { request } from '@/utils/request';

// 查询租户套餐列表
export function listTenantPackage(query?: SysTenantPackageQuery) {
  return request.get<TableDataInfo<SysTenantPackageVo>>({
    url: '/system/tenant/package/list',
    params: query,
  });
}

// 查询租户套餐下拉选列表
export function selectTenantPackage() {
  return request.get<R<Array<SysTenantPackageVo>>>({
    url: '/system/tenant/package/selectList',
  });
}

// 查询租户套餐详细
export function getTenantPackage(packageId: number) {
  return request.get<R<SysTenantPackageVo>>({
    url: `/system/tenant/package/${packageId}`,
  });
}

// 新增租户套餐
export function addTenantPackage(data: SysTenantPackageForm) {
  return request.post<R>({
    url: '/system/tenant/package',
    data,
  });
}

// 修改租户套餐
export function updateTenantPackage(data: SysTenantPackageForm) {
  return request.put<R>({
    url: '/system/tenant/package',
    data,
  });
}

// 租户套餐状态修改
export function changePackageStatus(packageId: number, status: string) {
  const data = {
    packageId,
    status,
  };
  return request.put<R>({
    url: '/system/tenant/package/changeStatus',
    data,
  });
}

// 删除租户套餐
export function delTenantPackage(packageId: string | number | string[] | number[]) {
  return request.delete<R>({
    url: `/system/tenant/package/${packageId}`,
  });
}
