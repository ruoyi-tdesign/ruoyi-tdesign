import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysUserOnline, SysUserOnlineQuery } from '@/api/monitor/model/userOnlineModel';
import { request } from '@/utils/request';

// 查询在线用户列表
export function list(query: SysUserOnlineQuery) {
  return request.get<TableDataInfo<SysUserOnline>>({
    url: '/monitor/online/list',
    params: query,
  });
}

// 强退用户
export function forceLogout(tokenId: string) {
  return request.delete<R>({
    url: `/monitor/online/${tokenId}`,
  });
}

// 获取当前用户登录在线设备
export function getOnline() {
  return request.get<TableDataInfo<SysUserOnline>>({
    url: '/monitor/online',
  });
}

// 删除当前在线设备
export function delOnline(tokenId: string) {
  return request.delete<R>({
    url: `/monitor/online/myself/${tokenId}`,
  });
}
