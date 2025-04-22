<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(6em + 12px)">
        <t-form-item label-width="0px" style="min-width: 0; margin-right: 0">
          <t-badge :value="userSelectCount" :max="10" class="item">
            <t-button theme="primary" @click="openUserSelect">选择申请人</t-button>
          </t-badge>
        </t-form-item>
        <t-form-item label="任务名称" name="nodeName">
          <t-input v-model="queryParams.nodeName" placeholder="请输入任务名称" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="流程定义名称" name="flowName">
          <t-input v-model="queryParams.flowName" placeholder="请输入流程定义名称" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="流程定义编码" name="flowCode">
          <t-input v-model="queryParams.flowCode" placeholder="请输入流程定义编码" clearable @enter="handleQuery" />
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
        <template #assigneeNames="{ row }">
          <template v-if="row.assigneeNames">
            <t-tag v-for="(name, index) in row.assigneeNames.split(',')" :key="index" type="success" variant="light">
              {{ name }}
            </t-tag>
          </template>
          <template v-else>
            <t-tag type="success" variant="light"> 无</t-tag>
          </template>
        </template>
        <template #flowStatusName="{ row }">
          <dict-tag :options="wf_business_status" :value="row.flowStatus"></dict-tag>
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link @click.stop="handleOpen(row)">
              <template #prefix-icon><edit-icon /></template>办理
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>
    <!-- 申请人 -->
    <user-select ref="userSelectRef" :multiple="true" :data="selectUserIds" @confirm-call-back="userSelectCallBack" />
  </t-card>
</template>

<script lang="ts" setup>
defineOptions({
  name: 'TaskWaiting',
});
import { EditIcon, RefreshIcon, SearchIcon, SettingIcon } from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { computed, ref } from 'vue';

import type { SysUserVo } from '@/api/system/model/userModel';
import type { FlowTaskVo, TaskQuery } from '@/api/workflow//model/taskModel';
import type { RouterJumpVo } from '@/api/workflow/model/workflowCommonModel';
import { pageByTaskWait } from '@/api/workflow/task';
import { useRouterJump } from '@/api/workflow/workflowCommon';
import UserSelect from '@/components/user-select/index.vue';

const { proxy } = getCurrentInstance();
const { wf_business_status } = proxy.useDict('wf_business_status');

const userSelectRef = ref<InstanceType<typeof UserSelect>>();
const routerJump = useRouterJump();
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
const taskList = ref<FlowTaskVo[]>([]);

// 申请人id
const selectUserIds = ref<Array<number | string>>([]);
// 申请人选择数量
const userSelectCount = ref(0);
const columnControllerVisible = ref(false);
// 查询参数
const queryParams = ref<TaskQuery>({
  pageNum: 1,
  pageSize: 10,
  nodeName: undefined,
  flowName: undefined,
  flowCode: undefined,
  createByIds: [],
});

// 列显隐信息
const columns = computed<Array<PrimaryTableCol>>(() => {
  return [
    { colKey: 'row-select', type: 'multiple', width: 30, align: 'center' },
    { title: `序号`, colKey: 'serial-number', width: 70 },
    { title: `流程定义名称`, colKey: 'flowName', ellipsis: true, align: 'center' },
    { title: `流程定义编码`, colKey: 'flowCode', align: 'center' },
    { title: `流程分类`, colKey: 'categoryName', align: 'center' },
    { title: `任务名称`, colKey: 'nodeName', align: 'center' },
    { title: `申请人`, colKey: 'createByName', align: 'center' },
    { title: `办理人`, colKey: 'assigneeNames', align: 'center' },
    { title: `流程状态`, colKey: 'flowStatusName', align: 'center' },
    { title: `创建时间`, colKey: 'createTime', align: 'center', width: '10%', minWidth: 112 },
    { title: `操作`, colKey: 'operation', align: 'center', fixed: 'right' },
  ] as PrimaryTableCol[];
});

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

onMounted(() => {
  getWaitingList();
});
/** 搜索按钮操作 */
const handleQuery = () => {
  getWaitingList();
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
// 分页
const getWaitingList = () => {
  loading.value = true;
  pageByTaskWait(queryParams.value)
    .then((resp) => {
      taskList.value = resp.rows;
      total.value = resp.total;
    })
    .finally(() => (loading.value = false));
};
// 办理
const handleOpen = async (row: FlowTaskVo) => {
  const routerJumpVo = reactive<RouterJumpVo>({
    businessId: row.businessId,
    taskId: row.id,
    type: 'approval',
    formCustom: row.formCustom,
    formPath: row.formPath,
  });
  routerJump(routerJumpVo);
};
// 打开申请人选择
const openUserSelect = () => {
  userSelectRef.value.open();
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
</script>
