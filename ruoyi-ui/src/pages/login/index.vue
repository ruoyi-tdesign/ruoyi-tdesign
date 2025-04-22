<template>
  <div class="login-wrapper">
    <login-header />

    <div class="login-container">
      <div class="title-container">
        <h1 class="title margin-no">{{ t('pages.login.loginTitle') }}</h1>
        <h1 class="title">{{ title }}</h1>
        <div v-if="registerOpen" class="sub-title">
          <p class="tip">{{ type === 'register' ? t('pages.login.existAccount') : t('pages.login.noAccount') }}</p>
          <p class="tip" @click="switchType(type === 'register' ? 'login' : 'register')">
            {{ type === 'register' ? t('pages.login.signIn') : t('pages.login.createAccount') }}
          </p>
        </div>
      </div>

      <login v-if="type === 'login'" />
      <register v-else @register-success="switchType('login')" />
      <tdesign-setting />
    </div>

    <footer class="copyright">
      Copyright @ 2021-{{ new Date().getFullYear() }} {{ companyName }} All Rights Reserved
    </footer>
  </div>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'LoginIndex',
});
import { ref } from 'vue';

import TdesignSetting from '@/layouts/setting.vue';
import { t } from '@/locales';

import LoginHeader from './components/Header.vue';
import Login from './components/Login.vue';
import Register from './components/Register.vue';

const title = ref(import.meta.env.VITE_APP_TITLE);
const companyName = ref(import.meta.env.VITE_APP_COMPANY_NAME);
// 注册开关
const registerOpen = ref(false);
const type = ref('login');
const switchType = (val: string) => {
  type.value = val;
};
</script>

<style lang="less" scoped>
@import './index.less';
</style>
