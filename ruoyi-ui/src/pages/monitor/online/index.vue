<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="登录地址" name="ipaddr">
          <t-input
            v-model="queryParams.ipaddr"
            placeholder="请输入登录地址"
            clearable
            style="width: 200px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="用户名称" name="userName">
          <t-input
            v-model="queryParams.userName"
            placeholder="请输入用户名称"
            clearable
            style="width: 200px"
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
        hover
        :loading="loading"
        row-key="tokenId"
        :data="onlineList"
        :columns="columns"
        :pagination="pagination"
        :column-controller="{
          placement: 'top-right',
        }"
        style="width: 100%"
      >
        <template #deviceType="{ row }">
          <dict-tag :options="sys_device_type" :value="row.deviceType" />
        </template>
        <template #loginTime="{ row }">
          <span>{{ parseTime(row.loginTime) }}</span>
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link v-hasPermi="['monitor:online:forceLogout']" theme="danger" @click.stop="handleForceLogout(row)">
              <template #prefix-icon><delete-icon /></template>强退
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 在线用户详情 -->
    <t-dialog
      v-model:visible="openView"
      destroy-on-close
      header="在线用户"
      placement="center"
      width="700px"
      attach="body"
      :footer="false"
    >
      <my-descriptions>
        <t-descriptions-item label="会话编号">{{ form.tokenId }}</t-descriptions-item>
        <t-descriptions-item label="租户id">{{ form.tenantId }}</t-descriptions-item>
        <t-descriptions-item label="部门名称">{{ form.deptName }}</t-descriptions-item>
        <t-descriptions-item label="用户名称">{{ form.userName }}</t-descriptions-item>
        <t-descriptions-item label="客户端">{{ form.clientKey }}</t-descriptions-item>
        <t-descriptions-item label="设备类型">
          <dict-tag :options="sys_device_type" :value="form.deviceType" />
        </t-descriptions-item>
        <t-descriptions-item label="登录IP地址">{{ form.ipaddr }}</t-descriptions-item>
        <t-descriptions-item label="登录地址">{{ form.loginLocation }}</t-descriptions-item>
        <t-descriptions-item label="浏览器类型">{{ form.browser }}</t-descriptions-item>
        <t-descriptions-item label="操作系统">{{ form.os }}</t-descriptions-item>
        <t-descriptions-item label="登录时间">{{ parseTime(form.loginTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'Online',
});
import { BrowseIcon, DeleteIcon, RefreshIcon, SearchIcon } from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import type { SysUserOnline, SysUserOnlineQuery } from '@/api/monitor/model/userOnlineModel';
import { forceLogout, list as initData } from '@/api/monitor/online';

const { proxy } = getCurrentInstance();
const { sys_device_type } = proxy.useDict('sys_device_type');

const onlineList = ref<SysUserOnline[]>([]);
const loading = ref(false);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const openView = ref(false);
// 提交表单对象
const form = ref<SysUserOnline>();

const queryParams = ref<SysUserOnlineQuery>({
  ipaddr: undefined,
  userName: undefined,
});

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: '序号', colKey: 'serial-number', width: 80, align: 'center' },
  { title: `会话编号`, colKey: 'tokenId', align: 'center', ellipsis: true },
  { title: `租户id`, colKey: 'tenantId', align: 'center', ellipsis: true },
  { title: `登录名称`, colKey: 'userName', align: 'center', ellipsis: true },
  { title: `客户端`, colKey: 'clientKey', align: 'center' },
  { title: `设备类型`, colKey: 'deviceType', align: 'center' },
  { title: `所属部门`, colKey: 'deptName', align: 'center', ellipsis: true },
  { title: `主机`, colKey: 'ipaddr', align: 'center', ellipsis: true },
  { title: `登录地点`, colKey: 'loginLocation', align: 'center', ellipsis: true },
  { title: `操作系统`, colKey: 'os', align: 'center', ellipsis: true },
  { title: `浏览器`, colKey: 'browser', align: 'center', ellipsis: true },
  { title: `登录时间`, colKey: 'loginTime', align: 'center', width: 180 },
  { title: `操作`, colKey: 'operation', align: 'center', width: 130, fixed: 'right' },
]);

// 分页
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

/** 查询登录日志列表 */
function getList() {
  loading.value = true;
  initData(queryParams.value).then((response) => {
    onlineList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}
/** 搜索按钮操作 */
function handleQuery() {
  pageNum.value = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
  handleQuery();
}
/** 详情按钮操作 */
function handleDetail(row: SysUserOnline) {
  openView.value = true;
  form.value = row;
}
/** 强退按钮操作 */
function handleForceLogout(row: SysUserOnline) {
  proxy.$modal.confirm(`是否确认强退名称为"${row.userName}"的用户?`, () => {
    return forceLogout(row.tokenId).then(() => {
      getList();
      proxy.$modal.msgSuccess('删除成功');
    });
  });
}

getList();
</script>
