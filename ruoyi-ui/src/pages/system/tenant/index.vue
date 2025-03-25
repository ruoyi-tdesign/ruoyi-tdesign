<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="租户编号" name="tenantId">
          <t-input v-model="queryParams.tenantId" placeholder="请输入租户编号" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="联系人" name="contactUserName">
          <t-input v-model="queryParams.contactUserName" placeholder="请输入联系人" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="联系电话" name="contactPhone">
          <t-input v-model="queryParams.contactPhone" placeholder="请输入联系电话" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="企业名称" name="companyName">
          <t-input v-model="queryParams.companyName" placeholder="请输入企业名称" clearable @enter="handleQuery" />
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
        :data="tenantList"
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
              <t-button v-hasPermi="['system:tenant:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:tenant:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:tenant:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button v-hasPermi="['system:tenant:export']" theme="default" variant="outline" @click="handleExport">
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
          <t-switch v-model="row.status" :custom-value="['1', '0']" @click.stop @change="handleStatusChange(row)" />
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['system:tenant:query']" @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link v-hasPermi="['system:tenant:edit']" @click.stop="handleUpdate(row)">
              <template #prefix-icon><edit-icon /></template>修改
            </my-link>
            <my-link
              v-if="row.packageId"
              v-hasPermi="['system:tenant:edit']"
              @click.stop="handleSyncTenantPackage(row)"
            >
              <template #prefix-icon><edit-icon /></template>同步套餐
            </my-link>
            <my-link v-hasPermi="['system:tenant:remove']" theme="danger" @click.stop="handleDelete(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 添加或修改租户对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      placement="center"
      width="800px"
      attach="body"
      :confirm-btn="{
        loading: buttonLoading,
      }"
      @confirm="onConfirm"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="tenantRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(4em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitForm"
        >
          <t-row :gutter="[0, 20]">
            <t-col :span="6">
              <t-form-item label="企业名称" name="companyName">
                <t-input v-model="form.companyName" placeholder="请输入企业名称" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="联系人" name="contactUserName">
                <t-input v-model="form.contactUserName" placeholder="请输入联系人" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="联系电话" name="contactPhone">
                <t-input v-model="form.contactPhone" placeholder="请输入联系电话" />
              </t-form-item>
            </t-col>
            <t-col v-if="form.tenantId !== DEFAULT_TENANT_ID" :span="6">
              <t-form-item label="租户套餐" name="packageId">
                <t-select v-model="form.packageId" placeholder="请选择租户套餐" clearable>
                  <t-option
                    v-for="item in packageList"
                    :key="item.packageId"
                    :label="item.packageName"
                    :value="item.packageId"
                  />
                </t-select>
              </t-form-item>
            </t-col>
            <t-col v-if="!form.id" :span="6">
              <t-form-item label="用户名" name="username">
                <t-input v-model="form.username" :maxlength="30" placeholder="请输入系统用户名" />
              </t-form-item>
            </t-col>
            <t-col v-if="!form.id" :span="6">
              <t-form-item label="用户密码" name="password">
                <t-input v-model="form.password" type="password" :maxlength="20" placeholder="请输入系统用户密码" />
              </t-form-item>
            </t-col>
            <t-col v-if="form.tenantId !== DEFAULT_TENANT_ID" :span="6">
              <t-form-item label="过期时间" name="expireTime">
                <t-date-picker
                  v-model="form.expireTime"
                  enable-time-picker
                  allow-input
                  clearable
                  placeholder="请选择过期时间"
                />
              </t-form-item>
            </t-col>
            <t-col v-if="form.tenantId !== DEFAULT_TENANT_ID" :span="6">
              <t-form-item label="用户数量" name="accountCount" help="-1不限制">
                <t-input-number v-model="form.accountCount" placeholder="请输入用户数量" :min="-1" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="域名" name="domain">
                <t-input v-model="form.domain" placeholder="请输入域名" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="地址" name="address">
                <t-input v-model="form.address" placeholder="请输入地址" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="企业代码" name="licenseNumber">
                <t-input v-model="form.licenseNumber" placeholder="请输入统一社会信用代码" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="企业简介" name="intro">
                <t-textarea v-model="form.intro" placeholder="请输入企业简介" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="备注" name="remark">
                <t-textarea v-model="form.remark" placeholder="请输入备注" />
              </t-form-item>
            </t-col>
          </t-row>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 租户详情 -->
    <t-dialog
      v-model:visible="openView"
      header="租户详情"
      placement="center"
      width="700px"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="id">{{ form.id }}</t-descriptions-item>
        <t-descriptions-item label="租户编号">{{ form.tenantId }}</t-descriptions-item>
        <t-descriptions-item label="联系人">{{ form.contactUserName }}</t-descriptions-item>
        <t-descriptions-item label="联系电话">{{ form.contactPhone }}</t-descriptions-item>
        <t-descriptions-item label="企业名称">{{ form.companyName }}</t-descriptions-item>
        <t-descriptions-item label="统一社会信用代码">{{ form.licenseNumber }}</t-descriptions-item>
        <t-descriptions-item label="地址">{{ form.address }}</t-descriptions-item>
        <t-descriptions-item label="企业简介" :span="2">{{ form.intro }}</t-descriptions-item>
        <t-descriptions-item label="域名">{{ form.domain }}</t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ form.remark }}</t-descriptions-item>
        <t-descriptions-item label="租户套餐编号">{{ form.packageId }}</t-descriptions-item>
        <t-descriptions-item label="过期时间">{{ parseTime(form.expireTime) }}</t-descriptions-item>
        <t-descriptions-item label="用户数量">{{ form.accountCount }}</t-descriptions-item>
        <t-descriptions-item label="租户状态">
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
  name: 'Tenant',
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

import type { SysTenantForm, SysTenantQuery, SysTenantVo } from '@/api/system/model/tenantModel';
import type { SysTenantPackageVo } from '@/api/system/model/tenantPackageModel';
import {
  addTenant,
  changeTenantStatus,
  delTenant,
  getTenant,
  listTenant,
  syncTenantPackage,
  updateTenant,
} from '@/api/system/tenant';
import { selectTenantPackage } from '@/api/system/tenantPackage';
import { DEFAULT_TENANT_ID } from '@/constants';
import { ArrayOps } from '@/utils/array';
import { handleChangeStatus } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const tenantList = ref<SysTenantVo[]>([]);
const tenantRef = ref<FormInstanceFunctions>();
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
// 租户套餐列表
const packageList = ref<SysTenantPackageVo[]>([]);

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  contactUserName: [{ required: true, message: '联系人不能为空' }],
  contactPhone: [
    { required: true, message: '联系电话不能为空' },
    { telnumber: true, message: '联系电话格式错误' },
  ],
  companyName: [{ required: true, message: '企业名称不能为空' }],
  packageId: [{ required: true, message: '租户套餐不能为空' }],
  username: [
    { required: true, message: '用户名不能为空' },
    { min: 2, max: 20, message: '用户名称长度必须介于 2 和 20 之间' },
  ],
  password: [
    { required: true, message: '密码不能为空' },
    { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间' },
  ],
});
// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `租户编号`, colKey: 'tenantId', align: 'center' },
  { title: `联系人`, colKey: 'contactUserName', align: 'center' },
  { title: `联系电话`, colKey: 'contactPhone', align: 'center' },
  { title: `企业名称`, colKey: 'companyName', align: 'center' },
  { title: `社会信用代码`, colKey: 'licenseNumber', align: 'center' },
  { title: `过期时间`, colKey: 'expireTime', align: 'center' },
  { title: `租户状态`, colKey: 'status', align: 'center' },
  { title: `更新时间`, colKey: 'updateTime', align: 'center', width: '10%', minWidth: 112 },
  { title: `创建时间`, colKey: 'createTime', align: 'center', width: '10%', minWidth: 112, sorter: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: '10%', minWidth: 160 },
]);
// 提交表单对象
const form = ref<SysTenantVo & SysTenantForm>({});
// 查询对象
const queryParams = ref<SysTenantQuery>({
  pageNum: 1,
  pageSize: 10,
  tenantId: undefined,
  contactUserName: undefined,
  contactPhone: undefined,
  companyName: undefined,
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

/** 查询租户列表 */
function getList() {
  loading.value = true;
  listTenant(queryParams.value)
    .then((response) => {
      tenantList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}

// 表单重置
function reset() {
  form.value = {
    accountCount: -1,
  };
  proxy.resetForm('tenantRef');
}

/** 查询所有租户套餐 */
function getTenantPackage() {
  selectTenantPackage().then((res) => {
    packageList.value = res.data;
  });
}

// 租户套餐状态修改
function handleStatusChange(row: SysTenantVo) {
  handleChangeStatus(tenantList.value, row, 'tenantId', 'status', `${row.companyName}租户`, (data) =>
    changeTenantStatus(data.id, data.tenantId, data.status).then(() => getList()),
  );
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
function handleSelectionChange(selection: Array<number | string>) {
  ids.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加租户';
  getTenantPackage();
}

/** 详情按钮操作 */
function handleDetail(row: SysTenantVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const id = row.id;
  getTenant(id).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysTenantVo) {
  buttonLoading.value = true;
  reset();
  open.value = true;
  title.value = '修改租户';
  const id = row?.id || ids.value.at(0);
  getTenantPackage();
  getTenant(id).then((response) => {
    buttonLoading.value = false;
    form.value = response.data;
  });
}

/** 提交按钮 */
function onConfirm() {
  tenantRef.value.submit();
}

/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.id) {
      updateTenant(form.value)
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
      addTenant(form.value)
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
function handleDelete(row?: SysTenantVo) {
  const $ids = row?.id || ids.value;
  proxy.$modal.confirm(`是否确认删除租户编号为${$ids}的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delTenant($ids)
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

/** 同步租户套餐按钮操作 */
function handleSyncTenantPackage(row: SysTenantVo) {
  proxy.$modal.confirm(`是否确认同步租户套餐租户编号为"${row.tenantId}"的数据项？`, () => {
    loading.value = true;
    const msgLoading = proxy.$modal.msgLoading('正在同步中...');
    return syncTenantPackage(row.tenantId, row.packageId)
      .then(() => {
        getList();
        proxy.$modal.msgSuccess('同步成功');
      })
      .finally(() => {
        loading.value = false;
        proxy.$modal.msgClose(msgLoading);
      });
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'system/tenant/export',
    {
      ...queryParams.value,
    },
    `tenant_${new Date().getTime()}.xlsx`,
  );
}

getList();
</script>
