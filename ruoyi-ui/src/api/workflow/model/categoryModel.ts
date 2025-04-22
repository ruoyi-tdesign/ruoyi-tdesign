import type { BaseEntity } from '@/api/model/resultModel';

/**
 * 流程分类查询对象
 */
export interface CategoryQuery extends BaseEntity {
  /** 流程分类名称 */
  categoryName?: string;
}
/**
 * 流程分类业务对象
 */
export interface CategoryForm {
  /** 流程分类ID */
  categoryId?: string | number;
  /** 流程分类名称 */
  categoryName?: string;
  /** 父流程分类id */
  parentId?: string | number;
  /** 显示顺序 */
  orderNum?: number;
}
/**
 * 流程分类视图对象
 */
export interface CategoryVO {
  /** 流程分类ID */
  categoryId?: number;
  /** 父级id */
  parentId?: number;
  /** 流程分类名称 */
  categoryName?: string;
  /** 显示顺序 */
  orderNum?: number;
  /** 创建时间 */
  createTime?: any;
  /** 更新时间 */
  updateTime?: any;
  /** 子流程分类 */
  children?: CategoryVO[];
}
