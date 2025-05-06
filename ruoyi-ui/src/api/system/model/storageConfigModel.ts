import type { BaseEntity } from '@/api/model/resultModel';

/**
 * 存储配置查询对象
 */
export interface SysStorageConfigQuery extends BaseEntity {
  /** 平台 */
  platform?: string;
  /** 启用状态 */
  status?: number;
}
/**
 * 存储配置业务对象
 */
export interface SysStorageConfigForm {
  /** 主建 */
  storageConfigId?: number;
  /** 平台 */
  platform?: string;
  /** 负载均衡权重 */
  weight?: number;
  /** 启用状态 */
  status?: number;
  /** 配置json */
  configJson?: string;
  /** 配置json对象 */
  configObject?: Record<string, any>;
  /** 备注 */
  remark?: string;
}
/**
 * 存储配置视图对象
 */
export interface SysStorageConfigVo {
  /** 主建 */
  storageConfigId?: number;
  /** 平台 */
  platform?: string;
  /** 负载均衡权重 */
  weight?: number;
  /** 启用状态 */
  status?: number;
  /** 配置json */
  configJson?: string;
  /** 配置json对象 */
  configObject?: Record<string, any>;
  /** 创建时间 */
  createTime?: any;
  /** 更新时间 */
  updateTime?: any;
  /** 备注 */
  remark?: string;
}
