<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(5em + 12px)">
        <t-form-item label="客户端id" name="clientId">
          <t-input v-model="queryParams.clientId" placeholder="请输入客户端id" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="客户端key" name="clientKey">
          <t-input v-model="queryParams.clientKey" placeholder="请输入客户端key" clearable @enter="handleQuery" />
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
        row-key="id"
        :data="clientList"
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
              <t-button v-hasPermi="['system:client:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:client:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:client:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button v-hasPermi="['system:client:export']" theme="default" variant="outline" @click="handleExport">
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
        <template #grantType="{ row }">
          <dict-tag :options="sys_grant_type" :value="row.grantType">
            <template #separator><br /></template>
          </dict-tag>
        </template>
        <template #deviceType="{ row }">
          <dict-tag :options="sys_device_type" :value="row.deviceType" />
        </template>
        <template #status="{ row }">
          <t-switch
            v-if="row.clientId !== clientId"
            v-model="row.status"
            :custom-value="['1', '0']"
            @click.stop
            @change="handleStatusChange(row)"
          />
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['system:client:query']" @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link v-hasPermi="['system:client:edit']" @click.stop="handleUpdate(row)">
              <template #prefix-icon><edit-icon /></template>修改
            </my-link>
            <my-link
              v-if="row.clientId !== clientId"
              v-hasPermi="['system:client:remove']"
              theme="danger"
              @click.stop="handleDelete(row)"
            >
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 添加或修改系统授权对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      width="600px"
      attach="body"
      :confirm-btn="{
        loading: buttonLoading,
      }"
      @confirm="onConfirm"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="clientRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(9em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitForm"
        >
          <t-form-item label="客户端key" name="clientKey">
            <t-input v-model="form.clientKey" placeholder="请输入客户端key" clearable />
          </t-form-item>
          <t-form-item label="客户端秘钥" name="clientSecret">
            <t-input v-model="form.clientSecret" placeholder="请输入客户端秘钥" clearable />
          </t-form-item>
          <t-form-item label="授权类型" name="grantTypeList">
            <t-select
              v-model="form.grantTypeList"
              multiple
              placeholder="请选择授权类型"
              clearable
              :tag-props="{ theme: 'primary', variant: 'light' }"
            >
              <t-option v-for="dict in sys_grant_type" :key="dict.value" :label="dict.label" :value="dict.value" />
            </t-select>
          </t-form-item>
          <t-form-item label="设备类型" name="deviceType">
            <t-select v-model="form.deviceType" placeholder="请选择设备类型" clearable>
              <t-option v-for="dict in sys_device_type" :key="dict.value" :label="dict.label" :value="dict.value" />
            </t-select>
          </t-form-item>
          <t-form-item label="token活跃超时时间" name="activeTimeout">
            <t-input-number v-model="form.activeTimeout" clearable :min="60" :allow-input-over-limit="false" />
          </t-form-item>
          <t-form-item label="token固定超时" name="timeout">
            <t-input-number v-model="form.timeout" clearable :min="180" :allow-input-over-limit="false" />
          </t-form-item>
          <t-form-item v-if="form.clientId !== clientId" label="状态" name="status">
            <t-radio-group v-model="form.status">
              <t-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">
                {{ dict.label }}
              </t-radio>
            </t-radio-group>
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 系统授权详情 -->
    <t-dialog v-model:visible="openView" header="系统授权详情" width="min(900px, 100%)" attach="body" :footer="false">
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="客户端id">{{ form.clientId }}</t-descriptions-item>
        <t-descriptions-item label="客户端key">{{ form.clientKey }}</t-descriptions-item>
        <t-descriptions-item label="客户端秘钥">{{ form.clientSecret }}</t-descriptions-item>
        <t-descriptions-item label="授权类型">
          <dict-tag :options="sys_grant_type" :value="form.grantType" />
        </t-descriptions-item>
        <t-descriptions-item label="设备类型">
          <dict-tag :options="sys_device_type" :value="form.deviceType" />
        </t-descriptions-item>
        <t-descriptions-item label="token活跃超时时间">{{ form.activeTimeout }}</t-descriptions-item>
        <t-descriptions-item label="token固定超时">{{ form.timeout }}</t-descriptions-item>
        <t-descriptions-item label="状态">
          <dict-tag :options="sys_normal_disable" :value="form.status" />
        </t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'Client',
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
  TableRowData,
  TableSort,
} from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import { addClient, changeStatus, delClient, getClient, listClient, updateClient } from '@/api/system/client';
import type { SysClientForm, SysClientQuery, SysClientVo } from '@/api/system/model/clientModel';
import { ArrayOps } from '@/utils/array';
import { handleChangeStatus } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance();
const { sys_device_type, sys_grant_type, sys_normal_disable } = proxy.useDict(
  'sys_device_type',
  'sys_grant_type',
  'sys_normal_disable',
);

const clientList = ref<SysClientVo[]>([]);
const clientRef = ref<FormInstanceFunctions>();
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
const clientId = import.meta.env.VITE_CLIENT_ID;

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  clientKey: [{ required: true, message: '客户端key不能为空' }],
  clientSecret: [{ required: true, message: '客户端秘钥不能为空' }],
  grantTypeList: [{ required: true, message: '授权类型不能为空' }],
  deviceType: [{ required: true, message: '设备类型不能为空' }],
});
// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  {
    title: `选择列`,
    colKey: 'row-select',
    type: 'multiple',
    width: 50,
    align: 'center',
    disabled: (options: { row: TableRowData; rowIndex: number }) => options.row.clientId === clientId,
  },
  { title: `客户端id`, colKey: 'clientId', align: 'center', ellipsis: true },
  { title: `客户端key`, colKey: 'clientKey', align: 'center' },
  { title: `授权类型`, colKey: 'grantType', align: 'center' },
  { title: `设备类型`, colKey: 'deviceType', align: 'center' },
  { title: `token活跃超时时间`, colKey: 'activeTimeout', align: 'center', sorter: true },
  { title: `token固定超时`, colKey: 'timeout', align: 'center', sorter: true },
  { title: `状态`, colKey: 'status', align: 'center', sorter: true },
  { title: `创建时间`, colKey: 'createTime', align: 'center', sorter: true, width: '10%', minWidth: 112 },
  { title: `更新时间`, colKey: 'updateTime', align: 'center', sorter: true, width: '10%', minWidth: 112 },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 提交表单对象
const form = ref<SysClientVo & SysClientForm>({
  grantTypeList: [],
});
// 查询对象
const queryParams = ref<SysClientQuery>({
  pageNum: 1,
  pageSize: 10,
  clientId: undefined,
  clientKey: undefined,
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

/** 查询系统授权列表 */
function getList() {
  loading.value = true;
  listClient(queryParams.value)
    .then((response) => {
      clientList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}

// 表单重置
function reset() {
  form.value = {
    grantTypeList: [],
    status: '1',
  };
  proxy.resetForm('clientRef');
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
  title.value = '添加系统授权';
}

/** 详情按钮操作 */
function handleDetail(row: SysClientVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const id = row.id;
  getClient(id).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysClientVo) {
  buttonLoading.value = true;
  reset();
  open.value = true;
  title.value = '修改系统授权';
  const id = row?.id || ids.value.at(0);
  getClient(id).then((response) => {
    buttonLoading.value = false;
    form.value = { ...response.data, grantTypeList: response.data.grantType?.split(',') };
  });
}

/** 状态修改 */
function handleStatusChange(row: SysClientVo) {
  handleChangeStatus(clientList.value, row, 'id', 'status', `${row.clientKey}客户端`, (obj) =>
    changeStatus(obj.clientId, obj.status),
  );
}

/** 提交按钮 */
function onConfirm() {
  clientRef.value.submit();
}

/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    form.value.grantType = form.value.grantTypeList?.join(',');
    if (form.value.id) {
      updateClient(form.value)
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
      addClient(form.value)
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
function handleDelete(row?: SysClientVo) {
  const $ids = row?.id || ids.value;
  proxy.$modal.confirm(`是否确认删除系统授权编号为${$ids}的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delClient($ids)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, $ids);
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
    'system/client/export',
    {
      ...queryParams.value,
    },
    `client_${new Date().getTime()}.xlsx`,
  );
}

getList();
</script>
