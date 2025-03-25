<template>
  <!-- 添加或修改用户配置对话框 -->
  <t-dialog
    v-model:visible="visible"
    header="修改密码"
    destroy-on-close
    :close-on-overlay-click="false"
    placement="center"
    width="450px"
    attach="body"
    @close="reset"
    @confirm="pwdRef.submit()"
  >
    <t-form ref="pwdRef" :data="form" :rules="rules" label-width="calc(4em + 41px)" @submit="submit">
      <t-form-item label="旧密码" name="oldPassword">
        <t-input v-model="form.oldPassword" placeholder="请输入旧密码" type="password" />
      </t-form-item>
      <t-form-item label="新密码" name="newPassword">
        <t-input v-model="form.newPassword" placeholder="请输入新密码" type="password" autocomplete="new-password" />
      </t-form-item>
      <t-form-item label="确认密码" name="confirmPassword">
        <t-input
          v-model="form.confirmPassword"
          placeholder="请确认新密码"
          type="password"
          autocomplete="new-password"
        />
      </t-form-item>
    </t-form>
  </t-dialog>
</template>

<script lang="ts" setup>
import type { FormInstanceFunctions, FormRule, SubmitContext } from 'tdesign-vue-next';
import { getCurrentInstance, ref } from 'vue';

import { updateUserPwd } from '@/api/system/profile';

const { proxy } = getCurrentInstance();
const pwdRef = ref<FormInstanceFunctions>();
const visible = defineModel('visible', {
  type: Boolean,
  default: false,
});

const form = ref({
  oldPassword: undefined,
  newPassword: undefined,
  confirmPassword: undefined,
});

/** 重置操作表单 */
function reset() {
  form.value = { confirmPassword: undefined, newPassword: undefined, oldPassword: undefined };
  proxy.resetForm('pwdRef');
}

const equalToPassword = (value: string) => {
  return form.value.newPassword === value;
};
const rules = ref<Record<string, Array<FormRule>>>({
  oldPassword: [{ required: true, message: '旧密码不能为空' }],
  newPassword: [
    { required: true, message: '新密码不能为空' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符' },
    { pattern: /^[^<>"'|\\]+$/, message: '不能包含非法字符：< > " \' \\ |', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '确认密码不能为空' },
    { validator: equalToPassword, message: '两次输入的密码不一致' },
  ],
});

/** 提交按钮 */
function submit({ validateResult }: SubmitContext) {
  if (validateResult === true) {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    updateUserPwd(form.value.oldPassword, form.value.newPassword)
      .then(() => {
        proxy.$modal.msgSuccess('修改成功');
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  }
}
</script>
