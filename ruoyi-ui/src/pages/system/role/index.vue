<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="角色名称" name="roleName">
          <t-input
            v-model="queryParams.roleName"
            placeholder="请输入角色名称"
            clearable
            style="width: 240px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="权限字符" name="roleKey">
          <t-input
            v-model="queryParams.roleKey"
            placeholder="请输入权限字符"
            clearable
            style="width: 240px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="queryParams.status" placeholder="角色状态" clearable style="width: 240px">
            <t-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
          </t-select>
        </t-form-item>
        <t-form-item label="创建时间" style="width: 340px">
          <t-date-range-picker
            v-model="dateRange"
            allow-input
            clearable
            separator="-"
            :placeholder="['开始日期', '结束日期']"
          />
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

      <!-- 表格数据 -->
      <t-table
        v-model:column-controller-visible="columnControllerVisible"
        :loading="loading"
        hover
        row-key="roleId"
        :data="roleList"
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
              <t-button v-hasPermi="['system:role:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:role:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:role:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button v-hasPermi="['system:role:export']" theme="default" variant="outline" @click="handleExport">
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
          <t-switch
            v-model="row.status"
            :custom-value="['1', '0']"
            @click.stop
            @change="handleStatusChange(row)"
          ></t-switch>
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <t-link v-hasPermi="['system:role:query']" theme="primary" hover="color" @click.stop="handleDetail(row)">
              <browse-icon />详情
            </t-link>
            <t-link
              v-if="row.roleId !== 1"
              v-hasPermi="['system:role:edit']"
              theme="primary"
              hover="color"
              @click.stop="handleUpdate(row)"
            >
              <edit-icon />修改
            </t-link>
            <t-link
              v-if="row.roleId !== 1"
              v-hasPermi="['system:role:remove']"
              theme="danger"
              hover="color"
              @click.stop="handleDelete(row)"
            >
              <delete-icon />删除
            </t-link>
            <t-link
              v-if="row.roleId !== 1"
              v-hasPermi="['system:role:edit']"
              theme="primary"
              hover="color"
              @click.stop="handleDataScope(row)"
            >
              <check-circle-icon />数据权限
            </t-link>
            <t-link
              v-if="row.roleId !== 1"
              v-hasPermi="['system:role:edit']"
              theme="primary"
              hover="color"
              @click.stop="handleAuthUser(row)"
            >
              <user-icon />分配用户
            </t-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 添加或修改角色配置对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      placement="center"
      :close-on-overlay-click="false"
      width="600px"
      attach="body"
      :confirm-btn="{
        loading: loadingEdit,
      }"
      @close="cancel"
      @confirm="confirm('menu')"
    >
      <t-loading :loading="loadingEdit" size="small">
        <t-form
          ref="roleRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(5em + 41px)"
          @submit="submitForm"
        >
          <t-form-item label="角色名称" name="roleName">
            <t-input v-model="form.roleName" placeholder="请输入角色名称" />
          </t-form-item>
          <t-form-item name="roleKey">
            <template #label>
              <span>
                <t-tooltip content="控制器中定义的权限字符，如：@SaCheckRole('admin')" placement="top">
                  <help-circle-filled-icon />
                </t-tooltip>
                权限字符
              </span>
            </template>
            <t-input v-model="form.roleKey" placeholder="请输入权限字符" />
          </t-form-item>
          <t-form-item label="角色顺序" name="roleSort">
            <t-input-number v-model="form.roleSort" :min="0" />
          </t-form-item>
          <t-form-item label="状态">
            <t-radio-group v-model="form.status">
              <t-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">
                {{ dict.label }}
              </t-radio>
            </t-radio-group>
          </t-form-item>
          <t-form-item label="菜单权限">
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
                :data="menuOptions"
                :expanded="menuExpandNode"
                checkable
                :check-strictly="!form.menuCheckStrictly"
                empty="加载中，请稍候"
                :keys="{ value: 'id', label: 'label', children: 'children' }"
                @expand="onExpand('menu', $event)"
              ></t-tree>
            </t-space>
          </t-form-item>
          <t-form-item label="备注">
            <t-textarea v-model="form.remark" placeholder="请输入备注"></t-textarea>
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 分配角色数据权限对话框 -->
    <t-dialog
      v-model:visible="openDataScope"
      :header="title"
      :close-on-overlay-click="false"
      placement="center"
      width="500px"
      attach="body"
      :confirm-btn="{
        loading: loadingEdit,
      }"
      @close="cancelDataScope"
      @confirm="confirm('dept')"
    >
      <t-loading :loading="loadingEdit" size="small">
        <t-form ref="dataScopeRef" :data="form" label-width="calc(4em + 12px)" @submit="submitDataScope">
          <t-form-item label="角色名称">
            <t-input v-model="form.roleName" :disabled="true" />
          </t-form-item>
          <t-form-item label="权限字符">
            <t-input v-model="form.roleKey" :disabled="true" />
          </t-form-item>
          <t-form-item label="权限范围">
            <t-select v-model="form.dataScope" @change="dataScopeSelectChange($event as string)">
              <t-option
                v-for="item in dataScopeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></t-option>
            </t-select>
          </t-form-item>
          <t-form-item v-show="form.dataScope === '2'" label="数据权限">
            <t-space direction="vertical">
              <t-space>
                <t-checkbox v-model="deptExpand" @change="handleCheckedTreeExpand($event, 'dept')"
                  >展开/折叠</t-checkbox
                >
                <t-checkbox v-model="deptNodeAll" @change="handleCheckedTreeNodeAll($event, 'dept')">
                  全选/全不选
                </t-checkbox>
                <t-checkbox v-model="form.deptCheckStrictly">父子联动</t-checkbox>
              </t-space>
              <t-tree
                ref="deptRef"
                v-model="deptIds"
                class="tree-border"
                :data="deptOptions"
                checkable
                :expanded="deptExpandNode"
                expand-all
                :check-strictly="!form.deptCheckStrictly"
                empty="加载中，请稍候"
                :keys="{ value: 'id', label: 'label', children: 'children' }"
                @expand="onExpand('dept', $event)"
              ></t-tree>
            </t-space>
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 角色信息详情 -->
    <t-dialog
      v-model:visible="openView"
      header="角色信息详情"
      placement="center"
      width="800px"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="角色ID">{{ form.roleId }}</t-descriptions-item>
        <t-descriptions-item label="角色名称">{{ form.roleName }}</t-descriptions-item>
        <t-descriptions-item label="角色权限字符串">{{ form.roleKey }}</t-descriptions-item>
        <t-descriptions-item label="显示顺序">{{ form.roleSort }}</t-descriptions-item>
        <t-descriptions-item label="数据范围">
          <dict-tag :options="dataScopeOptions" theme="primary" :value="form.dataScope" />
        </t-descriptions-item>
        <t-descriptions-item label="角色状态">
          <dict-tag :options="sys_normal_disable" :value="form.status" />
        </t-descriptions-item>
        <t-descriptions-item label="菜单树选择项是否关联显示" :span="2">
          {{ form.menuCheckStrictly }}
        </t-descriptions-item>
        <t-descriptions-item label="部门树选择项是否关联显示" :span="2">
          {{ form.deptCheckStrictly }}
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
  name: 'Role',
});
import {
  AddIcon,
  BrowseIcon,
  CheckCircleIcon,
  DeleteIcon,
  DownloadIcon,
  EditIcon,
  HelpCircleFilledIcon,
  RefreshIcon,
  SearchIcon,
  SettingIcon,
  UserIcon,
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
import { useRouter } from 'vue-router';

import type { TreeModel } from '@/api/model/resultModel';
import { menuTreeSelect, roleMenuTreeSelect } from '@/api/system/menu';
import type { SysRoleForm, SysRoleQuery, SysRoleVo } from '@/api/system/model/roleModel';
import {
  addRole,
  changeRoleStatus,
  dataScope,
  delRole,
  deptTreeSelect,
  getRole,
  listRole,
  updateRole,
} from '@/api/system/role';
import { ArrayOps } from '@/utils/array';
import { handleChangeStatus } from '@/utils/ruoyi';

const router = useRouter();
const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const openView = ref(false);
const openViewLoading = ref(false);
const roleList = ref<SysRoleVo[]>([]);
const open = ref(false);
const loading = ref(true);
const loadingEdit = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const dateRange = ref([]);
const menuOptions = ref<TreeModel<number>[]>([]);
const menuExpand = ref(false);
const menuNodeAll = ref(false);
const deptExpand = ref(true);
const deptNodeAll = ref(false);
const deptOptions = ref<TreeModel<number>[]>([]);
const openDataScope = ref(false);
const menuRef = ref<TreeInstanceFunctions>();
const deptRef = ref<TreeInstanceFunctions>();
const roleRef = ref<FormInstanceFunctions>();
const dataScopeRef = ref<FormInstanceFunctions>();
const menuIds = ref<number[]>([]);
const deptIds = ref<number[]>([]);
const menuExpandNode = ref<TreeNodeValue[]>([]);
const deptExpandNode = ref<TreeNodeValue[]>([]);
const columnControllerVisible = ref(false);
const sort = ref<TableSort>();

/** 数据范围选项 */
const dataScopeOptions = ref([
  { value: '1', label: '全部数据权限' },
  { value: '2', label: '自定数据权限' },
  { value: '3', label: '本部门数据权限' },
  { value: '4', label: '本部门及以下数据权限' },
  { value: '5', label: '仅本人数据权限' },
  { value: '6', label: '部门及以下或本人数据权限' },
]);
// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `角色编号`, colKey: 'roleId', align: 'center', width: 120 },
  { title: `角色名称`, colKey: 'roleName', ellipsis: true, align: 'center', width: 150 },
  { title: `权限字符`, colKey: 'roleKey', ellipsis: true, align: 'center', width: 150 },
  { title: `显示顺序`, colKey: 'roleSort', align: 'center', width: 120, sorter: true },
  { title: `状态`, colKey: 'status', align: 'center', width: 100 },
  { title: `创建时间`, colKey: 'createTime', align: 'center', sorter: true, width: '15%', minWidth: 112 },
  { title: `操作`, colKey: 'operation', align: 'center', width: '10%', minWidth: 185 },
]);

const rules = ref<Record<string, Array<FormRule>>>({
  roleName: [{ required: true, message: '角色名称不能为空' }],
  roleKey: [{ required: true, message: '权限字符不能为空' }],
  roleSort: [{ required: true, message: '角色顺序不能为空' }],
});
// 提交表单对象
const form = ref<SysRoleVo & SysRoleForm>({
  roleSort: 0,
  status: '1',
  menuIds: [],
  deptIds: [],
});
// 查询对象
const queryParams = ref<SysRoleQuery>({
  pageNum: 1,
  pageSize: 10,
  roleName: undefined,
  roleKey: undefined,
  status: undefined,
  createTime: undefined,
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
/** 查询角色列表 */
function getList() {
  loading.value = true;
  listRole(proxy.addDateRange(queryParams.value, dateRange.value)).then((response) => {
    roleList.value = response.rows;
    total.value = response.total;
    loading.value = false;
    handleSelectionChange([]);
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
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

/** 删除按钮操作 */
function handleDelete(row?: SysRoleVo) {
  const roleIds = row?.roleId || ids.value;
  proxy.$modal.confirm(`是否确认删除角色编号为"${roleIds}"的数据项?`, () => {
    return delRole(roleIds).then(() => {
      ids.value = ArrayOps.fastDeleteElement(ids.value, roleIds);
      getList();
      proxy.$modal.msgSuccess('删除成功');
    });
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'system/role/export',
    {
      ...proxy.addDateRange(queryParams.value, dateRange.value),
    },
    `role_${new Date().getTime()}.xlsx`,
  );
}

/** 多选框选中数据 */
function handleSelectionChange(selection: Array<string | number>) {
  ids.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 角色状态修改 */
function handleStatusChange(row: SysRoleVo) {
  handleChangeStatus(roleList.value, row, 'roleId', 'status', `${row.roleName}角色`, (row1) =>
    changeRoleStatus(row1.roleId, row1.status).then(() => getList()),
  );
}
/** 分配用户 */
function handleAuthUser(row: SysRoleVo) {
  router.push(`/system/role-auth/user/${row.roleId}`);
}
/** 查询菜单树结构 */
async function getMenuTreeSelect() {
  return menuTreeSelect().then((response) => {
    menuOptions.value = response.data;
  });
}
/** 所有部门节点数据 */
function getDeptAllCheckedKeys() {
  const items = deptRef.value.getItems();
  return items.filter((item) => item.indeterminate || item.checked).map((item) => item.value as number);
}
/** 重置新增的表单以及其他数据  */
function reset() {
  menuIds.value = [];
  deptIds.value = [];
  menuExpand.value = false;
  menuNodeAll.value = false;
  deptExpand.value = true;
  deptNodeAll.value = false;
  form.value = {
    roleId: undefined,
    roleName: undefined,
    roleKey: undefined,
    roleSort: 0,
    status: '1',
    menuIds: [],
    deptIds: [],
    remark: undefined,
    dataScope: undefined,
    menuCheckStrictly: true,
    deptCheckStrictly: true,
  };
  proxy.resetForm('roleRef');
}
/** 详情按钮操作 */
function handleDetail(row: SysRoleVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const roleId = row.roleId;
  getRole(roleId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}
/** 添加角色 */
async function handleAdd() {
  reset();
  open.value = true;
  loadingEdit.value = true;
  await getMenuTreeSelect();
  loadingEdit.value = false;
  title.value = '添加角色';
}
/** 修改角色 */
function handleUpdate(row?: SysRoleVo) {
  reset();
  const roleId = row?.roleId || ids.value.at(0);
  const roleMenu = getRoleMenuTreeSelect(roleId);
  open.value = true;
  loadingEdit.value = true;
  title.value = '修改角色';
  getRole(roleId).then((response) => {
    form.value = response.data;
    form.value.roleSort = form.value.roleSort as number;
    roleMenu.then((res) => {
      const { checkedKeys } = res.data;
      menuIds.value = checkedKeys;
    });
    loadingEdit.value = false;
  });
}
/** 根据角色ID查询菜单树结构 */
function getRoleMenuTreeSelect(roleId: number) {
  return roleMenuTreeSelect(roleId).then((response) => {
    menuOptions.value = response.data.menus;
    return response;
  });
}
/** 根据角色ID查询部门树结构 */
function getDeptTree(roleId: number) {
  return deptTreeSelect(roleId).then((response) => {
    deptOptions.value = response.data.depts;
    return response;
  });
}
/** 树权限（展开/折叠） */
function handleCheckedTreeExpand(value: boolean, type: string) {
  if (type === 'menu') {
    if (value) {
      menuExpandNode.value = menuOptions.value.map((row) => row.id);
    } else {
      menuExpandNode.value = [];
    }
  } else if (type === 'dept') {
    if (value) {
      deptExpandNode.value = deptOptions.value.map((row) => row.id);
    } else {
      deptExpandNode.value = [];
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
    // menuRef.value.setCheckedNodes(value ? menuOptions.value : []);
  } else if (type === 'dept') {
    if (value) {
      deptIds.value = deptRef.value.getItems().map((item) => item.value as number);
    } else {
      deptIds.value = [];
    }
  }
}
function onExpand(type: string, value: TreeNodeValue[]) {
  if (type === 'menu') {
    menuExpandNode.value = value;
  } else if (type === 'dept') {
    deptExpandNode.value = value;
  }
}
/** 所有菜单节点数据 */
function getMenuAllCheckedKeys() {
  const items = menuRef.value.getItems();
  return items.filter((item) => item.checked || item.indeterminate).map((item) => item.value as number);
}
function confirm(type: string) {
  if (type === 'menu') {
    roleRef.value.submit();
  } else if (type === 'dept') {
    dataScopeRef.value.submit();
  }
}
/** 提交按钮 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.roleId) {
      form.value.menuIds = getMenuAllCheckedKeys();
      updateRole(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        })
        .finally(() => proxy.$modal.msgClose(msgLoading));
    } else {
      form.value.menuIds = getMenuAllCheckedKeys();
      addRole(form.value)
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
/** 取消按钮 */
function cancel() {
  reset();
}
/** 选择角色权限范围触发 */
function dataScopeSelectChange(value: string) {
  if (value !== '2') {
    deptIds.value = [];
  }
}
/** 分配数据权限操作 */
function handleDataScope(row: SysRoleVo) {
  reset();
  const deptTreeSelect = getDeptTree(row.roleId);
  openDataScope.value = true;
  loadingEdit.value = true;
  getRole(row.roleId).then((response) => {
    form.value = { ...form.value, ...response.data };
    deptTreeSelect.then((res) => {
      deptIds.value = res.data.checkedKeys;
    });
    title.value = '分配数据权限';
    loadingEdit.value = false;
  });
}
/** 提交按钮（数据权限） */
function submitDataScope() {
  if (form.value.roleId) {
    form.value.deptIds = getDeptAllCheckedKeys();
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    dataScope(form.value)
      .then(() => {
        proxy.$modal.msgSuccess('修改成功');
        openDataScope.value = false;
        getList();
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  }
}
/** 取消按钮（数据权限） */
function cancelDataScope() {
  openDataScope.value = false;
  reset();
}

getList();
</script>
