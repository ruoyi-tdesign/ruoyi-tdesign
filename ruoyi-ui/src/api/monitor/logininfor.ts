import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysLogininforQuery, SysLogininforVo } from '@/api/monitor/model/logininforModel';
import { request } from '@/utils/request';

// 查询登录日志列表
export function listLogininfor(query: SysLogininforQuery) {
  return request.get<TableDataInfo<SysLogininforVo>>({
    url: '/monitor/logininfor/list',
    params: query,
  });
}

// 查询系统访问记录详细
export function getLogininfor(infoId: number) {
  return request.get<R<SysLogininforVo>>({
    url: `/monitor/logininfor/${infoId}`,
  });
}

// 删除登录日志
export function delLogininfor(infoId: number | number[]) {
  return request.delete<R>({
    url: `/monitor/logininfor/${infoId}`,
  });
}

// 解锁用户登录状态
export function unlockLogininfor(userName: string) {
  return request.get<R>({
    url: `/monitor/logininfor/unlock/${userName}`,
  });
}

// 清空登录日志
export function cleanLogininfor() {
  return request.delete<R>({
    url: '/monitor/logininfor/clean',
  });
}
