import type { BaseEntity } from '@/api/model/resultModel';

/**
 * 消息模板查询对象
 */
export interface SysMessageTemplateQuery extends BaseEntity {
  /** 模板名称 */
  templateName?: string;
  /** 消息key */
  messageKey?: string;
  /** 消息类型 */
  messageType?: string;
  /** 模板类型 */
  templateMode?: string;
  /** 状态（1正常 0停用） */
  status?: number;
}
/**
 * 消息模板业务对象
 */
export interface SysMessageTemplateForm {
  /** 消息模板id */
  messageTemplateId?: number;
  /** 模板名称 */
  templateName?: string;
  /** 消息配置id */
  messageConfigId?: number;
  /** 消息key主键 */
  messageKeyId?: number;
  /** 消息类型 */
  messageType?: string;
  /** 模板类型 */
  templateMode?: string;
  /** 标题 */
  title?: string;
  /** 签名 */
  signature?: string;
  /** 模板id */
  templateId?: string;
  /** 内容 */
  content?: string;
  /** 输入变量 */
  varsJson?: string;
  varsList?: SysMessageTemplateVar[];
  /** 状态（1正常 0停用） */
  status?: number;
  /** 备注 */
  remark?: string;
}
/**
 * 消息模板视图对象
 */
export interface SysMessageTemplateVo {
  /** 消息模板id */
  messageTemplateId?: number;
  /** 模板名称 */
  templateName?: string;
  /** 消息配置id */
  messageConfigId?: number;
  /** 消息key主键 */
  messageKeyId?: number;
  /** 消息key */
  messageKey?: string;
  /** 消息类型 */
  messageType?: string;
  /** 模板类型 */
  templateMode?: string;
  /** 标题 */
  title?: string;
  /** 签名 */
  signature?: string;
  /** 模板id */
  templateId?: string;
  /** 内容 */
  content?: string;
  /** 输入变量 */
  varsJson?: string;
  /** 状态（1正常 0停用） */
  status?: number;
  /** 备注 */
  remark?: string;
  /** 更新时间 */
  updateTime?: any;
  /** 创建时间 */
  createTime?: any;
  /** 支持平台标识 */
  supplierType?: string;
}

/** 模板测试 */
export interface SysMessageTemplateTest {
  /** 消息模板id */
  messageTemplateId?: number;
  /** 账号 */
  account?: string;
  /** 变量 */
  vars?: object;
}

export interface SysMessageTemplateVar {
  key: string;
  value?: string;
}
