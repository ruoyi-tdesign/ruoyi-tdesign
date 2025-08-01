import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysFileForm, SysFileQuery, SysFileUploadVo, SysFileVo } from '@/api/system/model/fileModel';
import { ContentTypeEnum } from '@/constants';
import { request } from '@/utils/request';

/**
 * 查询文件存储列表
 * @param query
 */
export function listFile(query: SysFileQuery) {
  return request.get<TableDataInfo<SysFileVo>>({
    url: '/system/file/list',
    params: query,
  });
}

/**
 * 查询我的文件存储列表
 * @param query
 */
export function listMyFile(query: SysFileQuery) {
  return request.get<TableDataInfo<SysFileVo>>({
    url: '/system/file/my/list',
    params: query,
  });
}

/**
 * 查询文件基于id串
 * @param fileIds
 */
export function listByIds(fileIds: string | string[]) {
  return request.get<R<Array<SysFileVo>>>({
    url: `/system/file/listByIds/${fileIds}`,
  });
}

/**
 * 查询文件存储详细
 * @param fileId
 */
export function getFile(fileId: number) {
  return request.get<R<SysFileVo>>({
    url: `/system/file/${fileId}`,
  });
}

/**
 * 修改文件存储
 * @param data
 */
export function updateFile(data: SysFileForm) {
  return request.put<R>({
    url: '/system/file/my',
    data,
  });
}

/**
 * 删除文件存储
 * @param fileIds
 */
export function delFile(fileIds: number | number[]) {
  return request.delete<R>({
    url: `/system/file/${fileIds}`,
  });
}

/**
 * 删除我的文件存储
 * @param fileIds
 */
export function delMyFile(fileIds: number | number[]) {
  return request.delete<R>({
    url: `/system/file/my/${fileIds}`,
  });
}

/**
 * 上传
 * @param formData
 */
export function uploader(formData: FormData) {
  return request.post<R<SysFileUploadVo>>({
    url: '/system/file/upload',
    headers: {
      'Content-Type': ContentTypeEnum.FormData,
    },
    data: formData,
  });
}

/**
 * 移动到分类
 * @param categoryId 分类id
 * @param fileIds 文件id
 */
export function moveFile(categoryId: number, fileIds: number[]) {
  return request.post<R>({
    url: `/system/file/${categoryId}/move`,
    data: fileIds,
  });
}

/**
 * 获取访问路径
 * @param rawUrl 原始路径
 */
export function getVisitUrl(rawUrl: string) {
  if (!rawUrl) {
    return '';
  }
  const baseUrl = import.meta.env.VITE_APP_BASE_API;
  const delimiter = !baseUrl.endsWith('/') && !rawUrl.startsWith('/') ? '/' : '';
  const url = `${baseUrl}${delimiter}${rawUrl}`;
  if (url.startsWith('http')) {
    return url;
  }
  return new URL(url, window.location.origin).toString();
}
