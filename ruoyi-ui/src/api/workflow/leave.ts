import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { LeaveForm, LeaveQuery, LeaveVo } from '@/api/workflow/model/leaveModel';
import { request } from '@/utils/request';

/**
 * 查询请假列表
 * @param query
 */
export function listLeave(query?: LeaveQuery) {
  return request.get<TableDataInfo<LeaveVo>>({
    url: '/workflow/leave/list',
    params: query,
  });
}

/**
 * 查询请假详细
 * @param id
 */
export function getLeave(id: string | number) {
  return request.get<R<LeaveVo>>({
    url: `/workflow/leave/${id}`,
  });
}

/**
 * 新增请假
 * @param data
 */
export function addLeave(data: LeaveForm) {
  return request.post<R<LeaveVo>>({
    url: '/workflow/leave',
    data,
  });
}

/**
 * 修改请假
 * @param data
 */
export function updateLeave(data: LeaveForm) {
  return request.put<R<LeaveVo>>({
    url: '/workflow/leave',
    data,
  });
}

/**
 * 删除请假
 * @param id
 */
export function delLeave(id: string | number | Array<string | number>) {
  return request.delete({
    url: `/workflow/leave/${id}`,
  });
}
