<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="标题" name="title">
          <t-input v-model="queryParams.title" placeholder="请输入标题" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="消息类型" name="messageType">
          <t-select v-model="queryParams.messageType" placeholder="请选择消息类型" clearable>
            <t-option
              v-for="item in supplierTypeList"
              :key="item.messageType"
              :label="item.description"
              :value="item.messageType"
            />
          </t-select>
        </t-form-item>
        <t-form-item v-if="queryParams.messageType" label="支持平台" name="supplierType">
          <t-select v-model="queryParams.supplierType" placeholder="请选择支持平台" clearable>
            <t-option v-for="item in supplierTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </t-select>
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
        row-key="messageConfigId"
        :data="messageConfigList"
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
              <t-button v-hasPermi="['system:messageConfig:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:messageConfig:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:messageConfig:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button
                v-hasPermi="['system:messageConfig:export']"
                theme="default"
                variant="outline"
                @click="handleExport"
              >
                <template #icon> <download-icon /> </template>
                导出
              </t-button>
              <t-button
                v-hasPermi="['system:messageConfig:remove']"
                theme="danger"
                variant="outline"
                @click="handleRefreshCache"
              >
                <template #icon> <refresh-icon /> </template>
                刷新缓存
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
        <template #supplierType="{ row }">
          <dict-tag :options="supplierTypeOptions" :value="row.supplierType" />
        </template>
        <template #status="{ row }">
          <dict-tag :options="sys_normal_disable" :value="row.status" />
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <t-link
              v-hasPermi="['system:messageConfig:query']"
              theme="primary"
              hover="color"
              @click.stop="handleDetail(row)"
            >
              <browse-icon />详情
            </t-link>
            <t-link
              v-hasPermi="['system:messageConfig:edit']"
              theme="primary"
              hover="color"
              @click.stop="handleUpdate(row)"
            >
              <edit-icon />修改
            </t-link>
            <t-link
              v-hasPermi="['system:messageConfig:remove']"
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

    <!-- 添加或修改消息配置对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      width="800px"
      attach="body"
      top="3%"
      :confirm-btn="{
        loading: buttonLoading,
      }"
      @confirm="onConfirm"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="messageConfigRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(8em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitForm"
        >
          <t-row :gutter="[0, 20]">
            <t-col :span="6">
              <t-form-item label="标题" name="title">
                <t-input v-model.trim="form.title" placeholder="请输入标题" clearable />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="消息类型" name="messageType">
                <t-select
                  v-model="form.messageType"
                  placeholder="请选择消息类型"
                  clearable
                  filterable
                  @change="handleMessageTypeChange"
                >
                  <t-option
                    v-for="dict in messageTypeOptions"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </t-select>
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="支持平台" name="supplierType">
                <t-select
                  v-model="form.supplierType"
                  placeholder="请选择支持平台"
                  clearable
                  filterable
                  @change="handleSupplierTypeChange"
                >
                  <t-option
                    v-for="dict in getSupplierTypeMap(form.messageType)"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </t-select>
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="状态" name="status">
                <t-select v-model="form.status" placeholder="请选择状态" clearable>
                  <t-option
                    v-for="dict in sys_normal_disable"
                    :key="dict.value"
                    :label="dict.label"
                    :value="parseInt(dict.value)"
                  />
                </t-select>
              </t-form-item>
            </t-col>
            <template v-for="(value, key) in messageConfig.supplierConfig" :key="form.supplierType + key">
              <t-col
                v-if="!value.visible || (form.configJson as Record<string, any>)[value.visible]"
                :span="value.span ?? 12"
              >
                <t-form-item
                  :label="value.name"
                  :name="`configJson[${key}]`"
                  :rules="[{ required: value.required, message: `${value.name}不能为空` }, ...(value.rules ?? [])]"
                >
                  <template #help><span v-html="value.help"></span></template>
                  <t-input
                    v-if="value.component === 'input'"
                    v-model.trim="(form.configJson as Record<string, any>)[key]"
                    :placeholder="`请输入${value.name}`"
                    :type="value.type"
                  />
                  <t-input-number
                    v-else-if="value.component === 'input-number'"
                    v-model="(form.configJson as Record<string, any>)[key]"
                    :allow-input-over-limit="false"
                    :min="value.min"
                    :max="value.max"
                    :decimal-places="0"
                  />
                  <t-switch
                    v-else-if="value.component === 'switch'"
                    v-model="(form.configJson as Record<string, any>)[key]"
                  />
                </t-form-item>
              </t-col>
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

    <!-- 消息配置详情 -->
    <t-dialog
      v-model:visible="openView"
      header="消息配置详情"
      placement="center"
      width="700px"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="消息设置id">{{ form.messageConfigId }}</t-descriptions-item>
        <t-descriptions-item label="标题">{{ form.title }}</t-descriptions-item>
        <t-descriptions-item label="消息类型">
          <dict-tag :options="messageTypeOptions" :value="form.messageType" />
        </t-descriptions-item>
        <t-descriptions-item label="支持平台标识">
          <dict-tag :options="supplierTypeOptions" :value="form.supplierType" />
        </t-descriptions-item>
        <t-descriptions-item label="配置json" :span="2">
          <div style="max-height: 300px; width: 100%" class="content-scrollbar">
            <template v-if="!isJson(form.configJson as string)">{{ form.configJson }}...</template>
            <preview-code
              v-else
              :code="JSON.stringify(JSON.parse(form.configJson as string), null, 2)"
              language="json"
              style="width: 100%"
            />
          </div>
        </t-descriptions-item>
        <t-descriptions-item label="状态">
          <dict-tag :options="sys_normal_disable" :value="form.status" />
        </t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ form.remark }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'MessageConfig',
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

import {
  addMessageConfig,
  delMessageConfig,
  getMessageConfig,
  getMessageFieldConfigs,
  getMessageSupplierType,
  listMessageConfig,
  refreshCache,
  updateMessageConfig,
} from '@/api/system/messageConfig';
import type {
  MessageFieldConfig,
  MessageTypeVo,
  SysMessageConfigForm,
  SysMessageConfigQuery,
  SysMessageConfigVo,
} from '@/api/system/model/messageConfigModel';
import { ArrayOps } from '@/utils/array';
import type { DictModel } from '@/utils/dict';
import { isJson } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const messageConfigList = ref<SysMessageConfigVo[]>([]);
const messageConfigRef = ref<FormInstanceFunctions>();
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
const messageFieldConfigs = ref<Record<string, MessageFieldConfig>>({});
const supplierTypeList = ref<MessageTypeVo[]>([]);

const messageConfig = computed<MessageFieldConfig>(() => {
  if (!form.value.supplierType) {
    return {} as MessageFieldConfig;
  }
  return messageFieldConfigs.value[form.value.supplierType];
});

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  title: [{ required: true, message: '标题不能为空' }],
  messageType: [{ required: true, message: '消息类型不能为空' }],
  supplierType: [{ required: true, message: '支持平台标识不能为空' }],
  status: [{ required: true, message: '状态不能为空' }],
});
// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `标题`, colKey: 'title', align: 'center' },
  { title: `消息类型`, colKey: 'messageType', align: 'center' },
  { title: `支持平台标识`, colKey: 'supplierType', align: 'center' },
  { title: `状态`, colKey: 'status', align: 'center' },
  { title: `备注`, colKey: 'remark', align: 'center', ellipsis: true },
  { title: `更新时间`, colKey: 'updateTime', align: 'center', width: 180, sorter: true },
  { title: `创建时间`, colKey: 'createTime', align: 'center', width: 180, sorter: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 提交表单对象
const form = ref<SysMessageConfigVo & SysMessageConfigForm>({
  configJson: {},
});
// 查询对象
const queryParams = ref<SysMessageConfigQuery>({
  pageNum: 1,
  pageSize: 10,
  title: undefined,
  messageType: undefined,
  supplierType: undefined,
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

const getSupplierTypeMap = (messageType: string): DictModel[] => {
  const supplierTypeMap = supplierTypeList.value.find((item) => item.messageType === messageType)?.supplierTypeMap;
  if (!supplierTypeMap) {
    return [];
  }
  return Object.entries(supplierTypeMap).map((value) => {
    return {
      value: value[0],
      label: value[1],
      tagType: 'primary',
    };
  });
};

/** 处理消息类型变更 */
function handleMessageTypeChange() {
  form.value.supplierType = undefined;
  form.value.configJson = {};
}

/** 处理支持平台变更事件 */
function handleSupplierTypeChange() {
  // 默认值赋值
  if (messageConfig.value?.supplierConfig) {
    const configValue = {};
    Object.entries(messageConfig.value.supplierConfig).forEach((value) => {
      // @ts-ignore
      configValue[value[0]] = value[1].value;
    });
    form.value.configJson = configValue;
  } else {
    form.value.configJson = {};
  }
}

/** 查询消息配置列表 */
function getList() {
  loading.value = true;
  listMessageConfig(queryParams.value)
    .then((response) => {
      messageConfigList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}

// 表单重置
function reset() {
  form.value = {
    status: 1,
    configJson: {},
  };
  proxy.resetForm('messageConfigRef');
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
  title.value = '添加消息配置';
}

/** 详情按钮操作 */
function handleDetail(row: SysMessageConfigVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const messageConfigId = row.messageConfigId;
  getMessageConfig(messageConfigId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysMessageConfigVo) {
  buttonLoading.value = true;
  reset();
  open.value = true;
  title.value = '修改消息配置';
  const messageConfigId = row?.messageConfigId || ids.value.at(0);
  getMessageConfig(messageConfigId).then((response) => {
    buttonLoading.value = false;
    form.value = { ...response.data, configJson: JSON.parse(response.data.configJson as string) };
  });
}

/** 提交按钮 */
function onConfirm() {
  messageConfigRef.value.submit();
}

/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    const data = { ...form.value, configJson: JSON.stringify(form.value.configJson) };
    if (form.value.messageConfigId) {
      updateMessageConfig(data)
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
      addMessageConfig(data)
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
function handleDelete(row?: SysMessageConfigVo) {
  const messageConfigIds = row?.messageConfigId || ids.value;
  proxy.$modal.confirm(`是否确认删除消息配置编号为${messageConfigIds}的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delMessageConfig(messageConfigIds)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, messageConfigIds);
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => {
        proxy.$modal.msgClose(msgLoading);
      });
  });
}

/** 刷新缓存 */
function handleRefreshCache() {
  proxy.$modal.confirm(`是否确认刷新消息模板缓存？`, () => {
    return refreshCache().then(() => {
      proxy.$modal.msgSuccess('刷新成功');
    });
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'system/messageConfig/export',
    {
      ...queryParams.value,
    },
    `messageConfig_${new Date().getTime()}.xlsx`,
  );
}

/** 初始化消息配置 */
function initMessageFieldConfigs() {
  getMessageFieldConfigs().then((res) => {
    messageFieldConfigs.value = res.data;
  });
  getMessageSupplierType().then((res) => {
    supplierTypeList.value = res.data;
  });
}

onMounted(() => {
  initMessageFieldConfigs();
  getList();
});
</script>
