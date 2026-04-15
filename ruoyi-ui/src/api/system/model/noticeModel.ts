import type { BaseEntity } from '@/api/model/resultModel';

/**
 * 通知公告查询对象
 */
export interface SysNoticeQuery extends BaseEntity {
  /** 公告标题 */
  noticeTitle?: string;
  /** 公告类型（1通知 2公告） */
  noticeType?: string;
  /** 公告状态（0正常 1关闭） */
  status?: string;
  /** 创建人名称 */
  createByName?: string;
}

/**
 * 通知公告业务对象
 */
export interface SysNoticeForm {
  /** 公告ID */
  noticeId?: number;
  /** 公告标题 */
  noticeTitle?: string;
  /** 公告类型（1通知 2公告） */
  noticeType?: string;
  /** 公告内容 */
  noticeContent?: string;
  /** 公告状态（0正常 1关闭） */
  status?: string;
  /** 备注 */
  remark?: string;
}
/**
 * 通知公告视图对象
 */
export interface SysNoticeVo {
  /** 公告ID */
  noticeId?: number;
  /** 公告标题 */
  noticeTitle?: string;
  /** 公告类型（1通知 2公告） */
  noticeType?: string;
  /** 公告内容 */
  noticeContent?: string;
  /** 公告状态（0正常 1关闭） */
  status?: string;
  /** 创建者 */
  createBy?: number;
  /** 创建时间 */
  createTime?: any;
  /** 更新者 */
  updateBy?: number;
  /** 更新时间 */
  updateTime?: any;
  /** 备注 */
  remark?: string;
}
