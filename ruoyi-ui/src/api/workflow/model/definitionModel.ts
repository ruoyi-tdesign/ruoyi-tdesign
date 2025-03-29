import type { BasePageQuery } from '@/api/model/resultModel';

export interface FlowDefinitionQuery extends BasePageQuery {
  flowCode?: string;
  flowName?: string;
  category: string | number;
  isPublish?: number;
}

export interface FlowDefinitionVo {
  id?: string;
  flowName?: string;
  flowCode?: string;
  formPath?: string;
  version?: string;
  isPublish?: number;
  activityStatus?: number;
  createTime?: Date;
  updateTime?: Date;
}

export interface FlowDefinitionForm {
  id?: string;
  flowName?: string;
  flowCode?: string;
  category?: string;
  formPath?: string;
}

export interface definitionXmlVO {
  xml: string[];
  xmlStr: string;
}
export interface Definition {
  /** 主键 */
  id?: number;
  /** 创建时间 */
  createTime?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 租户ID */
  tenantId?: string;
  /** 删除标记 */
  delFlag?: string;
  /** 流程编码 */
  flowCode?: string;
  /** 流程名称 */
  flowName?: string;
  /** 流程类别 */
  category?: string;
  /** 流程版本 */
  version?: string;
  /** 是否发布（0未开启 1开启） */
  isPublish?: number;
  /** 审批表单是否自定义（Y是 2否） */
  formCustom?: string;
  /** 审批表单是否自定义（Y是 2否） */
  formPath?: string;
  /** 流程激活状态（0挂起 1激活） */
  activityStatus?: number;
  /** 监听器类型 */
  listenerType?: string;
  /** 监听器路径 */
  listenerPath?: string;
  /** 扩展字段，预留给业务系统使用 */
  ext?: string;
  /** 审批表单是否自定义（Y是 2否） */
  xmlString?: string;
  nodeList?: Node[];
  userList?: User[];
}
export interface User {
  /** 主键 */
  id?: number;
  /** 创建时间 */
  createTime?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 租户ID */
  tenantId?: string;
  /** 删除标记 */
  delFlag?: string;
  /** 人员类型（1待办任务的审批人权限 2待办任务的转办人权限 3待办任务的委托人权限） */
  type?: string;
  /** 权限人 */
  processedBy?: string;
  /** 任务表id */
  associated?: number;
  /** 创建人：比如作为委托的人保存 */
  createBy?: string;
}
export interface Node {
  /** 主键 */
  id?: number;
  /** 创建时间 */
  createTime?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 租户ID */
  tenantId?: string;
  /** 删除标记 */
  delFlag?: string;
  /** 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
  nodeType?: number;
  /** 流程id */
  definitionId?: number;
  /** 流程节点编码   每个流程的nodeCode是唯一的,即definitionId+nodeCode唯一,在数据库层面做了控制 */
  nodeCode?: string;
  /** 流程节点名称 */
  nodeName?: string;
  /** 权限标识（权限类型:权限标识，可以多个，用逗号隔开) */
  permissionFlag?: string;
  /** 流程签署比例值 */
  nodeRatio?: number;
  /** 流程节点坐标 */
  coordinate?: string;
  /** 版本 */
  version?: string;
  /** 是否可以退回任意节点（Y是 N否）即将删除 */
  skipAnyNode?: string;
  /** 任意结点跳转 */
  anyNodeSkip?: string;
  /** 监听器类型 */
  listenerType?: string;
  /** 监听器路径 */
  listenerPath?: string;
  /** 处理器类型 */
  handlerType?: string;
  /** 处理器路径 */
  handlerPath?: string;
  /** 审批表单是否自定义（Y是 2否） */
  formCustom?: string;
  /** 审批表单是否自定义（Y是 2否） */
  formPath?: string;
  /** 跳转条件 */
  skipList?: Skip[];
}
export interface Skip {
  /** 主键 */
  id?: number;
  /** 创建时间 */
  createTime?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 租户ID */
  tenantId?: string;
  /** 删除标记 */
  delFlag?: string;
  /** 流程编码 */
  definitionId?: number;
  /** 流程名称 */
  nodeId?: number;
  /** 流程类别 */
  nowNodeCode?: string;
  /** 流程版本 */
  nowNodeType?: number;
  /** 是否发布（0未开启 1开启） */
  nextNodeCode?: string;
  /** 审批表单是否自定义（Y是 2否） */
  nextNodeType?: number;
  /** 审批表单是否自定义（Y是 2否） */
  skipName?: string;
  /** 流程激活状态（0挂起 1激活） */
  skipType?: string;
  /** 监听器类型 */
  skipCondition?: string;
  /** 监听器路径 */
  coordinate?: string;
}
