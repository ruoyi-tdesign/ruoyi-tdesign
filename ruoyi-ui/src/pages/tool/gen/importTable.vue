<template>
  <!-- 导入表 -->
  <t-dialog
    v-model:visible="visible"
    :close-on-overlay-click="false"
    header="导入表"
    width="min(1100px, 100%)"
    top="5vh"
    attach="body"
    :confirm-btn="{
      content: '导入',
      theme: 'primary',
      loading: buttonLoading,
    }"
    @opened="handleOpen"
    @confirm="handleImportTable"
  >
    <t-space direction="vertical" style="width: 100%">
      <t-form ref="queryRef" :data="queryParams" layout="inline" label-width="calc(3em + 12px)">
        <t-form-item label="数据源" name="dataName">
          <t-select
            v-model="queryParams.dataName"
            style="width: 200px"
            placeholder="请选择/输入数据源名称"
            filterable
            clearable
          >
            <t-option v-for="item in dataNameList" :key="item" :label="item" :value="item"> </t-option>
          </t-select>
        </t-form-item>
        <t-form-item label="表名称" name="tableName">
          <t-input
            v-model="queryParams.tableName"
            style="width: 150px"
            placeholder="请输入表名称"
            clearable
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="表描述" name="tableComment">
          <t-input
            v-model="queryParams.tableComment"
            style="width: 150px"
            placeholder="请输入表描述"
            clearable
            @enter="handleQuery"
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
        ref="table"
        hover
        max-height="90vh"
        :loading="loading"
        row-key="tableName"
        :data="dbTableList"
        :columns="columns"
        :selected-row-keys="tables"
        select-on-row-click
        :pagination="pagination"
        @select-change="handleSelectionChange"
      >
      </t-table>
    </t-space>
  </t-dialog>
</template>

<script lang="ts" setup>
import { RefreshIcon, SearchIcon } from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { getCurrentInstance, reactive, ref } from 'vue';

import { getDataNames, importTable, listDbTable } from '@/api/tool/gen';
import type { GenTableQuery, GenTableVo } from '@/api/tool/model/genModel';

const loading = ref(false);
const visible = ref(false);
const tables = ref([]);
const dbTableList = ref<GenTableVo[]>([]);
const dataNameList = ref<Array<string>>([]);
const { proxy } = getCurrentInstance();
const buttonLoading = ref(false);

const queryParams = reactive<GenTableQuery>({
  pageNum: 1,
  pageSize: 10,
  dataName: '',
  tableName: '',
  tableComment: '',
});
// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 55, align: 'center' },
  { title: `表名称`, colKey: 'tableName', align: 'center', ellipsis: true },
  { title: `表描述`, colKey: 'tableComment', align: 'center', ellipsis: true },
  { title: `创建时间`, colKey: 'createTime', align: 'center' },
  { title: `更新时间`, colKey: 'updateTime', align: 'center' },
]);

// 分页
// const pagination = ref({
//   pageSize: 10,
// });
const pagination = computed(() => {
  return {
    current: queryParams.pageNum,
    pageSize: queryParams.pageSize,
    total: dbTableList.value.length,
    showJumper: true,
    onChange: (pageInfo: PageInfo) => {
      queryParams.pageNum = pageInfo.current;
      queryParams.pageSize = pageInfo.pageSize;
      // getList();
    },
  };
});
const emit = defineEmits(['ok']);

/** 查询参数列表 */
function show(dataName: string) {
  getDataNames().then((res) => {
    dataNameList.value = res.data;
    if (dataName) {
      queryParams.dataName = dataName;
    } else {
      queryParams.dataName = dataNameList.value[0];
    }
    getList();
    visible.value = true;
  });
}
/** 多选框选中数据 */
function handleSelectionChange(selection: Array<string | number>) {
  tables.value = selection;
}
/** 查询表数据 */
function getList() {
  loading.value = true;
  listDbTable(queryParams).then((res) => {
    dbTableList.value = res.data;
    // total.value = res.total;
    loading.value = false;
  });
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
  handleQuery();
}
/** 处理打开弹窗事件 */
function handleOpen() {
  tables.value = [];
}
/** 导入按钮操作 */
function handleImportTable() {
  const tableNames = tables.value.join(',');
  if (!tableNames) {
    proxy.$modal.msgError('请选择要导入的表');
    return;
  }
  buttonLoading.value = true;
  importTable(tableNames, queryParams.dataName)
    .then((res) => {
      proxy.$modal.msgSuccess(res.msg);
      if (res.code === 200) {
        visible.value = false;
        emit('ok');
      }
    })
    .finally(() => (buttonLoading.value = false));
}
/** 查询多数据源名称 */
async function getDataNameList() {
  const res = await getDataNames();
  dataNameList.value = res.data;
}

const exposed = {
  show,
};
export type ImportTableInstance = typeof exposed;
defineExpose<ImportTableInstance>(exposed);
</script>
