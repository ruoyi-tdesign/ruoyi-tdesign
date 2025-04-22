<template>
  <div>
    <result v-if="hasError" title="授权失败" type="401" :tip="errorMessage || '未知原因'">
      <t-button @click="() => $router.push('/login')">返回登录</t-button>
    </result>
    <t-loading :loading="loading" :text="loadingText" :fullscreen="true"> </t-loading>
  </div>
</template>

<script lang="ts" setup>
import { storeToRefs } from 'pinia';
import { MessagePlugin } from 'tdesign-vue-next';
import { nextTick, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import { callback, login } from '@/api/login';
import type { LoginData } from '@/api/model/loginModel';
import Result from '@/components/result/index.vue';
import { useUserStore } from '@/store';

const route = useRoute();
const loading = ref(true);
const { token, tenantId } = storeToRefs(useUserStore());
const hasError = ref(false);
const errorMessage = ref<string>();
const loadingText = ref('');

/**
 * 接收Route传递的参数
 * @param {Object} route.query.
 */
const code = route.query.code as string;
const state = route.query.state as string;
const source = route.query.source as string;
const stateJson = JSON.parse(atob(state));

const effectTenantId = computed(() =>
  (stateJson.tenantId as string) ? (stateJson.tenantId as string) : tenantId.value,
);

const processResponse = async (res: any) => {
  if (res.code !== 200) {
    handleError(res.msg);
    return;
  }
  if (res.data !== null) {
    token.value = res.data.access_token;
  }
  MessagePlugin.success(res.msg);
  setTimeout(() => {
    window.location.href = `${import.meta.env.VITE_APP_CONTEXT_PATH}index`;
  }, 2000);
};

const handleError = (error: string) => {
  hasError.value = true;
  errorMessage.value = error;
};

const callbackByCode = async (data: LoginData) => {
  try {
    const res = await callback(data);
    await processResponse(res);
  } catch (error) {
    handleError(error?.message);
  } finally {
    loading.value = false;
  }
};

const loginByCode = async (data: LoginData) => {
  try {
    const res = await login(data);
    await processResponse(res);
  } catch (error) {
    handleError(error?.message);
  } finally {
    loading.value = false;
  }
};

const init = async () => {
  const data: LoginData = {
    socialCode: code,
    socialState: state,
    tenantId: effectTenantId.value,
    source,
    clientId: import.meta.env.VITE_CLIENT_ID,
    grantType: 'social',
  };

  if (!token.value) {
    loadingText.value = '登录中...';
    await loginByCode(data);
  } else {
    loadingText.value = '正在授权中...';
    await callbackByCode(data);
  }
};

onMounted(() => {
  nextTick(() => {
    init();
  });
});
</script>
