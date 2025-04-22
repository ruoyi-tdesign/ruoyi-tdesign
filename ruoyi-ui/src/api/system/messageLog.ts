import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysMessageLog, SysMessageLogQuery, SysMessageLogVo } from '@/api/system/model/messageLogModel';
import { request } from '@/utils/request';

// 查询消息发送记录列表
export function listMessageLog(query?: SysMessageLogQuery) {
  return request.get<TableDataInfo<SysMessageLogVo>>({
    url: '/system/messageLog/list',
    params: query,
  });
}

// 查询消息发送记录详细
export function getMessageLog(messageLogId: number) {
  return request.get<R<SysMessageLogVo>>({
    url: `/system/messageLog/${messageLogId}`,
  });
}

// 新增消息发送记录
export function addMessageLog(data: SysMessageLog) {
  return request.post<R>({
    url: '/system/messageLog',
    data,
  });
}

// 修改消息发送记录
export function updateMessageLog(data: SysMessageLog) {
  return request.put<R>({
    url: '/system/messageLog',
    data,
  });
}

// 删除消息发送记录
export function delMessageLog(messageLogIds: number | Array<number>) {
  return request.delete<R>({
    url: `/system/messageLog/${messageLogIds}`,
  });
}

// 清空消息发送记录
export function clearMessageLog() {
  return request.delete<R>({
    url: `/system/messageLog/clear`,
  });
}
