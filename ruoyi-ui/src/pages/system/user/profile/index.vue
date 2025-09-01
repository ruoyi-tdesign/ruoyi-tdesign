<template>
  <t-row :gutter="[24, 24]">
    <!--  欢迎信息  -->
    <t-col :flex="3">
      <div class="user-left-greeting">
        <div>
          Hi，{{ userInfo.user?.nickName }}
          <span class="regular">
            你好，今天是你加入 {{ companyName }} 的第
            {{ dateDiffAbs(userInfo.user?.createTime ?? new Date(), new Date(), 'day') }} 天
          </span>
        </div>
        <!--    腾讯logo，自定义logo放开注释    -->
        <!--        <img src="@/assets/assets-tencent-logo.png" class="logo" />-->
      </div>

      <!--   个人信息   -->
      <t-card class="user-info-list" title="个人信息" :bordered="false">
        <template #actions>
          <t-button theme="default" variant="text" @click="openModifyBasicInfo = true"> 修改基础信息 </t-button>
        </template>
        <t-descriptions :column="4" item-layout="vertical">
          <t-descriptions-item label="用户账号">{{ userInfo.user?.userName }}</t-descriptions-item>
          <t-descriptions-item>
            <template #label>
              <a class="user-intro-edit" @click="openModifyBasicInfo = true">用户名称 <edit2-icon /></a>
            </template>
            {{ userInfo.user?.nickName }}
          </t-descriptions-item>
          <t-descriptions-item>
            <template #label>
              <a class="user-intro-edit" @click="openBindingPhone = true">手机 <edit2-icon /></a>
            </template>
            +86 {{ userInfo.user?.phonenumber }}
          </t-descriptions-item>
          <t-descriptions-item>
            <template #label>
              <a class="user-intro-edit" @click="openBindingEmail = true">邮箱 <edit2-icon /></a>
            </template>
            {{ userInfo.user?.email }}
          </t-descriptions-item>
          <t-descriptions-item>
            <template #label>
              <a class="user-intro-edit" @click="openModifyBasicInfo = true">性别 <edit2-icon /></a>
            </template>
            <dict-tag :options="sys_user_sex" :value="userInfo.user?.sex" />
          </t-descriptions-item>
          <t-descriptions-item label="最后登录IP">{{ userInfo.user?.loginIp }}</t-descriptions-item>
          <t-descriptions-item label="用户账号">{{ userInfo.user?.loginDate }}</t-descriptions-item>
          <t-descriptions-item v-if="tenantEnabled" label="租户">{{ userInfo.user?.tenantId }}</t-descriptions-item>
          <t-descriptions-item v-if="tenantEnabled" label="租户企业名称">
            {{ userInfo.user?.tenantName }}
          </t-descriptions-item>
          <t-descriptions-item label="岗位">
            {{ userInfo.postGroup }}
          </t-descriptions-item>
          <t-descriptions-item label="加入时间">
            {{ dateFormat(userInfo.user?.createTime, 'YYYY-MM-DD') }}
          </t-descriptions-item>
        </t-descriptions>
        <t-descriptions :column="2" item-layout="vertical">
          <t-descriptions-item label="角色">
            {{ userInfo.roleGroup }}
          </t-descriptions-item>
          <t-descriptions-item label="部门">
            {{ userInfo.user?.deptName }}
          </t-descriptions-item>
        </t-descriptions>
      </t-card>

      <!--   修改基础信息   -->
      <modify-user-basic-info v-model:visible="openModifyBasicInfo" :user="userInfo.user" @submit="getUser()" />
      <!--   绑定手机号   -->
      <binding-phone
        v-model:visible="openBindingPhone"
        :phone="userInfo.user?.phonenumber"
        @submit="(phonenumber) => (userInfo.user.phonenumber = phonenumber)"
      />
      <!--   绑定邮箱   -->
      <binding-email
        v-model:visible="openBindingEmail"
        :email="userInfo.user?.email"
        @submit="(email) => (userInfo.user.email = email)"
      />

      <!--   第三方应用   -->
      <t-card class="content-container" title="第三方应用" :bordered="false">
        <third-party />
      </t-card>

      <!--   在线设备   -->
      <t-card title="在线设备" :bordered="false">
        <online-device />
      </t-card>
    </t-col>

    <!--  用户头像卡片  -->
    <t-col :flex="1">
      <t-card class="user-intro" :bordered="false">
        <user-avatar />
        <div class="name">{{ userInfo.user?.userName }}</div>
        <div class="position">{{ userInfo.user?.deptName }}员工</div>
      </t-card>

      <!--   登录日志   -->
      <t-card title="登录日志" class="user-team" :bordered="false">
        <template #actions>
          <t-button theme="default" shape="square" variant="text">
            <ellipsis-icon />
          </t-button>
        </template>
        <user-login-log />
      </t-card>
    </t-col>
  </t-row>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'UserIndex',
});

import { storeToRefs } from 'pinia';
import { Edit2Icon, EllipsisIcon } from 'tdesign-icons-vue-next';
import { getCurrentInstance, onMounted, ref } from 'vue';

import type { ProfileVo } from '@/api/system/model/userModel';
import { getUserProfile } from '@/api/system/profile';
import { useTenantStore } from '@/store/modules/tenant';
import { dateDiffAbs, dateFormat } from '@/utils/date';

import BindingEmail from './BindingEmail.vue';
import BindingPhone from './BindingPhone.vue';
import ModifyUserBasicInfo from './ModifyUserBasicInfo.vue';
import OnlineDevice from './OnlineDevice.vue';
import ThirdParty from './ThirdParty.vue';
import UserAvatar from './UserAvatar.vue';
import UserLoginLog from './UserLoginLog.vue';

const { proxy } = getCurrentInstance();
const { sys_user_sex } = proxy.useDict('sys_user_sex');

const userInfo = ref<ProfileVo>({});
const companyName = ref(import.meta.env.VITE_APP_COMPANY_NAME);
const { tenantEnabled } = storeToRefs(useTenantStore());
const openModifyBasicInfo = ref(false);
const openBindingPhone = ref(false);
const openBindingEmail = ref(false);

function getUser() {
  getUserProfile().then((response) => {
    userInfo.value = response.data;
  });
}

onMounted(() => {
  getUser();
});
</script>

<style lang="less" scoped>
@import './index.less';

.user-intro-edit {
  cursor: pointer;

  &:hover {
    color: var(--td-brand-color-active);
  }
}
</style>
