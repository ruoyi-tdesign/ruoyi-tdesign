<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form
        v-show="showSearch"
        ref="queryRef"
        :data="queryParams"
        layout="inline"
        reset-type="initial"
        label-width="calc(4em + 12px)"
      >
        <t-form-item label="应用类型" name="appType">
          <t-select v-model="queryParams.appType" placeholder="请选择应用类型" clearable>
            <t-option v-for="dict in sys_app_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </t-select>
        </t-form-item>
        <t-form-item label="应用key" name="appKey">
          <t-input v-model="queryParams.appKey" placeholder="请输入应用key" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="应用名称" name="appName">
          <t-input v-model="queryParams.appName" placeholder="请输入应用名称" clearable @enter="handleQuery" />
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
        row-key="appid"
        :data="tenantAppList"
        :columns="columns"
        :selected-row-keys="ids"
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
              <t-button v-hasPermi="['system:tenantApp:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:tenantApp:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:tenantApp:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button
                v-hasPermi="['system:tenantApp:export']"
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
        <template #appType="{ row }">
          <dict-tag :options="sys_app_type" :value="row.appType" />
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['system:tenantApp:query']" @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link v-hasPermi="['system:tenantApp:edit']" @click.stop="handleUpdate(row)">
              <template #prefix-icon><edit-icon /></template>修改
            </my-link>
            <my-link v-hasPermi="['system:tenantApp:remove']" theme="danger" @click.stop="handleDelete(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 添加或修改租户应用管理对话框 -->
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
      @confirm="tenantAppRef.submit()"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="tenantAppRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(4em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitForm"
        >
          <t-form-item label="应用名称" name="appName">
            <t-input v-model="form.appName" placeholder="请输入应用名称" clearable />
          </t-form-item>
          <t-form-item label="应用类型" name="appType">
            <t-select v-model="form.appType" placeholder="请选择应用类型" clearable>
              <t-option v-for="dict in sys_app_type" :key="dict.value" :label="dict.label" :value="dict.value" />
            </t-select>
          </t-form-item>
          <t-form-item label="应用key" name="appKey" help="请求头X-APP-KEY">
            <t-input v-model="form.appKey" placeholder="请输入应用key" clearable />
          </t-form-item>
          <t-form-item label="备注" name="remark">
            <t-textarea v-model="form.remark" placeholder="请输入备注" />
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 租户应用管理详情 -->
    <t-dialog
      v-model:visible="openView"
      header="租户应用管理详情"
      placement="center"
      width="700px"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="应用id">{{ form.appid }}</t-descriptions-item>
        <t-descriptions-item label="应用类型">
          <dict-tag :options="sys_app_type" :value="form.appType" />
        </t-descriptions-item>
        <t-descriptions-item label="应用key">{{ form.appKey }}</t-descriptions-item>
        <t-descriptions-item label="应用名称">{{ form.appName }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ form.remark }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'TenantApp',
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
import type { FormInstanceFunctions, FormRule, PageInfo, PrimaryTableCol, SubmitContext } from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import type { SysTenantAppForm, SysTenantAppQuery, SysTenantAppVo } from '@/api/system/model/tenantAppModel';
import { addTenantApp, delTenantApp, getTenantApp, listTenantApp, updateTenantApp } from '@/api/system/tenantApp';
import { ArrayOps } from '@/utils/array';

const { proxy } = getCurrentInstance();
const { sys_app_type } = proxy.useDict('sys_app_type');

const openView = ref(false);
const openViewLoading = ref(false);
const tenantAppRef = ref<FormInstanceFunctions>();
const open = ref(false);
const buttonLoading = ref(false);
const title = ref('');
const tenantAppList = ref<SysTenantAppVo[]>([]);
const loading = ref(false);
const columnControllerVisible = ref(false);
const showSearch = ref(true);
const total = ref(0);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  appType: [{ required: true, message: '应用类型不能为空' }],
  appKey: [{ required: true, message: '应用key不能为空' }],
  appName: [{ required: true, message: '应用名称不能为空' }],
});

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `应用类型`, colKey: 'appType', align: 'center' },
  { title: `应用key`, colKey: 'appKey', align: 'center' },
  { title: `应用名称`, colKey: 'appName', align: 'center' },
  { title: `更新时间`, colKey: 'updateTime', align: 'center', width: 180 },
  { title: `创建时间`, colKey: 'createTime', align: 'center', width: 180 },
  { title: `备注`, colKey: 'remark', align: 'center', ellipsis: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 提交表单对象
const form = ref<SysTenantAppVo & SysTenantAppForm>({});
// 查询对象
const queryParams = ref<SysTenantAppQuery>({
  pageNum: 1,
  pageSize: 10,
  appType: undefined,
  appKey: undefined,
  appName: undefined,
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

/** 查询租户应用管理列表 */
function getList() {
  loading.value = true;
  listTenantApp(queryParams.value)
    .then((response) => {
      tenantAppList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}

// 表单重置
function reset() {
  form.value = {};
  proxy.resetForm('tenantAppRef');
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
  handleQuery();
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
  title.value = '添加租户应用管理';
}

/** 详情按钮操作 */
function handleDetail(row: SysTenantAppVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const appid = row.appid;
  getTenantApp(appid).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysTenantAppVo) {
  buttonLoading.value = true;
  reset();
  open.value = true;
  title.value = '修改租户应用管理';
  const appid = row?.appid || ids.value.at(0);
  getTenantApp(appid).then((response) => {
    buttonLoading.value = false;
    form.value = response.data;
  });
}

/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.appid) {
      updateTenantApp(form.value)
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
      addTenantApp(form.value)
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
function handleDelete(row?: SysTenantAppVo) {
  const appids = row?.appid || ids.value;
  proxy.$modal.confirm(`是否确认删除租户应用管理编号为${appids}的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delTenantApp(appids)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, appids);
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
    'system/tenantApp/export',
    {
      ...queryParams.value,
    },
    `tenantApp_${new Date().getTime()}.xlsx`,
  );
}

getList();
</script>
