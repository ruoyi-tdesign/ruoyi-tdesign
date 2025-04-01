<template>
  <t-dialog
    v-model:visible="userDialog.visible.value"
    :header="userDialog.title.value"
    width="80%"
    :close-on-overlay-click="false"
    placement="center"
    attach="body"
    @opened="handleOpened"
    @confirm="onSubmit"
  >
    <t-row :gutter="20">
      <!--部门数据-->
      <t-col :sm="2" :xs="12">
        <t-card hover-shadow>
          <div class="head-container">
            <t-input v-model="deptName" placeholder="请输入部门名称" clearable style="margin-bottom: 20px">
              <template #prefixIcon><search-icon /></template>
            </t-input>
          </div>
          <div class="head-container">
            <t-loading :loading="loadingDept" size="small">
              <t-tree
                ref="deptTreeRef"
                v-model:actived="actived"
                v-model:expanded="expandedDept"
                class="t-tree--block-node"
                :data="deptOptions"
                :keys="{ value: 'id', label: 'label', children: 'children' }"
                :filter="filterNode"
                activable
                hover
                line
                check-strictly
                allow-fold-node-on-filter
                transition
                @active="handleQuery"
              />
            </t-loading>
          </div>
        </t-card>
      </t-col>
      <!--用户数据-->
      <t-col :sm="10" :xs="12">
        <t-space direction="vertical" style="width: 100%">
          <t-card hover-shadow>
            <t-form
              v-show="showSearch"
              ref="queryRef"
              :data="queryParams"
              layout="inline"
              label-width="calc(4em + 12px)"
            >
              <t-form-item label="用户名称" name="userName">
                <t-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable @enter="handleQuery" />
              </t-form-item>
              <t-form-item label="用户昵称" name="nickName">
                <t-input v-model="queryParams.nickName" placeholder="请输入用户昵称" clearable @enter="handleQuery" />
              </t-form-item>
              <t-form-item label="手机号码" name="phonenumber">
                <t-input
                  v-model="queryParams.phonenumber"
                  placeholder="请输入手机号码"
                  clearable
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
          <t-card hover-shadow header-bordered>
            <template #header>
              <t-space break-line size="4px">
                <t-tag
                  v-for="(user, index) in selectUserList"
                  :key="user.userId"
                  theme="primary"
                  variant="light"
                  closable
                  class="mb-2px"
                  @close="handleCloseTag(index)"
                >
                  {{ user.nickName }}
                </t-tag>
              </t-space>
            </template>
            <t-table
              v-model:column-controller-visible="columnControllerVisible"
              hover
              :loading="loading"
              row-key="userId"
              :data="userList"
              :columns="columns"
              :selected-row-keys="effectiveUserIds"
              select-on-row-click
              row-selection-allow-uncheck
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
              <template #avatar="{ row }">
                <image-preview :src="row.avatar" width="60px" height="60px" />
              </template>
            </t-table>
          </t-card>
        </t-space>
      </t-col>
    </t-row>
  </t-dialog>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'UserSelect',
});

import { RefreshIcon, SearchIcon, SettingIcon } from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol, TableProps, TableSort, TreeNodeModel } from 'tdesign-vue-next';
import type { PropType } from 'vue';
import { computed, getCurrentInstance, ref } from 'vue';

import type { TreeModel } from '@/api/model/resultModel';
import type { SysUserQuery, SysUserVo } from '@/api/system/model/userModel';
import { deptTreeSelect, listUser, userOptionSelect } from '@/api/system/user';
import useDialog from '@/hooks/useDialog';

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const props = defineProps({
  multiple: {
    type: Boolean,
    default: true,
  },
  data: [String, Number, Array] as PropType<string | number | Array<string | number>>,
  userIds: [String, Number, Array] as PropType<string | number | Array<string | number>>,
});

const modelValue = defineModel<SysUserVo[]>({
  default: () => [] as SysUserVo[],
});

const emit = defineEmits<{
  (e: 'confirmCallBack', value: SysUserVo[]): void;
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
  console.warn('<user-select> The data type of data should be array or string or number, but I received other');
  return [];
};
const defaultSelectUserIds = computed(() => computedIds(props.data).filter((item) => item));

const selectUserList = ref<SysUserVo[]>([]);
const userList = ref<SysUserVo[]>([]);
const loading = ref(true);
const loadingDept = ref(false);
const showSearch = ref(true);
const columnControllerVisible = ref(false);
const total = ref(0);
const deptName = ref('');
const deptOptions = ref<Array<TreeModel<number>>>([]);
const deptFormOptions = ref<Array<TreeModel<number>>>([]);
const expandedDept = ref<number[]>([]);
const actived = ref<number[]>([]);
const sort = ref<TableSort>();
const userDialog = useDialog({
  title: '用户选择',
});
// 列显隐信息
const columns = computed<Array<PrimaryTableCol>>(() => [
  { title: `选择列`, colKey: 'row-select', type: props.multiple ? 'multiple' : 'single', width: 50, align: 'center' },
  { title: `用户编号`, colKey: 'userId', align: 'center', ellipsis: true },
  { title: `用户名称`, colKey: 'userName', ellipsis: true, align: 'center' },
  { title: `用户昵称`, colKey: 'nickName', ellipsis: true, align: 'center' },
  // { title: `头像`, colKey: 'avatar', align: 'center' },
  { title: `部门`, colKey: 'dept.deptName', align: 'center' },
  { title: `手机号码`, colKey: 'phonenumber', align: 'center' },
  { title: `状态`, colKey: 'status', align: 'center' },
  { title: `创建时间`, colKey: 'createTime', align: 'center', sorter: true, width: '10%', minWidth: 112 },
]);
const queryParams = ref<SysUserQuery>({
  pageNum: 1,
  pageSize: 10,
  userName: undefined,
  nickName: undefined,
  phonenumber: undefined,
  email: undefined,
  status: undefined,
  deptId: undefined,
  userIds: undefined,
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
const effectiveUserIds = computed<number[]>(() => selectUserList.value?.map((item) => item.userId) ?? []);

watchEffect(() => {
  selectUserList.value = modelValue.value ?? [];
});

/** 通过条件过滤节点  */
const filterNode = computed(() => {
  const value = deptName.value;
  return (node: TreeNodeModel) => {
    if (!node.value || !value) return true;
    return node.label.indexOf(value) >= 0;
  };
});
/** 查询部门下拉树结构 */
async function getDeptTree() {
  loadingDept.value = true;
  return deptTreeSelect()
    .then((response) => {
      deptOptions.value = response.data;
    })
    .finally(() => (loadingDept.value = false));
}
function triggerExpandedDept() {
  expandedDept.value = deptOptions.value
    .flatMap((value) => value.children?.concat([value]) ?? [value])
    .map((value) => value.id);
}
/** 查询用户列表 */
function getList() {
  loading.value = true;
  queryParams.value.deptId = actived.value.at(0);
  queryParams.value.userIds = props.userIds?.split(',');
  listUser(queryParams.value).then((res) => {
    loading.value = false;
    userList.value = res.rows;
    total.value = res.total;
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
  actived.value = [];
  queryParams.value.deptId = undefined;
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

/** 选择条数  */
const handleSelectionChange: TableProps['onSelectChange'] = (selection, options) => {
  if (!props.multiple) {
    // 单选
    selectUserList.value = userList.value.filter((value) => selection.includes(value.userId));
    return;
  }
  if (options.type === 'check' && options.currentRowKey === 'CHECK_ALL_BOX') {
    // 全选
    userList.value
      .filter((value) => !effectiveUserIds.value.includes(value.userId))
      .forEach((value) => {
        selectUserList.value.push(value);
      });
  } else if (options.type === 'check') {
    // 选中
    selectUserList.value.push(options.currentRowData as SysUserVo);
  } else if (options.type === 'uncheck' && options.currentRowKey === 'CHECK_ALL_BOX') {
    // 取消全选
    const ids = userList.value.map((value) => value.userId);
    selectUserList.value = selectUserList.value.filter((value) => !ids.includes(value.userId));
  } else {
    // 取消选中
    selectUserList.value = selectUserList.value.filter((value) => value.userId !== options.currentRowData.userId);
  }
};

const onSubmit = () => {
  const value = [...selectUserList.value];
  modelValue.value = value;
  emit('confirmCallBack', value);
  userDialog.closeDialog();
};

const handleOpened = () => {
  getDeptTree().then(() => triggerExpandedDept());
  initSelectUser();
  deptFormOptions.value = deptOptions.value;
  getList();
};

const handleCloseTag = (index: number) => {
  // 使用splice删除用户
  selectUserList.value?.splice(index, 1);
};

const initSelectUser = async () => {
  if (defaultSelectUserIds.value.length > 0) {
    const { data } = await userOptionSelect(defaultSelectUserIds.value);
    selectUserList.value = data;
  }
};

defineExpose({
  open: userDialog.openDialog,
  close: userDialog.closeDialog,
});
</script>
