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
        <t-form-item label="部门名称" name="deptName">
          <t-input
            v-model="queryParams.deptName"
            placeholder="请输入部门名称"
            clearable
            style="width: 200px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="部门类别编码" name="deptCategory">
          <t-input v-model="queryParams.deptCategory" placeholder="请输入部门类别编码" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="queryParams.status" placeholder="部门状态" clearable style="width: 200px">
            <t-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
          </t-select>
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

      <t-enhanced-table
        ref="tableRef"
        v-model:column-controller-visible="columnControllerVisible"
        v-model:expandedTreeNodes="expandedTreeNodes"
        hover
        :loading="loading"
        :data="deptList"
        row-key="deptId"
        :columns="columns"
        :column-controller="{
          hideTriggerButton: true,
        }"
        :tree="{ childrenKey: 'children', expandTreeNodeOnClick: true }"
        :sort="sort"
        show-sort-column-bg-color
        @sort-change="handleSortChange"
      >
        <template #topContent>
          <t-row>
            <t-col flex="auto">
              <t-button v-hasPermi="['system:dept:add']" theme="primary" @click="handleAdd()">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button theme="default" variant="outline" @click="toggleExpandAll">
                <template #icon> <unfold-less-icon v-if="isExpand" /> <unfold-more-icon v-else /> </template>
                全部{{ isExpand ? '折叠' : '展开' }}
              </t-button>
              <t-button v-hasPermi="['system:dept:export']" theme="default" variant="outline" @click="handleExport">
                <template #icon> <download-icon /> </template>
                导出
              </t-button>
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
        <template #status="{ row }">
          <dict-tag :options="sys_normal_disable" :value="row.status" />
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['system:dept:query']" @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link v-hasPermi="['system:dept:edit']" @click.stop="handleUpdate(row)">
              <template #prefix-icon><edit-icon /></template>修改
            </my-link>
            <my-link v-hasPermi="['system:dept:add']" @click.stop="handleAdd(row)">
              <template #prefix-icon><add-icon /></template>新增
            </my-link>
            <my-link v-hasPermi="['system:dept:remove']" theme="danger" @click.stop="handleDelete(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-enhanced-table>
    </t-space>

    <!-- 添加或修改部门对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      width="min(700px, 100%)"
      attach="body"
      :confirm-btn="{
        loading: eLoading,
      }"
      @confirm="confirm"
    >
      <t-loading :loading="eLoading" size="small">
        <t-form
          ref="deptRef"
          :data="form"
          :rules="rules"
          label-align="right"
          label-width="calc(6em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitForm"
        >
          <t-row :gutter="[0, 20]">
            <t-col v-if="form.parentId !== 0" :span="12">
              <t-form-item label="上级部门" name="parentId">
                <t-tree-select
                  v-model="form.parentId"
                  :data="deptOptions"
                  :tree-props="{
                    keys: { value: 'deptId', label: 'deptName', children: 'children' },
                    checkStrictly: true,
                  }"
                  placeholder="选择上级部门"
                />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="部门名称" name="deptName">
                <t-input v-model="form.deptName" placeholder="请输入部门名称" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="部门类别编码" name="deptCategory">
                <t-input v-model="form.deptCategory" placeholder="请输入部门类别编码" clearable />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="显示排序" name="orderNum">
                <t-input-number v-model="form.orderNum" :min="0" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="负责人" name="leader">
                <t-select v-model="form.leader" placeholder="请选择负责人">
                  <t-option
                    v-for="item in deptUserList"
                    :key="item.userId"
                    :label="item.userName"
                    :value="item.userId"
                  />
                </t-select>
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="联系电话" name="phone">
                <t-input v-model="form.phone" placeholder="请输入联系电话" :maxlength="11" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="邮箱" name="email">
                <t-input v-model="form.email" placeholder="请输入邮箱" :maxlength="50" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="部门状态">
                <t-radio-group v-model="form.status">
                  <t-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">
                    {{ dict.label }}
                  </t-radio>
                </t-radio-group>
              </t-form-item>
            </t-col>
          </t-row>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 部门详情 -->
    <t-dialog
      v-model:visible="openView"
      header="部门详情"
      placement="center"
      width="min(700px, 100%)"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="部门id">{{ form.deptId }}</t-descriptions-item>
        <t-descriptions-item label="父部门id">{{ form.parentId }}</t-descriptions-item>
        <t-descriptions-item label="祖级列表" :span="2">{{ form.ancestors }}</t-descriptions-item>
        <t-descriptions-item label="部门名称">{{ form.deptName }}</t-descriptions-item>
        <t-descriptions-item label="部门类别编码">{{ form.deptCategory }}</t-descriptions-item>
        <t-descriptions-item label="显示顺序">{{ form.orderNum }}</t-descriptions-item>
        <t-descriptions-item label="负责人">{{ form.leader }}</t-descriptions-item>
        <t-descriptions-item label="联系电话">{{ form.phone }}</t-descriptions-item>
        <t-descriptions-item label="邮箱">{{ form.email }}</t-descriptions-item>
        <t-descriptions-item label="部门状态">
          <dict-tag :options="sys_normal_disable" :value="form.status" />
        </t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'Dept',
});
import {
  AddIcon,
  BrowseIcon,
  DeleteIcon,
  DownloadIcon,
  EditIcon,
  RefreshIcon,
  SearchIcon,
  SettingIcon,
  UnfoldLessIcon,
  UnfoldMoreIcon,
} from 'tdesign-icons-vue-next';
import type {
  EnhancedTableInstanceFunctions,
  FormInstanceFunctions,
  FormRule,
  PrimaryTableCol,
  SubmitContext,
  TableSort,
} from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import { addDept, delDept, getDept, listDept, listDeptExcludeChild, updateDept } from '@/api/system/dept';
import type { SysDeptForm, SysDeptQuery, SysDeptVo } from '@/api/system/model/deptModel';
import type { SysUserVo } from '@/api/system/model/userModel';
import { listUserByDeptId } from '@/api/system/user';

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const openView = ref(false);
const openViewLoading = ref(false);
const deptList = ref<SysDeptVo[]>([]);
const open = ref(false);
const loading = ref(true);
const eLoading = ref(true);
const showSearch = ref(true);
const title = ref('');
const deptOptions = ref<SysDeptVo[]>([]);
const sort = ref<TableSort>();
const tableRef = ref<EnhancedTableInstanceFunctions>();
const deptRef = ref<FormInstanceFunctions>();
const columnControllerVisible = ref(false);
const deptUserList = ref<SysUserVo[]>([]);
const expandedTreeNodes = ref([]);

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  parentId: [{ required: true, message: '上级部门不能为空' }],
  deptName: [{ required: true, message: '部门名称不能为空' }],
  deptCategory: [{ max: 100, message: '部门类别编码不能超过100个字符' }],
  orderNum: [{ required: true, message: '显示排序不能为空' }],
  email: [{ email: true, message: '请输入正确的邮箱地址' }],
  phone: [{ pattern: /^1[3456789][0-9]\d{8}$/, message: '请输入正确的手机号码' }],
});

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `部门名称`, colKey: 'deptName', align: 'left', width: '30%', ellipsis: true },
  { title: `排序`, colKey: 'orderNum', align: 'center', width: 25, sorter: true },
  { title: `状态`, colKey: 'status', align: 'center', width: 25 },
  { title: `创建时间`, colKey: 'createTime', align: 'center', width: '20%', minWidth: 112, sorter: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: '25%', minWidth: 180 },
]);
// 提交表单对象
const form = ref<SysDeptForm & SysDeptVo>({
  orderNum: 0,
  status: '1',
});
// 查询对象
const queryParams = ref<SysDeptQuery>({
  deptName: undefined,
  deptCategory: undefined,
  status: undefined,
});
const isExpand = computed(() => {
  return expandedTreeNodes.value.length !== 0;
});

/** 查询部门列表 */
function getList() {
  loading.value = true;
  listDept(queryParams.value)
    .then((response) => {
      deptList.value = proxy.handleTree(response.data, 'deptId', 'parentId');
    })
    .finally(() => (loading.value = false));
}
/** 查询当前部门的所有用户 */
async function getDeptAllUser(deptId: number) {
  if (deptId) {
    const res = await listUserByDeptId(deptId);
    deptUserList.value = res.data;
  }
}
/** 表单重置 */
function reset() {
  form.value = {
    orderNum: 0,
    status: '1',
  };
  proxy.resetForm('deptRef');
}
/** 搜索按钮操作 */
function handleQuery() {
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
  handleSortChange(null);
}
/** 排序触发事件 */
function handleSortChange(value?: TableSort) {
  sort.value = value;
  if (Array.isArray(value)) {
    queryParams.value.orderByColumn = value.map((item) => item.sortBy).join(',');
    queryParams.value.isAsc = value.map((item) => (item.descending ? 'descending' : 'ascending')).join(',');
  } else {
    queryParams.value.orderByColumn = value?.sortBy;
    queryParams.value.isAsc = value?.descending ? 'descending' : 'ascending';
  }
  getList();
}

/** 展开/折叠操作 */
function toggleExpandAll() {
  if (isExpand.value) {
    tableRef.value.foldAll();
  } else {
    tableRef.value.expandAll();
  }
}

/** 详情按钮操作 */
function handleDetail(row: SysDeptVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const deptId = row.deptId;
  getDept(deptId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 新增按钮操作 */
function handleAdd(row?: SysDeptVo) {
  reset();
  open.value = true;
  title.value = '添加部门';
  eLoading.value = true;
  listDept().then((response) => {
    deptOptions.value = proxy.handleTree(response.data, 'deptId');
    eLoading.value = false;
  });
  if (row) {
    form.value.parentId = row.deptId;
  }
}
/** 修改按钮操作 */
function handleUpdate(row: SysDeptVo) {
  reset();
  // 查询当前部门所有用户
  getDeptAllUser(row.deptId);
  open.value = true;
  title.value = '修改部门';
  eLoading.value = true;
  getDept(row.deptId).then((response) => {
    form.value = response.data;
    eLoading.value = false;
    listDeptExcludeChild(row.deptId).then((response) => {
      deptOptions.value = proxy.handleTree(response.data, 'deptId');
      if (deptOptions.value.length === 0) {
        deptOptions.value = [{ deptId: 0, deptName: '根部门', children: [] }];
      }
    });
  });
}
function confirm() {
  deptRef.value.submit();
}
/** 提交按钮 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.deptId) {
      updateDept(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        })
        .finally(() => proxy.$modal.msgClose(msgLoading));
    } else {
      addDept(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('新增成功');
          open.value = false;
          getList();
        })
        .finally(() => proxy.$modal.msgClose(msgLoading));
    }
  } else {
    proxy.$modal.msgError(firstError);
  }
}
/** 删除按钮操作 */
function handleDelete(row: SysDeptVo) {
  proxy.$modal.confirm(`是否确认删除名称为"${row.deptName}"的数据项?`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delDept(row.deptId)
      .then(() => {
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'system/dept/export',
    {
      ...queryParams.value,
    },
    `dept_${new Date().getTime()}.xlsx`,
  );
}

getList();
</script>
