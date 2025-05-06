import type { FieldConfig, FieldOption } from '@/api/model/fieldConfigModel';
import { evalFieldConfigs } from '@/api/model/fieldConfigModel';
import type { R, TableDataInfo } from '@/api/model/resultModel';
import type {
  SysStorageConfigForm,
  SysStorageConfigQuery,
  SysStorageConfigVo,
} from '@/api/system/model/storageConfigModel';
import { request } from '@/utils/request';

/**
 * 查询存储配置列表
 * @param query 查询参数
 */
export function listStorageConfig(query?: SysStorageConfigQuery) {
  return request.get<TableDataInfo<SysStorageConfigVo>>({
    url: '/system/storageConfig/list',
    params: query,
  });
}

/**
 * 查询存储配置详细
 * @param storageConfigId 主键
 */
export function getStorageConfig(storageConfigId: number) {
  return request
    .get<R<SysStorageConfigVo>>({
      url: `/system/storageConfig/${storageConfigId}`,
    })
    .then((res) => {
      res.data.configObject = JSON.parse(res.data.configJson);
      return res;
    });
}

/**
 * 新增存储配置
 * @param data 表单数据
 */
export function addStorageConfig(data: SysStorageConfigForm) {
  return request.post<R>({
    url: '/system/storageConfig',
    data,
  });
}

/**
 * 修改存储配置
 * @param data 表单数据
 */
export function updateStorageConfig(data: SysStorageConfigForm) {
  return request.put<R>({
    url: '/system/storageConfig',
    data,
  });
}

/**
 * 删除存储配置
 * @param storageConfigIds 主键串
 */
export function delStorageConfig(storageConfigIds: number | Array<number>) {
  return request.delete<R>({
    url: `/system/storageConfig/${storageConfigIds}`,
  });
}

/**
 * 获取所有存储平台
 */
export function getAllStoragePlatform() {
  return request.get<R<Array<FieldOption>>>({
    url: '/system/storageConfig/getAllStoragePlatform',
  });
}

/**
 * 获取支持的存储平台
 */
export function getSupportPlatform() {
  return request.get<R<Array<FieldOption>>>({
    url: '/system/storageConfig/getSupportPlatform',
  });
}

/**
 * 获取存储平台配置
 */
export function getStoragePlatformConfigs() {
  return request
    .get<R<Record<string, Record<string, FieldConfig<any>>>>>({
      url: '/system/storageConfig/getStoragePlatformConfigs',
    })
    .then((res) => {
      Object.values(res.data).forEach((value) => {
        evalFieldConfigs(value);
      });
      return res;
    });
}
