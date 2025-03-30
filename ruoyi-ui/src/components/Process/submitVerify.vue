<template>
  <t-dialog
    v-model:visible="dialog.visible"
    :header="dialog.title"
    width="50%"
    :close-on-overlay-click="false"
    placement="center"
    @close="cancel"
  >
    <t-loading :loading="loading">
      <t-form :data="form" label-width="120px">
        <t-form-item label="消息提醒">
          <t-checkbox-group v-model="form.messageType">
            <t-checkbox value="1" name="type" disabled>站内信</t-checkbox>
            <t-checkbox value="2" name="type">邮件</t-checkbox>
            <t-checkbox value="3" name="type">短信</t-checkbox>
          </t-checkbox-group>
        </t-form-item>
        <t-form-item v-if="task.flowStatus === 'waiting'" label="附件">
          <fileUpload
            v-model="form.fileId"
            :file-type="['png', 'jpg', 'jpeg', 'doc', 'docx', 'xlsx', 'xls', 'ppt', 'txt', 'pdf']"
            :file-size="20"
          />
        </t-form-item>
        <t-form-item v-if="task.flowStatus === 'waiting' && buttonObj.copy" label="抄送">
          <t-button theme="primary" shape="circle" @click="openUserSelectCopy">
            <template #icon><add-icon /></template>
          </t-button>
          <t-tag
            v-for="user in selectCopyUserList"
            :key="user.userId"
            closable
            style="margin: 2px"
            @close="handleCopyCloseTag(user)"
          >
            {{ user.nickName }}
          </t-tag>
        </t-form-item>
        <t-form-item v-if="task.flowStatus === 'waiting'" label="审批意见">
          <t-textarea v-model="form.message" />
        </t-form-item>
      </t-form>
    </t-loading>
    <template #footer>
      <span class="dialog-footer">
        <t-button :disabled="buttonDisabled" theme="primary" @click="handleCompleteTask"> 提交 </t-button>
        <t-button
          v-if="task.flowStatus === 'waiting' && buttonObj.trust"
          :disabled="buttonDisabled"
          theme="primary"
          @click="openDelegateTask"
        >
          委托
        </t-button>
        <t-button
          v-if="task.flowStatus === 'waiting' && buttonObj.transfer"
          :disabled="buttonDisabled"
          theme="primary"
          @click="openTransferTask"
        >
          转办
        </t-button>
        <t-button
          v-if="task.flowStatus === 'waiting' && Number(task.nodeRatio) > 0 && buttonObj.subSign"
          :disabled="buttonDisabled"
          theme="primary"
          @click="openMultiInstanceUser"
        >
          加签
        </t-button>
        <t-button
          v-if="task.flowStatus === 'waiting' && Number(task.nodeRatio) > 0 && buttonObj.subSign"
          :disabled="buttonDisabled"
          theme="primary"
          @click="handleTaskUser"
        >
          减签
        </t-button>
        <t-button
          v-if="task.flowStatus === 'waiting' && buttonObj.termination"
          :disabled="buttonDisabled"
          theme="danger"
          @click="handleTerminationTask"
        >
          终止
        </t-button>
        <t-button
          v-if="task.flowStatus === 'waiting' && buttonObj.back"
          :disabled="buttonDisabled"
          theme="danger"
          @click="handleBackProcessOpen"
        >
          退回
        </t-button>
        <t-button :disabled="buttonDisabled" variant="outline" @click="cancel">取消</t-button>
      </span>
    </template>
    <!-- 抄送 -->
    <user-select
      ref="userSelectCopyRef"
      :multiple="true"
      :data="selectCopyUserIds"
      @confirm-call-back="userSelectCopyCallBack"
    />
    <!-- 转办 -->
    <user-select ref="transferTaskRef" :multiple="false" @confirm-call-back="handleTransferTask"></user-select>
    <!-- 委托 -->
    <user-select ref="delegateTaskRef" :multiple="false" @confirm-call-back="handleDelegateTask"></user-select>
    <!-- 加签组件 -->
    <user-select ref="multiInstanceUserRef" :multiple="true" @confirm-call-back="addMultiInstanceUser"></user-select>

    <!-- 驳回开始 -->
    <t-dialog v-model:visible="backVisible" header="驳回" width="40%" :close-on-overlay-click="false">
      <t-form v-if="task.flowStatus === 'waiting'" :data="backForm" label-width="120px">
        <t-loading :loading="backLoading">
          <t-form-item name="驳回节点">
            <t-select v-model="backForm.nodeCode" clearable placeholder="请选择" style="width: 300px">
              <t-option
                v-for="item in taskNodeList"
                :key="item.nodeCode"
                :label="item.nodeName"
                :value="item.nodeCode"
              />
            </t-select>
          </t-form-item>
          <t-form-item name="消息提醒">
            <t-checkbox-group v-model="backForm.messageType">
              <t-checkbox label="1" name="type" disabled>站内信</t-checkbox>
              <t-checkbox label="2" name="type">邮件</t-checkbox>
              <t-checkbox label="3" name="type">短信</t-checkbox>
            </t-checkbox-group>
          </t-form-item>
          <t-form-item v-if="task.flowStatus === 'waiting'" name="附件">
            <file-upload
              v-model="backForm.fileId"
              :file-type="['png', 'jpg', 'jpeg', 'doc', 'docx', 'xlsx', 'xls', 'ppt', 'txt', 'pdf']"
              :file-size="20"
            />
          </t-form-item>
          <t-form-item name="审批意见">
            <t-textarea v-model="backForm.message" />
          </t-form-item>
        </t-loading>
      </t-form>
      <template #footer>
        <div class="dialog-footer" style="float: right; padding-bottom: 20px">
          <t-button :disabled="backButtonDisabled" theme="primary" @click="handleBackProcess">确认</t-button>
          <t-button :disabled="backButtonDisabled" variant="outline" @click="backVisible = false">取消</t-button>
        </div>
      </template>
    </t-dialog>
    <!-- 驳回结束 -->
    <t-dialog
      v-model="deleteSignatureVisible"
      draggable
      header="减签人员"
      width="700px"
      height="400px"
      attach="body"
      :close-on-overlay-click="false"
    >
      <div>
        <t-table :data="deleteUserList" border :columns="deleteSignatureColumns">
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
import { AddIcon, DeleteIcon } from 'tdesign-icons-vue-next';
import type { TableProps } from 'tdesign-vue-next';
import { ref } from 'vue';

import type { SysUserVo, UserDTO } from '@/api/system/model/userModel';
import type { Node } from '@/api/workflow/model/definitionModel';
import type { FlowTaskVo, FlowTerminationBo, TaskOperationBo } from '@/api/workflow/model/taskModel';
import {
  backProcess,
  completeTask,
  currentTaskAllUser,
  getBackTaskNode,
  getTask,
  taskOperation,
  terminationTask,
} from '@/api/workflow/task';
import UserSelect from '@/components/user-select/index.vue';

const { proxy } = getCurrentInstance();

const userSelectCopyRef = ref<InstanceType<typeof UserSelect>>();
const transferTaskRef = ref<InstanceType<typeof UserSelect>>();
const delegateTaskRef = ref<InstanceType<typeof UserSelect>>();

// 加签组件
const multiInstanceUserRef = ref<InstanceType<typeof UserSelect>>();

const deleteSignatureColumns: TableProps['columns'] = [
  { title: `任务名称`, colKey: 'nodeName', align: 'center' },
  { title: `办理人`, colKey: 'nickName', align: 'center' },
  { title: `操作`, colKey: 'operation', align: 'center', width: 160 },
];

const props = defineProps({
  taskVariables: {
    type: Object,
    default: () => {},
  },
});
// 遮罩层
const loading = ref(true);
// 按钮
const buttonDisabled = ref(true);
// 任务id
const taskId = ref<string | number>('');
// 抄送人
const selectCopyUserList = ref<SysUserVo[]>([]);
// 抄送人id
const selectCopyUserIds = ref<string>(undefined);
// 可减签的人员
const deleteUserList = ref<UserDTO[]>([]);
// 驳回是否显示
const backVisible = ref(false);
const backLoading = ref(true);
const backButtonDisabled = ref(true);
// 可驳回的任务节点
const taskNodeList = ref<Node[]>([]);
const buttonObj = ref<any>({
  code: undefined,
  show: false,
});
// 任务
const task = ref<FlowTaskVo>({
  buttonList: [],
});
const dialog = reactive({
  visible: false,
  title: '提示',
});
// 减签弹窗
const deleteSignatureVisible = ref(false);
const form = ref<Record<string, any>>({
  taskId: undefined,
  message: undefined,
  variables: {},
  messageType: ['1'],
  flowCopyList: [],
});
const backForm = ref<Record<string, any>>({
  taskId: undefined,
  nodeCode: undefined,
  message: undefined,
  variables: {},
  messageType: ['1'],
});

// 打开弹窗
const openDialog = (id?: string | number) => {
  selectCopyUserIds.value = undefined;
  selectCopyUserList.value = [];
  form.value.fileId = undefined;
  taskId.value = id;
  form.value.message = undefined;
  dialog.visible = true;
  loading.value = true;
  buttonDisabled.value = true;
  nextTick(() => {
    getTask(taskId.value).then((response) => {
      task.value = response.data;
      buttonObj.value = [];
      task.value.buttonList.forEach((e) => {
        buttonObj.value[e.code] = e.show;
      });
      loading.value = false;
      buttonDisabled.value = false;
    });
  });
};

onMounted(() => {});
const emits = defineEmits(['submitCallback', 'cancelCallback']);

/** 办理流程 */
const handleCompleteTask = async () => {
  form.value.taskId = taskId.value;
  form.value.taskVariables = props.taskVariables;
  if (selectCopyUserList.value && selectCopyUserList.value.length > 0) {
    const flowCopyList: SysUserVo[] = [];
    selectCopyUserList.value.forEach((e) => {
      const copyUser = {
        userId: e.userId,
        userName: e.nickName,
      };
      flowCopyList.push(copyUser);
    });
    form.value.flowCopyList = flowCopyList;
  }
  proxy?.$modal.confirm('是否确认提交？', async () => {
    loading.value = true;
    buttonDisabled.value = true;
    try {
      await completeTask(form.value);
      dialog.visible = false;
      emits('submitCallback');
      proxy?.$modal.msgSuccess('操作成功');
    } finally {
      loading.value = false;
      buttonDisabled.value = false;
    }
  });
};

/** 驳回弹窗打开 */
const handleBackProcessOpen = async () => {
  backForm.value = {};
  backForm.value.messageType = ['1'];
  backVisible.value = true;
  backLoading.value = true;
  backButtonDisabled.value = true;
  const data = await getBackTaskNode(task.value.definitionId, task.value.nodeCode);
  taskNodeList.value = data.data;
  backLoading.value = false;
  backButtonDisabled.value = false;
  backForm.value.nodeCode = taskNodeList.value[0].nodeCode;
};
/** 驳回流程 */
const handleBackProcess = async () => {
  backForm.value.taskId = taskId.value;
  proxy?.$modal.confirm('是否确认驳回到申请人？', async () => {
    loading.value = true;
    backLoading.value = true;
    backButtonDisabled.value = true;
    await backProcess(backForm.value).finally(() => {
      loading.value = false;
      buttonDisabled.value = false;
    });
    dialog.visible = false;
    backLoading.value = false;
    backButtonDisabled.value = false;
    emits('submitCallback');
    await proxy?.$modal.msgSuccess('操作成功');
  });
};
// 取消
const cancel = async () => {
  dialog.visible = false;
  buttonDisabled.value = false;
  emits('cancelCallback');
};
// 打开抄送人员
const openUserSelectCopy = () => {
  userSelectCopyRef.value.open();
};
// 确认抄送人员
const userSelectCopyCallBack = (data: SysUserVo[]) => {
  if (data && data.length > 0) {
    selectCopyUserList.value = data;
    selectCopyUserIds.value = selectCopyUserList.value.map((item) => item.userId).join(',');
  }
};
// 删除抄送人员
const handleCopyCloseTag = (user: SysUserVo) => {
  const userId = user.userId;
  // 使用split删除用户
  const index = selectCopyUserList.value.findIndex((item) => item.userId === userId);
  selectCopyUserList.value.splice(index, 1);
  selectCopyUserIds.value = selectCopyUserList.value.map((item) => item.userId).join(',');
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
      taskId: taskId.value,
      message: form.value.message,
    });
    proxy?.$modal.confirm('是否确认提交？', async () => {
      loading.value = true;
      buttonDisabled.value = true;
      await taskOperation(taskOperationBo, 'addSignature').finally(() => {
        loading.value = false;
        buttonDisabled.value = false;
      });
      dialog.visible = false;
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
      taskId: taskId.value,
      message: form.value.message,
    });
    await taskOperation(taskOperationBo, 'reductionSignature').finally(() => {
      loading.value = false;
      buttonDisabled.value = false;
    });
    dialog.visible = false;
    emits('submitCallback');
    await proxy?.$modal.msgSuccess('操作成功');
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
      taskId: taskId.value,
      message: form.value.message,
    });
    proxy?.$modal.confirm('是否确认提交？', async () => {
      loading.value = true;
      buttonDisabled.value = true;
      await taskOperation(taskOperationBo, 'transferTask').finally(() => {
        loading.value = false;
        buttonDisabled.value = false;
      });
      dialog.visible = false;
      emits('submitCallback');
      await proxy?.$modal.msgSuccess('操作成功');
    });
  } else {
    await proxy?.$modal.msgWarning('请选择用户！');
  }
};

// 打开委托
const openDelegateTask = () => {
  delegateTaskRef.value.open();
};
// 委托
const handleDelegateTask = async (data: SysUserVo[]) => {
  if (data && data.length > 0) {
    const taskOperationBo = reactive<TaskOperationBo>({
      userId: data[0].userId,
      taskId: taskId.value,
      message: form.value.message,
    });
    proxy?.$modal.confirm('是否确认提交？', async () => {
      loading.value = true;
      buttonDisabled.value = true;
      await taskOperation(taskOperationBo, 'delegateTask').finally(() => {
        loading.value = false;
        buttonDisabled.value = false;
      });
      dialog.visible = false;
      emits('submitCallback');
      await proxy?.$modal.msgSuccess('操作成功');
    });
  } else {
    await proxy?.$modal.msgWarning('请选择用户！');
  }
};

// 终止任务
const handleTerminationTask = async () => {
  const params: FlowTerminationBo = {
    taskId: taskId.value,
    comment: form.value.message,
  };
  proxy?.$modal.confirm('是否确认终止？', async () => {
    loading.value = true;
    buttonDisabled.value = true;
    await terminationTask(params).finally(() => {
      loading.value = false;
      buttonDisabled.value = false;
    });
    dialog.visible = false;
    emits('submitCallback');
    await proxy?.$modal.msgSuccess('操作成功');
  });
};
const handleTaskUser = async () => {
  const data = await currentTaskAllUser(taskId.value);
  deleteUserList.value = data.data;
  if (deleteUserList.value && deleteUserList.value.length > 0) {
    deleteUserList.value.forEach((e) => {
      // @ts-ignore
      e.nodeName = task.value.nodeName;
    });
  }
  deleteSignatureVisible.value = true;
};

/**
 * 对外暴露子组件方法
 */
defineExpose({
  openDialog,
});
</script>
