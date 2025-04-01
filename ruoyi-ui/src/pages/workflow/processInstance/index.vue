<template>
  <t-card>
    <t-row :gutter="20">
      <!-- 流程分类树 -->
      <t-col :sm="2" :xs="12">
        <category-tree v-model="treeActived" @active="handleQuery" />
      </t-col>

      <t-col :lg="10" :xs="12">
        <t-space direction="vertical" style="width: 100%">
          <!--          <t-card class="text-center">
            <t-radio-group v-model="tab" variant="primary-filled" @change="changeTab(tab)">
              <t-radio-button value="running">运行中</t-radio-button>
              <t-radio-button value="finish">已完成</t-radio-button>
            </t-radio-group>
          </t-card>-->

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

          <t-tabs v-model="tab" @change="changeTab">
            <t-tab-panel value="running" label="运行中"></t-tab-panel>
            <t-tab-panel value="finish" label="已完成"></t-tab-panel>
          </t-tabs>
          <t-table
            v-model:column-controller-visible="columnControllerVisible"
            hover
            :loading="loading"
            row-key="id"
            :data="processInstanceList"
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
                  <t-button theme="danger" variant="outline" :disabled="multiple" @click="handleDelete()">
                    <template #icon> <delete-icon /> </template>
                    删除
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
            <template #flowName="{ row }">
              <span>{{ row.flowName }}v{{ row.version }}.0</span>
            </template>
            <template #version="{ row }"> v{{ row.version }}.0 </template>
            <template #isSuspended="{ row }">
              <t-tag v-if="!row.isSuspended" theme="success" variant="light">激活</t-tag>
              <t-tag v-else theme="danger" variant="light">挂起</t-tag>
            </template>
            <template #flowStatus="{ row }">
              <dict-tag :options="wf_business_status" :value="row.flowStatus"></dict-tag>
            </template>
            <template #operation="{ row, rowIndex }">
              <t-space :size="8" break-line>
                <t-popup
                  :ref="`popoverRef${rowIndex}`"
                  trigger="click"
                  placement="left"
                  :overlay-style="{ width: '300px' }"
                >
                  <my-link theme="danger" @click.stop="handleInvalid(row)">
                    <template #prefix-icon><close-circle-icon /></template>作废
                  </my-link>
                  <template #content>
                    <t-textarea
                      v-model="deleteReason"
                      :autosize="{ minRows: 3, maxRows: 3 }"
                      placeholder="请输入作废原因"
                    />
                    <div style="text-align: right; margin: 5px 0 0 0">
                      <t-button size="small" variant="text" @click="cancelPopover(rowIndex)">取消</t-button>
                      <t-button size="small" theme="primary" @click="handleInvalid(row)">确认</t-button>
                    </div>
                  </template>
                </t-popup>
                <my-link theme="danger" @click.stop="handleDelete(row)">
                  <template #prefix-icon><delete-icon /></template>删除
                </my-link>
                <my-link @click.stop="handleView(row)">
                  <template #prefix-icon><browse-icon /></template>查看
                </my-link>
                <my-link @click.stop="handleInstanceVariable(row)">
                  <template #prefix-icon><adjustment-icon /></template>变量
                </my-link>
              </t-space>
            </template>
          </t-table>
        </t-space>
      </t-col>
    </t-row>
    <t-dialog
      v-if="processDefinitionDialog.visible"
      v-model:visible="processDefinitionDialog.visible"
      :header="processDefinitionDialog.title"
      width="70%"
    >
      <t-table :loading="loading" :columns="definitionColumns" :data="processDefinitionHistoryList">
        <template #version="{ row }"> v{{ row.version }}.0 </template>
        <template #suspensionState="{ row }">
          <t-tag v-if="row.suspensionState == 1" variant="outline" theme="success">激活</t-tag>
          <t-tag v-else variant="outline" theme="danger">挂起</t-tag>
        </template>
      </t-table>
    </t-dialog>
    <!-- 流程变量开始 -->
    <t-dialog v-model:visible="variableVisible" draggable header="流程变量" width="60%" :close-on-overlay-click="false">
      <t-loading :loading="variableLoading">
        <t-card class="box-card">
          <template #header>
            <div class="clearfix">
              <span>
                流程定义名称：<t-tag>{{ processDefinitionName }}</t-tag>
              </span>
            </div>
          </template>
          <div class="max-h-500px overflow-y-auto">
            <vue-json-pretty :data="formatToJsonObject(variables)" />
          </div>
        </t-card>
      </t-loading>
    </t-dialog>
    <!-- 流程变量结束 -->

    <!-- 申请人 -->
    <user-select
      ref="userSelectRef"
      :multiple="true"
      :data="selectUserIds"
      @confirm-call-back="userSelectCallBack"
    ></user-select>
  </t-card>
</template>

<script lang="ts" setup>
import 'vue-json-pretty/lib/styles.css';

import {
  AdjustmentIcon,
  BrowseIcon,
  CloseCircleIcon,
  DeleteIcon,
  RefreshIcon,
  SearchIcon,
  SettingIcon,
} from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol, TableProps, TabsProps } from 'tdesign-vue-next';
import { computed, ref } from 'vue';
import VueJsonPretty from 'vue-json-pretty';

import type { SysUserVo } from '@/api/system/model/userModel';
import { deleteByInstanceIds, instanceVariable, invalid, pageByFinish, pageByRunning } from '@/api/workflow/instance';
import type { FlowInstanceQuery, FlowInstanceVo } from '@/api/workflow/model/instanceModel';
import type { RouterJumpVo } from '@/api/workflow/model/workflowCommonModel';
import { useRouterJump } from '@/api/workflow/workflowCommon';
import UserSelect from '@/components/user-select/index.vue';
import CategoryTree from '@/pages/workflow/category/CategoryTree.vue';

const userSelectRef = ref<InstanceType<typeof UserSelect>>();
const { proxy } = getCurrentInstance();
const { wf_business_status } = proxy.useDict('wf_business_status');
const routerJump = useRouterJump();

const treeActived = ref<string[]>([]);
const columnControllerVisible = ref(false);
// 遮罩层
const loading = ref(true);
// 选中数组
const ids = ref<Array<any>>([]);
// 选中实例id数组
const instanceIds = ref<Array<number | string>>([]);
// 非单个禁用
const single = ref(true);
// 非多个禁用
const multiple = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 总条数
const total = ref(0);

// 流程变量是否显示
const variableVisible = ref(false);
const variableLoading = ref(true);
const variables = ref<string>('');
// 流程定义名称
const processDefinitionName = ref<string>();
// 模型定义表格数据
const processInstanceList = ref<FlowInstanceVo[]>([]);
const processDefinitionHistoryList = ref<Array<any>>([]);

// 列显隐信息
const columns = computed<Array<PrimaryTableCol>>(() => {
  return (
    [
      { colKey: 'row-select', type: 'multiple', width: 30, align: 'center' },
      { title: `序号`, colKey: 'serial-number', width: 70 },
      { title: `流程定义名称`, colKey: 'flowName', ellipsis: true, align: 'center' },
      { title: `任务名称`, colKey: 'nodeName', align: 'center' },
      { title: `流程定义编码`, colKey: 'flowCode', align: 'center' },
      { title: `流程分类`, colKey: 'categoryName', align: 'center' },
      { title: `申请人`, colKey: 'createByName', align: 'center' },
      { title: `版本号`, colKey: 'version', align: 'center' },
      { title: `状态`, colKey: 'isSuspended', align: 'center' },
      { title: `流程状态`, colKey: 'flowStatus', align: 'center' },
      { title: `启动时间`, colKey: 'createTime', align: 'center', width: '10%', minWidth: 112 },
      { title: `结束时间`, colKey: 'createTime', align: 'center', width: '10%', minWidth: 112 },
      { title: `操作`, colKey: 'operation', align: 'center', fixed: 'right' },
    ] as PrimaryTableCol[]
  ).filter((item) => {
    if (item.colKey === 'isSuspended') {
      return tab.value === 'running';
    }
    if (item.colKey === 'endTime') {
      return tab.value === 'finish';
    }
    return true;
  });
});
// 流程定义列显隐信息
const definitionColumns = computed<Array<PrimaryTableCol>>(() => [
  { title: `序号`, colKey: 'serial-number', width: 70 },
  { title: `流程定义名称`, colKey: 'name', align: 'center' },
  { title: `任务名称`, colKey: 'nodeName', align: 'center' },
  { title: `标识Key`, colKey: 'key', align: 'center' },
  { title: `版本号`, colKey: 'version', align: 'center' },
  { title: `状态`, colKey: 'suspensionState', align: 'center' },
  { title: `部署时间`, colKey: 'deploymentTime', align: 'center', width: '10%', minWidth: 112, ellipsis: true },
]);

const processDefinitionDialog = reactive({
  visible: false,
  title: '流程定义',
});

const tab = ref('running');
// 作废原因
const deleteReason = ref('');
// 申请人id
const selectUserIds = ref<Array<number | string>>([]);
// 申请人选择数量
const userSelectCount = ref(0);
// 查询参数
const queryParams = ref<FlowInstanceQuery>({
  pageNum: 1,
  pageSize: 10,
  nodeName: undefined,
  flowName: undefined,
  flowCode: undefined,
  createByIds: [],
  category: undefined,
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
      getProcessInstanceRunningList();
    },
  };
});

/** 搜索按钮操作 */
const handleQuery = () => {
  if (tab.value === 'running') {
    getProcessInstanceRunningList();
  } else {
    getProcessInstanceFinishList();
  }
};
/** 重置按钮操作 */
const resetQuery = () => {
  proxy.resetForm('queryRef');
  queryParams.value.pageNum = 1;
  queryParams.value.createByIds = [];
  userSelectCount.value = 0;
  treeActived.value = [];
  handleQuery();
};
const selectList = ref<any[]>();
// 多选框选中数据
const handleSelectionChange: TableProps['onSelectChange'] = (selection, options) => {
  if (options.type === 'uncheck' && options.currentRowKey === 'CHECK_ALL_BOX') {
    // 取消全选. 注：此处组件有bug，无法获取到取消全选的数据，只能通过获取到全部数据再做对比
    const ids = processInstanceList.value.map((value) => value.id);
    selectList.value = selectList.value.filter((value) => !ids.includes(value.id));
  } else {
    selectList.value = options.selectedRowData;
  }
  ids.value = selection as string[];
  instanceIds.value = selectList.value.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};
// 分页
const getProcessInstanceRunningList = () => {
  loading.value = true;
  queryParams.value.category = treeActived.value.at(0);
  pageByRunning(queryParams.value).then((resp) => {
    processInstanceList.value = resp.rows;
    total.value = resp.total;
    loading.value = false;
  });
};
// 分页
const getProcessInstanceFinishList = () => {
  loading.value = true;
  queryParams.value.category = treeActived.value.at(0);
  pageByFinish(queryParams.value).then((resp) => {
    processInstanceList.value = resp.rows;
    total.value = resp.total;
    loading.value = false;
  });
};

/** 删除按钮操作 */
const handleDelete = async (row?: FlowInstanceVo) => {
  const instanceIdList = row.id || instanceIds.value;
  proxy?.$modal.confirm(`是否确认删除？`, async () => {
    loading.value = true;
    if (tab.value === 'running') {
      await deleteByInstanceIds(instanceIdList).finally(() => (loading.value = false));
      getProcessInstanceRunningList();
    } else {
      await deleteByInstanceIds(instanceIdList).finally(() => (loading.value = false));
      getProcessInstanceFinishList();
    }
    await proxy?.$modal.msgSuccess('删除成功');
  });
};
const changeTab: TabsProps['onChange'] = (value) => {
  processInstanceList.value = [];
  queryParams.value.pageNum = 1;
  if (value === 'running') {
    getProcessInstanceRunningList();
  } else {
    getProcessInstanceFinishList();
  }
};
/** 作废按钮操作 */
const handleInvalid = async (row: FlowInstanceVo) => {
  proxy?.$modal.confirm(`是否确认作废？`, async () => {
    loading.value = true;
    if (tab.value === 'running') {
      const param = {
        id: row.id,
        comment: deleteReason.value,
      };
      await invalid(param).finally(() => (loading.value = false));
      getProcessInstanceRunningList();
      await proxy?.$modal.msgSuccess('操作成功');
    }
  });
};
const cancelPopover = async (index: any) => {
  (proxy?.$refs[`popoverRef${index}`] as any).hide(); // 关闭弹窗
};

/** 查看按钮操作 */
const handleView = (row: FlowInstanceVo) => {
  const routerJumpVo = reactive<RouterJumpVo>({
    businessId: row.businessId,
    taskId: row.id,
    type: 'view',
    formCustom: row.formCustom,
    formPath: row.formPath,
  });
  routerJump(routerJumpVo);
};

// 查询流程变量
const handleInstanceVariable = async (row: FlowInstanceVo) => {
  variableLoading.value = true;
  variableVisible.value = true;
  processDefinitionName.value = row.flowName;
  const data = await instanceVariable(row.id);
  variables.value = data.data.variable;
  variableLoading.value = false;
};

/**
 * json转为对象
 * @param data 原始数据
 */
function formatToJsonObject(data: string) {
  try {
    return JSON.parse(data);
  } catch (error) {
    return data;
  }
}

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
onMounted(() => {
  getProcessInstanceRunningList();
});
</script>
