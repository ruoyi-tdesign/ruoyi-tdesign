import type { CaptchaImage, LoginData, LoginTenantVo, LoginVo, RegisterBody, UserInfo } from '@/api/model/loginModel';
import type { R } from '@/api/model/resultModel';
import { request } from '@/utils/request';

// pc端固定客户端授权id
const clientId = import.meta.env.VITE_CLIENT_ID;

/**
 * 是否登录状态
 */
export function isLogin() {
  return request.get<R<boolean>>({
    url: '/auth/isLogin',
  });
}

// 登录方法
export function login(loginData: LoginData) {
  const data = {
    ...loginData,
    clientId: loginData.clientId || clientId,
    grantType: loginData.grantType || 'password',
  };
  return request.post<R<LoginVo>>(
    {
      url: '/auth/login',
      data,
    },
    {
      withToken: false,
      withEncrypt: true,
    },
  );
}

// 注册方法
export function register(data: RegisterBody) {
  const params = {
    ...data,
    clientId,
    grantType: 'password',
  };
  return request.post<R>(
    {
      url: '/auth/register',
      data: params,
    },
    {
      withToken: false,
      withEncrypt: true,
    },
  );
}

/**
 * 第三方登录
 */
export function callback(data: LoginData) {
  const LoginData = {
    ...data,
    clientId,
    grantType: 'social',
  };
  return request.post<R>({
    url: '/auth/social/callback',
    data: LoginData,
  });
}

// 获取用户详细信息
export function getInfo() {
  return request.get<R<UserInfo>>({
    url: '/system/user/getInfo',
  });
}

// 退出方法
export function logout() {
  return request.post<R>({
    url: '/auth/logout',
  });
}

// 获取验证码
export function getCodeImg() {
  return request.get<R<CaptchaImage>>(
    {
      url: '/auth/code',
      timeout: 20000,
    },
    {
      withToken: false,
    },
  );
}

// 短信验证码
export function getCodeSms() {
  return request.get<R>(
    {
      url: '/resource/sms/code',
      timeout: 20000,
    },
    {
      withToken: false,
    },
  );
}

// 获取租户列表
export function getTenantList() {
  return request.get<R<LoginTenantVo>>(
    {
      url: '/auth/tenant/list',
    },
    {
      withToken: false,
    },
  );
}
