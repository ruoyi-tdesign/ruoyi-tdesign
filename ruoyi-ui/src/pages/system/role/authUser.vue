<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="用户名称" name="userName">
          <t-input
            v-model="queryParams.userName"
            placeholder="请输入用户名称"
            clearable
            style="width: 240px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="手机号码" name="phonenumber">
          <t-input
            v-model="queryParams.phonenumber"
            placeholder="请输入手机号码"
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

      <t-table
        v-model:column-controller-visible="columnControllerVisible"
        hover
        :loading="loading"
        row-key="userId"
        :data="userList"
        :columns="columns"
        :selected-row-keys="userIds"
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
              <t-button v-hasPermi="['system:role:add']" theme="primary" @click="openSelectUser">
                <template #icon> <add-icon /></template>
                添加用户
              </t-button>
              <t-button
                v-hasPermi="['system:role:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="cancelAuthUserAll"
              >
                <template #icon> <close-circle-icon /> </template>
                批量取消授权
              </t-button>
              <t-button theme="default" variant="outline" @click="handleClose">
                <template #icon> <close-icon /> </template>
                关闭
              </t-button>
              <span class="selected-count">已选 {{ userIds.length }} 项</span>
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
          <my-link v-hasPermi="['system:role:remove']" theme="danger" @click="cancelAuthUser(row)">
            <template #prefix-icon><close-circle-icon /></template>
            取消授权
          </my-link>
        </template>
      </t-table>
    </t-space>
    <select-user ref="selectRef" :role-id="queryParams.roleId" @ok="handleQuery" />
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'AuthUser',
});
import { AddIcon, CloseCircleIcon, CloseIcon, RefreshIcon, SearchIcon, SettingIcon } from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { computed, getCurrentInstance, reactive, ref } from 'vue';
import { useRoute } from 'vue-router';

import type { SysUserQuery, SysUserVo } from '@/api/system/model/userModel';
import { allocatedUserList, authUserCancel, authUserCancelAll } from '@/api/system/role';
import SelectUser, { type SelectUserInstance } from '@/pages/system/role/selectUser.vue';
import { useTabsRouterStore } from '@/store';
import { ArrayOps } from '@/utils/array';

const removeCurrentTab = useTabsRouterStore().useRemoveCurrentTab();
const route = useRoute();
const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const userList = ref<SysUserVo[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const multiple = ref(true);
const total = ref(0);
const userIds = ref([]);
const columnControllerVisible = ref(false);
const selectRef = ref<SelectUserInstance>();

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `用户名称`, colKey: 'userName', align: 'center', ellipsis: true },
  { title: `用户昵称`, colKey: 'nickName', ellipsis: true, align: 'center' },
  { title: `邮箱`, colKey: 'email', ellipsis: true, align: 'center' },
  { title: `手机`, colKey: 'phonenumber', align: 'center', ellipsis: true },
  { title: `状态`, colKey: 'status', align: 'center', ellipsis: true },
  { title: `创建时间`, colKey: 'createTime', align: 'center', width: '180' },
  { title: `操作`, colKey: 'operation', align: 'center' },
]);
const queryParams = reactive<SysUserQuery>({
  pageNum: 1,
  pageSize: 10,
  roleId: route.params.roleId as any,
  userName: undefined,
  phonenumber: undefined,
});

const pagination = computed(() => {
  return {
    current: queryParams.pageNum,
    pageSize: queryParams.pageSize,
    total: total.value,
    showJumper: true,
    onChange: (pageInfo: PageInfo) => {
      queryParams.pageNum = pageInfo.current;
      queryParams.pageSize = pageInfo.pageSize;
      getList();
    },
  };
});
/** 查询授权用户列表 */
function getList() {
  loading.value = true;
  allocatedUserList(queryParams).then((response) => {
    userList.value = response.rows;
    total.value = response.total;
    loading.value = false;
    handleSelectionChange([]);
  });
}
// 返回按钮
function handleClose() {
  removeCurrentTab('/system/role');
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
  handleQuery();
}
// 多选框选中数据
function handleSelectionChange(selection: Array<string | number>) {
  userIds.value = selection;
  multiple.value = !selection.length;
}
/** 打开授权用户表弹窗 */
function openSelectUser() {
  selectRef.value.show();
}
/** 取消授权按钮操作 */
function cancelAuthUser(row: SysUserVo) {
  proxy.$modal.confirm(`确认要取消该用户"${row.userName}"角色吗？`, () => {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    return authUserCancel({ userId: row.userId, roleId: queryParams.roleId })
      .then(() => {
        userIds.value = ArrayOps.fastDeleteElement(userIds.value, row.userId);
        getList();
        proxy.$modal.msgSuccess('取消授权成功');
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  });
}
/** 批量取消授权按钮操作 */
function cancelAuthUserAll() {
  const { roleId } = queryParams;
  const uIds = userIds.value.join(',');
  proxy.$modal.confirm('是否取消选中用户授权数据项?', () => {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    return authUserCancelAll({ roleId, userIds: uIds })
      .then(() => {
        userIds.value = ArrayOps.fastDeleteElement(userIds.value, userIds.value);
        getList();
        proxy.$modal.msgSuccess('取消授权成功');
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  });
}

getList();
</script>
