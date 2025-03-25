<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="套餐名称" name="packageName">
          <t-input v-model="queryParams.packageName" placeholder="请输入套餐名称" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="queryParams.status" placeholder="请选择状态" clearable>
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

      <t-table
        v-model:column-controller-visible="columnControllerVisible"
        :loading="loading"
        hover
        row-key="packageId"
        :data="tenantPackageList"
        :columns="columns"
        :selected-row-keys="ids"
        select-on-row-click
        :pagination="pagination"
        :column-controller="{
          hideTriggerButton: true,
        }"
        :sort="sort"
        show-sort-column-bg-color
        @sort-change="handleSortChange"
        @select-change="handleSelectionChange"
      >
        <template #topContent>
          <t-row>
            <t-col flex="auto">
              <t-button v-hasPermi="['system:tenantPackage:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:tenantPackage:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:tenantPackage:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button
                v-hasPermi="['system:tenantPackage:export']"
                theme="default"
                variant="outline"
                @click="handleExport"
              >
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
        <template #status="{ row }">
          <t-switch v-model="row.status" :custom-value="['1', '0']" @click.stop @change="handleStatusChange(row)" />
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['system:tenantPackage:query']" @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link v-hasPermi="['system:tenantPackage:edit']" @click.stop="handleUpdate(row)">
              <template #prefix-icon><edit-icon /></template>修改
            </my-link>
            <my-link v-hasPermi="['system:tenantPackage:remove']" theme="danger" @click.stop="handleDelete(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 添加或修改租户套餐对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      placement="center"
      width="700px"
      attach="body"
      :confirm-btn="{
        loading: buttonLoading,
      }"
      @confirm="onConfirm"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="tenantPackageRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(4em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitForm"
        >
          <t-form-item label="套餐名称" name="packageName">
            <t-input v-model="form.packageName" placeholder="请输入套餐名称" />
          </t-form-item>
          <t-form-item label="关联菜单" name="menuIds">
            <t-loading :loading="loadingTree" size="small">
              <t-space direction="vertical">
                <t-space>
                  <t-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event, 'menu')"
                    >展开/折叠</t-checkbox
                  >
                  <t-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event, 'menu')">
                    全选/全不选
                  </t-checkbox>
                  <t-checkbox v-model="form.menuCheckStrictly">父子联动</t-checkbox>
                </t-space>
                <t-tree
                  ref="menuRef"
                  v-model="menuIds"
                  class="tree-border"
                  style="padding: 0 10px"
                  :data="menuOptions"
                  checkable
                  :expanded="menuExpandNode"
                  :check-strictly="!form.menuCheckStrictly"
                  empty="加载中，请稍候"
                  :keys="{ value: 'id', label: 'label', children: 'children' }"
                  @expand="onExpand('menu', $event)"
                ></t-tree>
              </t-space>
            </t-loading>
          </t-form-item>
          <t-form-item label="备注" name="remark">
            <t-textarea v-model="form.remark" placeholder="请输入备注" />
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 租户套餐详情 -->
    <t-dialog
      v-model:visible="openView"
      header="租户套餐详情"
      placement="center"
      width="700px"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="租户套餐id">{{ form.packageId }}</t-descriptions-item>
        <t-descriptions-item label="套餐名称">{{ form.packageName }}</t-descriptions-item>
        <t-descriptions-item label="菜单树是否关联显示" :span="2">{{ form.menuCheckStrictly }}</t-descriptions-item>
        <t-descriptions-item label="状态">
          <dict-tag :options="sys_normal_disable" :value="form.status" />
        </t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ form.remark }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'TenantPackage',
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
} from 'tdesign-icons-vue-next';
import type {
  FormInstanceFunctions,
  FormRule,
  PageInfo,
  PrimaryTableCol,
  SubmitContext,
  TableSort,
  TreeInstanceFunctions,
  TreeNodeValue,
} from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import type { TreeModel } from '@/api/model/resultModel';
import { menuTreeSelect, tenantPackageMenuTreeSelect } from '@/api/system/menu';
import type {
  SysTenantPackageForm,
  SysTenantPackageQuery,
  SysTenantPackageVo,
} from '@/api/system/model/tenantPackageModel';
import {
  addTenantPackage,
  changePackageStatus,
  delTenantPackage,
  getTenantPackage,
  listTenantPackage,
  updateTenantPackage,
} from '@/api/system/tenantPackage';
import { ArrayOps } from '@/utils/array';
import { handleChangeStatus } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const tenantPackageList = ref<SysTenantPackageVo[]>([]);
const tenantPackageRef = ref<FormInstanceFunctions>();
const open = ref(false);
const openView = ref(false);
const openViewLoading = ref(false);
const buttonLoading = ref(false);
const loadingTree = ref(false);
const loading = ref(false);
const columnControllerVisible = ref(false);
const showSearch = ref(true);
const total = ref(0);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const sort = ref<TableSort>();
const title = ref('');
const menuRef = ref<TreeInstanceFunctions>();
const menuIds = ref<number[]>([]);
const menuExpand = ref(false);
const menuNodeAll = ref(false);
const menuOptions = ref<TreeModel<number>[]>([]);
const menuExpandNode = ref<TreeNodeValue[]>([]);

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  packageName: [{ required: true, message: '套餐名称不能为空' }],
});
// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `套餐名称`, colKey: 'packageName', align: 'center' },
  { title: `状态`, colKey: 'status', align: 'center', sorter: true },
  { title: `备注`, colKey: 'remark', align: 'center', ellipsis: true },
  { title: `更新时间`, colKey: 'updateTime', align: 'center', width: '15%', minWidth: 112 },
  { title: `创建时间`, colKey: 'createTime', align: 'center', width: '15%', minWidth: 112, sorter: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: '15%', minWidth: 180 },
]);
// 提交表单对象
const form = ref<SysTenantPackageVo & SysTenantPackageForm>({
  menuIds: [],
});
// 查询对象
const queryParams = ref<SysTenantPackageQuery>({
  pageNum: 1,
  pageSize: 10,
  packageName: undefined,
  status: undefined,
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

/** 查询菜单树结构 */
function getMenuTreeSelect() {
  menuTreeSelect().then((response) => {
    menuOptions.value = response.data;
  });
}

/** 根据租户套餐ID查询菜单树结构 */
function getPackageMenuTreeSelect(packageId: number) {
  return tenantPackageMenuTreeSelect(packageId).then((response) => {
    menuOptions.value = response.data.menus;
    return response;
  });
}

/** 查询租户套餐列表 */
function getList() {
  loading.value = true;
  listTenantPackage(queryParams.value)
    .then((response) => {
      tenantPackageList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}

function onExpand(type: string, value: TreeNodeValue[]) {
  if (type === 'menu') {
    menuExpandNode.value = value;
  }
}

// 表单重置
function reset() {
  menuIds.value = [];
  menuExpand.value = false;
  menuNodeAll.value = false;
  form.value = {
    status: '1',
    menuIds: [],
  };
  proxy.resetForm('tenantPackageRef');
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
  queryParams.value.pageNum = 1;
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

/** 多选框选中数据 */
function handleSelectionChange(selection: Array<string | number>) {
  ids.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

// 租户套餐状态修改
function handleStatusChange(row: SysTenantPackageVo) {
  handleChangeStatus(tenantPackageList.value, row, 'packageId', 'status', `${row.packageName}套餐`, (data) =>
    changePackageStatus(data.packageId, data.status).then(() => getList()),
  );
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加租户套餐';
  getMenuTreeSelect();
}

/** 详情按钮操作 */
function handleDetail(row: SysTenantPackageVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const packageId = row.packageId;
  getTenantPackage(packageId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
async function handleUpdate(row?: SysTenantPackageVo) {
  buttonLoading.value = true;
  loadingTree.value = true;
  reset();
  open.value = true;
  title.value = '修改租户套餐';
  const packageId = row?.packageId || ids.value.at(0);
  await getTenantPackage(packageId).then((response) => {
    form.value = response.data;
  });
  await getPackageMenuTreeSelect(packageId).then((res) => {
    menuIds.value = res.data.checkedKeys;
    loadingTree.value = false;
  });
  buttonLoading.value = false;
}

/** 树权限（展开/折叠） */
function handleCheckedTreeExpand(value: boolean, type: string) {
  if (type === 'menu') {
    if (value) {
      menuExpandNode.value = menuOptions.value.map((row) => row.id);
    } else {
      menuExpandNode.value = [];
    }
  }
}
/** 树权限（全选/全不选） */
function handleCheckedTreeNodeAll(value: boolean, type: string) {
  if (type === 'menu') {
    if (value) {
      menuIds.value = menuRef.value.getItems().map((item) => item.value as number);
    } else {
      menuIds.value = [];
    }
  }
}
/** 所有菜单节点数据 */
function getMenuAllCheckedKeys() {
  const items = menuRef.value.getItems();
  return items.filter((item) => item.checked || item.indeterminate).map((item) => item.value as number);
}

/** 提交按钮 */
function onConfirm() {
  tenantPackageRef.value.submit();
}

/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.packageId) {
      form.value.menuIds = getMenuAllCheckedKeys();
      updateTenantPackage(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        })
        .finally(() => {
          buttonLoading.value = false;
          proxy.$modal.msgClose(msgLoading);
        });
    } else {
      form.value.menuIds = getMenuAllCheckedKeys();
      addTenantPackage(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('新增成功');
          open.value = false;
          getList();
        })
        .finally(() => {
          buttonLoading.value = false;
          proxy.$modal.msgClose(msgLoading);
        });
    }
  } else {
    proxy.$modal.msgError(firstError);
  }
}

/** 删除按钮操作 */
function handleDelete(row?: SysTenantPackageVo) {
  const packageIds = row?.packageId || ids.value;
  proxy.$modal.confirm(`是否确认删除租户套餐编号为${packageIds}的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delTenantPackage(packageIds)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, packageIds);
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => {
        proxy.$modal.msgClose(msgLoading);
      });
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'system/tenant/package/export',
    {
      ...queryParams.value,
    },
    `tenantPackage_${new Date().getTime()}.xlsx`,
  );
}

getList();
</script>
