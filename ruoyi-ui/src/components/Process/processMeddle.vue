<template>
  <t-dialog
    v-model:visible="visible"
    draggable
    header="流程干预"
    :width="props.width"
    :height="props.height"
    :close-on-click-modal="false"
  >
    <my-descriptions :loading="loading" class="margin-top" :title="`${task.flowName}(${task.flowCode})`" bordered>
      <t-descriptions-item label="任务名称">{{ task.nodeName }}</t-descriptions-item>
      <t-descriptions-item label="节点编码">{{ task.nodeCode }}</t-descriptions-item>
      <t-descriptions-item label="开始时间">{{ task.createTime }}</t-descriptions-item>
      <t-descriptions-item label="流程实例ID">{{ task.instanceId }}</t-descriptions-item>
      <t-descriptions-item label="版本号">{{ task.version }}.0</t-descriptions-item>
      <t-descriptions-item label="业务ID">{{ task.businessId }}</t-descriptions-item>
    </my-descriptions>
    <template #footer>
      <span class="dialog-footer">
        <t-button
          v-if="task.flowStatus === 'waiting'"
          :disabled="buttonDisabled"
          theme="primary"
          @click="openTransferTask"
        >
          转办
        </t-button>
        <t-button
          v-if="task.flowStatus === 'waiting' && Number(task.nodeRatio) > 0"
          :disabled="buttonDisabled"
          theme="primary"
          @click="openMultiInstanceUser"
        >
          加签
        </t-button>
        <t-button
          v-if="task.flowStatus === 'waiting' && Number(task.nodeRatio) > 0"
          :disabled="buttonDisabled"
          theme="primary"
          @click="handleTaskUser"
        >
          减签
        </t-button>
        <t-button
          v-if="task.flowStatus === 'waiting'"
          :disabled="buttonDisabled"
          theme="danger"
          @click="handleTerminationTask"
        >
          终止
        </t-button>
      </span>
    </template>
    <!-- 转办 -->
    <user-select ref="transferTaskRef" :multiple="false" @confirm-call-back="handleTransferTask"></user-select>
    <!-- 加签组件 -->
    <user-select ref="multiInstanceUserRef" :multiple="true" @confirm-call-back="addMultiInstanceUser"></user-select>
    <t-dialog
      v-model:visible="deleteSignatureVisible"
      draggable
      header="减签人员"
      width="700px"
      height="400px"
      attach="body"
      :close-on-overlay-click="false"
      ><div>
        <t-table :data="deleteUserList" hover bordered :columns="deleteSignatureColumns">
          <template #operation="{ row }">
            <my-link theme="danger" @click.stop="deleteMultiInstanceUser(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </template>
        </t-table>
      </div>
    </t-dialog>
  </t-dialog>
</template>
<script lang="ts" setup>
import { DeleteIcon } from 'tdesign-icons-vue-next';
import type { TableProps } from 'tdesign-vue-next';

import type { FlowTaskVo, TaskOperationBo } from '@/api/workflow/model/taskModel';
import UserSelect from '@/components/user-select/index.vue';

const { proxy } = getCurrentInstance();

import type { SysUserVo, UserDTO } from '@/api/system/model/userModel';
import { currentTaskAllUser, getTask, taskOperation, terminationTask } from '@/api/workflow/task';

const props = defineProps({
  width: {
    type: String,
    default: '80%',
  },
  height: {
    type: String,
    default: '100%',
  },
});
const deleteSignatureColumns: TableProps['columns'] = [
  { title: `任务名称`, colKey: 'nodeName', align: 'center' },
  { title: `办理人`, colKey: 'nickName', align: 'center' },
  { title: `操作`, colKey: 'operation', align: 'center', width: 160 },
];
const emits = defineEmits(['submitCallback']);
const transferTaskRef = ref<InstanceType<typeof UserSelect>>();
const multiInstanceUserRef = ref<InstanceType<typeof UserSelect>>();
// 遮罩层
const loading = ref(true);
// 按钮
const buttonDisabled = ref(true);
const visible = ref(false);
// 减签弹窗
const deleteSignatureVisible = ref(false);
// 可减签的人员
const deleteUserList = ref<UserDTO[]>([]);
// 任务
const task = ref<FlowTaskVo>({
  id: undefined,
  createTime: undefined,
  updateTime: undefined,
  tenantId: undefined,
  definitionId: undefined,
  instanceId: undefined,
  flowName: undefined,
  businessId: undefined,
  nodeCode: undefined,
  nodeName: undefined,
  flowCode: undefined,
  flowStatus: undefined,
  formCustom: undefined,
  formPath: undefined,
  nodeType: undefined,
  nodeRatio: undefined,
  version: undefined,
  applyNode: undefined,
  buttonList: [],
});

const open = (taskId: string | number) => {
  visible.value = true;
  getTask(taskId).then((response) => {
    loading.value = false;
    buttonDisabled.value = false;
    task.value = response.data;
  });
};

// 打开转办
const openTransferTask = () => {
  transferTaskRef.value.open();
};
// 转办
const handleTransferTask = async (data: SysUserVo[]) => {
  if (data && data.length > 0) {
    const taskOperationBo = reactive<TaskOperationBo>({
      userId: data[0].userId,
      taskId: task.value.id,
      message: '',
    });
    proxy?.$modal.confirm('是否确认提交？', async () => {
      loading.value = true;
      buttonDisabled.value = true;
      await taskOperation(taskOperationBo, 'transferTask').finally(() => {
        loading.value = false;
        buttonDisabled.value = false;
      });
      visible.value = false;
      emits('submitCallback');
      await proxy?.$modal.msgSuccess('操作成功');
    });
  } else {
    await proxy?.$modal.msgWarning('请选择用户！');
  }
};
// 加签
const openMultiInstanceUser = async () => {
  multiInstanceUserRef.value.open();
};
// 加签
const addMultiInstanceUser = async (data: SysUserVo[]) => {
  if (data && data.length > 0) {
    const taskOperationBo = reactive<TaskOperationBo>({
      userIds: data.map((e) => e.userId),
      taskId: task.value.id,
      message: '',
    });
    proxy?.$modal.confirm('是否确认提交？', async () => {
      loading.value = true;
      buttonDisabled.value = true;
      await taskOperation(taskOperationBo, 'addSignature').finally(() => {
        loading.value = false;
        buttonDisabled.value = false;
      });
      visible.value = false;
      emits('submitCallback');
      await proxy?.$modal.msgSuccess('操作成功');
    });
  } else {
    await proxy?.$modal.msgWarning('请选择用户！');
  }
};
// 减签
const deleteMultiInstanceUser = async (row: UserDTO) => {
  proxy?.$modal.confirm('是否确认提交？', async () => {
    loading.value = true;
    buttonDisabled.value = true;
    const taskOperationBo = reactive<TaskOperationBo>({
      userIds: [row.userId],
      taskId: task.value.id,
      message: '',
    });
    await taskOperation(taskOperationBo, 'reductionSignature').finally(() => {
      loading.value = false;
      buttonDisabled.value = false;
    });
    visible.value = false;
    emits('submitCallback');
    await proxy?.$modal.msgSuccess('操作成功');
  });
};
// 获取办理人
const handleTaskUser = async () => {
  const data = await currentTaskAllUser(task.value.id);
  deleteUserList.value = data.data;
  if (deleteUserList.value && deleteUserList.value.length > 0) {
    deleteUserList.value.forEach((e) => {
      // @ts-ignore
      e.nodeName = task.value.nodeName;
    });
  }
  deleteSignatureVisible.value = true;
};

// 终止任务
const handleTerminationTask = async () => {
  const params = {
    taskId: task.value.id,
    comment: '',
  };
  proxy?.$modal.confirm('是否确认终止？', async () => {
    loading.value = true;
    buttonDisabled.value = true;
    await terminationTask(params).finally(() => {
      loading.value = false;
      buttonDisabled.value = false;
    });
    visible.value = false;
    emits('submitCallback');
    await proxy?.$modal.msgSuccess('操作成功');
  });
};
/**
 * 对外暴露子组件方法
 */
defineExpose({
  open,
});
</script>
