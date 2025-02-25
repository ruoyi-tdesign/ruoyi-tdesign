<template>
  <t-card>
    <t-form
      ref="formRef"
      :data="form"
      label-width="calc(8em + 41px)"
      :rules="rules"
      @submit="onSubmit"
      @validate="onValidate"
    >
      <t-tabs v-model="activeName">
        <t-tab-panel label="基本信息" value="basic" :destroy-on-hide="false">
          <div class="panel-top">
            <basic-info-form :info="info" :loading="loading" />
          </div>
        </t-tab-panel>
        <t-tab-panel label="字段信息" value="columnInfo" :destroy-on-hide="false">
          <div class="panel-top">
            <t-table
              v-model:column-controller-visible="columnControllerVisible"
              :loading="loading"
              hover
              :data="columnsData"
              :columns="columns"
              row-key="columnId"
              max-height="calc(100vh - 385px)"
              :column-controller="{
                hideTriggerButton: true,
              }"
              resizable
            >
              <template #topContent>
                <t-row>
                  <t-col flex="auto">
                    <t-space>
                      <t-checkbox v-model="isSort">排序</t-checkbox>
                      <t-checkbox v-model="isInsert">插入</t-checkbox>
                      <t-checkbox v-model="isEdit">编辑</t-checkbox>
                      <t-checkbox v-model="isList">列表</t-checkbox>
                      <t-checkbox v-model="isDetail">详情</t-checkbox>
                      <t-checkbox v-model="isQuery">查询</t-checkbox>
                    </t-space>
                  </t-col>
                  <t-col flex="none">
                    <t-button theme="default" variant="outline" @click="loadDictOption">
                      <template #icon> <refresh-icon /> </template>
                      更新字典
                    </t-button>
                    <t-button theme="default" variant="outline" @click="columnControllerVisible = true">
                      <template #icon> <setting-icon /> </template>
                      列配置
                    </t-button>
                  </t-col>
                </t-row>
              </template>
              <template #columnComment="{ row }">
                <t-input v-model="row.columnComment"></t-input>
              </template>
              <template #javaType="{ row }">
                <t-select v-model="row.javaType">
                  <t-option label="Long" value="Long" />
                  <t-option label="String" value="String" />
                  <t-option label="Integer" value="Integer" />
                  <t-option label="Double" value="Double" />
                  <t-option label="BigDecimal" value="BigDecimal" />
                  <t-option label="Date" value="Date" />
                  <t-option label="Boolean" value="Boolean" />
                </t-select>
              </template>
              <template #javaField="{ row }">
                <t-input v-model="row.javaField"></t-input>
              </template>
              <template #isInsert="{ row }">
                <t-custom-checkbox v-model="row.isInsert" true-label="1" false-label="0"></t-custom-checkbox>
              </template>
              <template #isEdit="{ row }">
                <t-custom-checkbox v-model="row.isEdit" true-label="1" false-label="0"></t-custom-checkbox>
              </template>
              <template #isList="{ row }">
                <t-custom-checkbox v-model="row.isList" true-label="1" false-label="0"></t-custom-checkbox>
              </template>
              <template #isDetail="{ row }">
                <t-custom-checkbox v-model="row.isDetail" true-label="1" false-label="0"></t-custom-checkbox>
              </template>
              <template #isSort="{ row }">
                <t-custom-checkbox v-model="row.isSort" true-label="1" false-label="0"></t-custom-checkbox>
              </template>
              <template #isQuery="{ row }">
                <t-custom-checkbox v-model="row.isQuery" true-label="1" false-label="0"></t-custom-checkbox>
              </template>
              <template #queryType="{ row }">
                <t-select v-model="row.queryType">
                  <t-option label="=" value="EQ" />
                  <t-option label="!=" value="NE" />
                  <t-option label=">" value="GT" />
                  <t-option label=">=" value="GE" />
                  <t-option label="<" value="LT" />
                  <t-option label="<=" value="LE" />
                  <t-option label="LIKE" value="LIKE" />
                  <t-option label="LEFT LIKE" value="LEFT_LIKE" />
                  <t-option label="RIGHT LIKE" value="RIGHT_LIKE" />
                  <t-option label="NOT LIKE" value="NOT_LIKE" />
                  <t-option label="IS NULL" value="IS_NULL" />
                  <t-option label="IS NOT NULL" value="IS_NOT_NULL" />
                  <t-option label="IN" value="IN" />
                  <t-option label="BETWEEN" value="BETWEEN" />
                </t-select>
              </template>
              <template #isRequired="{ row }">
                <t-custom-checkbox v-model="row.isRequired" true-label="1" false-label="0"></t-custom-checkbox>
              </template>
              <template #htmlType="{ row }">
                <t-select v-model="row.htmlType">
                  <t-option label="文本框" value="input" />
                  <t-option label="数字输入框" value="input-number" />
                  <t-option label="文本域" value="textarea" />
                  <t-option label="下拉框" value="select" />
                  <t-option label="单选框" value="radio" />
                  <t-option label="复选框" value="checkbox" />
                  <t-option label="日期控件" value="datetime" />
                  <t-option label="图片上传" value="imageUpload" />
                  <t-option label="文件上传" value="fileUpload" />
                  <t-option label="富文本控件" value="editor" />
                </t-select>
              </template>
              <template #dictType="{ row }">
                <t-select
                  v-model="row.dictType"
                  clearable
                  filterable
                  placeholder="请选择"
                  :popup-props="{ overlayInnerStyle: { width: '100%' } }"
                >
                  <t-option
                    v-for="dict in dictOptions"
                    :key="dict.dictType"
                    :label="dict.dictName"
                    :value="dict.dictType"
                    class="gen-option"
                  >
                    <span style="float: left">{{ dict.dictName }}</span>
                    <span style="float: right; color: #8492a6; font-size: 13px; margin-left: 5px">
                      {{ dict.dictType }}
                    </span>
                  </t-option>
                </t-select>
              </template>
            </t-table>
          </div>
        </t-tab-panel>
        <t-tab-panel label="生成信息" value="genInfo" :destroy-on-hide="false">
          <div class="panel-top">
            <gen-info-form :info="info" :loading="loading" />
          </div>
        </t-tab-panel>
      </t-tabs>
      <div style="text-align: center; margin-left: -100px; margin-top: 10px">
        <t-button theme="default" :loading="buttonLoading" variant="outline" @click="handlePreview()">
          实时预览
        </t-button>
        <t-button theme="primary" :loading="buttonLoading" type="submit">提交</t-button>
        <t-button theme="primary" :loading="buttonLoading" variant="outline" @click="handleSyncDb()">同步代码</t-button>
        <t-button theme="primary" :loading="buttonLoading" variant="outline" @click="openUpdateTable = true">
          修改表名
        </t-button>
        <t-button theme="default" :loading="buttonLoading" variant="outline" @click="close()">返回</t-button>
      </div>
    </t-form>
    <!-- 预览界面 -->
    <gen-preview v-model:visible="preview.open" :data="preview.data" :loading="preview.loading" />
    <!-- 更新表名 -->
    <update-table-name
      v-model:visible="openUpdateTable"
      v-model:loading="buttonLoading"
      :table-vo="info"
      @submit="getGenTableData(info.tableId)"
    />
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'GenEdit',
});

import { RefreshIcon, SettingIcon } from 'tdesign-icons-vue-next';
import type { FormInstanceFunctions, FormRule, PrimaryTableCol, SubmitContext } from 'tdesign-vue-next';
import { computed, getCurrentInstance, onMounted, reactive, ref, toRefs } from 'vue';
import { useRoute } from 'vue-router';

import { optionselect as getDictOptionselect } from '@/api/system/dict/type';
import type { SysDictTypeVo } from '@/api/system/model/dictModel';
import { getGenTable, synchDb, tempPreviewTable, updateGenTable } from '@/api/tool/gen';
import type { GenTableColumn, GenTableForm, GenTableVo } from '@/api/tool/model/genModel';
import GenPreview from '@/pages/tool/gen/components/preview.vue';
import { useTabsRouterStore } from '@/store';

import BasicInfoForm from './basicInfoForm.vue';
import TCustomCheckbox from './components/checkbox.vue';
import UpdateTableName from './components/UpdateTableName.vue';
import GenInfoForm from './genInfoForm.vue';

const removeCurrentTab = useTabsRouterStore().useRemoveCurrentTab();
const route = useRoute();
const { proxy } = getCurrentInstance();

const formRef = ref<FormInstanceFunctions>();
const columnControllerVisible = ref(false);
const activeName = ref('columnInfo');
const dictOptions = ref<SysDictTypeVo[]>([]);
const loading = ref(false);
const buttonLoading = ref(false);
const openUpdateTable = ref(false);
const preview = ref({
  open: false,
  loading: true,
  data: {},
});
const columns = ref<Array<PrimaryTableCol>>([
  { title: '序号', colKey: 'serial-number', width: '5%', align: 'center' },
  { title: `字段列名`, colKey: 'columnName', align: 'center', ellipsis: true, width: '10%' },
  { title: `字段描述`, colKey: 'columnComment', align: 'center', width: '10%' },
  { title: `物理类型`, colKey: 'columnType', align: 'center', ellipsis: true, width: '10%' },
  { title: `Java类型`, colKey: 'javaType', align: 'center', width: '10%' },
  { title: `java属性`, colKey: 'javaField', align: 'center', width: '10%' },
  { title: `排序`, colKey: 'isSort', align: 'center', width: '4%' },
  { title: `插入`, colKey: 'isInsert', align: 'center', width: '4%' },
  { title: `编辑`, colKey: 'isEdit', align: 'center', width: '4%' },
  { title: `列表`, colKey: 'isList', align: 'center', width: '4%' },
  { title: `详情`, colKey: 'isDetail', align: 'center', width: '4%' },
  { title: `查询`, colKey: 'isQuery', align: 'center', width: '4%' },
  { title: `查询方式`, colKey: 'queryType', align: 'center', width: '10%' },
  { title: `必填`, colKey: 'isRequired', align: 'center', width: '4%' },
  { title: `显示类型`, colKey: 'htmlType', align: 'center', width: '10%' },
  { title: `字典类型`, colKey: 'dictType', align: 'center', width: '12%' },
]);

const rules = ref<Record<string, Array<FormRule>>>({
  tplCategory: [{ required: true, message: '请选择生成模板' }],
  packageName: [{ required: true, message: '请输入生成包路径' }],
  moduleName: [{ required: true, message: '请输入生成模块名' }],
  businessName: [{ required: true, message: '请输入生成业务名' }],
  functionName: [{ required: true, message: '请输入生成功能名' }],
  tableName: [{ required: true, message: '请输入表名称' }],
  tableComment: [{ required: true, message: '请输入表描述' }],
  className: [{ required: true, message: '请输入实体类名称' }],
  functionAuthor: [{ required: true, message: '请输入作者' }],
  treeCode: [{ required: true, message: '请选择树编码字段' }],
  treeParentCode: [{ required: true, message: '请选择树父编码字段' }],
  treeName: [{ required: true, message: '请选择树名称字段' }],
});

const form = reactive<{
  columnsData: GenTableColumn[];
  info: GenTableForm & GenTableVo;
}>({
  columnsData: [],
  info: {
    tableOptions: {},
  },
});

const { columnsData, info } = toRefs(form);

const isInsert = computed({
  get() {
    return !columnsData.value.some((value) => value.isInsert === '0' || !value.isInsert);
  },
  set(val) {
    columnsData.value.forEach((value) => (value.isInsert = val ? '1' : '0'));
  },
});
const isEdit = computed({
  get() {
    return !columnsData.value.some((value) => value.isEdit === '0' || !value.isEdit);
  },
  set(val) {
    columnsData.value.forEach((value) => (value.isEdit = val ? '1' : '0'));
  },
});
const isList = computed({
  get() {
    return !columnsData.value.some((value) => value.isList === '0' || !value.isList);
  },
  set(val) {
    columnsData.value.forEach((value) => (value.isList = val ? '1' : '0'));
  },
});
const isDetail = computed({
  get() {
    return !columnsData.value.some((value) => value.isDetail === '0' || !value.isDetail);
  },
  set(val) {
    columnsData.value.forEach((value) => (value.isDetail = val ? '1' : '0'));
  },
});
const isQuery = computed({
  get() {
    return !columnsData.value.some((value) => value.isQuery === '0' || !value.isQuery);
  },
  set(val) {
    columnsData.value.forEach((value) => (value.isQuery = val ? '1' : '0'));
  },
});
const isSort = computed({
  get() {
    return !columnsData.value.some((value) => value.isSort === '0' || !value.isSort);
  },
  set(val) {
    columnsData.value.forEach((value) => (value.isSort = val ? '1' : '0'));
  },
});

/** 实时预览按钮 */
async function handlePreview() {
  const result = await formRef.value.validate();
  if (result === true) {
    const genTable = { ...info.value };
    genTable.columns = columnsData.value;
    genTable.tableOptions = info.value.tableOptions;
    preview.value.loading = true;
    preview.value.open = true;
    tempPreviewTable(genTable)
      .then((response) => {
        preview.value.data = response.data;
      })
      .finally(() => (preview.value.loading = false));
  }
}

/** 同步数据库操作 */
function handleSyncDb() {
  const { tableName, tableId, dataName } = info.value;
  proxy.$modal.confirm(`未保存的编辑将被覆盖，确认要强制同步"${dataName}.${tableName}"表结构吗？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在同步中...');
    return synchDb(tableId)
      .then(() => {
        // 重新获取表详细信息
        const msgLoading = proxy.$modal.msgLoading('同步成功，正在重新获取表数据中...');
        getGenTableData(tableId)
          .then(() => {
            proxy.$modal.msgSuccess('同步完成');
          })
          .finally(() => {
            proxy.$modal.msgClose(msgLoading);
          });
      })
      .finally(() => {
        proxy.$modal.msgClose(msgLoading);
      });
  });
}

/** 表单校验 */
function onValidate({ validateResult, firstError }: SubmitContext) {
  if (validateResult !== true) {
    const basicInfo = ['info.tableName', 'info.tableComment', 'info.className', 'info.functionAuthor'];
    const isBasic = Object.keys(validateResult).some((value) => basicInfo.includes(value));
    if (isBasic) {
      activeName.value = 'basic';
    } else {
      activeName.value = 'genInfo';
    }
    proxy.$modal.msgError(firstError);
  }
}

/** 表单提交 */
function onSubmit({ validateResult }: SubmitContext) {
  if (validateResult === true) {
    proxy.$modal.confirm('是否确认保存表结构信息？', () => {
      const genTable = { ...info.value };
      genTable.columns = columnsData.value;
      genTable.tableOptions = info.value.tableOptions;
      const msgLoading = proxy.$modal.msgLoading('正在提交中...');
      buttonLoading.value = true;
      updateGenTable(genTable)
        .then((res) => {
          proxy.$modal.msgSuccess(res.msg);
        })
        .finally(() => {
          buttonLoading.value = false;
          proxy.$modal.msgClose(msgLoading);
        });
    });
  }
}

// 返回到列表
function close() {
  removeCurrentTab('/tool/gen');
}

/**
 * 加载表信息
 * @param tableId 表id
 */
async function getGenTableData(tableId: string | number) {
  loading.value = true;
  // 获取表详细信息
  return getGenTable(String(tableId))
    .then((res) => {
      columnsData.value = res.data.rows;
      info.value = res.data.info;
    })
    .finally(() => (loading.value = false));
}

/** 查询字典下拉列表 */
function loadDictOption() {
  getDictOptionselect().then((response) => {
    dictOptions.value = response.data;
    proxy.$modal.msgSuccess('成功加载字典');
  });
}

onMounted(() => {
  const tableId = route.params && route.params.tableId;
  if (tableId) {
    getGenTableData(tableId as string);
    loadDictOption();
  }
});
</script>
<style lang="less" scoped>
.panel-top {
  margin: 20px 0;
}
</style>
