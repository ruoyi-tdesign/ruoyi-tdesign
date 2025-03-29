import type { BasePageQuery } from '@/api/model/resultModel';
import type { User } from '@/api/workflow/model/definitionModel';

export interface TaskQuery extends BasePageQuery {
  nodeName?: string;
  flowCode?: string;
  flowName?: string;
  createByIds?: Array<string | number>;
}

export interface ParticipantVo {
  groupIds?: Array<string | number>;
  candidate: Array<string | number>;
  candidateName: string[];
  claim: boolean;
}
/**
 * 任务视图
 *
 * @author may
 */
export interface FlowTaskVo {
  id?: number;
  /** 创建时间 */
  createTime?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 租户ID */
  tenantId?: string;
  /** 删除标记 */
  delFlag?: string;
  /** 对应flow_definition表的id */
  definitionId?: number;
  /** 流程实例表id */
  instanceId?: number;
  /** 流程定义名称 */
  flowName?: string;
  /** 业务id */
  businessId?: string;
  /** 节点编码 */
  nodeCode?: string;
  /** 节点名称 */
  nodeName?: string;
  /** 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
  nodeType?: number;
  /** 权限标识 permissionFlag的list形式 */
  permissionList?: string[];
  /** 流程用户列表 */
  userList?: User[];
  /** 审批表单是否自定义（Y是 N否） */
  formCustom?: string;
  /** 审批表单 */
  formPath?: string;
  /** 流程定义编码 */
  flowCode?: string;
  /** 流程版本号 */
  version?: string;
  /** 流程状态 */
  flowStatus?: string;
  /** 流程分类id */
  category?: string;
  /** 流程分类名称 */
  categoryName?: string;
  /** 流程状态 */
  flowStatusName?: string;
  /** 办理人类型 */
  type?: string;
  /** 办理人ids */
  assigneeIds?: string;
  /** 办理人名称 */
  assigneeNames?: string;
  /** 抄送人id */
  processedBy?: string;
  /** 抄送人名称 */
  processedByName?: string;
  /** 流程签署比例值 大于0为票签，会签 */
  nodeRatio?: number;
  /** 申请人id */
  createBy?: string;
  /** 申请人名称 */
  createByName?: string;
}
/**
 * 历史任务视图
 */
export interface FlowHisTaskVo {
  id?: number;
  /** 创建时间 */
  createTime?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 租户ID */
  tenantId?: string;
  /** 删除标记 */
  delFlag?: string;
  /** 对应flow_definition表的id */
  definitionId?: number;
  /** 流程定义名称 */
  flowName?: string;
  /** 流程实例表id */
  instanceId?: number;
  /** 任务表id */
  taskId?: number;
  /** 协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签) */
  cooperateType?: number;
  /** 协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签) */
  cooperateTypeName?: string;
  /** 业务id */
  businessId?: string;
  /** 开始节点编码 */
  nodeCode?: string;
  /** 开始节点名称 */
  nodeName?: string;
  /** 开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
  nodeType?: number;
  /** 目标节点编码 */
  targetNodeCode?: string;
  /** 结束节点名称 */
  targetNodeName?: string;
  /** 审批者 */
  approver?: string;
  /** 审批者 */
  approveName?: string;
  /** 协作人(只有转办、会签、票签、委派) */
  collaborator?: string;
  /** 权限标识 permissionFlag的list形式 */
  permissionList?: string[];
  /** 跳转类型（PASS通过 REJECT退回 NONE无动作） */
  skipType?: string;
  /** 流程状态 */
  flowStatus?: string;
  /** 任务状态 */
  flowTaskStatus?: string;
  /** 流程状态 */
  flowStatusName?: string;
  /** 审批意见 */
  message?: string;
  /** 业务详情 存业务类的json */
  ext?: string;
  /** 创建者 */
  createBy?: string;
  /** 申请人 */
  createByName?: string;
  /** 流程分类id */
  category?: string;
  /** 流程分类名称 */
  categoryName?: string;
  /** 审批表单是否自定义（Y是 N否） */
  formCustom?: string;
  /** 审批表单路径 */
  formPath?: string;
  /** 流程定义编码 */
  flowCode?: string;
  /** 流程版本号 */
  version?: string;
  /** 运行时长 */
  runDuration?: string;
}

export interface VariableVo {
  key: string;
  value: string;
}

export interface TaskOperationBo {
  /** 委派/转办人的用户ID（必填，准对委派/转办人操作） */
  userId?: string | number;
  /** 加签/减签人的用户ID列表（必填，针对加签/减签操作） */
  userIds?: Array<string | number>;
  /** 任务ID（必填） */
  taskId: string | number;
  /** 意见或备注信息（可选） */
  message?: string;
}

/**
 * 终止任务请求对象
 */
export interface FlowTerminationBo {
  /** 任务id */
  taskId?: number | string;
  /** 审批意见 */
  comment?: string;
}
/**
 * 启动流程对象
 */
export interface StartProcessBo {
  /** 业务唯一值id */
  businessId?: string | number;
  /** 流程定义编码 */
  flowCode?: string;
  /** 流程变量，前端会提交一个元素{'entity': {业务详情数据对象}} */
  variables?: Record<string, any>;
}
/**
 * 驳回参数请求
 */
export interface BackProcessBo {
  /** 任务ID */
  taskId?: number;
  /** 消息类型 */
  messageType?: string[];
  /** 驳回的节点id(目前未使用，直接驳回到申请人) */
  nodeCode?: string;
  /** 办理意见 */
  message?: string;
  /** 通知 */
  notice?: string;
  /** 流程变量 */
  variables?: Record<string, any>;
}
/**
 * 办理任务请求对象
 */
export interface CompleteTaskBo {
  /** 任务id */
  taskId?: number;
  /** 附件id */
  fileId?: string;
  /** 抄送人员 */
  flowCopyList?: FlowCopyBo;
  /** 消息类型 */
  messageType?: string[];
  /** 办理意见 */
  message?: string;
  /** 消息通知 */
  notice?: string;
  /** 流程变量 */
  variables?: Record<string, any>;
  /** 扩展变量(此处为逗号分隔的ossId) */
  ext?: string;
}
/**
 * 抄送
 */
export interface FlowCopyBo {
  /** 用户id */
  userId?: number;
  /** 用户名称 */
  userName?: string;
}
