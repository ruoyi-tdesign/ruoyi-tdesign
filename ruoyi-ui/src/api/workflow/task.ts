import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { UserDTO } from '@/api/system/model/userModel';
import type { FlowNode } from '@/api/workflow/model/definitionModel';
import type {
  BackProcessBo,
  CompleteTaskBo,
  FlowHisTaskVo,
  FlowNextNodeBo,
  FlowTaskVo,
  FlowTerminationBo,
  StartProcessBo,
  StartProcessReturnDTO,
  TaskOperationBo,
  TaskQuery,
} from '@/api/workflow/model/taskModel';
import { request } from '@/utils/request';

/**
 * 查询待办列表
 * @param query
 */
export function pageByTaskWait(query: TaskQuery) {
  return request.get<TableDataInfo<FlowTaskVo>>({
    url: '/workflow/task/pageByTaskWait',
    params: query,
  });
}

/**
 * 查询已办列表
 * @param query
 */
export function pageByTaskFinish(query: TaskQuery) {
  return request.get<TableDataInfo<FlowHisTaskVo>>({
    url: '/workflow/task/pageByTaskFinish',
    params: query,
  });
}

/**
 * 查询当前用户的抄送列表
 * @param query
 */
export function pageByTaskCopy(query: TaskQuery) {
  return request.get<TableDataInfo<FlowTaskVo>>({
    url: '/workflow/task/pageByTaskCopy',
    params: query,
  });
}

/**
 * 当前租户所有待办任务
 * @param query
 */
export function pageByAllTaskWait(query: TaskQuery) {
  return request.get<TableDataInfo<FlowTaskVo>>({
    url: '/workflow/task/pageByAllTaskWait',
    params: query,
  });
}

/**
 * 当前租户所有已办任务
 * @param query
 */
export function pageByAllTaskFinish(query: TaskQuery) {
  return request.get<TableDataInfo<FlowHisTaskVo>>({
    url: '/workflow/task/pageByAllTaskFinish',
    params: query,
  });
}

/**
 * 启动流程
 * @param data
 */
export function startWorkFlow(data: StartProcessBo) {
  return request.post<R<StartProcessReturnDTO>>({
    url: '/workflow/task/startWorkFlow',
    data,
  });
}

/**
 * 办理流程
 * @param data
 */
export function completeTask(data: CompleteTaskBo) {
  return request.post<R>({
    url: '/workflow/task/completeTask',
    data,
  });
}

/**
 * 任务驳回
 * @param data
 */
export function backProcess(data: BackProcessBo) {
  return request.post<R>({
    url: '/workflow/task/backProcess',
    data,
  });
}

/**
 * 获取当前任务
 * @param taskId
 * @returns
 */
export function getTask(taskId: string | number) {
  return request.get<R<FlowTaskVo>>({
    url: `/workflow/task/getTask/${taskId}`,
  });
}

/**
 * 修改任务办理人
 * @param taskIdList 任务id
 * @param userId     办理人id
 * @returns
 */
export function updateAssignee(taskIdList: Array<string>, userId: string | number) {
  return request.put<R>({
    url: `/workflow/task/updateAssignee/${userId}`,
    data: taskIdList,
  });
}

/**
 * 终止任务
 * @returns
 */
export function terminationTask(data: FlowTerminationBo) {
  return request.post<R<boolean>>({
    url: `/workflow/task/terminationTask`,
    data,
  });
}

/**
 * 获取可驳回得任务节点
 * @param definitionId 流程定义id
 * @param nodeCode     当前节点
 */
export function getBackTaskNode(definitionId: string | number, nodeCode: string) {
  return request.get<R<FlowNode[]>>({
    url: `/workflow/task/getBackTaskNode/${definitionId}/${nodeCode}`,
  });
}

/**
 * 任务操作 操作类型，委派 delegateTask、转办 transferTask、加签 addSignature、减签 reductionSignature
 * @returns
 */
export function taskOperation(
  data: TaskOperationBo,
  operation: 'delegateTask' | 'transferTask' | 'addSignature' | 'reductionSignature',
) {
  return request.post<R>({
    url: `/workflow/task/taskOperation/${operation}`,
    data,
  });
}

/**
 * 获取当前任务办理人
 * @param taskId 任务id
 * @returns
 */
export function currentTaskAllUser(taskId: string | number) {
  return request.get<R<UserDTO[]>>({
    url: `/workflow/task/currentTaskAllUser/${taskId}`,
  });
}

/**
 * 获取下一节点
 * @param data 参数
 * @returns
 */
export function getNextNodeList(data: FlowNextNodeBo): any {
  return request.post<R<FlowNode[]>>({
    url: '/workflow/task/getNextNodeList',
    data,
  });
}
