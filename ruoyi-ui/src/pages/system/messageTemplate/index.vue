<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="68px">
        <t-form-item label="模板名称" name="templateName">
          <t-input v-model="queryParams.templateName" placeholder="请输入模板名称" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="消息key" name="messageKey">
          <t-input v-model="queryParams.messageKey" placeholder="请输入消息key" clearable @enter="handleQuery" />
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
        row-key="messageTemplateId"
        :data="messageTemplateList"
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
              <t-button v-hasPermi="['system:messageTemplate:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:messageTemplate:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:messageTemplate:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button
                v-hasPermi="['system:messageTemplate:export']"
                theme="default"
                variant="outline"
                @click="handleExport"
              >
                <template #icon> <download-icon /> </template>
                导出
              </t-button>
              <t-button
                v-hasPermi="['system:messageTemplate:remove']"
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
        <template #templateMode="{ row }">
          <dict-tag :options="sys_message_template_mode" :value="row.templateMode" />
        </template>
        <template #status="{ row }">
          <dict-tag :options="sys_normal_disable" :value="row.status" />
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <t-link
              v-hasPermi="['system:messageTemplate:test']"
              theme="primary"
              hover="color"
              @click.stop="handleTest(row)"
            >
              <swap-icon />测试
            </t-link>
            <t-link
              v-hasPermi="['system:messageTemplate:query']"
              theme="primary"
              hover="color"
              @click.stop="handleDetail(row)"
            >
              <browse-icon />详情
            </t-link>
            <t-link
              v-hasPermi="['system:messageTemplate:edit']"
              theme="primary"
              hover="color"
              @click.stop="handleUpdate(row)"
            >
              <edit-icon />修改
            </t-link>
            <t-link
              v-hasPermi="['system:messageTemplate:remove']"
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

    <!-- 添加或修改消息模板对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      placement="center"
      width="700px"
      attach="body"
      :confirm-btn="{
        loading: buttonLoading,
      }"
      @confirm="onConfirm"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="messageTemplateRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(4em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitForm"
        >
          <t-row :gutter="[0, 20]">
            <t-col :span="6">
              <t-form-item label="模板名称" name="templateName">
                <t-input v-model="form.templateName" placeholder="请输入模板名称" clearable />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="消息key" name="messageKeyId">
                <t-select v-model="form.messageKeyId" placeholder="请选择消息key" clearable>
                  <t-option
                    v-for="item in messageKeys"
                    :key="item.messageKeyId"
                    :label="item.name"
                    :value="item.messageKeyId"
                  />
                </t-select>
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="消息类型" name="messageType">
                <t-select
                  v-model="form.messageType"
                  placeholder="请选择消息类型"
                  clearable
                  @change="handleMessageTypeChange($event as string)"
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
            <t-col v-show="form.messageType" :span="6">
              <t-form-item label="消息配置" name="messageConfigId">
                <t-select
                  v-model="form.messageConfigId"
                  placeholder="请选择消息配置"
                  filterable
                  clearable
                  @change="form.templateMode = null"
                >
                  <t-option
                    v-for="item in messageConfigs"
                    :key="item.messageConfigId"
                    :label="item.title"
                    :value="item.messageConfigId"
                  />
                </t-select>
              </t-form-item>
            </t-col>
            <t-col v-if="form.messageType === 'SMS'" :span="6">
              <t-form-item label="签名" name="signature">
                <t-input v-model="form.signature" placeholder="请输入签名" clearable />
              </t-form-item>
            </t-col>
            <t-col v-if="form.messageType === 'MAIL'" :span="6">
              <t-form-item label="标题" name="title" help="支持变量模式：${title}">
                <t-input v-model="form.title" placeholder="请输入标题" clearable @change="handleVarsChange" />
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
            <t-col v-show="form.messageConfigId" :span="6">
              <t-form-item label="模板类型" name="templateMode">
                <t-select v-model="form.templateMode" placeholder="请选择模板类型" clearable>
                  <t-option
                    v-for="dict in sys_message_template_mode.filter(
                      (value) =>
                        (value.value === 'TEMPLATE_ID' && messageConfig?.templateMode.supportTemplateId) ||
                        (value.value === 'TEMPLATE_CONTENT' && messageConfig?.templateMode.supportTemplateContent),
                    )"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </t-select>
              </t-form-item>
            </t-col>
            <t-col v-if="form.templateMode === 'TEMPLATE_ID'" :span="6">
              <t-form-item label="模板ID" name="templateId">
                <t-input v-model="form.templateId" placeholder="请输入模板ID" clearable />
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="内容" name="content">
                <template #help>
                  <b>变量格式</b>：${name}；例如，尊敬的 ${name}，您的快递已飞奔在路上，将今天 ${time}
                  送达您的手里，请留意查收。
                  <p v-if="form.templateMode === 'TEMPLATE_ID'" class="color-red">
                    <b>模版内容以实际平台配置的内容为准</b>
                  </p>
                </template>
                <t-textarea v-model="form.content" placeholder="请输入内容" @change="handleVarsChange" />
              </t-form-item>
            </t-col>
            <t-col v-if="form.varsList && form.varsList.length > 0" :span="12">
              <t-form-item label="变量属性">
                <t-space direction="vertical">
                  <t-form-item
                    v-for="(value, index) in form.varsList"
                    :key="value.key"
                    :label="value.key"
                    :name="`varsList[${index}].value`"
                    label-align="left"
                    :label-width="`${maxVarsLabelWidth * 12}px`"
                    :rules="[{ required: true, message: '输入变量不能为空' }]"
                  >
                    <template v-if="form.varsList.length - 1 === index" #help>
                      <b>变量格式</b>：${name}、${name:这里可以填写默认值}<br />
                      变量属性是根据内容自动生成的，变量属性值需要在调用的时候注入到Map中，后台会根据值组合填入内容或模板变量中发送
                    </template>
                    <t-input v-model="form.varsList[index].value" />
                  </t-form-item>
                </t-space>
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="备注" name="remark">
                <t-textarea v-model="form.remark" placeholder="请输入备注" />
              </t-form-item>
            </t-col>
          </t-row>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 消息模板详情 -->
    <t-dialog
      v-model:visible="openView"
      header="消息模板详情"
      placement="center"
      width="min(100%, 850px)"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="消息模板id">{{ form.messageTemplateId }}</t-descriptions-item>
        <t-descriptions-item label="模板名称">{{ form.templateName }}</t-descriptions-item>
        <t-descriptions-item label="消息配置id">{{ form.messageConfigId }}</t-descriptions-item>
        <t-descriptions-item label="消息key">{{ form.messageKey }}</t-descriptions-item>
        <t-descriptions-item label="消息类型">
          <dict-tag :options="messageTypeOptions" :value="form.messageType" />
        </t-descriptions-item>
        <t-descriptions-item label="支持平台类型">
          <dict-tag :options="supplierTypeOptions" :value="form.supplierType" />
        </t-descriptions-item>
        <t-descriptions-item label="模板类型">
          <dict-tag :options="sys_message_template_mode" :value="form.templateMode" />
        </t-descriptions-item>
        <t-descriptions-item label="标题">{{ form.title }}</t-descriptions-item>
        <t-descriptions-item label="签名">{{ form.signature }}</t-descriptions-item>
        <t-descriptions-item label="模板id">{{ form.templateId }}</t-descriptions-item>
        <t-descriptions-item label="内容" :span="2">
          <div v-html="form.content"></div>
        </t-descriptions-item>
        <t-descriptions-item label="输入变量" :span="2">{{ form.varsJson }}</t-descriptions-item>
        <t-descriptions-item label="状态">
          <dict-tag :options="sys_normal_disable" :value="form.status" />
        </t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ form.remark }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>

    <t-dialog
      v-model:visible="openTest"
      destroy-on-close
      header="消息模板测试"
      width="500px"
      attach="body"
      :confirm-btn="{
        content: '发送测试',
        theme: 'primary',
        loading: buttonTestLoading,
      }"
      @confirm="onConfirmTest"
    >
      <t-form
        ref="messageTemplateTestRef"
        label-align="right"
        :data="formTest"
        :rules="testRules"
        label-width="calc(4em + 41px)"
        scroll-to-first-error="smooth"
        @submit="submitFormTest"
      >
        <t-form-item label="发送账号" name="account">
          <t-input v-model="formTest.account" placeholder="请输入手机号/邮箱" clearable />
        </t-form-item>
        <t-form-item label="变量属性">
          <t-space direction="vertical">
            <t-form-item
              v-for="(value, key) in formTest.vars"
              :key="key"
              :label="key"
              :name="`vars[${key}]`"
              label-align="left"
              :label-width="`calc(${maxVarsTestLabelWidth / 2}em + 41px)`"
              :rules="[{ required: true, message: '输入变量不能为空' }]"
            >
              <t-input v-model="formTest.vars[key]" />
            </t-form-item>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'MessageTemplate',
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
  SwapIcon,
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

import { getMessageFieldConfigs, getMessageSupplierType } from '@/api/system/messageConfig';
import {
  addMessageTemplate,
  delMessageTemplate,
  getMessageConfigs,
  getMessageKeys,
  getMessageTemplate,
  listMessageTemplate,
  refreshCache,
  sendMessageTest,
  updateMessageTemplate,
} from '@/api/system/messageTemplate';
import type { MessageFieldConfig, MessageTypeVo, SysMessageConfigVo } from '@/api/system/model/messageConfigModel';
import type { SysMessageKeyVo } from '@/api/system/model/messageKeyModel';
import type {
  SysMessageTemplateForm,
  SysMessageTemplateQuery,
  SysMessageTemplateTest,
  SysMessageTemplateVar,
  SysMessageTemplateVo,
} from '@/api/system/model/messageTemplateModel';
import { ArrayOps } from '@/utils/array';
import type { DictModel } from '@/utils/dict';

const { proxy } = getCurrentInstance();
const { sys_message_template_mode, sys_normal_disable } = proxy.useDict(
  'sys_message_template_mode',
  'sys_normal_disable',
);

const messageTemplateList = ref<SysMessageTemplateVo[]>([]);
const messageTemplateRef = ref<FormInstanceFunctions>();
const messageTemplateTestRef = ref<FormInstanceFunctions>();
const open = ref(false);
const openView = ref(false);
const openViewLoading = ref(false);
const buttonLoading = ref(false);
const buttonTestLoading = ref(false);
const loading = ref(false);
const columnControllerVisible = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const sort = ref<TableSort>();
const messageKeys = ref<SysMessageKeyVo[]>([]);
const messageConfigs = ref<SysMessageConfigVo[]>([]);
const openTest = ref(false);
const formTest = ref<SysMessageTemplateTest>({});
const messageFieldConfigs = ref<Record<string, MessageFieldConfig>>({});
const supplierTypeList = ref<MessageTypeVo[]>([]);

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  templateName: [{ required: true, message: '模板名称不能为空' }],
  messageConfigId: [{ required: true, message: '消息配置不能为空' }],
  messageKeyId: [{ required: true, message: '消息key不能为空' }],
  messageType: [{ required: true, message: '消息类型不能为空' }],
  title: [{ required: true, message: '标题不能为空' }],
  templateMode: [{ required: true, message: '模板类型不能为空' }],
  status: [{ required: true, message: '状态不能为空' }],
  templateId: [{ required: true, message: '模板ID不能为空' }],
  content: [{ required: true, message: '内容不能为空' }],
});
// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `模板名称`, colKey: 'templateName', align: 'center' },
  { title: `消息key`, colKey: 'messageKey', align: 'center', sorter: true },
  { title: `消息类型`, colKey: 'messageType', align: 'center' },
  { title: `支持平台标识`, colKey: 'supplierType', align: 'center' },
  { title: `模板类型`, colKey: 'templateMode', align: 'center' },
  { title: `状态`, colKey: 'status', align: 'center' },
  { title: `内容`, colKey: 'content', align: 'center', ellipsis: true },
  { title: `备注`, colKey: 'remark', align: 'center', ellipsis: true },
  { title: `更新时间`, colKey: 'updateTime', align: 'center', sorter: true },
  { title: `创建时间`, colKey: 'createTime', align: 'center', sorter: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 校验测试规则
const testRules = ref<Record<string, Array<FormRule>>>({
  account: [{ required: true, message: '账号不能为空' }],
});
// 提交表单对象
const form = ref<SysMessageTemplateVo & SysMessageTemplateForm>({
  varsList: [],
});
// 查询对象
const queryParams = ref<SysMessageTemplateQuery>({
  pageNum: 1,
  pageSize: 10,
  templateName: undefined,
  messageKey: undefined,
  messageType: undefined,
  templateMode: undefined,
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

const messageConfig = computed<MessageFieldConfig>(() => {
  if (!form.value.messageConfigId) {
    return null;
  }
  const config = messageConfigs.value.find((value) => value.messageConfigId === form.value.messageConfigId);
  return messageFieldConfigs.value[config?.supplierType];
});

const maxVarsLabelWidth = computed(() => {
  return Math.max(...form.value.varsList.map((value) => value.key.length));
});
const maxVarsTestLabelWidth = computed(() => {
  if (formTest.value.vars instanceof Object) {
    return Math.max(...Object.keys(formTest.value.vars).map((value) => value.length));
  }
  return 0;
});

/**
 * 提取变量
 * @param contents
 */
function getVars(...contents: string[]) {
  let vars: string[] = [];
  if (contents) {
    contents.forEach((content) => {
      const rex = /\$\{([^}]+)}/g;
      let temp;
      do {
        temp = rex.exec(content);
        if (temp) {
          vars.push(temp[1]);
        }
      } while (temp);
    });
  }
  // 去重
  vars = vars.filter((value, index) => {
    return vars.indexOf(value) === index;
  });
  return vars;
}

/** 处理变量变更 */
function handleVarsChange() {
  let vars: string[];
  if (form.value.messageType === 'SMS') {
    vars = getVars(form.value.content);
  } else {
    vars = getVars(form.value.title, form.value.content);
  }
  form.value.varsList = vars.map<SysMessageTemplateVar>((key) => {
    const value = form.value.varsList.find((item) => item.key === key);
    return { key, value: value?.value ?? `$\{${key}}` };
  });
}

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

/** 处理消息类型变更 */
function handleMessageTypeChange(value: string) {
  form.value.messageConfigId = undefined;
  messageTemplateRef.value.clearValidate(['messageConfigId']);
  if (!value) {
    messageConfigs.value = [];
    return;
  }
  getMessageConfigs(value).then((res) => {
    messageConfigs.value = res.data;
  });
}

/** 查询消息模板列表 */
function getList() {
  loading.value = true;
  listMessageTemplate(queryParams.value)
    .then((response) => {
      messageTemplateList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}

// 表单重置
function reset() {
  form.value = {
    status: 1,
    varsList: [],
  };
  formTest.value = {
    vars: {},
  };
  proxy.resetForm('messageTemplateRef');
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
  title.value = '添加消息模板';
  getMessageKeys().then((res) => {
    messageKeys.value = res.data;
  });
}

/** 测试按钮操作 */
function handleTest(row: SysMessageTemplateVo) {
  reset();
  openTest.value = true;
  getMessageTemplate(row.messageTemplateId).then((response) => {
    const data = response.data;
    const values: SysMessageTemplateVar[] = JSON.parse(data.varsJson as string);
    let vars = {};
    // 将变量合并到vars属性中
    function merge(value?: string) {
      if (!value) return;
      vars = {
        ...vars,
        ...Object.fromEntries(
          getVars(value).map((value) => {
            if (value.includes(':')) {
              const v = value.split(':');
              return [v[0], v[1]];
            }
            return [value, ''];
          }),
        ),
      };
    }
    values.forEach((item) => merge(item.value));
    merge(data.title);
    formTest.value = { messageTemplateId: data.messageTemplateId, vars };
  });
}

/** 详情按钮操作 */
function handleDetail(row: SysMessageTemplateVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const messageTemplateId = row.messageTemplateId;
  getMessageTemplate(messageTemplateId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysMessageTemplateVo) {
  buttonLoading.value = true;
  reset();
  open.value = true;
  title.value = '修改消息模板';
  const messageTemplateId = row?.messageTemplateId || ids.value.at(0);
  getMessageTemplate(messageTemplateId).then((response) => {
    buttonLoading.value = false;
    const varsList = JSON.parse(response.data.varsJson);
    form.value = { ...response.data, varsList: Array.isArray(varsList) ? varsList : [] };
    getMessageConfigs(form.value.messageType).then((res) => {
      messageConfigs.value = res.data;
    });
  });
  getMessageKeys().then((res) => {
    messageKeys.value = res.data;
  });
}

/** 提交按钮 */
function onConfirm() {
  messageTemplateRef.value.submit();
}

/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.messageTemplateId) {
      updateMessageTemplate(form.value)
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
      addMessageTemplate(form.value)
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

/** 提交测试按钮 */
function onConfirmTest() {
  messageTemplateTestRef.value.submit();
}

/** 提交测试表单 */
function submitFormTest({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonTestLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('发送测试消息中...');
    sendMessageTest(formTest.value)
      .then(() => {
        proxy.$modal.msgSuccess('已提交发送申请');
      })
      .finally(() => {
        buttonTestLoading.value = false;
        proxy.$modal.msgClose(msgLoading);
      });
  } else {
    proxy.$modal.msgError(firstError);
  }
}

/** 删除按钮操作 */
function handleDelete(row?: SysMessageTemplateVo) {
  const messageTemplateIds = row?.messageTemplateId || ids.value;
  proxy.$modal.confirm(`是否确认删除消息模板编号为${messageTemplateIds}的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delMessageTemplate(messageTemplateIds)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, messageTemplateIds);
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
    'system/messageTemplate/export',
    {
      ...queryParams.value,
    },
    `messageTemplate_${new Date().getTime()}.xlsx`,
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
  getList();
  initMessageFieldConfigs();
});
</script>
