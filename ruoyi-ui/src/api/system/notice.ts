import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysNoticeForm, SysNoticeQuery, SysNoticeVo } from '@/api/system/model/noticeModel';
import { request } from '@/utils/request';

// 查询公告列表
export function listNotice(query: SysNoticeQuery) {
  return request.get<TableDataInfo<SysNoticeVo>>({
    url: '/system/notice/list',
    params: query,
  });
}

// 查询公告详细
export function getNotice(noticeId: number) {
  return request.get<R<SysNoticeVo>>({
    url: `/system/notice/${noticeId}`,
  });
}

// 新增公告
export function addNotice(data: SysNoticeForm) {
  return request.post<R>({
    url: '/system/notice',
    data,
  });
}

// 修改公告
export function updateNotice(data: SysNoticeForm) {
  return request.put<R>({
    url: '/system/notice',
    data,
  });
}

// 删除公告
export function delNotice(noticeId: number | number[]) {
  return request.delete<R>({
    url: `/system/notice/${noticeId}`,
  });
}
