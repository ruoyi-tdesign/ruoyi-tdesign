import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { TestLeaveForm, TestLeaveQuery, TestLeaveVo } from '@/api/workflow/model/leaveModel';
import { request } from '@/utils/request';

/**
 * 查询请假列表
 * @param query
 */
export function listLeave(query?: TestLeaveQuery) {
  return request.get<TableDataInfo<TestLeaveVo>>({
    url: '/workflow/leave/list',
    params: query,
  });
}

/**
 * 查询请假详细
 * @param id
 */
export function getLeave(id: string | number) {
  return request.get<R<TestLeaveVo>>({
    url: `/workflow/leave/${id}`,
  });
}

/**
 * 新增请假
 * @param data
 */
export function addLeave(data: TestLeaveForm) {
  return request.post<R<TestLeaveVo>>({
    url: '/workflow/leave',
    data,
  });
}

/**
 * 修改请假
 * @param data
 */
export function updateLeave(data: TestLeaveForm) {
  return request.put<R<TestLeaveVo>>({
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
