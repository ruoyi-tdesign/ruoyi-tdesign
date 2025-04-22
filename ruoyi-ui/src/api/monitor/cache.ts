import type { R } from '@/api/model/resultModel';
import type { SysCache, SysCacheInfo } from '@/api/monitor/model/cacheModel';
import { request } from '@/utils/request';

// 查询缓存详细
export function getCache() {
  return request.get<R<SysCacheInfo>>({
    url: '/monitor/cache',
  });
}

// 查询缓存名称列表
export function listCacheName() {
  return request.get<R<Array<SysCache>>>({
    url: '/monitor/cache/getNames',
  });
}

// 查询缓存键名列表
export function listCacheKey(cacheName: string) {
  return request.get<R<Array<String>>>({
    url: `/monitor/cache/getKeys/${cacheName}`,
  });
}

// 查询缓存内容
export function getCacheValue(cacheName: string, cacheKey: string) {
  return request.get<R<SysCache>>({
    url: `/monitor/cache/getValue/${cacheName}/${cacheKey}`,
  });
}

// 清理指定名称缓存
export function clearCacheName(cacheName: string) {
  return request.delete<R>({
    url: `/monitor/cache/clearCacheName/${cacheName}`,
  });
}

// 清理指定键名缓存
export function clearCacheKey(cacheName: string, cacheKey: string) {
  return request.delete<R>({
    url: `/monitor/cache/clearCacheKey/${cacheName}/${cacheKey}`,
  });
}

// 清理全部缓存
export function clearCacheAll() {
  return request.delete<R>({
    url: '/monitor/cache/clearCacheAll',
  });
}
