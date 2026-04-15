<template>
  <t-dialog
    v-model:visible="visible"
    header="绑定邮箱"
    destroy-on-close
    :close-on-overlay-click="false"
    placement="center"
    width="450px"
    attach="body"
    @opened="reset"
    @confirm="formRef.submit()"
  >
    <t-form ref="formRef" :data="form" :rules="rules" label-width="calc(4em + 41px)" @submit="submit">
      <t-form-item label="邮箱" name="email">
        <t-input v-model="form.email" :maxlength="50" />
      </t-form-item>
    </t-form>
  </t-dialog>
</template>
<script setup lang="ts">
defineOptions({
  name: 'BindingEmail',
});

const props = defineProps({
  email: {
    type: String,
    required: true,
  },
});
const emit = defineEmits<{
  (e: 'submit', email: string): void;
}>();
import type { FormInstanceFunctions, FormRule, SubmitContext } from 'tdesign-vue-next';
import { getCurrentInstance, ref } from 'vue';

import { updateUserEmail } from '@/api/system/profile';

const visible = defineModel('visible', {
  type: Boolean,
  default: false,
});

const { proxy } = getCurrentInstance();
const formRef = ref<FormInstanceFunctions>();

const rules = ref<Record<string, Array<FormRule>>>({
  email: [{ email: true, message: '请输入正确的邮箱地址' }],
});

const form = ref({
  email: undefined,
});

function reset() {
  form.value.email = props.email || '';
}

/** 提交按钮 */
function submit({ validateResult }: SubmitContext) {
  if (validateResult === true) {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    updateUserEmail(form.value.email)
      .then(() => {
        proxy.$modal.msgSuccess('修改成功');
        visible.value = false;
        emit('submit', form.value.email);
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  }
}
</script>
