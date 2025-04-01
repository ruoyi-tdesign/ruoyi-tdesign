import type { BaseEntity } from '@/api/model/resultModel';

/**
 * 请假申请查询对象
 */
export interface LeaveQuery extends BaseEntity {
  /** 开始时间 */
  startLeaveDays?: number;
  /** 结束时间 */
  endLeaveDays?: number;
}
/**
 * 请假申请业务对象
 */
export interface LeaveForm {
  /** 主键 */
  id?: number;
  /** 请假类型 */
  leaveType?: string;
  /** 开始时间 */
  startDate?: any;
  /** 结束时间 */
  endDate?: any;
  /** 请假天数 */
  leaveDays?: number;
  /** 请假原因 */
  remark?: string;
  /** 状态 */
  status?: string;
}
/**
 * 请假申请视图对象
 */
export interface LeaveVo {
  /** 主键 */
  id?: number;
  /** 请假类型 */
  leaveType?: string;
  /** 开始时间 */
  startDate?: any;
  /** 结束时间 */
  endDate?: any;
  /** 请假天数 */
  leaveDays?: number;
  /** 请假原因 */
  remark?: string;
  /** 状态 */
  status?: string;
  /** 创建时间 */
  createTime?: any;
  /** 更新时间 */
  updateTime?: any;
}
