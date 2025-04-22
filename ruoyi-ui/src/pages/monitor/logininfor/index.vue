<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="登录地址" name="ipaddr">
          <t-input
            v-model="queryParams.ipaddr"
            placeholder="请输入登录地址"
            clearable
            style="width: 240px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="用户名称" name="userName">
          <t-input
            v-model="queryParams.userName"
            placeholder="请输入用户名称"
            clearable
            style="width: 240px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="queryParams.status" placeholder="登录状态" clearable style="width: 240px">
            <t-option v-for="dict in sys_common_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </t-select>
        </t-form-item>
        <t-form-item label="客户端" name="clientKey">
          <t-input v-model="queryParams.clientKey" placeholder="请输入客户端" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="设备类型" name="deviceType">
          <t-select v-model="queryParams.deviceType" placeholder="请选择设备类型" clearable>
            <t-option v-for="dict in sys_device_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </t-select>
        </t-form-item>
        <t-form-item label="登录时间">
          <t-date-range-picker
            v-model="dateRange"
            style="width: 240px"
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

      <t-table
        v-model:column-controller-visible="columnControllerVisible"
        :loading="loading"
        hover
        row-key="infoId"
        :data="logininforList"
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
              <t-button
                v-hasPermi="['monitor:logininfor:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button
                v-hasPermi="['monitor:logininfor:remove']"
                theme="danger"
                variant="outline"
                @click="handleClean"
              >
                <template #icon> <delete-icon /> </template>
                清空
              </t-button>
              <t-button
                v-hasPermi="['monitor:logininfor:unlock']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUnlock"
              >
                <template #icon> <lock-off-icon /> </template>
                解锁
              </t-button>
              <t-button
                v-hasPermi="['monitor:logininfor:export']"
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
          <dict-tag :options="sys_common_status" :value="row.status" />
        </template>
        <template #deviceType="{ row }">
          <dict-tag :options="sys_device_type" :value="row.deviceType" />
        </template>
        <template #loginTime="{ row }">
          <span>{{ parseTime(row.loginTime) }}</span>
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['monitor:logininfor:query']" @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link v-hasPermi="['monitor:logininfor:remove']" theme="danger" @click.stop="handleDelete(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 系统访问记录详情 -->
    <t-dialog v-model:visible="openView" header="系统访问记录详情" width="700px" attach="body" :footer="false">
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="访问ID">{{ form.infoId }}</t-descriptions-item>
        <t-descriptions-item label="租户编号">{{ form.tenantId }}</t-descriptions-item>
        <t-descriptions-item label="用户id">{{ form.userId }}</t-descriptions-item>
        <t-descriptions-item label="用户账号">{{ form.userName }}</t-descriptions-item>
        <t-descriptions-item label="登录IP地址">{{ form.ipaddr }}</t-descriptions-item>
        <t-descriptions-item label="登录地点">{{ form.loginLocation }}</t-descriptions-item>
        <t-descriptions-item label="浏览器类型">{{ form.browser }}</t-descriptions-item>
        <t-descriptions-item label="操作系统">{{ form.os }}</t-descriptions-item>
        <t-descriptions-item label="登录状态">
          <dict-tag :options="sys_common_status" :value="form.status" />
        </t-descriptions-item>
        <t-descriptions-item label="客户端">{{ form.clientKey }}</t-descriptions-item>
        <t-descriptions-item label="设备类型">
          <dict-tag :options="sys_device_type" :value="form.deviceType" />
        </t-descriptions-item>
        <t-descriptions-item label="提示消息">{{ form.msg }}</t-descriptions-item>
        <t-descriptions-item label="访问时间">{{ parseTime(form.loginTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'Logininfor',
});

import {
  BrowseIcon,
  DeleteIcon,
  DownloadIcon,
  LockOffIcon,
  RefreshIcon,
  SearchIcon,
  SettingIcon,
} from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol, TableProps, TableSort } from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import {
  cleanLogininfor,
  delLogininfor,
  getLogininfor,
  listLogininfor,
  unlockLogininfor,
} from '@/api/monitor/logininfor';
import type { SysLogininforQuery, SysLogininforVo } from '@/api/monitor/model/logininforModel';
import { ArrayOps } from '@/utils/array';

const { proxy } = getCurrentInstance();
const { sys_common_status, sys_device_type } = proxy.useDict('sys_common_status', 'sys_device_type');

const openView = ref(false);
const openViewLoading = ref(false);
const logininforList = ref<SysLogininforVo[]>([]);
const loading = ref(false);
const columnControllerVisible = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const selectName = ref('');
const total = ref(0);
const dateRange = ref([]);
const sort = ref<TableSort>();

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `访问编号`, colKey: 'infoId', align: 'center', ellipsis: true },
  { title: `用户名称`, colKey: 'userName', align: 'center' },
  { title: `地址`, colKey: 'ipaddr', align: 'center', ellipsis: true },
  { title: `登录地点`, colKey: 'loginLocation', align: 'center', ellipsis: true },
  { title: `操作系统`, colKey: 'os', align: 'center', sorter: true, ellipsis: true },
  { title: `浏览器`, colKey: 'browser', align: 'center', ellipsis: true },
  { title: `登录状态`, colKey: 'status', align: 'center' },
  { title: `客户端`, colKey: 'clientKey', align: 'center' },
  { title: `设备类型`, colKey: 'deviceType', align: 'center' },
  { title: `描述`, colKey: 'msg', align: 'center', ellipsis: true },
  { title: `访问时间`, colKey: 'loginTime', align: 'center', width: 170, sorter: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: 160 },
]);
// 提交表单对象
const form = ref<SysLogininforVo>({});
// 查询参数
const queryParams = ref<SysLogininforQuery>({
  pageNum: 1,
  pageSize: 10,
  userName: undefined,
  ipaddr: undefined,
  status: undefined,
  clientKey: undefined,
  deviceType: undefined,
  orderByColumn: undefined,
  isAsc: undefined,
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

/** 查询登录日志列表 */
function getList() {
  loading.value = true;
  listLogininfor(proxy.addDateRange(queryParams.value, dateRange.value))
    .then((response) => {
      logininforList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}

// 表单重置
function reset() {
  form.value = {};
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

/** 多选框选中数据 */
const handleSelectionChange: TableProps['onSelectChange'] = (selection, options) => {
  ids.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
  selectName.value = options.selectedRowData.map((item) => item.userName)[0];
};

/** 详情按钮操作 */
function handleDetail(row: SysLogininforVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const infoId = row.infoId;
  getLogininfor(infoId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 删除按钮操作 */
function handleDelete(row?: SysLogininforVo) {
  const infoIds = row?.infoId || ids.value;
  proxy.$modal.confirm(`是否确认删除访问编号为"${infoIds}"的数据项?`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delLogininfor(infoIds)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, infoIds);
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => {
        proxy.$modal.msgClose(msgLoading);
      });
  });
}
/** 清空按钮操作 */
function handleClean() {
  proxy.$modal.confirm('是否确认清空所有登录日志数据项?', () => {
    const msgLoading = proxy.$modal.msgLoading('正在清空中...');
    return cleanLogininfor()
      .then(() => {
        ids.value = [];
        getList();
        proxy.$modal.msgSuccess('清空成功');
      })
      .finally(() => {
        proxy.$modal.msgClose(msgLoading);
      });
  });
}
/** 解锁按钮操作 */
function handleUnlock() {
  const username = selectName.value;
  proxy.$modal.confirm(`是否确认解锁用户"${username}"数据项?`, () => {
    return unlockLogininfor(username).then(() => {
      proxy.$modal.msgSuccess(`用户${username}解锁成功`);
    });
  });
}
/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'monitor/logininfor/export',
    {
      ...proxy.addDateRange(queryParams.value, dateRange.value),
    },
    `logininfor_${new Date().getTime()}.xlsx`,
  );
}

getList();
</script>
