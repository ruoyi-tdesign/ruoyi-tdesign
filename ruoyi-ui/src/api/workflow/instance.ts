import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { FlowCancelBo, FlowInstanceQuery, FlowInstanceVo } from '@/api/workflow/model/instanceModel';
import { request } from '@/utils/request';

/**
 * 查询运行中实例列表
 * @param query
 */
export function pageByRunning(query: FlowInstanceQuery) {
  return request.get<TableDataInfo<FlowInstanceVo>>({
    url: '/workflow/instance/pageByRunning',
    params: query,
  });
}

/**
 * 查询已完成实例列表
 * @param query
 */
export function pageByFinish(query: FlowInstanceQuery) {
  return request.get<TableDataInfo<FlowInstanceVo>>({
    url: '/workflow/instance/pageByFinish',
    params: query,
  });
}

/**
 * 通过业务id获取历史流程图
 */
export function flowImage(businessId: string | number) {
  return request.get<R<Record<string, any>>>({
    url: `/workflow/instance/flowImage/${businessId}?t${Math.random()}`,
  });
}

/**
 * 分页查询当前登录人单据
 * @param query
 */
export function pageByCurrent(query: FlowInstanceQuery) {
  return request.get<TableDataInfo<FlowInstanceVo>>({
    url: '/workflow/instance/pageByCurrent',
    params: query,
  });
}

/**
 * 撤销流程
 * @param data 参数
 * @returns
 */
export function cancelProcessApply(data: FlowCancelBo) {
  return request.put<R>({
    url: `/workflow/instance/cancelProcessApply`,
    data,
  });
}

/**
 * 获取流程变量
 * @param instanceId 实例id
 * @returns
 */
export function instanceVariable(instanceId: string | number) {
  return request.get<R<Record<string, any>>>({
    url: `/workflow/instance/instanceVariable/${instanceId}`,
  });
}

/**
 * 删除
 * @param instanceIds 流程实例id
 * @returns
 */
export function deleteByInstanceIds(instanceIds: Array<string | number> | string | number) {
  return request.delete<R>({
    url: `/workflow/instance/deleteByInstanceIds/${instanceIds}`,
  });
}
/**
 * 作废流程
 * @param data 参数
 * @returns
 */
export function invalid(data: any) {
  return request.post<R<boolean>>({
    url: `/workflow/instance/invalid`,
    data,
  });
}
