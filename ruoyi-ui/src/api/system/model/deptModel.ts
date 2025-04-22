import type { BaseEntity } from '@/api/model/resultModel';

/**
 * 部门查询对象
 */
export interface SysDeptQuery extends BaseEntity {
  /** 部门id */
  deptId?: number;
  /** 父部门id */
  parentId?: number;
  /** 部门名称 */
  deptName?: string;
  /** 部门类别编码 */
  deptCategory?: string;
  /** 部门状态（1正常 0停用） */
  status?: string;
}

/**
 * 部门业务对象
 */
export interface SysDeptForm {
  /** 部门id */
  deptId?: number;
  /** 父部门id */
  parentId?: number;
  /** 部门名称 */
  deptName?: string;
  /** 部门类别编码 */
  deptCategory?: string;
  /** 显示顺序 */
  orderNum?: number;
  /** 负责人 */
  leader?: number;
  /** 联系电话 */
  phone?: string;
  /** 邮箱 */
  email?: string;
  /** 部门状态（1正常 0停用） */
  status?: string;
}
/**
 * 部门视图对象
 */
export interface SysDeptVo {
  /** 部门id */
  deptId?: number;
  /** 父部门id */
  parentId?: number;
  /** 祖级列表 */
  ancestors?: string;
  /** 部门名称 */
  deptName?: string;
  /** 部门类别编码 */
  deptCategory?: string;
  /** 显示顺序 */
  orderNum?: number;
  /** 负责人 */
  leader?: number;
  /** 负责人名称 */
  leaderName?: string;
  /** 联系电话 */
  phone?: string;
  /** 邮箱 */
  email?: string;
  /** 部门状态（1正常 0停用） */
  status?: string;
  /** 创建时间 */
  createTime?: any;
  /** 更新时间 */
  updateTime?: any;
  /** 子部门 */
  children?: SysDeptVo[];
}
