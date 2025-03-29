import type { BasePageQuery } from '@/api/model/resultModel';
import type { FlowTaskVo } from '@/api/workflow/model/taskModel';

export interface FlowInstanceQuery extends BasePageQuery {
  category?: string | number;
  nodeName?: string;
  flowCode?: string;
  flowName?: string;
  createByIds?: Array<string | number>;
  businessId?: string;
}
/**
 * 撤销任务请求对象
 */
export interface FlowCancelBo {
  /** 任务ID */
  businessId?: string;
  /** 办理意见 */
  message?: string;
}
/**
 * 流程实例视图
 *
 * @author may
 */
export interface FlowInstanceVo {
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
  /** 流程定义编码 */
  flowCode?: string;
  /** 业务id */
  businessId?: string;
  /** 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
  nodeType?: number;
  /** 流程节点编码   每个流程的nodeCode是唯一的,即definitionId+nodeCode唯一,在数据库层面做了控制 */
  nodeCode?: string;
  /** 流程节点名称 */
  nodeName?: string;
  /** 流程变量 */
  variable?: string;
  /** 流程状态（0待提交 1审批中 2 审批通过 3自动通过 8已完成 9已退回 10失效） */
  flowStatus?: string;
  /** 流程状态 */
  flowStatusName?: string;
  /** 流程激活状态（0挂起 1激活） */
  activityStatus?: number;
  /** 审批表单是否自定义（Y是 N否） */
  formCustom?: string;
  /** 审批表单路径 */
  formPath?: string;
  /** 扩展字段，预留给业务系统使用 */
  ext?: string;
  /** 流程定义版本 */
  version?: string;
  /** 创建者 */
  createBy?: string;
  /** 申请人 */
  createByName?: string;
  /** 流程分类id */
  category?: string;
  /** 流程分类名称 */
  categoryName?: string;
  flowTaskList: FlowTaskVo[];
}
