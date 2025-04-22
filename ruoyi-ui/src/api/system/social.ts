import type { R } from '@/api/model/resultModel';
import type { SysSocialVo } from '@/api/system/model/socialModel';
import { request } from '@/utils/request';

// 绑定账号
export function authBinding(source: string) {
  return request.get<R<string>>({
    url: `/auth/binding/${source}`,
  });
}

// 解绑账号
export function authUnlock(socialId: number) {
  return request.delete<R>({
    url: `/auth/unlock/${socialId}`,
  });
}

// 获取授权列表
export function getAuthList() {
  return request.get<R<SysSocialVo[]>>({
    url: '/system/social/list',
  });
}
