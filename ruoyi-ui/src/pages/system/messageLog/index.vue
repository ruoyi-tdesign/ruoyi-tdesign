<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="消息key" name="messageKey">
          <t-input v-model="queryParams.messageKey" placeholder="请输入消息key" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="模板名称" name="messageTemplateName">
          <t-input
            v-model="queryParams.messageTemplateName"
            placeholder="请输入模板名称"
            clearable
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="消息类型" name="messageType">
          <t-select v-model="queryParams.messageType" placeholder="请选择消息类型" clearable>
            <t-option v-for="dict in messageTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
          </t-select>
        </t-form-item>
        <t-form-item label="模板类型" name="templateMode">
          <t-select v-model="queryParams.templateMode" placeholder="请选择模板类型" clearable>
            <t-option
              v-for="dict in sys_message_template_mode"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </t-select>
        </t-form-item>
        <t-form-item label="发送账号" name="account">
          <t-input v-model="queryParams.account" placeholder="请输入发送账号" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="模板ID" name="templateId">
          <t-input v-model="queryParams.templateId" placeholder="请输入模板ID" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="平台标识" name="supplierType">
          <t-select v-model="queryParams.supplierType" placeholder="请选择平台标识" clearable>
            <t-option
              v-for="dict in supplierTypeOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </t-select>
        </t-form-item>
        <t-form-item label="是否成功" name="isSuccess">
          <t-select v-model="queryParams.isSuccess" placeholder="请选择是否成功" clearable>
            <t-option v-for="dict in sys_common_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </t-select>
        </t-form-item>
        <t-form-item label="记录时间">
          <t-date-range-picker
            v-model="dateRangeLogTime"
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
        row-key="messageLogId"
        :data="messageLogList"
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
                v-hasPermi="['system:messageLog:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button v-hasPermi="['system:messageLog:remove']" theme="danger" variant="outline" @click="handleClear">
                <template #icon> <delete-icon /> </template>
                清空记录
              </t-button>
              <t-button
                v-hasPermi="['system:messageLog:export']"
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
        <template #messageType="{ row }">
          <dict-tag :options="messageTypeOptions" :value="row.messageType" />
        </template>
        <template #templateMode="{ row }">
          <dict-tag :options="sys_message_template_mode" :value="row.templateMode" />
        </template>
        <template #supplierType="{ row }">
          <dict-tag :options="supplierTypeOptions" :value="row.supplierType" />
        </template>
        <template #isSuccess="{ row }">
          <dict-tag :options="sys_common_status" :value="row.isSuccess" />
        </template>
        <template #costTime="{ row }"> {{ row.costTime }}毫秒 </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <t-link
              v-hasPermi="['system:messageLog:query']"
              theme="primary"
              hover="color"
              @click.stop="handleDetail(row)"
            >
              <browse-icon />详情
            </t-link>
            <t-link
              v-hasPermi="['system:messageLog:remove']"
              theme="danger"
              hover="color"
              @click.stop="handleDelete(row)"
            >
              <delete-icon />删除
            </t-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 消息发送记录详情 -->
    <t-dialog
      v-model:visible="openView"
      header="消息发送记录详情"
      top="3%"
      width="min(950px, 90%)"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="消息发送记录id">{{ form.messageLogId }}</t-descriptions-item>
        <t-descriptions-item label="消息模板id">{{ form.messageTemplateId }}</t-descriptions-item>
        <t-descriptions-item label="消息key">{{ form.messageKey }}</t-descriptions-item>
        <t-descriptions-item label="模板名称">{{ form.messageTemplateName }}</t-descriptions-item>
        <t-descriptions-item label="消息类型">
          <dict-tag :options="messageTypeOptions" :value="form.messageType" />
        </t-descriptions-item>
        <t-descriptions-item label="模板类型">
          <dict-tag :options="sys_message_template_mode" :value="form.templateMode" />
        </t-descriptions-item>
        <t-descriptions-item label="发送账号">{{ form.account }}</t-descriptions-item>
        <t-descriptions-item label="标题">{{ form.title }}</t-descriptions-item>
        <t-descriptions-item label="模板ID">{{ form.templateId }}</t-descriptions-item>
        <t-descriptions-item label="发送内容" :span="2">
          <div v-html="form.content"></div>
        </t-descriptions-item>
        <t-descriptions-item label="消息配置标题">{{ form.messageConfigTitle }}</t-descriptions-item>
        <t-descriptions-item label="平台标识">
          <dict-tag :options="supplierTypeOptions" :value="form.supplierType" />
        </t-descriptions-item>
        <t-descriptions-item label="是否成功">
          <dict-tag :options="sys_common_status" :value="form.isSuccess" />
        </t-descriptions-item>
        <t-descriptions-item label="返回主体消息" :span="2">{{ form.responseBody }}</t-descriptions-item>
        <t-descriptions-item label="记录时间">{{ parseTime(form.logTime) }}</t-descriptions-item>
        <t-descriptions-item label="消耗时间">{{ form.costTime ?? 0 }}毫秒</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'MessageLog',
});

import { BrowseIcon, DeleteIcon, DownloadIcon, RefreshIcon, SearchIcon, SettingIcon } from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol, TableSort } from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import { getMessageSupplierType } from '@/api/system/messageConfig';
import { clearMessageLog, delMessageLog, getMessageLog, listMessageLog } from '@/api/system/messageLog';
import type { MessageTypeVo } from '@/api/system/model/messageConfigModel';
import type { SysMessageLogQuery, SysMessageLogVo } from '@/api/system/model/messageLogModel';
import { ArrayOps } from '@/utils/array';
import type { DictModel } from '@/utils/dict';

const { proxy } = getCurrentInstance();
const { sys_message_template_mode, sys_common_status } = proxy.useDict(
  'sys_message_template_mode',
  'sys_common_status',
);

const openView = ref(false);
const openViewLoading = ref(false);
const messageLogList = ref<SysMessageLogVo[]>([]);
const loading = ref(false);
const columnControllerVisible = ref(false);
const showSearch = ref(true);
const total = ref(0);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const sort = ref<TableSort>();
const dateRangeLogTime = ref([]);
const supplierTypeList = ref<MessageTypeVo[]>([]);

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `消息key`, colKey: 'messageKey', align: 'center' },
  { title: `模板名称`, colKey: 'messageTemplateName', align: 'center' },
  { title: `消息类型`, colKey: 'messageType', align: 'center' },
  { title: `模板类型`, colKey: 'templateMode', align: 'center' },
  { title: `发送账号`, colKey: 'account', align: 'center' },
  { title: `标题`, colKey: 'title', align: 'center' },
  { title: `发送内容`, colKey: 'content', align: 'center', ellipsis: true },
  { title: `平台标识`, colKey: 'supplierType', align: 'center' },
  { title: `是否成功`, colKey: 'isSuccess', align: 'center' },
  { title: `消耗`, colKey: 'costTime', align: 'center', sorter: true },
  { title: `记录时间`, colKey: 'logTime', align: 'center', width: 180, sorter: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 提交表单对象
const form = ref<SysMessageLogVo>({});
// 查询对象
const queryParams = ref<SysMessageLogQuery>({
  pageNum: 1,
  pageSize: 10,
  messageKey: undefined,
  messageTemplateName: undefined,
  messageType: undefined,
  templateMode: undefined,
  account: undefined,
  templateId: undefined,
  supplierType: undefined,
  isSuccess: undefined,
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

const messageTypeOptions = computed(() => {
  return (
    supplierTypeList.value?.map<DictModel>((item) => ({
      label: item.description,
      value: item.messageType,
      tagType: 'primary',
    })) ?? []
  );
});

const supplierTypeOptions = computed(() => {
  return supplierTypeList.value.flatMap((item) => {
    return Object.entries(item.supplierTypeMap).map<DictModel>(([key, value]) => ({
      label: value,
      value: key,
      tagType: 'primary',
    }));
  });
});

/** 查询消息发送记录列表 */
function getList() {
  loading.value = true;
  proxy.addDateRange(queryParams.value, dateRangeLogTime.value, 'LogTime');
  listMessageLog(queryParams.value)
    .then((response) => {
      messageLogList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}

// 表单重置
function reset() {
  form.value = {};
  proxy.resetForm('messageLogRef');
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRangeLogTime.value = [];
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

/** 详情按钮操作 */
function handleDetail(row: SysMessageLogVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const messageLogId = row.messageLogId;
  getMessageLog(messageLogId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 删除按钮操作 */
function handleDelete(row?: SysMessageLogVo) {
  const messageLogIds = row?.messageLogId || ids.value;
  proxy.$modal.confirm(`是否确认删除消息发送记录编号为${messageLogIds}的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delMessageLog(messageLogIds)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, messageLogIds);
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => {
        proxy.$modal.msgClose(msgLoading);
      });
  });
}

/** 清空记录按钮操作 */
function handleClear() {
  proxy.$modal.confirm(`是否确认删除所有消息发送记录？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return clearMessageLog()
      .then(() => {
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
  proxy.addDateRange(queryParams.value, dateRangeLogTime.value, 'LogTime');
  proxy.download(
    'system/messageLog/export',
    {
      ...queryParams.value,
    },
    `messageLog_${new Date().getTime()}.xlsx`,
  );
}

/** 初始化消息配置 */
function initMessageFieldConfigs() {
  getMessageSupplierType().then((res) => {
    supplierTypeList.value = res.data;
  });
}

onMounted(() => {
  getList();
  initMessageFieldConfigs();
});
</script>
