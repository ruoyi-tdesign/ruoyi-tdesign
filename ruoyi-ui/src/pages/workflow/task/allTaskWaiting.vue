<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(6em + 12px)">
        <t-form-item label-width="0px">
          <t-badge :count="userSelectCount" :max-count="10" class="item">
            <t-button theme="primary" @click="openUserSelect">选择申请人</t-button>
          </t-badge>
        </t-form-item>
        <t-form-item label="任务名称" name="nodeName">
          <t-input v-model="queryParams.nodeName" placeholder="请输入任务名称" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="流程定义名称" name="flowName">
          <t-input v-model="queryParams.flowName" placeholder="请输入流程定义名称" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label-width="0px">
          <t-button theme="primary" @click="handleQuery">
            <template #icon> <search-icon /></template>
            搜索
          </t-button>
          <t-button theme="default" @click="resetQuery">
            <template #icon> <refresh-icon /></template>
            重置
          </t-button>
        </t-form-item>
      </t-form>

      <t-tabs v-model="tab" @tab-click="changeTab">
        <t-tab-panel value="waiting" label="待办任务"> </t-tab-panel>
        <t-tab-panel value="finish" label="已办任务"> </t-tab-panel>
        <t-table
          v-model:column-controller-visible="columnControllerVisible"
          hover
          :loading="loading"
          row-key="id"
          :data="taskList"
          :columns="columns"
          :selected-row-keys="ids"
          select-on-row-click
          :pagination="pagination"
          :column-controller="{
            hideTriggerButton: true,
          }"
          @select-change="handleSelectionChange"
        >
          <template #topContent>
            <t-row>
              <t-col flex="auto">
                <t-button
                  v-if="tab === 'waiting'"
                  theme="primary"
                  variant="outline"
                  :disabled="multiple"
                  @click="handleUpdate"
                >
                  <template #icon> <edit-icon /> </template>
                  修改办理人
                </t-button>
                <span class="selected-count">已选 {{ ids.length }} 项</span>
              </t-col>
              <t-col flex="none">
                <t-button theme="default" shape="square" variant="outline" @click="showSearch = !showSearch">
                  <template #icon> <search-icon /> </template>
                </t-button>
                <t-button theme="default" variant="outline" @click="columnControllerVisible = true">
                  <template #icon> <setting-icon /> </template>
                  列配置
                </t-button>
              </t-col>
            </t-row>
          </template>
          <template #version="{ row }">
            <span>v{{ row.version }}.0</span>
          </template>
          <template #assigneeNames="{ row }">
            <template v-if="tab === 'waiting'">
              <template v-if="row.assigneeNames">
                <t-tag
                  v-for="(name, index) in row.assigneeNames.split(',')"
                  :key="index"
                  variant="light"
                  theme="success"
                >
                  {{ name }}
                </t-tag>
              </template>
              <template v-else>
                <t-tag variant="light" theme="success"> 无</t-tag>
              </template>
            </template>
            <template v-else>
              <t-tag variant="light" theme="success"> {{ row.approveName }}</t-tag>
            </template>
          </template>
          <template #flowStatus="{ row }">
            <dict-tag v-if="tab === 'waiting'" :options="wf_business_status" :value="row.flowStatus"></dict-tag>
            <t-tag v-else variant="light" theme="success">已完成</t-tag>
          </template>
          <template #flowTaskStatus="{ row }">
            <dict-tag :options="wf_task_status" :value="row.flowTaskStatus"></dict-tag>
          </template>
          <template #operation="{ row }">
            <t-space :size="8" break-line>
              <my-link v-if="tab === 'waiting' || tab === 'finish'" @click.stop="handleView(row)">
                <template #prefix-icon><browse-icon /></template>查看
              </my-link>
              <my-link v-if="tab === 'waiting'" @click.stop="handleMeddle(row)">
                <template #prefix-icon><setting1-icon /></template>流程干预
              </my-link>
            </t-space>
          </template>
        </t-table>
      </t-tabs>
    </t-space>
    <!-- 选人组件 -->
    <user-select ref="userSelectRef" :multiple="false" @confirm-call-back="submitCallback"></user-select>
    <!-- 选人组件 -->
    <process-meddle ref="processMeddleRef" @submit-callback="getWaitingList"></process-meddle>
    <!-- 申请人 -->
    <user-select
      ref="applyUserSelectRef"
      :multiple="true"
      :data="selectUserIds"
      @confirm-call-back="userSelectCallBack"
    ></user-select>
  </t-card>
</template>

<script lang="ts" setup>
import { BrowseIcon, EditIcon, RefreshIcon, SearchIcon, Setting1Icon, SettingIcon } from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol, TabsProps } from 'tdesign-vue-next';
import { computed, ref } from 'vue';

import type { SysUserVo } from '@/api/system/model/userModel';
import type { FlowHisTaskVo, FlowTaskVo, TaskQuery } from '@/api/workflow/model/taskModel';
import type { RouterJumpVo } from '@/api/workflow/model/workflowCommonModel';
import { pageByAllTaskFinish, pageByAllTaskWait, updateAssignee } from '@/api/workflow/task';
import { useRouterJump } from '@/api/workflow/workflowCommon';
import ProcessMeddle from '@/components/Process/processMeddle.vue';
import UserSelect from '@/components/user-select/index.vue';
// 选人组件
const userSelectRef = ref<InstanceType<typeof UserSelect>>();
// 流程干预组件
const processMeddleRef = ref<InstanceType<typeof ProcessMeddle>>();
// 选人组件
const applyUserSelectRef = ref<InstanceType<typeof UserSelect>>();
const columnControllerVisible = ref(false);

const { proxy } = getCurrentInstance();
const routerJump = useRouterJump();
const { wf_business_status, wf_task_status } = proxy.useDict('wf_business_status', 'wf_task_status');

// 遮罩层
const loading = ref(true);
// 选中数组
const ids = ref<Array<any>>([]);
// 非单个禁用
const single = ref(true);
// 非多个禁用
const multiple = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 总条数
const total = ref(0);
// 模型定义表格数据
const taskList = ref<Array<FlowTaskVo | FlowHisTaskVo>>([]);
// 申请人id
const selectUserIds = ref<Array<number | string>>([]);
// 申请人选择数量
const userSelectCount = ref(0);
// 查询参数
const queryParams = ref<TaskQuery>({
  pageNum: 1,
  pageSize: 10,
  nodeName: undefined,
  flowName: undefined,
  flowCode: undefined,
  createByIds: [],
});
const tab = ref('waiting');

// 列显隐信息
const columns = computed<Array<PrimaryTableCol>>(
  () =>
    [
      { colKey: 'row-select', type: 'multiple', width: 30, align: 'center' },
      { title: `序号`, colKey: 'serial-number', width: 70 },
      { title: `流程定义名称`, colKey: 'flowName', ellipsis: true, align: 'center' },
      { title: `流程定义编码`, colKey: 'flowCode', align: 'center' },
      { title: `版本号`, colKey: 'version', align: 'center' },
      { title: `任务名称`, colKey: 'nodeName', align: 'center' },
      { title: `申请人`, colKey: 'createByName', align: 'center' },
      { title: `办理人`, colKey: 'assigneeNames', align: 'center' },
      { title: `流程状态`, colKey: 'flowStatus', align: 'center' },
      { title: `任务状态`, colKey: 'flowTaskStatus', align: 'center' },
      { title: `创建时间`, colKey: 'createTime', align: 'center', width: '10%', minWidth: 112 },
      { title: `操作`, colKey: 'operation', align: 'center', fixed: 'right' },
    ].filter((item) => {
      if (item.colKey === 'createTime') {
        return tab.value === 'waiting';
      }
      if (item.colKey === 'startTime') {
        return tab.value === 'finish';
      }
      return true;
    }) as PrimaryTableCol[],
);

const pagination = computed(() => {
  return {
    current: queryParams.value.pageNum,
    pageSize: queryParams.value.pageSize,
    total: total.value,
    showJumper: true,
    onChange: (pageInfo: PageInfo) => {
      queryParams.value.pageNum = pageInfo.current;
      queryParams.value.pageSize = pageInfo.pageSize;
      getWaitingList();
    },
  };
});

/** 搜索按钮操作 */
const handleQuery = () => {
  if (tab.value === 'waiting') {
    getWaitingList();
  } else {
    getFinishList();
  }
};
/** 重置按钮操作 */
const resetQuery = () => {
  proxy.resetForm('queryRef');
  queryParams.value.pageNum = 1;
  queryParams.value.createByIds = [];
  userSelectCount.value = 0;
  selectUserIds.value = [];
  handleQuery();
};
// 多选框选中数据
const handleSelectionChange = (selection: Array<string | number>) => {
  ids.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};
const changeTab: TabsProps['onChange'] = async (value) => {
  taskList.value = [];
  queryParams.value.pageNum = 1;
  if (value === 'waiting') {
    getWaitingList();
  } else {
    getFinishList();
  }
};
// 分页
const getWaitingList = () => {
  loading.value = true;
  pageByAllTaskWait(queryParams.value).then((resp) => {
    taskList.value = resp.rows;
    total.value = resp.total;
    loading.value = false;
  });
};
const getFinishList = () => {
  loading.value = true;
  pageByAllTaskFinish(queryParams.value).then((resp) => {
    taskList.value = resp.rows;
    total.value = resp.total;
    loading.value = false;
  });
};
// 打开修改选人
const handleUpdate = () => {
  userSelectRef.value.open();
};
// 修改办理人
const submitCallback = async (data: SysUserVo[]) => {
  if (data && data.length > 0) {
    proxy?.$modal.confirm('是否确认提交？', async () => {
      loading.value = true;
      await updateAssignee(ids.value, data[0].userId);
      handleQuery();
      proxy?.$modal.msgSuccess('操作成功');
    });
  } else {
    proxy?.$modal.msgWarning('请选择用户！');
  }
};

/** 查看按钮操作 */
const handleView = (row: FlowTaskVo | FlowHisTaskVo) => {
  const routerJumpVo = reactive<RouterJumpVo>({
    businessId: row.businessId,
    taskId: row.id,
    type: 'view',
    formCustom: row.formCustom,
    formPath: row.formPath,
  });
  routerJump(routerJumpVo);
};
const handleMeddle = (row: FlowTaskVo | FlowHisTaskVo) => {
  processMeddleRef.value.open(row.id);
};
// 打开申请人选择
const openUserSelect = () => {
  applyUserSelectRef.value.open();
};
// 确认选择申请人
const userSelectCallBack = (data: SysUserVo[]) => {
  userSelectCount.value = 0;
  if (data && data.length > 0) {
    userSelectCount.value = data.length;
    selectUserIds.value = data.map((item) => item.userId);
    queryParams.value.createByIds = selectUserIds.value;
  }
};
onMounted(() => {
  getWaitingList();
});
</script>
