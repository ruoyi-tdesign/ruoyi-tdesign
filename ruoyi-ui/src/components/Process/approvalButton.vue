<template>
  <div style="display: flex; justify-content: space-between">
    <div>
      <t-button v-if="submitButtonShow" :loading="props.buttonLoading" theme="default" @click="submitForm('draft')">
        暂存
      </t-button>
      <t-button v-if="submitButtonShow" :loading="props.buttonLoading" theme="primary" @click="submitForm('submit')">
        提 交
      </t-button>
      <t-button v-if="approvalButtonShow" :loading="props.buttonLoading" theme="primary" @click="approvalVerifyOpen">
        审批
      </t-button>
      <t-button v-if="props.id && props.status !== 'draft'" theme="primary" @click="handleApprovalRecord">
        流程进度
      </t-button>
      <slot />
      <t-button style="float: right" variant="outline" @click="goBack()">返回</t-button>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useTabsRouterStore } from '@/store';

const props = defineProps({
  status: {
    type: String,
    default: '',
  },
  pageType: {
    type: String,
    default: '',
  },
  buttonLoading: {
    type: Boolean,
    default: false,
  },
  id: {
    type: [String, Number],
    default: '',
  },
});
const emits = defineEmits(['submit-form', 'approval-verify-open', 'handle-approval-record']);
const removeCurrentTab = useTabsRouterStore().useRemoveCurrentTab();
// 暂存，提交
const submitForm = async (type: 'draft' | 'submit') => {
  emits('submit-form', type);
};
// 审批
const approvalVerifyOpen = async () => {
  emits('approval-verify-open');
};
// 审批记录
const handleApprovalRecord = () => {
  emits('handle-approval-record');
};

// 校验提交按钮是否显示
const submitButtonShow = computed(() => {
  return (
    props.pageType === 'add' ||
    (props.pageType === 'update' &&
      props.status &&
      (props.status === 'draft' || props.status === 'cancel' || props.status === 'back'))
  );
});

// 校验审批按钮是否显示
const approvalButtonShow = computed(() => {
  return props.pageType === 'approval' && props.status && props.status === 'waiting';
});

// 返回
const goBack = () => {
  removeCurrentTab();
};
</script>
