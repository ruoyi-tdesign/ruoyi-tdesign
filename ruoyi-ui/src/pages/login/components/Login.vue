<template>
  <t-form
    ref="form"
    :class="['item-container', `login-${type}`]"
    :data="formData"
    :rules="FORM_RULES"
    label-width="0"
    @submit="onSubmit"
  >
    <template v-if="type === 'password'">
      <t-form-item name="account">
        <t-input v-model="formData.account" size="large" :placeholder="t('pages.login.input.account')">
          <template #prefix-icon>
            <user-icon />
          </template>
        </t-input>
      </t-form-item>

      <t-form-item name="password">
        <t-input
          v-model="formData.password"
          size="large"
          :type="showPsw ? 'text' : 'password'"
          clearable
          :placeholder="t('pages.login.input.password')"
        >
          <template #prefix-icon>
            <lock-on-icon />
          </template>
          <template #suffix-icon>
            <browse-icon v-if="showPsw" @click="showPsw = !showPsw" />
            <browse-off-icon v-else @click="showPsw = !showPsw" />
          </template>
        </t-input>
      </t-form-item>

      <t-form-item v-if="captchaEnabled" class="verification-code" name="code">
        <t-input v-model="formData.code" size="large" :placeholder="t('pages.login.input.verification')">
          <template #label>
            <secured-icon />
          </template>
        </t-input>
        <div class="login-code">
          <img :src="codeUrl" class="login-code-img" @click="getCode" />
        </div>
        <!--        <t-button variant="outline" :disabled="countDown > 0" @click="sendCode">-->
        <!--          {{ countDown === 0 ? t('pages.login.sendVerification') : `${countDown}秒后可重发` }}-->
        <!--        </t-button>-->
      </t-form-item>

      <div class="check-container remember-pwd">
        <t-checkbox v-model="formData.rememberMe">{{ t('pages.login.remember') }}</t-checkbox>
        <span class="tip">{{ t('pages.login.forget') }}</span>
      </div>
    </template>

    <!-- 扫码登录 -->
    <template v-else-if="type === 'qrcode'">
      <div class="tip-container">
        <span class="tip">{{ t('pages.login.wechatLogin') }}</span>
        <span class="refresh">{{ t('pages.login.refresh') }} <refresh-icon /> </span>
      </div>
      <qrcode-vue value="" :size="160" level="H" />
    </template>

    <!-- 手机号登录 -->
    <template v-else>
      <t-form-item name="phone">
        <t-input v-model="formData.phone" size="large" :placeholder="t('pages.login.input.phone')">
          <template #prefix-icon>
            <mobile-icon />
          </template>
        </t-input>
      </t-form-item>

      <t-form-item class="verification-code" name="code">
        <t-input v-model="formData.code" size="large" :placeholder="t('pages.login.input.verification')" />
        <t-button size="large" variant="outline" :disabled="countDown > 0" @click="sendCode">
          {{ countDown === 0 ? t('pages.login.sendVerification') : `${countDown}秒后可重发` }}
        </t-button>
      </t-form-item>
    </template>

    <t-form-item v-if="type !== 'qrcode'" class="btn-container">
      <t-button block size="large" type="submit" :loading="loading">
        {{ loading ? t('pages.login.loading') : t('pages.login.signIn') }}
      </t-button>
    </t-form-item>

    <div class="switch-container">
      <span v-if="type !== 'password'" class="tip" @click="switchType('password')">
        {{ t('pages.login.accountLogin') }}
      </span>
      <span v-show="false" v-if="type !== 'qrcode'" class="tip" @click="switchType('qrcode')">
        {{ t('pages.login.wechatLogin') }}
      </span>
      <span v-show="false" v-if="type !== 'phone'" class="tip" @click="switchType('phone')">
        {{ t('pages.login.phoneLogin') }}
      </span>
    </div>
    <div style="display: flex; justify-content: flex-end; flex-direction: row">
      <t-button shape="circle" variant="outline" title="微信登录" @click="doSocialLogin('wechat_open')">
        <logo-wechat-stroke-filled-icon style="color: #1aad19" />
      </t-button>
      <t-button shape="circle" variant="outline" title="QQ登录" @click="doSocialLogin('qq')">
        <logo-qq-icon />
      </t-button>
      <t-button shape="circle" variant="outline" title="MaxKey登录" @click="doSocialLogin('maxkey')">
        <max-key class="t-icon" />
      </t-button>
      <t-button shape="circle" variant="outline" title="TopIam登录" @click="doSocialLogin('topiam')">
        <img :src="TopIam" class="t-icon" alt="使用 TopIam 账号授权登录" />
      </t-button>
      <t-button shape="circle" variant="outline" title="Gitee登录" @click="doSocialLogin('gitee')">
        <gitee-svg class="t-icon" />
      </t-button>
      <t-button shape="circle" variant="outline" title="Github登录" @click="doSocialLogin('github')">
        <logo-github-filled-icon />
      </t-button>
    </div>
  </t-form>
</template>

<script lang="ts" setup>
import QrcodeVue from 'qrcode.vue';
import {
  BrowseIcon,
  BrowseOffIcon,
  LockOnIcon,
  LogoGithubFilledIcon,
  LogoQqIcon,
  LogoWechatStrokeFilledIcon,
  MobileIcon,
  RefreshIcon,
  SecuredIcon,
  UserIcon,
} from 'tdesign-icons-vue-next';
import type { FormInstanceFunctions, FormRule, SubmitContext } from 'tdesign-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { getCurrentInstance, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { getCodeImg } from '@/api/login';
import type { LoginParam } from '@/api/model/loginModel';
import { authBinding } from '@/api/system/social';
import GiteeSvg from '@/assets/icons/svg/gitee.svg?component';
import MaxKey from '@/assets/icons/svg/maxkey.svg?component';
import TopIam from '@/assets/icons/svg/topiam.svg?url';
import { useCounter } from '@/hooks';
import { t } from '@/locales';
import { useTabsRouterStore, useUserStore } from '@/store';

const userStore = useUserStore();

const FORM_RULES: Record<string, FormRule[]> = {
  phone: [{ required: true, message: t('pages.login.required.phone'), type: 'error' }],
  account: [{ required: true, message: t('pages.login.required.account'), type: 'error' }],
  password: [{ required: true, message: t('pages.login.required.password'), type: 'error' }],
  code: [{ required: true, message: t('pages.login.required.verification'), type: 'error' }],
};

const type = ref('password');
const form = ref<FormInstanceFunctions>();
const formData = ref({
  phone: '',
  account: import.meta.env.VITE_APP_ACCOUNT || '',
  password: import.meta.env.VITE_APP_PASSWORD || '',
  rememberMe: false,
  code: '',
  uuid: '',
});
const showPsw = ref(false);
const codeUrl = ref('');
const loading = ref(false);
// 验证码开关
const captchaEnabled = ref(true);

const [countDown, handleCounter] = useCounter();

const switchType = (val: string) => {
  type.value = val;
};

const tabsRouterStore = useTabsRouterStore();
const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

function getCode() {
  getCodeImg().then((res) => {
    captchaEnabled.value = res.data.captchaEnabled === undefined ? true : res.data.captchaEnabled;
    if (captchaEnabled.value) {
      codeUrl.value = `data:image/gif;base64,${res.data.img}`;
      formData.value.uuid = res.data.uuid;
    }
  });
}

function getLoginData() {
  const account = localStorage.getItem('account');
  const password = localStorage.getItem('password');
  const rememberMe = localStorage.getItem('rememberMe');
  formData.value = {
    account: account || formData.value.account,
    phone: '',
    password: password || formData.value.password,
    rememberMe: rememberMe ? Boolean(rememberMe) : formData.value.rememberMe,
    code: '',
    uuid: '',
  };
}

/**
 * 发送验证码
 */
const sendCode = () => {
  form.value.validate({ fields: ['phone'] }).then((e) => {
    if (e === true) {
      handleCounter();
    }
  });
};

const onSubmit = async (ctx: SubmitContext) => {
  if (ctx.validateResult === true) {
    try {
      loading.value = true;
      let username;
      switch (type.value) {
        case 'password':
          username = formData.value.account;
          break;
        case 'phone':
          username = formData.value.phone;
          break;
        default:
      }
      const loginParam: LoginParam = {
        username,
        password: formData.value.password,
        code: formData.value.code,
        uuid: formData.value.uuid,
      };
      const msgLoading = proxy.$modal.msgLoading('登录中...');
      try {
        await userStore.login(loginParam);
      } finally {
        proxy.$modal.msgClose(msgLoading);
      }
      // 勾选了需要记住密码设置在 localStorage 中设置记住用户名和密码
      if (formData.value.rememberMe && type.value === 'password') {
        localStorage.setItem('account', String(formData.value.account));
        localStorage.setItem('password', String(formData.value.password));
        localStorage.setItem('rememberMe', String(formData.value.rememberMe));
      } else {
        // 否则移除
        localStorage.removeItem('account');
        localStorage.removeItem('password');
        localStorage.removeItem('rememberMe');
      }

      await MessagePlugin.success('登录成功');
      // 登录时删除保留的菜单项
      tabsRouterStore.removeTabRouterList();
      // 重定向到保留的菜单
      const redirect = route.query.redirect as string;
      const redirectUrl = redirect ? decodeURIComponent(redirect) : '/';
      await router.push(redirectUrl);
    } catch (e) {
      // 重新获取验证码
      if (captchaEnabled.value) {
        getCode();
      }
    } finally {
      loading.value = false;
    }
  }
};

/**
 * 第三方登录
 * @param type
 */
function doSocialLogin(type: string) {
  authBinding(type).then((res) => {
    // 获取授权地址跳转
    window.location.href = res.data;
  });
}

getCode();
getLoginData();
</script>

<style lang="less" scoped>
@import '../index.less';
</style>
