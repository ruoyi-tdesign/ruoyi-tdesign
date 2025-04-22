import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysMessageKeyForm, SysMessageKeyQuery, SysMessageKeyVo } from '@/api/system/model/messageKeyModel';
import { request } from '@/utils/request';

// 查询消息常量列表
export function listMessageKey(query?: SysMessageKeyQuery) {
  return request.get<TableDataInfo<SysMessageKeyVo>>({
    url: '/system/messageKey/list',
    params: query,
  });
}

// 查询消息常量详细
export function getMessageKey(messageKeyId: number) {
  return request.get<R<SysMessageKeyVo>>({
    url: `/system/messageKey/${messageKeyId}`,
  });
}

// 新增消息常量
export function addMessageKey(data: SysMessageKeyForm) {
  return request.post<R>({
    url: '/system/messageKey',
    data,
  });
}

// 修改消息常量
export function updateMessageKey(data: SysMessageKeyForm) {
  return request.put<R>({
    url: '/system/messageKey',
    data,
  });
}

// 删除消息常量
export function delMessageKey(messageKeyIds: number | Array<number>) {
  return request.delete<R>({
    url: `/system/messageKey/${messageKeyIds}`,
  });
}
