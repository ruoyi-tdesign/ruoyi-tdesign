import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysPostForm, SysPostQuery, SysPostVo } from '@/api/system/model/postModel';
import { request } from '@/utils/request';

// 查询岗位列表
export function listPost(query: SysPostQuery) {
  return request.get<TableDataInfo<SysPostVo>>({
    url: '/system/post/list',
    params: query,
  });
}

// 查询岗位详细
export function getPost(postId: number) {
  return request.get<R<SysPostVo>>({
    url: `/system/post/${postId}`,
  });
}

// 获取岗位选择框列表
export function postOptionSelect(deptId?: number | string, postIds?: (number | string)[]) {
  return request.get<R<SysPostVo[]>>({
    url: '/system/post/optionSelect',
    params: {
      postIds,
      deptId,
    },
  });
}

// 新增岗位
export function addPost(data: SysPostForm) {
  return request.post<R>({
    url: '/system/post',
    data,
  });
}

// 修改岗位
export function updatePost(data: SysPostForm) {
  return request.put<R>({
    url: '/system/post',
    data,
  });
}

// 删除岗位
export function delPost(postId: number | number[]) {
  return request.delete<R>({
    url: `/system/post/${postId}`,
  });
}
