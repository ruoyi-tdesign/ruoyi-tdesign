import type { BaseEntity } from '@/api/model/resultModel';

/**
 * 对象存储配置查询对象
 */
export interface SysOssConfigQuery extends BaseEntity {
  /** 配置key */
  configKey?: string;
  /** 桶名称 */
  bucketName?: string;
  /** 是否默认（1=是,0=否） */
  status?: string;
}
/**
 * 对象存储配置业务对象
 */
export interface SysOssConfigForm {
  /** 主建 */
  ossConfigId?: number;
  /** 配置key */
  configKey?: string;
  /** accessKey */
  accessKey?: string;
  /** 秘钥 */
  secretKey?: string;
  /** 桶名称 */
  bucketName?: string;
  /** 前缀 */
  prefix?: string;
  /** 访问站点 */
  endpoint?: string;
  /** 自定义域名 */
  domain?: string;
  /** 是否https（Y=是,N=否） */
  isHttps?: string;
  /** 域 */
  region?: string;
  /** 桶权限类型(0=private 1=public 2=custom) */
  accessPolicy?: string;
  /** 是否默认（1=是,0=否） */
  status?: string;
  /** 扩展字段 */
  ext1?: string;
  /** 备注 */
  remark?: string;
}
/**
 * 对象存储配置视图对象
 */
export interface SysOssConfigVo {
  /** 主建 */
  ossConfigId?: number;
  /** 配置key */
  configKey?: string;
  /** accessKey */
  accessKey?: string;
  /** 秘钥 */
  secretKey?: string;
  /** 桶名称 */
  bucketName?: string;
  /** 前缀 */
  prefix?: string;
  /** 访问站点 */
  endpoint?: string;
  /** 自定义域名 */
  domain?: string;
  /** 是否https（Y=是,N=否） */
  isHttps?: string;
  /** 域 */
  region?: string;
  /** 桶权限类型(0=private 1=public 2=custom) */
  accessPolicy?: string;
  /** 是否默认（1=是,0=否） */
  status?: string;
  /** 扩展字段 */
  ext1?: string;
  /** 创建时间 */
  createTime?: any;
  /** 更新时间 */
  updateTime?: any;
  /** 备注 */
  remark?: string;
}
