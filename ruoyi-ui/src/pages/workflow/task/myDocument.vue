<template>
  <t-card>
    <t-row :gutter="20">
      <!-- 流程分类树 -->
      <t-col :sm="2" :xs="12">
        <category-tree v-model="treeActived" @active="handleQuery" />
      </t-col>
      <t-col :lg="10" :xs="12">
        <t-space direction="vertical" style="width: 100%">
          <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(6em + 12px)">
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
            row-key="businessKey"
            :data="processInstanceList"
            :columns="columns"
            :selected-row-keys="businessIds"
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
                  <span class="selected-count">已选 {{ businessIds.length }} 项</span>
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
            <template #version="{ row }"> v{{ row.version }}.0 </template>
            <template #isSuspended="{ row }">
              <t-tag v-if="!row.isSuspended" theme="success" variant="light">激活</t-tag>
              <t-tag v-else theme="danger" variant="light">挂起</t-tag>
            </template>
            <template #flowStatus="{ row }">
              <dict-tag :options="wf_business_status" :value="row.flowStatus"></dict-tag>
            </template>
            <template #operation="{ row }">
              <t-space :size="8" break-line>
                <my-link
                  v-if="row.flowStatus === 'draft' || row.flowStatus === 'cancel' || row.flowStatus === 'back'"
                  @click.stop="handleOpen(row, 'update')"
                >
                  <template #prefix-icon><edit-icon /></template>编辑
                </my-link>
                <my-link
                  v-if="row.flowStatus === 'draft' || row.flowStatus === 'cancel' || row.flowStatus === 'back'"
                  theme="danger"
                  @click.stop="handleDelete(row)"
                >
                  <template #prefix-icon><delete-icon /></template>删除
                </my-link>
                <my-link @click.stop="handleOpen(row, 'view')">
                  <template #prefix-icon><browse-icon /></template>查看
                </my-link>
                <my-link v-if="row.flowStatus === 'waiting'" @click.stop="handleCancelProcessApply(row.businessId)">
                  <template #prefix-icon><rollback-icon /></template>撤销
                </my-link>
              </t-space>
            </template>
          </t-table>
        </t-space>
      </t-col>
    </t-row>
    <!-- 提交组件 -->
    <submit-verify @submit-callback="getList" />
  </t-card>
</template>

<script lang="ts" setup>
import {
  BrowseIcon,
  DeleteIcon,
  EditIcon,
  RefreshIcon,
  RollbackIcon,
  SearchIcon,
  SettingIcon,
} from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol, TableProps } from 'tdesign-vue-next';
import { computed, ref } from 'vue';

import { cancelProcessApply, deleteByInstanceIds, pageByCurrent } from '@/api/workflow/instance';
import type { FlowInstanceQuery, FlowInstanceVo } from '@/api/workflow/model/instanceModel';
import type { RouterJumpVo } from '@/api/workflow/model/workflowCommonModel';
import { useRouterJump } from '@/api/workflow/workflowCommon';
import SubmitVerify from '@/components/Process/submitVerify.vue';
import CategoryTree from '@/pages/workflow/category/CategoryTree.vue';

const routerJump = useRouterJump();
const { proxy } = getCurrentInstance();
const { wf_business_status } = proxy.useDict('wf_business_status');

const treeActived = ref<string[]>([]);
const columnControllerVisible = ref(false);
// 遮罩层
const loading = ref(true);
// 选中数组
const businessIds = ref<Array<number | string>>([]);
const instanceIds = ref<Array<number | string>>([]);
// 非单个禁用
const single = ref(true);
// 非多个禁用
const multiple = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 总条数
const total = ref(0);
// 模型定义表格数据
const processInstanceList = ref<FlowInstanceVo[]>([]);

const tab = ref('running');
// 查询参数
const queryParams = ref<FlowInstanceQuery>({
  pageNum: 1,
  pageSize: 10,
  flowCode: undefined,
  category: undefined,
});

// 列显隐信息
const columns = computed<Array<PrimaryTableCol>>(() => {
  return (
    [
      { colKey: 'row-select', type: 'multiple', width: 30, align: 'center' },
      { title: `序号`, colKey: 'serial-number', width: 70 },
      // { title: `id`, colKey: 'id', ellipsis: true, align: 'center' },
      { title: `流程定义名称`, colKey: 'flowName', ellipsis: true, align: 'center' },
      { title: `流程定义编码`, colKey: 'flowCode', align: 'center' },
      { title: `流程分类`, colKey: 'categoryName', align: 'center' },
      { title: `版本号`, colKey: 'version', align: 'center' },
      { title: `状态`, colKey: 'isSuspended', align: 'center' },
      { title: `流程状态`, colKey: 'flowStatus', align: 'center' },
      { title: `启动时间`, colKey: 'createTime', align: 'center', width: '10%', minWidth: 112 },
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

const pagination = computed(() => {
  return {
    current: queryParams.value.pageNum,
    pageSize: queryParams.value.pageSize,
    total: total.value,
    showJumper: true,
    onChange: (pageInfo: PageInfo) => {
      queryParams.value.pageNum = pageInfo.current;
      queryParams.value.pageSize = pageInfo.pageSize;
      getList();
    },
  };
});

onMounted(() => {
  getList();
});

/** 搜索按钮操作 */
const handleQuery = () => {
  getList();
};
/** 重置按钮操作 */
const resetQuery = () => {
  proxy.resetForm('queryRef');
  queryParams.value.pageNum = 1;
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
  businessIds.value = selectList.value.map((item) => item.businessId);
  instanceIds.value = selectList.value.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};
// 分页
const getList = () => {
  loading.value = true;
  queryParams.value.category = treeActived.value.at(0);
  pageByCurrent(queryParams.value).then((resp) => {
    processInstanceList.value = resp.rows;
    total.value = resp.total;
    loading.value = false;
  });
};

/** 删除按钮操作 */
const handleDelete = async (row: FlowInstanceVo) => {
  const instanceIdList = row.id || instanceIds.value;
  proxy?.$modal.confirm(`是否确认删除？`, async () => {
    loading.value = true;
    if (tab.value === 'running') {
      await deleteByInstanceIds(instanceIdList).finally(() => (loading.value = false));
      getList();
    }
    await proxy?.$modal.msgSuccess('删除成功');
  });
};

/** 撤销按钮操作 */
const handleCancelProcessApply = async (businessId: string) => {
  proxy?.$modal.confirm('是否确认撤销当前单据？', async () => {
    loading.value = true;
    if (tab.value === 'running') {
      const data = {
        businessId,
        message: '申请人撤销流程！',
      };
      await cancelProcessApply(data).finally(() => (loading.value = false));
      getList();
    }
    await proxy?.$modal.msgSuccess('撤销成功');
  });
};

// 办理
const handleOpen = async (row: FlowInstanceVo, type: string) => {
  const routerJumpVo = reactive<RouterJumpVo>({
    businessId: row.businessId,
    taskId: row.id,
    type,
    formCustom: row.formCustom,
    formPath: row.formPath,
  });
  routerJump(routerJumpVo);
};
</script>
