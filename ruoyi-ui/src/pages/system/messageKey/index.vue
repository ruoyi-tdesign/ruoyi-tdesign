<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="消息名称" name="name">
          <t-input v-model="queryParams.name" placeholder="请输入消息名称" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="消息key" name="messageKey">
          <t-input v-model="queryParams.messageKey" placeholder="请输入消息key" clearable @enter="handleQuery" />
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
        row-key="messageKeyId"
        :data="messageKeyList"
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
              <t-button v-hasPermi="['system:messageKey:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:messageKey:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:messageKey:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button
                v-hasPermi="['system:messageKey:export']"
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
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['system:messageKey:query']" @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link v-hasPermi="['system:messageKey:edit']" @click.stop="handleUpdate(row)">
              <template #prefix-icon><edit-icon /></template>修改
            </my-link>
            <my-link v-hasPermi="['system:messageKey:remove']" theme="danger" @click.stop="handleDelete(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 添加或修改消息常量对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      width="500px"
      attach="body"
      :confirm-btn="{
        loading: buttonLoading,
      }"
      @confirm="onConfirm"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="messageKeyRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(4em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitForm"
        >
          <t-form-item label="消息名称" name="name">
            <t-input v-model="form.name" placeholder="请输入消息名称" clearable />
          </t-form-item>
          <t-form-item label="消息key" name="messageKey">
            <t-input v-model="form.messageKey" placeholder="请输入消息key" clearable />
          </t-form-item>
          <t-form-item label="备注" name="remark">
            <t-textarea v-model="form.remark" placeholder="请输入备注" />
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 消息常量详情 -->
    <t-dialog v-model:visible="openView" header="消息常量详情" width="700px" attach="body" :footer="false">
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="消息key主键">{{ form.messageKeyId }}</t-descriptions-item>
        <t-descriptions-item label="消息名称">{{ form.name }}</t-descriptions-item>
        <t-descriptions-item label="消息key">{{ form.messageKey }}</t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ form.remark }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'MessageKey',
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
} from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import { addMessageKey, delMessageKey, getMessageKey, listMessageKey, updateMessageKey } from '@/api/system/messageKey';
import type { SysMessageKeyForm, SysMessageKeyQuery, SysMessageKeyVo } from '@/api/system/model/messageKeyModel';
import { ArrayOps } from '@/utils/array';

const { proxy } = getCurrentInstance();

const messageKeyList = ref<SysMessageKeyVo[]>([]);
const messageKeyRef = ref<FormInstanceFunctions>();
const open = ref(false);
const openView = ref(false);
const openViewLoading = ref(false);
const buttonLoading = ref(false);
const loading = ref(false);
const columnControllerVisible = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const sort = ref<TableSort>();

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  name: [{ required: true, message: '消息名称不能为空' }],
  messageKey: [{ required: true, message: '消息key不能为空' }],
});
// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `消息名称`, colKey: 'name', align: 'center' },
  { title: `消息key`, colKey: 'messageKey', align: 'center', sorter: true },
  { title: `备注`, colKey: 'remark', align: 'center', ellipsis: true },
  { title: `更新时间`, colKey: 'updateTime', align: 'center', width: 180, sorter: true },
  { title: `创建时间`, colKey: 'createTime', align: 'center', width: 180, sorter: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 提交表单对象
const form = ref<SysMessageKeyVo & SysMessageKeyForm>({});
// 查询对象
const queryParams = ref<SysMessageKeyQuery>({
  pageNum: 1,
  pageSize: 10,
  name: undefined,
  messageKey: undefined,
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

/** 查询消息常量列表 */
function getList() {
  loading.value = true;
  listMessageKey(queryParams.value)
    .then((response) => {
      messageKeyList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}

// 表单重置
function reset() {
  form.value = {};
  proxy.resetForm('messageKeyRef');
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

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加消息常量';
}

/** 详情按钮操作 */
function handleDetail(row: SysMessageKeyVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const messageKeyId = row.messageKeyId;
  getMessageKey(messageKeyId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysMessageKeyVo) {
  buttonLoading.value = true;
  reset();
  open.value = true;
  title.value = '修改消息常量';
  const messageKeyId = row?.messageKeyId || ids.value.at(0);
  getMessageKey(messageKeyId).then((response) => {
    buttonLoading.value = false;
    form.value = response.data;
  });
}

/** 提交按钮 */
function onConfirm() {
  messageKeyRef.value.submit();
}

/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.messageKeyId) {
      updateMessageKey(form.value)
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
      addMessageKey(form.value)
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
function handleDelete(row?: SysMessageKeyVo) {
  const messageKeyIds = row?.messageKeyId || ids.value;
  proxy.$modal.confirm(`是否确认删除消息常量编号为${messageKeyIds}的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delMessageKey(messageKeyIds)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, messageKeyIds);
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
    'system/messageKey/export',
    {
      ...queryParams.value,
    },
    `messageKey_${new Date().getTime()}.xlsx`,
  );
}

getList();
</script>
