<template>
  <div>
    <t-space direction="vertical" class="w100%">
      <t-card>
        <div style="display: flex; justify-content: space-between">
          <!-- mode用于直接后端发起流程 不同接口实现方式可查看具体后端代码 -->
          <!-- 默认前端发起 前端发起更多样性 比如可以选审批人 选抄送人 上传附件等等 后端发起需要用户自行编写代码传这些参数 -->
          <approval-button
            :id="form.id"
            ref="approvalButtonRef"
            :button-loading="buttonLoading"
            :status="form.status"
            :page-type="routeParams.type"
            :mode="false"
            @submit-form="submitForm"
            @approval-verify-open="approvalVerifyOpen"
            @handle-approval-record="handleApprovalRecord"
          />
        </div>
      </t-card>
      <t-card style="height: 70vh; overflow-y: auto">
        <t-loading :loading="loading">
          <t-form
            ref="leaveFormRef"
            :disabled="routeParams.type === 'view'"
            :data="form"
            :rules="rules"
            label-width="80px"
            @submit="onSubmit"
          >
            <t-form-item v-if="routeParams.type === 'add'" label="流程定义">
              <t-select v-model="flowCode" placeholder="选择流程定义" style="width: 100%">
                <t-option v-for="item in flowCodeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </t-select>
            </t-form-item>
            <t-form-item label="请假类型" name="leaveType">
              <t-select v-model="form.leaveType" placeholder="请选择请假类型" style="width: 100%">
                <t-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
              </t-select>
            </t-form-item>
            <t-form-item label="请假时间" required-mark>
              <t-date-range-picker
                v-model="leaveTime"
                value-type="YYYY-MM-DD HH:mm:ss"
                allow-input
                clearable
                separator="至"
                :placeholder="['开始日期', '结束日期']"
                @change="changeLeaveTime()"
              />
            </t-form-item>
            <t-form-item label="请假天数" name="leaveDays">
              <t-input-number v-model="form.leaveDays" theme="normal" disabled placeholder="请输入" />
            </t-form-item>
            <t-form-item label="请假原因" name="remark">
              <t-textarea v-model="form.remark" placeholder="请输入请假原因" />
            </t-form-item>
          </t-form>
        </t-loading>
      </t-card>
    </t-space>
    <!-- 提交组件 -->
    <submit-verify ref="submitVerifyRef" :task-variables="taskVariables" @submit-callback="submitCallback" />
    <!-- 审批记录 -->
    <approval-record ref="approvalRecordRef" />
  </div>
</template>
<script setup lang="ts">
defineOptions({
  name: 'LeaveEdit',
});

import type { FormInstanceFunctions, FormRule, SubmitContext } from 'tdesign-vue-next';
import { ref } from 'vue';

import type { R } from '@/api/model/resultModel';
import { addLeave, getLeave, submitAndFlowStart, updateLeave } from '@/api/workflow/leave';
import type { LeaveForm, LeaveVo } from '@/api/workflow/model/leaveModel';
import type { StartProcessBo } from '@/api/workflow/model/taskModel';
import { startWorkFlow } from '@/api/workflow/task';
import ApprovalButton from '@/components/Process/approvalButton.vue';
import ApprovalRecord from '@/components/Process/approvalRecord.vue';
import SubmitVerify from '@/components/Process/submitVerify.vue';
import { useTabsRouterStore } from '@/store';

const { proxy } = getCurrentInstance();

const route = useRoute();
const removeCurrentTab = useTabsRouterStore().useRemoveCurrentTab();
const buttonLoading = ref(false);
const loading = ref(true);
const leaveTime = ref<Array<string>>([]);
// 路由参数
const routeParams = ref<Record<string, any>>({});
const options = [
  {
    value: '1',
    label: '事假',
  },
  {
    value: '2',
    label: '调休',
  },
  {
    value: '3',
    label: '病假',
  },
  {
    value: '4',
    label: '婚假',
  },
];
const flowCodeOptions = [
  {
    value: 'leave1',
    label: '请假申请-普通',
  },
  {
    value: 'leave2',
    label: '请假申请-排他网关',
  },
  {
    value: 'leave3',
    label: '请假申请-并行网关',
  },
  {
    value: 'leave4',
    label: '请假申请-会签',
  },
  {
    value: 'leave5',
    label: '请假申请-并行会签网关',
  },
];

// 自定义流程可不选择 直接填写flowCode 例如 'leave1'
const flowCode = ref<string>('leave1');
// 提交类型（draft/submit）与提交模式（是否后端发起）
const submitStatus = ref<string>('');
const submitMode = ref(false);

// 提交组件
const submitVerifyRef = ref<InstanceType<typeof SubmitVerify>>();
// 审批记录组件
const approvalRecordRef = ref<InstanceType<typeof ApprovalRecord>>();
// 按钮组件
const approvalButtonRef = ref<InstanceType<typeof ApprovalButton>>();

const leaveFormRef = ref<FormInstanceFunctions>();

const submitFormData = ref<StartProcessBo>({
  businessId: '',
  flowCode: '',
  variables: {},
  bizExt: {},
});
const taskVariables = ref<Record<string, any>>({});
const flowInstanceBizExt = ref<Record<string, any>>({});

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  leaveType: [{ required: true, message: '请假类型不能为空' }],
  leaveDays: [{ required: true, message: '请假天数不能为空' }],
  remark: [{ max: 255, message: '请假原因不能超过255个字符' }],
});

const form = ref<LeaveVo & LeaveForm>({});

/** 表单重置 */
const reset = () => {
  form.value = {};
  leaveTime.value = [];
  leaveTime.value = [];
  submitFormData.value = {
    businessId: '',
    flowCode: '',
    variables: {},
    bizExt: {},
  };
  leaveFormRef.value?.reset();
};

const changeLeaveTime = () => {
  const startDate = new Date(leaveTime.value[0]);
  startDate.setHours(0, 0, 0, 0);
  const startTime = startDate.getTime();
  const endDate = new Date(leaveTime.value[1]);
  endDate.setHours(0, 0, 0, 0);
  const endTime = endDate.getTime();
  const diffInMilliseconds = endTime - startTime;
  form.value.leaveDays = Math.floor(diffInMilliseconds / (1000 * 60 * 60 * 24)) + 1;
  form.value.startDate = leaveTime.value[0];
  form.value.endDate = leaveTime.value[1];
};
/** 获取详情 */
const getInfo = () => {
  loading.value = true;
  buttonLoading.value = false;
  nextTick(async () => {
    const res = await getLeave(routeParams.value.id);
    Object.assign(form.value, res.data);
    leaveTime.value = [];
    leaveTime.value.push(form.value.startDate);
    leaveTime.value.push(form.value.endDate);
    loading.value = false;
    buttonLoading.value = false;
  });
};

/** 提交按钮 */
const submitForm = async (status: string, mode: boolean) => {
  if (leaveTime.value.length === 0) {
    proxy?.$modal.msgError('请假时间不能为空');
    return;
  }
  submitStatus.value = status;
  submitMode.value = mode;
  leaveFormRef.value?.submit();
};

/** 表单提交校验通过后回调 */
const onSubmit = async ({ validateResult }: SubmitContext) => {
  if (validateResult !== true) {
    return;
  }
  const status = submitStatus.value;
  const mode = submitMode.value;
  try {
    buttonLoading.value = true;
    // 后端发起流程模式且非草稿 直接走流程发起
    if (mode && status !== 'draft') {
      const res = await submitAndFlowStart(form.value).finally(() => (buttonLoading.value = false));
      form.value = res.data;
      buttonLoading.value = false;
      proxy?.$modal.msgSuccess('操作成功');
      removeCurrentTab();
    } else {
      let res: R<LeaveVo>;
      if (form.value.id) {
        res = await updateLeave(form.value).finally(() => (buttonLoading.value = false));
      } else {
        res = await addLeave(form.value).finally(() => (buttonLoading.value = false));
      }
      form.value = res.data;
      if (status === 'draft') {
        buttonLoading.value = false;
        proxy?.$modal.msgSuccess('暂存成功');
        removeCurrentTab();
      } else {
        await handleStartWorkFlow(res.data);
      }
    }
  } finally {
    buttonLoading.value = false;
  }
};

// 提交申请
const handleStartWorkFlow = async (data: LeaveForm) => {
  try {
    submitFormData.value.flowCode = flowCode.value;
    submitFormData.value.businessId = data.id;
    // 流程变量
    taskVariables.value = {
      // leave2/6 使用的流程变量
      leaveDays: data.leaveDays,
      // leave4/5 使用的流程变量
      userList: ['1', '3', '4'],
    };
    // 流程实例业务扩展字段
    flowInstanceBizExt.value = {
      businessTitle: '请假申请',
      businessCode: data.applyCode,
    };
    submitFormData.value.variables = taskVariables.value;
    submitFormData.value.bizExt = flowInstanceBizExt.value;
    const resp = await startWorkFlow(submitFormData.value);
    if (submitVerifyRef.value) {
      buttonLoading.value = false;
      submitVerifyRef.value.openDialog(resp.data.taskId);
    }
  } finally {
    buttonLoading.value = false;
  }
};
// 审批记录
const handleApprovalRecord = () => {
  approvalRecordRef.value.init(form.value.id);
};
// 提交回调
const submitCallback = async () => {
  removeCurrentTab();
};
// 审批
const approvalVerifyOpen = async () => {
  submitVerifyRef.value.openDialog(routeParams.value.taskId);
};

onMounted(() => {
  nextTick(async () => {
    routeParams.value = route.query;
    reset();
    loading.value = false;
    if (
      routeParams.value.type === 'update' ||
      routeParams.value.type === 'view' ||
      routeParams.value.type === 'approval'
    ) {
      getInfo();
    }
  });
});
</script>
