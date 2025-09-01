import type { R, TableDataInfo } from '@/api/model/resultModel';
import type {
  MessageFieldConfig,
  MessageTypeVo,
  SysMessageConfigForm,
  SysMessageConfigQuery,
  SysMessageConfigVo,
} from '@/api/system/model/messageConfigModel';
import { request } from '@/utils/request';

// 查询消息配置列表
export function listMessageConfig(query?: SysMessageConfigQuery) {
  return request.get<TableDataInfo<SysMessageConfigVo>>({
    url: '/system/messageConfig/list',
    params: query,
  });
}

// 查询消息配置详细
export function getMessageConfig(messageConfigId: number) {
  return request.get<R<SysMessageConfigVo>>({
    url: `/system/messageConfig/${messageConfigId}`,
  });
}

// 新增消息配置
export function addMessageConfig(data: SysMessageConfigForm) {
  return request.post<R>({
    url: '/system/messageConfig',
    data,
  });
}

// 修改消息配置
export function updateMessageConfig(data: SysMessageConfigForm) {
  return request.put<R>({
    url: '/system/messageConfig',
    data,
  });
}

// 删除消息配置
export function delMessageConfig(messageConfigIds: number | Array<number>) {
  return request.delete<R>({
    url: `/system/messageConfig/${messageConfigIds}`,
  });
}

// 刷新消息配置缓存
export function refreshCache() {
  return request.delete<R>({
    url: '/system/messageConfig/refreshCache',
  });
}

/**
 * 获取消息字段配置
 */
export function getMessageFieldConfigs() {
  return request
    .get<R<Record<string, MessageFieldConfig>>>({
      url: '/system/messageConfig/getMessageFieldConfigs',
    })
    .then((res) => {
      Object.entries(res.data).forEach(([, value]) => {
        Object.entries(value.supplierConfig).forEach(([, v]) => {
          v.rules?.forEach((rule) => {
            rule.pattern = new RegExp(rule.pattern);
          });
        });
      });
      return res;
    });
}

/**
 * 获取消息字段配置
 */
export function getMessageSupplierType() {
  return request.get<R<MessageTypeVo[]>>({
    url: '/system/messageConfig/getSupplierType',
  });
}
