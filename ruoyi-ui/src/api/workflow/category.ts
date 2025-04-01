import type { R, TreeModel } from '@/api/model/resultModel';
import type { CategoryForm, CategoryQuery, CategoryVO } from '@/api/workflow/model/categoryModel';
import { request } from '@/utils/request';

// 查询流程分类列表
export function listCategory(query?: CategoryQuery) {
  return request.get<R<Array<CategoryVO>>>({
    url: '/workflow/category/list',
    params: query,
  });
}

/**
 * 查询流程分类详细
 * @param categoryId
 */
export function getCategory(categoryId: number) {
  return request.get<R<CategoryVO>>({
    url: `/workflow/category/${categoryId}`,
  });
}

// 新增流程分类
export function addCategory(data: CategoryForm) {
  return request.post<R>({
    url: '/workflow/category',
    data,
  });
}

// 修改流程分类
export function updateCategory(data: CategoryForm) {
  return request.put<R>({
    url: '/workflow/category',
    data,
  });
}

// 删除流程分类
export function delCategory(categoryId: number | Array<number>) {
  return request.delete<R>({
    url: `/workflow/category/${categoryId}`,
  });
}

/**
 * 获取流程分类树列表
 * @param query 流程实例id
 * @returns
 */
export function flowCategoryTree(query?: CategoryForm) {
  return request.get<R<TreeModel<string>[]>>({
    url: `/workflow/category/categoryTree`,
    params: query,
  });
}
