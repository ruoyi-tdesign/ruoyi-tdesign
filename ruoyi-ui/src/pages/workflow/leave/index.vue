<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form
        v-show="showSearch"
        ref="queryRef"
        :data="queryParams"
        layout="inline"
        reset-type="initial"
        label-width="calc(4em + 12px)"
      >
        <t-form-item label="请假天数">
          <t-range-input v-model="rangeLeaveDays" placeholder="请输入请假天数" clearable @enter="handleQuery" />
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
        :loading="loading"
        hover
        row-key="id"
        :data="leaveList"
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
              <t-button v-hasPermi="['workflow:leave:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['workflow:leave:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['workflow:leave:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button v-hasPermi="['workflow:leave:export']" theme="default" variant="outline" @click="handleExport">
                <template #icon> <download-icon /> </template>
                导出
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
        <template #leaveType="{ row }">
          <t-tag variant="light">{{ options.find((e) => e.value === row.leaveType)?.label }}</t-tag>
        </template>
        <template #startDate="{ row }">{{ parseTime(row.startDate, '{y}-{m}-{d}') }}</template>
        <template #endDate="{ row }">{{ parseTime(row.endDate, '{y}-{m}-{d}') }}</template>
        <template #status="{ row }">
          <dict-tag :options="wf_business_status" :value="row.status"></dict-tag>
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['workflow:leave:query']" @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link
              v-if="row.status === 'draft' || row.status === 'cancel' || row.status === 'back'"
              v-hasPermi="['workflow:leave:edit']"
              @click.stop="handleUpdate(row)"
            >
              <template #prefix-icon><edit-icon /></template>修改
            </my-link>
            <my-link
              v-if="row.status === 'draft' || row.status === 'cancel' || row.status === 'back'"
              v-hasPermi="['workflow:leave:remove']"
              theme="danger"
              @click.stop="handleDelete(row)"
            >
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
            <my-link @click.stop="handleView(row)">
              <template #prefix-icon><browse-icon /></template>查看
            </my-link>
            <my-link v-if="row.status === 'waiting'" theme="warning" @click.stop="handleCancelProcessApply(row.id)">
              <template #prefix-icon><rollback-icon /></template>撤销
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 请假申请详情 -->
    <t-dialog
      v-model:visible="openView"
      header="请假申请详情"
      placement="center"
      width="min(700px, 100%)"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="请假类型">{{ form.leaveType }}</t-descriptions-item>
        <t-descriptions-item label="开始时间">{{ parseTime(form.startDate) }}</t-descriptions-item>
        <t-descriptions-item label="结束时间">{{ parseTime(form.endDate) }}</t-descriptions-item>
        <t-descriptions-item label="请假天数">{{ form.leaveDays }}</t-descriptions-item>
        <t-descriptions-item label="请假原因" :span="2">{{ form.remark }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'Leave',
});

import {
  AddIcon,
  BrowseIcon,
  DeleteIcon,
  DownloadIcon,
  EditIcon,
  RefreshIcon,
  RollbackIcon,
  SearchIcon,
  SettingIcon,
} from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import { cancelProcessApply } from '@/api/workflow/instance';
import { delLeave, getLeave, listLeave } from '@/api/workflow/leave';
import type { LeaveForm, LeaveQuery, LeaveVo } from '@/api/workflow/model/leaveModel';
import { ArrayOps } from '@/utils/array';

const { proxy } = getCurrentInstance();
const { wf_business_status } = proxy.useDict('wf_business_status');

const router = useRouter();
const openView = ref(false);
const openViewLoading = ref(false);
const leaveList = ref<LeaveVo[]>([]);
const loading = ref(false);
const columnControllerVisible = ref(false);
const showSearch = ref(true);
const total = ref(0);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
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

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `请假类型`, colKey: 'leaveType', align: 'center' },
  { title: `开始时间`, colKey: 'startDate', align: 'center', minWidth: 112, width: 180 },
  { title: `结束时间`, colKey: 'endDate', align: 'center', minWidth: 112, width: 180 },
  { title: `请假天数`, colKey: 'leaveDays', align: 'center' },
  { title: `请假原因`, colKey: 'remark', align: 'center', ellipsis: true },
  { title: `流程状态`, colKey: 'status', align: 'center' },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 提交表单对象
const form = ref<LeaveVo & LeaveForm>({});
// 查询对象
const queryParams = ref<LeaveQuery>({
  pageNum: 1,
  pageSize: 10,
});
// 分页
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

const rangeLeaveDays = computed({
  get() {
    return [queryParams.value.startLeaveDays, queryParams.value.endLeaveDays];
  },
  set(value) {
    queryParams.value.startLeaveDays = value[0];
    queryParams.value.endLeaveDays = value[1];
  },
});

/** 查询请假申请列表 */
function getList() {
  loading.value = true;
  listLeave(queryParams.value)
    .then((response) => {
      leaveList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}

// 表单重置
function reset() {
  form.value = {};
  proxy.resetForm('leaveRef');
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  rangeLeaveDays.value = [];
  proxy.resetForm('queryRef');
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection: Array<string | number>) {
  ids.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  // proxy.$tab.closePage(proxy.$route);
  router.push({
    path: `/workflow/leaveEdit/index`,
    query: {
      type: 'add',
    },
  });
}

/** 详情按钮操作 */
function handleDetail(row: LeaveVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const id = row.id;
  getLeave(id).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: LeaveVo) {
  // proxy.$tab.closePage(proxy.$route);
  router.push({
    path: `/workflow/leaveEdit/index`,
    query: {
      id: row.id,
      type: 'update',
    },
  });
}

/** 查看按钮操作 */
function handleView(row?: LeaveVo) {
  // proxy.$tab.closePage(proxy.$route);
  router.push({
    path: `/workflow/leaveEdit/index`,
    query: {
      id: row.id,
      type: 'view',
    },
  });
}

/** 删除按钮操作 */
function handleDelete(row?: LeaveVo) {
  const $ids = row?.id || ids.value;
  proxy.$modal.confirm(`是否确认删除请假申请编号为${$ids}的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delLeave($ids)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, $ids);
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => {
        proxy.$modal.msgClose(msgLoading);
      });
  });
}
/** 撤销按钮操作 */
const handleCancelProcessApply = async (id: string) => {
  proxy?.$modal.confirm('是否确认撤销当前单据？', async () => {
    loading.value = true;
    const data = {
      businessId: id,
      message: '申请人撤销流程！',
    };
    await cancelProcessApply(data).finally(() => (loading.value = false));
    getList();
    await proxy?.$modal.msgSuccess('撤销成功');
  });
};

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'workflow/leave/export',
    {
      ...queryParams.value,
    },
    `leave_${new Date().getTime()}.xlsx`,
  );
}

getList();
</script>
