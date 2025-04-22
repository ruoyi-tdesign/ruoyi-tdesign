import type { R, TreeModel } from '@/api/model/resultModel';
import type {
  MenuTreeSelectVo,
  RoleMenuTreeselect,
  SysMenuForm,
  SysMenuQuery,
  SysMenuVo,
} from '@/api/system/model/menuModel';
import { request } from '@/utils/request';

// 查询菜单列表
export function listMenu(query?: SysMenuQuery) {
  return request.get<R<Array<SysMenuVo>>>({
    url: '/system/menu/list',
    params: query,
  });
}

// 查询菜单详细
export function getMenu(menuId: number) {
  return request.get<R<SysMenuVo>>({
    url: `/system/menu/${menuId}`,
  });
}

// 查询菜单下拉树结构
export function menuTreeSelect() {
  return request.get<R<Array<TreeModel<number>>>>({
    url: '/system/menu/treeSelect',
  });
}

// 根据角色ID查询菜单下拉树结构
export function roleMenuTreeSelect(roleId: number) {
  return request.get<R<RoleMenuTreeselect>>({
    url: `/system/menu/roleMenuTreeSelect/${roleId}`,
  });
}

// 根据角色ID查询菜单下拉树结构
export function tenantPackageMenuTreeSelect(packageId: number) {
  return request.get<R<MenuTreeSelectVo>>({
    url: `/system/menu/tenantPackageMenuTreeSelect/${packageId}`,
  });
}

// 新增菜单
export function addMenu(data: SysMenuForm) {
  return request.post<R>({
    url: '/system/menu',
    data,
  });
}

// 修改菜单
export function updateMenu(data: SysMenuForm) {
  return request.put<R>({
    url: '/system/menu',
    data,
  });
}

// 删除菜单
export function delMenu(menuId: number) {
  return request.delete<R>({
    url: `/system/menu/${menuId}`,
  });
}
