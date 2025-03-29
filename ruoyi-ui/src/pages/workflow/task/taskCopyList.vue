<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(6em + 12px)">
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
        <template #version="{ row }">
          <span>v{{ row.version }}.0</span>
        </template>
        <template #flowStatus="{ row }">
          <dict-tag :options="wf_business_status" :value="row.flowStatus"></dict-tag>
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <t-link theme="primary" hover="color" @click.stop="handleView(row)"> <browse-icon />查看 </t-link>
          </t-space>
        </template>
      </t-table>
    </t-space>
  </t-card>
</template>

<script lang="ts" setup>
import { BrowseIcon, RefreshIcon, SearchIcon, SettingIcon } from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { computed, ref } from 'vue';

import type { FlowTaskVo, TaskQuery } from '@/api/workflow/model/taskModel';
import type { RouterJumpVo } from '@/api/workflow/model/workflowCommonModel';
import { pageByTaskCopy } from '@/api/workflow/task';
import { useRouterJump } from '@/api/workflow/workflowCommon';

const routerJump = useRouterJump();
const { proxy } = getCurrentInstance();
const { wf_business_status } = proxy.useDict('wf_business_status');
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
const columnControllerVisible = ref(false);
// 查询参数
const queryParams = ref<TaskQuery>({
  pageNum: 1,
  pageSize: 10,
  nodeName: undefined,
  flowName: undefined,
  flowCode: undefined,
});

// 列显隐信息
const columns = computed<Array<PrimaryTableCol>>(() => {
  return [
    { colKey: 'row-select', type: 'multiple', width: 30, align: 'center' },
    { title: `序号`, colKey: 'serial-number', width: 70 },
    { title: `流程定义名称`, colKey: 'flowName', ellipsis: true, align: 'center' },
    { title: `流程定义编码`, colKey: 'flowCode', align: 'center' },
    { title: `版本号`, colKey: 'version', align: 'center' },
    { title: `任务名称`, colKey: 'nodeName', align: 'center' },
    { title: `流程状态`, colKey: 'flowStatus', align: 'center' },
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
      getTaskCopyList();
    },
  };
});

/** 搜索按钮操作 */
const handleQuery = () => {
  getTaskCopyList();
};
/** 重置按钮操作 */
const resetQuery = () => {
  proxy.resetForm('queryRef');
  queryParams.value.pageNum = 1;
  handleQuery();
};
// 多选框选中数据
const handleSelectionChange = (selection: Array<string | number>) => {
  ids.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};
// 分页
const getTaskCopyList = () => {
  loading.value = true;
  pageByTaskCopy(queryParams.value)
    .then((resp) => {
      taskList.value = resp.rows;
      total.value = resp.total;
    })
    .finally(() => (loading.value = false));
};

/** 查看按钮操作 */
const handleView = (row: FlowTaskVo) => {
  const routerJumpVo = reactive<RouterJumpVo>({
    businessId: row.businessId,
    taskId: row.id,
    type: 'view',
    formCustom: row.formCustom,
    formPath: row.formPath,
  });
  routerJump(routerJumpVo);
};

onMounted(() => {
  getTaskCopyList();
});
</script>
