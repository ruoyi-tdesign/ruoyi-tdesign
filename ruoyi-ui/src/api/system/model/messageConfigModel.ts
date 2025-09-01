import type { FormRule } from 'tdesign-vue-next';

import type { BaseEntity } from '@/api/model/resultModel';

/**
 * 消息配置查询对象
 */
export interface SysMessageConfigQuery extends BaseEntity {
  /** 标题 */
  title?: string;
  /** 消息类型 */
  messageType?: string;
  /** 支持平台标识 */
  supplierType?: string;
  /** 状态（1正常 0停用） */
  status?: number;
}
/**
 * 消息配置业务对象
 */
export interface SysMessageConfigForm {
  /** 消息设置id */
  messageConfigId?: number;
  /** 标题 */
  title?: string;
  /** 消息类型 */
  messageType?: string;
  /** 支持平台标识 */
  supplierType?: string;
  /** 配置json */
  configJson?: string | object;
  /** 状态（1正常 0停用） */
  status?: number;
  /** 备注 */
  remark?: string;
}
/**
 * 消息配置视图对象
 */
export interface SysMessageConfigVo {
  /** 消息设置id */
  messageConfigId?: number;
  /** 标题 */
  title?: string;
  /** 消息类型 */
  messageType?: string;
  /** 支持平台标识 */
  supplierType?: string;
  /** 配置json */
  configJson?: string | object;
  /** 状态（1正常 0停用） */
  status?: number;
  /** 备注 */
  remark?: string;
  /** 更新时间 */
  updateTime?: any;
  /** 创建时间 */
  createTime?: any;
}

export interface TemplateMode {
  /** 支持模板id */
  supportTemplateId: boolean;
  /** 支持模板内容 */
  supportTemplateContent: boolean;
}

/** 字段基本配置对象 */
export interface FieldConfig<T extends string | number | boolean | Array<string | number> = string> {
  /** 字段默认值 */
  value?: T;
  /** 字段名称 */
  name: string;
  /** 组件 */
  component: string;
  /** 帮助信息 */
  help?: string;
  /** 是否必填 */
  required: boolean;
  /** 选项 */
  options?: [];
  /** 占用栅格数 */
  span?: number;
  /** 最小值 */
  min?: number;
  /** 最大值 */
  max?: number;
  /** 可见性依赖字段。例如a=true，则b设置visible为a */
  visible?: string;
  /** 其他校验规则 */
  rules?: FormRule[];
  /** 组件类型 */
  type?: 'number' | 'submit' | 'url' | 'text' | 'search' | 'password' | 'hidden' | 'tel';
}
export type SupplierFieldConfig = Record<string, FieldConfig<any>>;

export interface MessageFieldConfig {
  /** 消息配置 */
  supplierConfig: SupplierFieldConfig;

  /** 消息类型 */
  messageType: 'MAIL' | 'SMS';

  /** 消息模板支持类型 */
  templateMode: TemplateMode;
}

/**
 * 消息类型
 */
export interface MessageTypeVo {
  messageType: string;
  description: string;
  supplierTypeMap: Record<string, string>;
}
