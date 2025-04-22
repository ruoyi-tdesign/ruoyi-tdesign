import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysOperLogQuery, SysOperLogVo } from '@/api/monitor/model/operlogModel';
import { request } from '@/utils/request';

// 查询操作日志列表
export function list(query?: SysOperLogQuery) {
  return request.get<TableDataInfo<SysOperLogVo>>({
    url: '/monitor/operlog/list',
    params: query,
  });
}

// 删除操作日志
export function delOperlog(operId: number | number[]) {
  return request.delete<R>({
    url: `/monitor/operlog/${operId}`,
  });
}

// 清空操作日志
export function cleanOperlog() {
  return request.delete<R>({
    url: '/monitor/operlog/clean',
  });
}
