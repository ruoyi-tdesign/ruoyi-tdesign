import { R, TableDataInfo } from '@/api/model/resultModel';
import { SysOssQuery, SysOssUploadVo, SysOssVo } from '@/api/system/model/ossModel';
import { ContentTypeEnum } from '@/constants';
import { request } from '@/utils/request';

// 查询OSS对象存储列表
export function listOss(query: SysOssQuery) {
  return request.get<TableDataInfo<SysOssVo>>({
    url: '/resource/oss/list',
    params: query,
  });
}

// 查询我的OSS对象存储列表
export function listMyOss(query: SysOssQuery) {
  return request.get<TableDataInfo<SysOssVo>>({
    url: '/resource/oss/my/list',
    params: query,
  });
}

// 查询OSS对象基于id串
export function listByIds(ossId: string) {
  return request.get<R<Array<SysOssVo>>>({
    url: `/resource/oss/listByIds/${ossId}`,
  });
}

// 查询OSS对象基于url串
export function listByUrls(urls: string) {
  return request.get<R<Array<SysOssVo>>>({
    url: `/resource/oss/listByUrls`,
    params: { urls: encodeURIComponent(urls) },
  });
}

// 删除OSS对象存储
export function delOss(ossId: number | number[]) {
  return request.delete<R<void>>({
    url: `/resource/oss/${ossId}`,
  });
}

export function uploader(formData: FormData) {
  return request.post<R<SysOssUploadVo>>({
    url: '/resource/oss/upload',
    headers: {
      'Content-Type': ContentTypeEnum.FormData,
    },
    data: formData,
  });
}
