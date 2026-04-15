<template>
  <t-dialog
    v-model:visible="visible"
    header="绑定手机号"
    destroy-on-close
    :close-on-overlay-click="false"
    placement="center"
    width="450px"
    attach="body"
    @opened="reset"
    @confirm="formRef.submit()"
  >
    <t-form ref="formRef" :data="form" :rules="rules" label-width="calc(4em + 41px)" @submit="submit">
      <t-form-item label="手机号码" name="phonenumber">
        <t-input v-model="form.phonenumber" :maxlength="11" />
      </t-form-item>
    </t-form>
  </t-dialog>
</template>
<script setup lang="ts">
defineOptions({
  name: 'BindingPhone',
});

const props = defineProps({
  phone: {
    type: String,
    required: true,
  },
});
const emit = defineEmits<{
  (e: 'submit', phonenumber: string): void;
}>();
import type { FormInstanceFunctions, FormRule, SubmitContext } from 'tdesign-vue-next';
import { getCurrentInstance, ref } from 'vue';

import { updateUserPhonenumber } from '@/api/system/profile';

const visible = defineModel('visible', {
  type: Boolean,
  default: false,
});

const { proxy } = getCurrentInstance();
const formRef = ref<FormInstanceFunctions>();

const rules = ref<Record<string, Array<FormRule>>>({
  phonenumber: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码' }],
});

const form = ref({
  phonenumber: undefined,
});

function reset() {
  form.value.phonenumber = props.phone || '';
}

/** 提交按钮 */
function submit({ validateResult }: SubmitContext) {
  if (validateResult === true) {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    updateUserPhonenumber(form.value.phonenumber)
      .then(() => {
        proxy.$modal.msgSuccess('修改成功');
        visible.value = false;
        emit('submit', form.value.phonenumber);
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  }
}
</script>
