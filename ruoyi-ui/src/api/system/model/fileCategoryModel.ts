import type { BaseEntity } from '@/api/model/resultModel';

/**
 * 文件分类查询对象
 */
export interface SysFileCategoryQuery extends BaseEntity {
  /** 分类名称 */
  categoryName?: string;
  /** 父级分类id */
  parentId?: number;
  /** 用户类型 */
  loginType?: string;
  /** 上传人 */
  createBy?: number;
  /** 文件最大字节长度 */
  maxSize?: number;
  /** 内容类型 */
  contentTypes?: string[];
  /** 多个文件后缀，忽略大小写 */
  suffixes?: string[];
  /** oss分类id */
  ossCategoryId?: number;
}
/**
 * 文件分类业务对象
 */
export interface SysFileCategoryForm {
  /** 文件分类id */
  fileCategoryId?: number;
  /** 分类名称 */
  categoryName?: string;
  /** 父级分类id */
  parentId?: number;
  /** 显示顺序 */
  orderNum?: number;
  /** 用户类型 */
  loginType?: string;
  /** 上传人 */
  createBy?: number;
}
/**
 * 文件分类视图对象
 */
export interface SysFileCategoryVo {
  /** 文件分类id */
  fileCategoryId?: number;
  /** 分类名称 */
  categoryName?: string;
  /** 父级分类id */
  parentId?: number;
  /** 父级分类名称 */
  parentCategoryName?: string;
  /** 分类路径 */
  categoryPath?: string;
  /** 层级 */
  level?: number;
  /** 显示顺序 */
  orderNum?: number;
  /** 用户类型 */
  loginType?: string;
  /** 上传人 */
  createBy?: number;
  /** 更新时间 */
  updateTime?: any;
  /** 创建时间 */
  createTime?: any;
  /** 文件数量 */
  fileCount?: number;
  /** 子分类 */
  children?: SysFileCategoryVo[];
}
