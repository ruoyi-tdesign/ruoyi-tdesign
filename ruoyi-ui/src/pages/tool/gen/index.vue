<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="数据源" name="dataName">
          <t-select v-model="queryParams.dataName" filterable clearable placeholder="请选择/输入数据源名称">
            <t-option v-for="item in dataNameList" :key="item" :value="item" :label="item"></t-option>
          </t-select>
        </t-form-item>
        <t-form-item label="表名称" name="tableName">
          <t-input
            v-model="queryParams.tableName"
            placeholder="请输入表名称"
            clearable
            style="width: 200px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="表描述" name="tableComment">
          <t-input
            v-model="queryParams.tableComment"
            placeholder="请输入表描述"
            clearable
            style="width: 200px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="创建时间" style="width: 340px">
          <t-date-range-picker
            v-model="dateRange"
            allow-input
            clearable
            separator="-"
            :placeholder="['开始日期', '结束日期']"
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
        v-model:column-controller-visible="columnControllerVisible"
        hover
        :loading="loading"
        row-key="tableId"
        :data="tableList"
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
              <t-button v-hasPermi="['tool:gen:code']" theme="primary" @click="handleGenTable()">
                <template #icon> <download-icon /></template>
                生成
              </t-button>
              <t-button v-hasPermi="['tool:gen:import']" theme="default" variant="outline" @click="openImportTable">
                <template #icon> <upload-icon /> </template>
                导入
              </t-button>
              <t-button
                v-hasPermi="['tool:gen:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleEditTable()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['tool:gen:remove']"
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
        <template #operation="{ row }">
          <t-space :size="8">
            <t-tooltip content="预览" placement="top">
              <my-link v-hasPermi="['tool:gen:preview']" @click.stop="handlePreview(row)">
                <template #prefix-icon><browse-icon /></template>
              </my-link>
            </t-tooltip>
            <t-tooltip content="编辑" placement="top">
              <my-link v-hasPermi="['tool:gen:edit']" @click.stop="handleEditTable(row)">
                <template #prefix-icon><edit-icon /></template>
              </my-link>
            </t-tooltip>
            <t-tooltip content="删除" placement="top">
              <my-link v-hasPermi="['tool:gen:remove']" theme="danger" @click.stop="handleDelete(row)">
                <template #prefix-icon><delete-icon /></template>
              </my-link>
            </t-tooltip>
            <t-tooltip content="同步" placement="top">
              <my-link v-hasPermi="['tool:gen:edit']" @click.stop="handleSyncDb(row)">
                <template #prefix-icon><refresh-icon /></template>
              </my-link>
            </t-tooltip>
            <t-tooltip content="生成代码" placement="top">
              <my-link v-hasPermi="['tool:gen:code']" @click.stop="handleGenTable(row)">
                <template #prefix-icon><download-icon /></template>
              </my-link>
            </t-tooltip>
          </t-space>
        </template>
      </t-table>
    </t-space>
    <!-- 预览界面 -->
    <gen-preview v-model:visible="preview.open" :data="preview.data" :loading="preview.loading" />
    <import-table ref="importRef" @ok="handleQuery" />
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'Gen',
});

import {
  BrowseIcon,
  DeleteIcon,
  DownloadIcon,
  EditIcon,
  RefreshIcon,
  SearchIcon,
  SettingIcon,
  UploadIcon,
} from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol, TableSort } from 'tdesign-vue-next';
import { computed, getCurrentInstance, onActivated, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import { delTable, genCode, getDataNames, listTable, previewTable, synchDb } from '@/api/tool/gen';
import type { GenTableQuery, GenTableVo } from '@/api/tool/model/genModel';
import GenPreview from '@/pages/tool/gen/components/preview.vue';
import type { ImportTableInstance } from '@/pages/tool/gen/importTable.vue';
import router from '@/router';
import { ArrayOps } from '@/utils/array';

import ImportTable from './importTable.vue';

const route = useRoute();
const { proxy } = getCurrentInstance();

const tableList = ref<GenTableVo[]>([]);
const loading = ref(false);
const columnControllerVisible = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dataNameList = ref<Array<string>>([]);
const dateRange = ref([]);
const uniqueId = ref('');
const importRef = ref<ImportTableInstance>();
const sort = ref<TableSort>();

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 55, align: 'center' },
  { title: '序号', colKey: 'serial-number', width: 80, align: 'center' },
  { title: `数据源`, colKey: 'dataName', align: 'center', ellipsis: true },
  { title: `表名称`, colKey: 'tableName', align: 'center', ellipsis: true },
  { title: `表描述`, colKey: 'tableComment', align: 'center', ellipsis: true },
  { title: `实体`, colKey: 'className', align: 'center', ellipsis: true },
  { title: `更新时间`, colKey: 'updateTime', align: 'center', minWidth: 112, width: '15%', sorter: true },
  { title: `创建时间`, colKey: 'createTime', align: 'center', minWidth: 112, width: '15%', sorter: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: 150 },
]);

const queryParams = ref<GenTableQuery>({
  pageNum: 1,
  pageSize: 10,
  tableName: undefined,
  tableComment: undefined,
  dataName: '',
});
const preview = ref({
  open: false,
  loading: true,
  data: {},
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

onActivated(() => {
  const time = route.query.t;
  if (time != null && time !== uniqueId.value) {
    uniqueId.value = time.toString();
    queryParams.value.pageNum = route.query.pageNum as any;
    dateRange.value = [];
    proxy.resetForm('queryForm');
    getList();
  }
});

/** 查询多数据源名称 */
async function getDataNameList() {
  const res = await getDataNames();
  dataNameList.value = res.data;
}

/** 查询表集合 */
function getList() {
  loading.value = true;
  listTable(proxy.addDateRange(queryParams.value, dateRange.value)).then((response) => {
    tableList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 生成代码操作 */
function handleGenTable(row?: GenTableVo) {
  const tbIds = row?.tableId || ids.value;
  if (!row?.tableId && ids.value.length === 0) {
    proxy.$modal.msgError('请选择要生成的数据');
    return;
  }
  if (row?.genType === '1') {
    genCode(row.tableId).then(() => {
      proxy.$modal.msgSuccess(`成功生成到自定义路径：${row.genPath}`);
    });
  } else {
    proxy.$download.zip(`/tool/gen/batchGenCode?tableIdStr=${tbIds}`, 'ruoyi.zip');
  }
}

/** 同步数据库操作 */
function handleSyncDb(row: GenTableVo) {
  const { tableName, tableId, dataName } = row;
  proxy.$modal.confirm(`确认要强制同步"${dataName}.${tableName}"表结构吗？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在同步中...');
    return synchDb(tableId)
      .then(() => {
        proxy.$modal.msgSuccess('同步成功');
      })
      .finally(() => {
        proxy.$modal.msgClose(msgLoading);
      });
  });
}

/** 打开导入表弹窗 */
function openImportTable() {
  importRef.value.show(queryParams.value.dataName);
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
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

/** 预览按钮 */
function handlePreview(row: GenTableVo) {
  preview.value.loading = true;
  preview.value.open = true;
  previewTable(row.tableId)
    .then((response) => {
      preview.value.data = response.data;
    })
    .finally(() => (preview.value.loading = false));
}

/** 多选框选中数据 */
function handleSelectionChange(selection: Array<string | number>) {
  ids.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}
/** 修改按钮操作 */
function handleEditTable(row?: GenTableVo) {
  const tableId = row?.tableId || ids.value[0];
  router.push({ path: `/tool/gen-edit/index/${tableId}`, query: { pageNum: queryParams.value.pageNum } });
}
/** 删除按钮操作 */
function handleDelete(row?: GenTableVo) {
  const tableIds = row?.tableId || ids.value;
  proxy.$modal.confirm(`是否确认删除表编号为"${tableIds}"的数据项？`, () => {
    return delTable(tableIds).then(() => {
      ids.value = ArrayOps.fastDeleteElement(ids.value, tableIds);
      getList();
      if (!row) {
        ids.value = [];
      }
      proxy.$modal.msgSuccess('删除成功');
    });
  });
}

onMounted(() => {
  getList();
  getDataNameList();
});
</script>
