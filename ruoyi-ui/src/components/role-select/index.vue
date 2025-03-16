<template>
  <t-dialog
    v-model:visible="roleDialog.visible.value"
    :header="roleDialog.title.value"
    width="80%"
    :close-on-overlay-click="false"
    placement="center"
    attach="body"
    @opened="handleOpened"
    @confirm="onSubmit"
  >
    <t-space direction="vertical" style="width: 100%">
      <t-card hover-shadow>
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
      </t-card>

      <!-- 表格数据 -->
      <t-card hover-shadow header-bordered>
        <template #header>
          <t-space break-line size="4px">
            <t-tag
              v-for="(role, index) in selectRoleList"
              :key="role.roleId"
              theme="primary"
              variant="light"
              closable
              class="mb-2px"
              @close="handleCloseTag(index)"
            >
              {{ role.roleName }}
            </t-tag>
          </t-space>
        </template>
        <t-table
          v-model:column-controller-visible="columnControllerVisible"
          :loading="loading"
          hover
          row-key="roleId"
          :data="roleList"
          :columns="columns"
          :selected-row-keys="roleIds"
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
              <t-col flex="auto"> </t-col>
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
        </t-table>
      </t-card>
    </t-space>
  </t-dialog>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'RoleSelect',
});

import { RefreshIcon, SearchIcon, SettingIcon } from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol, TableProps, TableSort } from 'tdesign-vue-next';
import { computed, getCurrentInstance, type PropType, ref } from 'vue';

import type { SysRoleQuery, SysRoleVo } from '@/api/system/model/roleModel';
import { listRole, roleOptionSelect } from '@/api/system/role';
import useDialog from '@/hooks/useDialog';

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const props = defineProps({
  multiple: {
    type: Boolean,
    default: true,
  },
  data: [String, Number, Array] as PropType<string | number | Array<string | number>>,
});

const modelValue = defineModel<SysRoleVo[]>({
  default: () => [] as SysRoleVo[],
});

const emit = defineEmits<{
  (e: 'confirmCallBack', value: SysRoleVo[]): void;
}>();

const computedIds = (data: string | number | Array<string | number>) => {
  if (data instanceof Array) {
    return [...data];
  }
  if (typeof data === 'string') {
    return data.split(',');
  }
  if (typeof data === 'number') {
    return [data];
  }
  console.warn('<role-select> The data type of data should be array or string or number, but I received other');
  return [];
};
const defaultSelectRoleIds = computed(() => computedIds(props.data).filter((item) => item));

const roleDialog = useDialog({
  title: '角色选择',
});

const selectRoleList = ref<SysRoleVo[]>([]);
const roleList = ref<SysRoleVo[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const columnControllerVisible = ref(false);
const sort = ref<TableSort>();
// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: props.multiple ? 'multiple' : 'single', width: 50, align: 'center' },
  { title: `角色名称`, colKey: 'roleName', ellipsis: true, align: 'center', width: 150 },
  { title: `权限字符`, colKey: 'roleKey', ellipsis: true, align: 'center', width: 150 },
  { title: `显示顺序`, colKey: 'roleSort', align: 'center', width: 120, sorter: true },
  { title: `状态`, colKey: 'status', align: 'center', width: 100 },
  { title: `创建时间`, colKey: 'createTime', align: 'center', sorter: true, width: '15%', minWidth: 112 },
]);
// 查询对象
const queryParams = ref<SysRoleQuery>({
  pageNum: 1,
  pageSize: 10,
  roleName: undefined,
  roleKey: undefined,
  status: undefined,
  createTime: undefined,
});

const roleIds = computed(() => selectRoleList.value.map((item) => item.roleId));
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
  listRole(queryParams.value).then((response) => {
    roleList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
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
const handleSelectionChange: TableProps['onSelectChange'] = (selection, options) => {
  if (!props.multiple) {
    selectRoleList.value = roleList.value.filter((value) => selection.includes(value.roleId));
    return;
  }
  if (options.type === 'check' && options.currentRowKey === 'CHECK_ALL_BOX') {
    // 全选
    roleList.value
      .filter((value) => !roleIds.value.includes(value.roleId))
      .forEach((value) => {
        selectRoleList.value.push(value);
      });
  } else if (options.type === 'check') {
    // 勾选
    selectRoleList.value.push(options.currentRowData as SysRoleVo);
  } else if (options.type === 'uncheck' && options.currentRowKey === 'CHECK_ALL_BOX') {
    const ids = roleList.value.map((value) => value.roleId);
    selectRoleList.value = selectRoleList.value.filter((value) => !ids.includes(value.roleId));
  } else {
    selectRoleList.value = selectRoleList.value.filter((value) => value.roleId !== options.currentRowData.roleId);
  }
};

/** 提交按钮 */
function onSubmit() {
  const value = [...selectRoleList.value];
  modelValue.value = value;
  emit('confirmCallBack', value);
  roleDialog.closeDialog();
}

const handleCloseTag = (index: number) => {
  selectRoleList.value.splice(index, 1);
};

const handleOpened = () => {
  initSelectRole();
  getList();
};

const initSelectRole = async () => {
  if (defaultSelectRoleIds.value.length > 0) {
    const { data } = await roleOptionSelect(defaultSelectRoleIds.value);
    selectRoleList.value = data;
  }
};

defineExpose({
  open: roleDialog.openDialog,
  close: roleDialog.closeDialog,
});
</script>
