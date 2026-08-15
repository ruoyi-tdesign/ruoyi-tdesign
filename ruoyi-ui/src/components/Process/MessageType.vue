<template>
  <t-dialog
    v-model:visible="visible"
    :header="props.title"
    width="50%"
    placement="center"
    :close-on-overlay-click="false"
    @close="cancel"
  >
    <t-loading :loading="loading">
      <t-form ref="ruleFormRef" :data="form" :rules="rules" label-width="120px" @submit="submitForm">
        <t-form-item label="消息提醒" name="messageType">
          <t-checkbox-group v-model="form.messageType">
            <t-checkbox value="1" name="type" disabled>站内信</t-checkbox>
            <t-checkbox value="2" name="type">邮件</t-checkbox>
            <t-checkbox value="3" name="type">短信</t-checkbox>
          </t-checkbox-group>
        </t-form-item>
        <t-form-item label="消息内容" name="message">
          <t-textarea v-model="form.message" :autosize="{ minRows: 3, maxRows: 5 }" />
        </t-form-item>
      </t-form>
    </t-loading>
    <template #footer>
      <div class="dialog-footer" style="float: right; padding-bottom: 20px">
        <t-button :disabled="buttonDisabled" theme="primary" @click="ruleFormRef.submit()">确认</t-button>
        <t-button :disabled="buttonDisabled" variant="outline" @click="cancel">取消</t-button>
      </div>
    </template>
  </t-dialog>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'MessageType',
});
const props = defineProps({
  title: {
    type: String,
    default: '提示',
  },
});
const emits = defineEmits<{
  (e: 'submit-callback', value: Record<string, any>): void;
  (e: 'cancel-callback'): void;
}>();
import type { FormInstanceFunctions, FormRule, SubmitContext } from 'tdesign-vue-next';
import { ref } from 'vue';

const ruleFormRef = ref<FormInstanceFunctions>();
// 遮罩层
const loading = ref(true);
const visible = ref(false);
const buttonDisabled = ref(true);
const form = ref<Record<string, any>>({
  message: undefined,
  messageType: ['1'],
});
const rules = ref<Record<string, Array<FormRule>>>({
  messageType: [
    {
      required: true,
      message: '请选择消息提醒',
      trigger: 'change',
    },
  ],
  message: [
    {
      required: true,
      message: '请输入消息内容',
      trigger: 'blur',
    },
  ],
});
// 打开弹窗
const open = async () => {
  reset();
  visible.value = true;
  loading.value = false;
  buttonDisabled.value = false;
};
// 关闭弹窗
const close = async () => {
  reset();
  visible.value = false;
};
/** 提交表单 */
function submitForm({ validateResult }: SubmitContext) {
  if (validateResult === true) {
    emits('submit-callback', form.value);
  }
}
// 取消
const cancel = async () => {
  visible.value = false;
  buttonDisabled.value = false;
  emits('cancel-callback');
};
// 重置
const reset = async () => {
  form.value.taskIdList = [];
  form.value.message = '';
  form.value.messageType = ['1'];
};
/**
 * 对外暴露子组件方法
 */
defineExpose({
  open,
  close,
});
</script>
