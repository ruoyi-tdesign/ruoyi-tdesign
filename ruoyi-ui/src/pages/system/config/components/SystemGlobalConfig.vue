<template>
  <div>
    <t-loading :loading="loading" size="small">
      <t-form
        label-align="right"
        colon
        :data="form"
        :rules="rules"
        reset-type="initial"
        label-width="calc(15em + 41px)"
        scroll-to-first-error="smooth"
        :disabled="disabled"
        @submit="submitForm"
      >
        <t-form-item label="账号自助-是否开启用户注册功能" name="sys.account.registerUser">
          <t-select v-model="form['sys.account.registerUser']" auto-width>
            <t-option label="开启" value="true" />
            <t-option label="关闭" value="false" />
          </t-select>
        </t-form-item>
        <t-form-item label="OSS预览列表资源开关" name="sys.oss.previewListResource">
          <t-select v-model="form['sys.oss.previewListResource']" auto-width>
            <t-option label="开启" value="true" />
            <t-option label="关闭" value="false" />
          </t-select>
        </t-form-item>
        <t-form-item v-hasPermi="['system:config:edit']">
          <t-button theme="default" variant="outline" @click="init()">还原</t-button>
          <t-button theme="primary" type="submit" :loading="buttonLoading">保存</t-button>
          <t-button v-hasPermi="['system:config:remove']" theme="danger" variant="outline" @click="handleRefreshCache">
            刷新缓存
          </t-button>
        </t-form-item>
      </t-form>
    </t-loading>
  </div>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'SystemGlobalConfig',
});
defineProps({
  disabled: {
    type: Boolean,
    required: true,
  },
});
import type { FormRule, SubmitContext } from 'tdesign-vue-next';
import { getCurrentInstance, ref } from 'vue';

import { getConfigByKeys, refreshCache, updateConfigMaps } from '@/api/system/config';

const loading = ref(false);
const buttonLoading = ref(false);

const rules = ref<Record<string, Array<FormRule>>>({
  'sys.user.initPassword': [{ required: true, message: '账号初始密码不能为空' }],
});

const form = ref<Record<string, string>>({
  'sys.account.registerUser': 'false',
  'sys.oss.previewListResource': 'true',
});

const { proxy } = getCurrentInstance();

/**
 * 提交配置
 * @param validateResult
 * @param firstError
 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    updateConfigMaps(1, form.value)
      .then(() => {
        proxy.$modal.msgSuccess('更新成功');
      })
      .finally(() => {
        buttonLoading.value = false;
        proxy.$modal.msgClose(msgLoading);
      });
  } else {
    proxy.$modal.msgError(firstError);
  }
}

/**
 * 读取配置
 */
function init() {
  loading.value = true;
  const keys = Object.keys(form.value);
  getConfigByKeys(keys.join(',')).then((res) => {
    keys.forEach((key) => {
      form.value[key] = res.data[key]?.configValue ?? form.value[key];
    });
    loading.value = false;
  });
}

/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  proxy.$modal.confirm(`是否确认刷新缓存？`, () => {
    return refreshCache().then(() => {
      proxy.$modal.msgSuccess('刷新缓存成功');
    });
  });
}

init();
</script>
<style scoped></style>
