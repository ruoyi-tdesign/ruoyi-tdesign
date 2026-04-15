<template>
  <div>
    <t-loading :loading="loading" size="small">
      <t-form
        label-align="right"
        colon
        :data="form"
        :rules="rules"
        reset-type="initial"
        label-width="calc(13em + 41px)"
        scroll-to-first-error="smooth"
        :disabled="disabled"
        @submit="submitForm"
      >
        <!-- <t-form-item label="主框架页-默认皮肤样式名称" name="sys.index.skinName">
          <t-select v-model="form['sys.index.skinName']" auto-width>
            <t-option label="蓝色" value="skin-blue" />
            <t-option label="绿色" value="skin-green" />
            <t-option label="紫色" value="skin-purple" />
            <t-option label="红色" value="skin-red" />
            <t-option label="黄色" value="skin-yellow" />
          </t-select>
        </t-form-item> -->
        <t-form-item label="用户管理-账号初始密码" name="sys.user.initPassword">
          <t-input
            v-model="form['sys.user.initPassword']"
            placeholder="请输入账号初始密码"
            :maxlength="50"
            show-limit-number
          />
        </t-form-item>
        <!-- <t-form-item label="主框架页-侧边栏主题" name="sys.index.sideTheme">
          <t-select v-model="form['sys.index.sideTheme']" auto-width>
            <t-option label="深色主题" value="theme-dark" />
            <t-option label="浅色主题" value="theme-light" />
          </t-select>
        </t-form-item> -->
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
  name: 'SystemConfig',
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
  // 'sys.index.skinName': 'skin-blue',
  'sys.user.initPassword': '123456',
  // 'sys.index.sideTheme': 'theme-dark',
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
    updateConfigMaps(0, form.value)
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
