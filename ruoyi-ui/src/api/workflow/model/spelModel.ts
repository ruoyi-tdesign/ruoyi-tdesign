import type { BaseEntity, BasePageQuery } from '@/api/model/resultModel';

/**
 * 流程spel表达式定义查询对象
 */
export interface SpelQuery extends BasePageQuery {
  /** 组件名称 */
  componentName?: string;
  /** 方法名 */
  methodName?: string;
  /** 参数 */
  methodParams?: string;
  /** 预览spel值 */
  viewSpel?: string;
  /** 状态（0正常 1停用） */
  status?: string;
  /** 日期范围参数 */
  params?: any;
}

/**
 * 流程spel表达式定义业务对象
 */
export interface SpelForm extends BaseEntity {
  /** 主键id */
  id?: string | number;
  /** 组件名称 */
  componentName?: string;
  /** 方法名 */
  methodName?: string;
  /** 参数 */
  methodParams?: string;
  /** 预览spel值 */
  viewSpel?: string;
  /** 状态（0正常 1停用） */
  status?: string;
  /** 备注 */
  remark?: string;
}

/**
 * 流程spel表达式定义视图对象
 */
export interface SpelVo {
  /** 主键id */
  id?: string | number;
  /** 组件名称 */
  componentName?: string;
  /** 方法名 */
  methodName?: string;
  /** 参数 */
  methodParams?: string;
  /** 预览spel值 */
  viewSpel?: string;
  /** 状态（0正常 1停用） */
  status?: string;
  /** 备注 */
  remark?: string;
}
