import type { R, TableDataInfo } from '@/api/model/resultModel';
import type { SysLogininforQuery, SysLogininforVo } from '@/api/monitor/model/logininforModel';
import type { AvatarVo, ProfileVo, SysUserProfileBo } from '@/api/system/model/userModel';
import { request } from '@/utils/request';

// 查询用户个人信息
export function getUserProfile() {
  return request.get<R<ProfileVo>>({
    url: '/system/user/profile',
  });
}

// 修改用户个人信息
export function updateUserProfile(data: SysUserProfileBo) {
  return request.put<R>({
    url: '/system/user/profile/basic',
    data,
  });
}

// 修改用户手机号
export function updateUserPhonenumber(phonenumber: string) {
  return request.put<R>({
    url: '/system/user/profile/updatePhonenumber',
    params: { phonenumber },
  });
}

// 修改用户邮箱
export function updateUserEmail(email: string) {
  return request.put<R>({
    url: '/system/user/profile/updateEmail',
    params: { email },
  });
}

// 用户密码重置
export function updateUserPwd(oldPassword: string, newPassword: string) {
  const data = {
    oldPassword,
    newPassword,
  };
  return request.put<R>(
    {
      url: '/system/user/profile/updatePwd',
      data,
    },
    {
      withEncrypt: true,
      repeatSubmit: false,
    },
  );
}

// 用户头像上传
export function uploadAvatar(data: FormData) {
  return request.post<R<AvatarVo>>({
    url: '/system/user/profile/avatar',
    headers: {
      'content-type': 'multipart/form-data',
    },
    data,
  });
}

// 删除用户头像
export function removeAvatar() {
  return request.delete<void>({
    url: '/system/user/profile/avatar/remove',
  });
}

// 查询用户访问记录列表
export function loginLogList(query: SysLogininforQuery) {
  return request.get<TableDataInfo<SysLogininforVo>>({
    url: '/system/user/profile/loginLog/list',
    params: query,
  });
}
