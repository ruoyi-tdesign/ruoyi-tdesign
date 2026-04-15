<template>
  <t-dialog
    v-model:visible="visible"
    header="修改基础信息"
    destroy-on-close
    :close-on-overlay-click="false"
    placement="center"
    width="450px"
    attach="body"
    @opened="reset"
    @confirm="userRef.submit()"
  >
    <t-form ref="userRef" :data="form" :rules="rules" label-width="calc(4em + 41px)" @submit="submit">
      <t-form-item label="用户昵称" name="nickName">
        <t-input v-model="form.nickName" :maxlength="30" />
      </t-form-item>
      <t-form-item label="性别">
        <t-radio-group v-model="form.sex">
          <t-radio value="0">男</t-radio>
          <t-radio value="1">女</t-radio>
        </t-radio-group>
      </t-form-item>
    </t-form>
  </t-dialog>
</template>
<script lang="ts" setup>
import type { FormInstanceFunctions, FormRule, SubmitContext } from 'tdesign-vue-next';
import { getCurrentInstance, ref } from 'vue';

import type { SysUserProfileBo } from '@/api/system/model/userModel';
import { updateUserProfile } from '@/api/system/profile';

const props = defineProps({
  user: {
    type: Object,
  },
});

const emit = defineEmits(['submit']);

const visible = defineModel('visible', {
  type: Boolean,
  default: false,
});

const { proxy } = getCurrentInstance();
const userRef = ref<FormInstanceFunctions>();

const form = ref<SysUserProfileBo>({});

const rules = ref<Record<string, Array<FormRule>>>({
  nickName: [{ required: true, message: '用户昵称不能为空' }],
});

function reset() {
  const { user } = props;
  form.value = {
    nickName: user?.nickName,
    sex: user?.sex,
  };
}

/** 提交按钮 */
function submit({ validateResult }: SubmitContext) {
  if (validateResult === true) {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    updateUserProfile(form.value)
      .then(() => {
        proxy.$modal.msgSuccess('修改成功');
        emit('submit');
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  }
}
</script>
