import type { R } from '@/api/model/resultModel';
import type {
  SysFileCategoryForm,
  SysFileCategoryQuery,
  SysFileCategoryVo,
} from '@/api/system/model/fileCategoryModel';
import { request } from '@/utils/request';

/**
 * 查询文件分类列表
 * @param query 查询参数
 */
export function listFileCategory(query?: SysFileCategoryQuery) {
  return request.get<R<Array<SysFileCategoryVo>>>({
    url: '/system/fileCategory/list',
    params: query,
  });
}

/**
 * 查询文件分类详细
 * @param query 查询参数
 */
export function getFileCategory(query: SysFileCategoryQuery & { fileCategoryId: number }) {
  return request.get<R<SysFileCategoryVo>>({
    url: `/system/fileCategory/query`,
    params: query,
  });
}

/**
 * 新增文件分类
 * @param data 表单数据
 */
export function addFileCategory(data: SysFileCategoryForm) {
  return request.post<R>({
    url: '/system/fileCategory',
    data,
  });
}

/**
 * 修改文件分类
 * @param data 表单数据
 */
export function updateFileCategory(data: SysFileCategoryForm) {
  return request.put<R>({
    url: '/system/fileCategory',
    data,
  });
}

/**
 * 删除文件分类
 * @param fileCategoryIds 主键串
 */
export function delFileCategory(fileCategoryIds: number | Array<number>) {
  return request.delete<R>({
    url: `/system/fileCategory/${fileCategoryIds}`,
  });
}
