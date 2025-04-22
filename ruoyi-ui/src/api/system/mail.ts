import type { R } from '@/api/model/resultModel';
import { request } from '@/utils/request';

// 发送测试邮件
export function sendTestMail(toMail: string) {
  return request.post<R>({
    url: '/system/config/mail/sendTestMail',
    params: { toMail },
  });
}
