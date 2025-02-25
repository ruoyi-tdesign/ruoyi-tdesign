import type { BasePageQuery } from '@/api/model/resultModel';
import type { DefinitionConfigVO } from '@/api/workflow/definitionConfig/types';
import type { NodeConfigVO } from '@/api/workflow/nodeConfig/types';
import type { TaskVo } from '@/api/workflow/task/types';

export interface ProcessInstanceQuery extends BasePageQuery {
  categoryCode?: string;
  name?: string;
  key?: string;
  startUserId?: string;
  businessKey?: string;
}

/**
 * 流程实例视图
 */
export interface ProcessInstanceVo {
  /** 流程实例id */
  id?: string;
  /** 流程定义id */
  processDefinitionId?: string;
  /** 流程定义名称 */
  processDefinitionName?: string;
  /** 流程定义key */
  processDefinitionKey?: string;
  /** 流程定义版本 */
  processDefinitionVersion?: number;
  /** 部署id */
  deploymentId?: string;
  /** 业务id */
  businessKey?: string;
  /** 是否挂起 */
  isSuspended?: boolean;
  /** 租户id */
  tenantId?: string;
  /** 启动时间 */
  startTime?: string;
  /** 结束时间 */
  endTime?: string;
  /** 启动人id */
  startUserId?: string;
  /** 流程状态 */
  businessStatus?: string;
  /** 流程状态 */
  businessStatusName?: string;
  /** 待办任务集合 */
  taskVoList?: TaskVo[];
  /** 节点配置 */
  wfNodeConfigVo?: NodeConfigVO;
  /** 多余，不报错 */
  wfDefinitionConfigVo?: DefinitionConfigVO;
}
