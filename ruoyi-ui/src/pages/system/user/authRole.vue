<template>
  <t-card>
    <h4 class="form-header h4">基本信息</h4>
    <t-form :data="form" label-width="calc(4em + 12px)">
      <t-row>
        <t-col :span="4" :offset="1">
          <t-form-item label="用户昵称" prop="nickName">
            <t-input v-model="form.nickName" disabled />
          </t-form-item>
        </t-col>
        <t-col :span="4" :offset="1">
          <t-form-item label="登录账号" prop="userName">
            <t-input v-model="form.userName" disabled />
          </t-form-item>
        </t-col>
      </t-row>
    </t-form>

    <h4 class="form-header h4">角色信息</h4>
    <t-table
      hover
      :loading="loading"
      row-key="roleId"
      :columns="columns"
      :data="roles.slice((pageNum - 1) * pageSize, pageNum * pageSize)"
      :selected-row-keys="roleIds"
      select-on-row-click
      :pagination="pagination"
      @select-change="handleSelectionChange"
    >
    </t-table>

    <t-form label-width="100px">
      <div style="text-align: center; margin-left: -120px; margin-top: 30px">
        <t-button theme="primary" @click="submitForm()">提交</t-button>
        <t-button theme="default" variant="outline" @click="close()">返回</t-button>
      </div>
    </t-form>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'AuthRole',
});
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { computed, getCurrentInstance, nextTick, ref } from 'vue';
import { useRoute } from 'vue-router';

import type { SysRoleVo } from '@/api/system/model/roleModel';
import type { SysUserVo } from '@/api/system/model/userModel';
import { getAuthRole, updateAuthRole } from '@/api/system/user';
import { useTabsRouterStore } from '@/store';

const { proxy } = getCurrentInstance();

const loading = ref(true);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const roleIds = ref([]);
const roles = ref<SysRoleVo[]>([]);
const removeCurrentTab = useTabsRouterStore().useRemoveCurrentTab();
const route = useRoute();
const form = ref<SysUserVo>({
  nickName: undefined,
  userName: undefined,
  userId: undefined,
});
// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: '序号', colKey: 'serial-number', width: 80, align: 'center' },
  { colKey: 'row-select', type: 'multiple', width: 55, align: 'center' },
  { title: `角色编号`, colKey: 'roleId', align: 'center' },
  { title: `角色名称`, colKey: 'roleName', align: 'center' },
  { title: `权限字符`, colKey: 'roleKey', align: 'center' },
  { title: `创建时间`, colKey: 'createTime', width: 180, align: 'center' },
]);

const pagination = computed(() => {
  return {
    current: pageNum.value,
    pageSize: pageSize.value,
    total: total.value,
    showJumper: true,
    onChange: (pageInfo: PageInfo) => {
      pageNum.value = pageInfo.current;
      pageSize.value = pageInfo.pageSize;
    },
  };
});
/** 多选框选中数据 */
function handleSelectionChange(selection: Array<string | number>) {
  roleIds.value = selection;
}
/** 关闭按钮 */
function close() {
  removeCurrentTab('/system/user');
}
/** 提交按钮 */
function submitForm() {
  const { userId } = form.value;
  const rIds = roleIds.value.join(',');
  const msgLoading = proxy.$modal.msgLoading('提交中...');
  updateAuthRole({ userId, roleIds: rIds })
    .then(() => {
      proxy.$modal.msgSuccess('授权成功');
      close();
    })
    .finally(() => proxy.$modal.msgClose(msgLoading));
}

(() => {
  const userId = route.params && route.params.userId;
  if (userId) {
    loading.value = true;
    getAuthRole(userId as string).then((response) => {
      handleSelectionChange([]);
      form.value = response.data.user;
      roles.value = response.data.roles;
      total.value = roles.value.length;
      nextTick(() => {
        roleIds.value = response.data.roles.filter((row) => row.flag).map((row) => row.roleId);
      });
      loading.value = false;
    });
  }
})();
</script>
