import type { BaseEntity } from '@/api/model/resultModel';

/**
 * 存储配置查询对象
 */
export interface SysStorageConfigQuery extends BaseEntity {
  /** 配置名称 */
  name?: string;
  /** 平台 */
  platform?: string;
  /** 启用状态 */
  status?: number;
  /** 请求模式 proxy：代理转发请求 direct：源地址重定向请求 direct_signature：预签名重定向请求 */
  requestMode?: string;
}
/**
 * 存储配置业务对象
 */
export interface SysStorageConfigForm {
  /** 主建 */
  storageConfigId?: number;
  /** 配置名称 */
  name?: string;
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
  /** 请求模式 proxy：代理转发请求 direct：源地址重定向请求 direct_signature：预签名重定向请求 */
  requestMode?: string;
  /** 备注 */
  remark?: string;
}
/**
 * 存储配置视图对象
 */
export interface SysStorageConfigVo {
  /** 主建 */
  storageConfigId?: number;
  /** 配置名称 */
  name?: string;
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
  /** 请求模式 proxy：代理转发请求 direct：源地址重定向请求 direct_signature：预签名重定向请求 */
  requestMode?: string;
  /** 创建时间 */
  createTime?: any;
  /** 更新时间 */
  updateTime?: any;
  /** 备注 */
  remark?: string;
}
