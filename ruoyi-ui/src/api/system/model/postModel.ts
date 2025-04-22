import type { BaseEntity } from '@/api/model/resultModel';

/**
 * 岗位信息查询对象
 */
export interface SysPostQuery extends BaseEntity {
  /** 部门id */
  deptId?: number;
  /** 岗位编码 */
  postCode?: string;
  /** 岗位类别编码 */
  postCategory?: string;
  /** 岗位名称 */
  postName?: string;
  /** 状态（1正常 0停用） */
  status?: string;
}
/**
 * 岗位信息业务对象
 */
export interface SysPostForm {
  /** 岗位ID */
  postId?: number;
  /** 部门id */
  deptId?: number;
  /** 岗位编码 */
  postCode?: string;
  /** 岗位类别编码 */
  postCategory?: string;
  /** 岗位名称 */
  postName?: string;
  /** 显示顺序 */
  postSort?: number;
  /** 状态（1正常 0停用） */
  status?: string;
  /** 备注 */
  remark?: string;
}
/**
 * 岗位信息视图对象
 */
export interface SysPostVo {
  /** 岗位ID */
  postId?: number;
  /** 部门id */
  deptId?: number;
  /** 部门名称 */
  deptName?: string;
  /** 岗位编码 */
  postCode?: string;
  /** 岗位类别编码 */
  postCategory?: string;
  /** 岗位名称 */
  postName?: string;
  /** 显示顺序 */
  postSort?: number;
  /** 状态（1正常 0停用） */
  status?: string;
  /** 创建时间 */
  createTime?: any;
  /** 更新时间 */
  updateTime?: any;
  /** 备注 */
  remark?: string;
}
