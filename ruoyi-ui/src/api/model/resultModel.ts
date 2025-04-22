/**
 * 返回分页结果
 */
export interface TableDataInfo<T> {
  /** 总记录数 */
  total: number;
  /** 列表数据 */
  rows: Array<T>;
  /** 消息状态码 */
  code: number;
  /** 消息内容 */
  msg: string;
}
/**
 * 返回结果
 */
export interface R<T = void> {
  // 消息码
  code: number;
  // 消息
  msg: string;
  // 数据
  data: T;
}
/**
 * 基本类型
 */
export interface BaseEntity {
  /** 分页页码 */
  pageNum?: number;
  /** 分页数 */
  pageSize?: number;
  /** 搜索值 */
  searchValue?: string;
  /** 排序字段 */
  orderByColumn?: string;
  /** 排序字段顺序 */
  isAsc?: string;
  /** 请求参数 */
  params?: Record<string, any | undefined>;
}
/**
 * 租户基类
 */
export interface TenantEntity extends BaseEntity {
  /** 租户编号 */
  tenantId?: string;
}
export interface TreeModel<ID> {
  id: ID;
  parentId?: ID;
  label: string;
  weight?: any;
  children?: Array<TreeModel<ID>>;
}
export interface BasePageQuery extends BaseEntity {
  /** 页码 */
  pageNum: number;
  /** 每页大小 */
  pageSize: number;
}
