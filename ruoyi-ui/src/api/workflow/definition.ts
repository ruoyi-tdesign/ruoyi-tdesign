import type { R, TableDataInfo } from '@/api/model/resultModel';
import type {
  Definition,
  FlowDefinitionForm,
  FlowDefinitionQuery,
  FlowDefinitionVo,
} from '@/api/workflow/model/definitionModel';
import { request } from '@/utils/request';

/**
 * 获取流程定义列表
 * @param query 流程实例id
 * @returns
 */
export function listDefinition(query: FlowDefinitionQuery) {
  return request.get<TableDataInfo<FlowDefinitionVo>>({
    url: `/workflow/definition/list`,
    params: query,
  });
}

/**
 * 查询未发布的流程定义列表
 * @param query 流程实例id
 * @returns
 */
export function unPublishList(query: FlowDefinitionQuery) {
  return request.get<TableDataInfo<FlowDefinitionVo>>({
    url: `/workflow/definition/unPublishList`,
    params: query,
  });
}

// /**
//  * 通过流程定义id获取xml
//  * @param definitionId 流程定义id
//  * @returns
//  */
// export function definitionXml(definitionId: string) {
//   return request.get<R<definitionXmlVO>>({
//     url: `/workflow/definition/definitionXml/${definitionId}`,
//   });
// }

/**
 * 删除流程定义
 * @param ids 流程定义id
 * @returns
 */
export function deleteDefinition(ids: string | string[]) {
  return request.delete<R>({
    url: `/workflow/definition/${ids}`,
  });
}

/**
 * 挂起/激活
 * @param definitionId 流程定义id
 * @param activityStatus 状态
 * @returns
 */
export function active(definitionId: string, activityStatus: boolean) {
  return request.put<R<boolean>>({
    url: `/workflow/definition/active/${definitionId}`,
    params: {
      active: activityStatus,
    },
  });
}

/**
 * 通过zip或xml部署流程定义
 * @returns
 */
export function importDef(data: any) {
  return request.post<R<boolean>>({
    url: '/workflow/definition/importDef',
    data,
    headers: {
      repeatSubmit: false,
    },
  });
}

/**
 * 发布流程定义
 * @param id 流程定义id
 * @returns
 */
export function publish(id: string) {
  return request.put<R<boolean>>({
    url: `/workflow/definition/publish/${id}`,
  });
}

/**
 * 取消发布流程定义
 * @param id 流程定义id
 * @returns
 */
export function unPublish(id: string) {
  return request.put<R<boolean>>({
    url: `/workflow/definition/unPublish/${id}`,
  });
}

/**
 * 获取流程定义xml字符串
 * @param id 流程定义id
 * @returns
 */
export function xmlString(id: string) {
  return request.get<R<string>>({
    url: `/workflow/definition/xmlString/${id}`,
  });
}

/**
 * 新增
 * @param data 参数
 * @returns
 */
export function add(data: FlowDefinitionForm) {
  return request.post<R<boolean>>({
    url: `/workflow/definition`,
    data,
  });
}

/**
 * 修改
 * @param data 参数
 * @returns
 */
export function edit(data: FlowDefinitionForm) {
  return request.put<R<boolean>>({
    url: `/workflow/definition`,
    data,
  });
}

/**
 * 查询详情
 * @param id 参数
 * @returns
 */
export function getInfo(id: number | string) {
  return request.get<R<Definition>>({
    url: `/workflow/definition/${id}`,
  });
}

/**
 * 复制流程定义
 * @param id 流程定义id
 * @returns
 */
export function copy(id: string) {
  return request.post<R<boolean>>({
    url: `/workflow/definition/copy/${id}`,
  });
}
