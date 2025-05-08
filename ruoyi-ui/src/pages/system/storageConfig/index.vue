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
        <t-form-item label="平台" name="platform">
          <t-select v-model="queryParams.platform" placeholder="请选择平台" clearable>
            <t-option label="请选择字典生成" value="" />
          </t-select>
        </t-form-item>
        <t-form-item label="启用状态" name="status">
          <t-select v-model="queryParams.status" placeholder="请选择启用状态" clearable>
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
        row-key="storageConfigId"
        :data="storageConfigList"
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
              <t-button v-hasPermi="['system:storageConfig:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:storageConfig:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:storageConfig:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button
                v-hasPermi="['system:storageConfig:export']"
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
        <template #platform="{ row }">
          <dict-tag :options="allStoragePlatform" :value="row.platform" />
        </template>
        <template #status="{ row }">
          <dict-tag :options="sys_normal_disable" :value="row.status" />
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['system:storageConfig:query']" @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link v-hasPermi="['system:storageConfig:edit']" @click.stop="handleUpdate(row)">
              <template #prefix-icon><edit-icon /></template>修改
            </my-link>
            <my-link
              v-hasPermi="['system:storageConfig:remove']"
              size="small"
              theme="danger"
              @click.stop="handleDelete(row)"
            >
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 添加或修改存储配置对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      width="min(800px, 100%)"
      placement="center"
      attach="body"
      :confirm-btn="{
        loading: buttonLoading,
      }"
      @confirm="storageConfigRef.submit()"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="storageConfigRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(9em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitForm"
        >
          <t-row :gutter="[0, 20]">
            <t-col :span="12">
              <t-form-item label="平台" name="platform">
                <t-select v-model="form.platform" :options="supportPlatform" placeholder="请选择平台" clearable />
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item
                label="负载均衡权重"
                name="weight"
                help="启用多个存储方案时，将适用负载均衡方式，此为负载权重"
              >
                <t-input-number v-model="form.weight" placeholder="请输入" />
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="启用状态" name="status">
                <t-select v-model="form.status" placeholder="请选择启用状态" clearable>
                  <t-option
                    v-for="dict in sys_normal_disable"
                    :key="dict.value"
                    :label="dict.label"
                    :value="parseInt(dict.value)"
                  />
                </t-select>
              </t-form-item>
            </t-col>
            <template v-for="(value, key) in currentStoragePlatformConfigs" :key="form.platform + key">
              <form-field-render
                v-model="form.configObject[key]"
                :config-value="form.configObject"
                :name="`configObject[${key}]`"
                :field-config="value"
                :init-value="!form.storageConfigId"
              />
            </template>
            <t-col :span="12">
              <t-form-item label="备注" name="remark">
                <t-textarea v-model="form.remark" placeholder="请输入备注" />
              </t-form-item>
            </t-col>
          </t-row>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 存储配置详情 -->
    <t-dialog
      v-model:visible="openView"
      header="存储配置详情"
      placement="center"
      width="min(700px, 100%)"
      attach="body"
      :footer="false"
    >
      <field-descriptions
        :loading="openViewLoading"
        :config-value="form.configObject"
        :field-configs="currentStoragePlatformConfigs"
      >
        <template #prefix>
          <t-descriptions-item label="主建">{{ form.storageConfigId }}</t-descriptions-item>
          <t-descriptions-item label="平台">
            <dict-tag :options="allStoragePlatform" :value="form.platform" />
          </t-descriptions-item>
          <t-descriptions-item label="负载均衡权重">{{ form.weight }}</t-descriptions-item>
          <t-descriptions-item label="启用状态">
            <dict-tag :options="sys_normal_disable" :value="form.status" />
          </t-descriptions-item>
        </template>
        <template #suffix>
          <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
          <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
          <t-descriptions-item label="备注" :span="2">{{ form.remark }}</t-descriptions-item>
        </template>
      </field-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'StorageConfig',
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

import type { FieldConfig, FieldOption } from '@/api/model/fieldConfigModel';
import type {
  SysStorageConfigForm,
  SysStorageConfigQuery,
  SysStorageConfigVo,
} from '@/api/system/model/storageConfigModel';
import {
  addStorageConfig,
  delStorageConfig,
  getAllStoragePlatform,
  getStorageConfig,
  getStoragePlatformConfigs,
  getSupportPlatform,
  listStorageConfig,
  updateStorageConfig,
} from '@/api/system/storageConfig';
import FormFieldRender from '@/components/field-config/FormFieldRender';
import { ArrayOps } from '@/utils/array';

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const openView = ref(false);
const openViewLoading = ref(false);
const storageConfigRef = ref<FormInstanceFunctions>();
const open = ref(false);
const buttonLoading = ref(false);
const title = ref('');
const storageConfigList = ref<SysStorageConfigVo[]>([]);
const loading = ref(false);
const columnControllerVisible = ref(false);
const showSearch = ref(true);
const total = ref(0);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const allStoragePlatform = ref<Array<FieldOption>>();
const supportPlatform = ref<Array<FieldOption>>();
const storagePlatformConfigs = ref<Record<string, Record<string, FieldConfig<any>>>>();

const currentStoragePlatformConfigs = computed(() => {
  if (form.value.platform) {
    return storagePlatformConfigs.value[form.value.platform];
  }
  return null;
});
// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  platform: [
    { required: true, message: '平台不能为空' },
    { max: 50, message: '平台不能超过50个字符' },
  ],
  weight: [{ required: true, message: '负载均衡权重不能为空' }],
  status: [{ required: true, message: '启用状态不能为空' }],
  remark: [{ max: 500, message: '备注不能超过500个字符' }],
});

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `平台`, colKey: 'platform', align: 'center' },
  { title: `负载均衡权重`, colKey: 'weight', align: 'center' },
  { title: `启用状态`, colKey: 'status', align: 'center' },
  { title: `创建时间`, colKey: 'createTime', align: 'center', minWidth: 112, width: 180 },
  { title: `更新时间`, colKey: 'updateTime', align: 'center', minWidth: 112, width: 180 },
  { title: `备注`, colKey: 'remark', align: 'center', ellipsis: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 提交表单对象
const form = ref<SysStorageConfigVo & SysStorageConfigForm>({
  configObject: {},
});
// 查询对象
const queryParams = ref<SysStorageConfigQuery>({
  pageNum: 1,
  pageSize: 10,
  platform: undefined,
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

/**
 * 初始化存储配置信息
 */
function initStoragePlatformInfo() {
  getAllStoragePlatform().then((response) => {
    allStoragePlatform.value = response.data;
  });
  getSupportPlatform().then((response) => {
    supportPlatform.value = response.data;
  });
  getStoragePlatformConfigs().then((response) => {
    storagePlatformConfigs.value = response.data;
  });
}

/** 查询存储配置列表 */
function getList() {
  loading.value = true;
  listStorageConfig(queryParams.value)
    .then((response) => {
      storageConfigList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}

// 表单重置
function reset() {
  form.value = {
    weight: 1,
    status: 1,
    configObject: {},
  };
  proxy.resetForm('storageConfigRef');
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
  title.value = '添加存储配置';
}

/** 详情按钮操作 */
function handleDetail(row: SysStorageConfigVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const storageConfigId = row.storageConfigId;
  getStorageConfig(storageConfigId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysStorageConfigVo) {
  buttonLoading.value = true;
  reset();
  open.value = true;
  title.value = '修改存储配置';
  const storageConfigId = row?.storageConfigId || ids.value.at(0);
  getStorageConfig(storageConfigId).then((response) => {
    buttonLoading.value = false;
    form.value = response.data;
  });
}

/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    form.value.configJson = JSON.stringify(form.value.configObject);
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.storageConfigId) {
      updateStorageConfig(form.value)
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
      addStorageConfig(form.value)
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
function handleDelete(row?: SysStorageConfigVo) {
  const storageConfigIds = row?.storageConfigId || ids.value;
  proxy.$modal.confirm(`是否确认删除存储配置编号为${storageConfigIds}的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delStorageConfig(storageConfigIds)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, storageConfigIds);
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
    'system/storageConfig/export',
    {
      ...queryParams.value,
    },
    `storageConfig_${new Date().getTime()}.xlsx`,
  );
}

onMounted(() => {
  initStoragePlatformInfo();
  getList();
});
</script>
