<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="配置key" name="configKey">
          <t-input
            v-model="queryParams.configKey"
            placeholder="配置key"
            clearable
            style="width: 200px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="桶名称" name="bucketName">
          <t-input
            v-model="queryParams.bucketName"
            placeholder="请输入桶名称"
            clearable
            style="width: 200px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="是否默认" name="status">
          <t-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
            <t-option key="1" label="是" value="1" />
            <t-option key="0" label="否" value="0" />
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
        row-key="ossConfigId"
        :data="ossConfigList"
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
              <t-button v-hasPermi="['system:ossConfig:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:ossConfig:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:ossConfig:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
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
        <template #accessPolicy="{ row }">
          <dict-tag :options="accessPolicyOptions" :value="row.accessPolicy" />
        </template>
        <template #status="{ row }">
          <t-switch
            v-model="row.status"
            :custom-value="['1', '0']"
            @change="handleStatusChange(row)"
            @click.stop
          ></t-switch>
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <t-link
              v-hasPermi="['system:ossConfig:query']"
              theme="primary"
              hover="color"
              @click.stop="handleDetail(row)"
            >
              <browse-icon />详情
            </t-link>
            <t-link
              v-hasPermi="['system:ossConfig:edit']"
              theme="primary"
              hover="color"
              @click.stop="handleUpdate(row)"
            >
              <edit-icon />修改
            </t-link>
            <t-link
              v-hasPermi="['system:ossConfig:remove']"
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

    <!-- 添加或修改对象存储配置对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      width="800px"
      attach="body"
      top="3vh"
      :confirm-btn="{
        loading: buttonLoading,
      }"
      @confirm="onConfirm"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form ref="ossConfigRef" :data="form" :rules="rules" label-width="calc(6em + 41px)" @submit="submitForm">
          <t-form-item label="配置key" name="configKey">
            <t-input v-model="form.configKey" placeholder="请输入配置key" />
          </t-form-item>
          <t-form-item label="访问站点" name="endpoint">
            <t-input v-model="form.endpoint" placeholder="请输入访问站点" />
          </t-form-item>
          <t-form-item label="自定义域名" name="domain">
            <t-input v-model="form.domain" placeholder="请输入自定义域名" />
          </t-form-item>
          <t-form-item label="accessKey" name="accessKey">
            <t-input v-model="form.accessKey" placeholder="请输入accessKey" />
          </t-form-item>
          <t-form-item label="secretKey" name="secretKey">
            <t-input v-model="form.secretKey" placeholder="请输入秘钥" type="password" />
          </t-form-item>
          <t-form-item label="桶名称" name="bucketName">
            <t-input v-model="form.bucketName" placeholder="请输入桶名称" />
          </t-form-item>
          <t-form-item label="前缀" name="prefix">
            <t-input v-model="form.prefix" placeholder="请输入前缀" />
          </t-form-item>
          <t-form-item label="是否HTTPS">
            <t-radio-group v-model="form.isHttps">
              <t-radio v-for="dict in sys_yes_no" :key="dict.value" :value="dict.value">{{ dict.label }}</t-radio>
            </t-radio-group>
          </t-form-item>
          <t-form-item label="桶权限类型">
            <t-radio-group v-model="form.accessPolicy" :options="accessPolicyOptions" />
          </t-form-item>
          <t-form-item label="域" name="region">
            <t-input v-model="form.region" placeholder="请输入域" />
          </t-form-item>
          <t-form-item label="备注" name="remark">
            <t-textarea v-model="form.remark" placeholder="请输入备注" />
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 对象存储配置详情 -->
    <t-dialog
      v-model:visible="openView"
      header="对象存储配置详情"
      placement="center"
      width="700px"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="主建">{{ form.ossConfigId }}</t-descriptions-item>
        <t-descriptions-item label="配置key">{{ form.configKey }}</t-descriptions-item>
        <t-descriptions-item label="accessKey">{{ form.accessKey }}</t-descriptions-item>
        <t-descriptions-item label="秘钥">{{ form.secretKey }}</t-descriptions-item>
        <t-descriptions-item label="桶名称">{{ form.bucketName }}</t-descriptions-item>
        <t-descriptions-item label="前缀">{{ form.prefix }}</t-descriptions-item>
        <t-descriptions-item label="访问站点">{{ form.endpoint }}</t-descriptions-item>
        <t-descriptions-item label="自定义域名">{{ form.domain }}</t-descriptions-item>
        <t-descriptions-item label="是否https">
          <dict-tag :options="sys_yes_no" :value="form.isHttps" />
        </t-descriptions-item>
        <t-descriptions-item label="域">{{ form.region }}</t-descriptions-item>
        <t-descriptions-item label="桶权限类型">
          <dict-tag :options="accessPolicyOptions" :value="form.accessPolicy" />
        </t-descriptions-item>
        <t-descriptions-item label="状态">
          <dict-tag :options="sys_normal_disable" :value="form.status" />
        </t-descriptions-item>
        <t-descriptions-item label="扩展字段">{{ form.ext1 }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ form.remark }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'OssConfig',
});

import {
  AddIcon,
  BrowseIcon,
  DeleteIcon,
  EditIcon,
  RefreshIcon,
  SearchIcon,
  SettingIcon,
} from 'tdesign-icons-vue-next';
import type { FormInstanceFunctions, FormRule, PageInfo, PrimaryTableCol, SubmitContext } from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import type { SysOssConfigForm, SysOssConfigQuery, SysOssConfigVo } from '@/api/system/model/ossConfigModel';
import {
  addOssConfig,
  changeOssConfigStatus,
  delOssConfig,
  getOssConfig,
  listOssConfig,
  updateOssConfig,
} from '@/api/system/ossConfig';
import { ArrayOps } from '@/utils/array';
import type { DictModel } from '@/utils/dict';
import { handleChangeStatus } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance();
const { sys_yes_no, sys_normal_disable } = proxy.useDict('sys_yes_no', 'sys_normal_disable');

const openView = ref(false);
const openViewLoading = ref(false);
const ossConfigList = ref<SysOssConfigVo[]>([]);
const ossConfigRef = ref<FormInstanceFunctions>();
const open = ref(false);
const buttonLoading = ref(false);
const loading = ref(false);
const columnControllerVisible = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const accessPolicyOptions = ref<DictModel[]>([
  { value: '0', tagType: 'warning', label: 'private' },
  { value: '1', tagType: 'success', label: 'public' },
  { value: '2', tagType: 'default', label: 'custom' },
]);

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  // { title: `主建`, colKey: 'ossConfigId', align: 'center' },
  { title: `配置key`, colKey: 'configKey', align: 'center' },
  { title: `访问站点`, colKey: 'endpoint', align: 'center', ellipsis: true },
  { title: `自定义域名`, colKey: 'domain', align: 'center', ellipsis: true },
  { title: `桶名称`, colKey: 'bucketName', align: 'center' },
  { title: `前缀`, colKey: 'prefix', align: 'center' },
  { title: `域`, colKey: 'region', align: 'center' },
  { title: `桶权限类型`, colKey: 'accessPolicy', align: 'center' },
  { title: `是否默认`, colKey: 'status', align: 'center' },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);

const rules = ref<Record<string, Array<FormRule>>>({
  configKey: [{ required: true, message: 'configKey不能为空' }],
  accessKey: [
    { required: true, message: 'accessKey不能为空' },
    {
      min: 2,
      max: 200,
      message: 'accessKey长度必须介于 2 和 100 之间',
      trigger: 'blur',
    },
  ],
  secretKey: [
    { required: true, message: 'secretKey不能为空' },
    {
      min: 2,
      max: 100,
      message: 'secretKey长度必须介于 2 和 100 之间',
      trigger: 'blur',
    },
  ],
  bucketName: [
    { required: true, message: 'bucketName不能为空' },
    {
      min: 2,
      max: 100,
      message: 'bucketName长度必须介于 2 和 100 之间',
      trigger: 'blur',
    },
  ],
  endpoint: [
    { required: true, message: 'endpoint不能为空' },
    {
      min: 2,
      max: 100,
      message: 'endpoint名称长度必须介于 2 和 100 之间',
      trigger: 'blur',
    },
  ],
  accessPolicy: [{ required: true, message: 'accessPolicy不能为空' }],
});
// 提交表单对象
const form = ref<SysOssConfigVo & SysOssConfigForm>({
});
// 查询对象
const queryParams = ref<SysOssConfigQuery>({
  pageNum: 1,
  pageSize: 10,
  configKey: undefined,
  bucketName: undefined,
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

/** 查询对象存储配置列表 */
function getList() {
  loading.value = true;
  listOssConfig(queryParams.value)
    .then((response) => {
      ossConfigList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}
/** 表单重置 */
function reset() {
  form.value = {
    isHttps: 'N',
    accessPolicy: '1',
    status: '1',
  };
  proxy.resetForm('ossConfigRef');
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
  title.value = '添加对象存储配置';
}

/** 详情按钮操作 */
function handleDetail(row: SysOssConfigVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const ossConfigId = row.ossConfigId;
  getOssConfig(ossConfigId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysOssConfigVo) {
  buttonLoading.value = true;
  reset();
  open.value = true;
  title.value = '修改对象存储配置';
  const ossConfigId = row?.ossConfigId || ids.value.at(0);
  getOssConfig(ossConfigId).then((response) => {
    buttonLoading.value = false;
    form.value = response.data;
  });
}
function onConfirm() {
  ossConfigRef.value.submit();
}
/** 提交按钮 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.ossConfigId) {
      updateOssConfig(form.value)
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
      addOssConfig(form.value)
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
/** 用户状态修改  */
function handleStatusChange(row: SysOssConfigVo) {
  handleChangeStatus(ossConfigList.value, row, 'ossConfigId', 'status', `${row.configKey}配置`, (ossConfig) =>
    changeOssConfigStatus(ossConfig.ossConfigId, ossConfig.status, ossConfig.configKey).then(() => getList()),
  );
}
/** 删除按钮操作 */
function handleDelete(row?: SysOssConfigVo) {
  const ossConfigIds = row?.ossConfigId || ids.value;
  proxy.$modal.confirm(`是否确认删除OSS配置编号为"${ossConfigIds}"的数据项?`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delOssConfig(ossConfigIds)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, ossConfigIds);
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => {
        proxy.$modal.msgClose(msgLoading);
      });
  });
}

getList();
</script>
