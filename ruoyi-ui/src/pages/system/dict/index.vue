<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="字典名称" name="dictName">
          <t-input
            v-model="queryParams.dictName"
            placeholder="请输入字典名称"
            clearable
            style="width: 240px"
            @enter="handleQuery"
          />
        </t-form-item>
        <t-form-item label="字典类型" name="dictType">
          <t-input
            v-model="queryParams.dictType"
            placeholder="请输入字典类型"
            clearable
            style="width: 240px"
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
        :loading="loading"
        hover
        row-key="dictId"
        :data="typeList"
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
              <t-button v-hasPermi="['system:dict:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:dict:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:dict:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button v-hasPermi="['system:dict:export']" theme="default" variant="outline" @click="handleExport">
                <template #icon> <download-icon /> </template>
                导出
              </t-button>
              <t-button
                v-hasPermi="['system:dict:remove']"
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
        <template #dictType="{ row }">
          <router-link :to="'/system/dict-data/index/' + row.dictId" class="link-type" @click.stop>
            <my-link>
              {{ row.dictType }}
            </my-link>
          </router-link>
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['system:dict:query']" @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link v-hasPermi="['system:dict:edit']" @click.stop="handleUpdate(row)">
              <template #prefix-icon><edit-icon /></template>修改
            </my-link>
            <my-link v-hasPermi="['system:dict:remove']" theme="danger" @click.stop="handleDelete(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 添加或修改参数配置对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      width="500px"
      attach="body"
      :confirm-btn="{
        loading: eLoading,
      }"
      @confirm="onConfirm"
    >
      <t-loading :loading="eLoading" size="small">
        <t-form ref="dictRef" label-align="right" :data="form" :rules="rules" label-width="80px" @submit="submitForm">
          <t-form-item label="字典名称" name="dictName">
            <t-input v-model="form.dictName" placeholder="请输入字典名称" />
          </t-form-item>
          <t-form-item label="字典类型" name="dictType">
            <t-input v-model="form.dictType" placeholder="请输入字典类型" />
          </t-form-item>
          <t-form-item label="备注" name="remark">
            <t-textarea v-model="form.remark" placeholder="请输入备注"></t-textarea>
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 字典类型详情 -->
    <t-dialog
      v-model:visible="openView"
      header="字典类型详情"
      placement="center"
      width="700px"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="字典主键">{{ form.dictId }}</t-descriptions-item>
        <t-descriptions-item label="字典名称">{{ form.dictName }}</t-descriptions-item>
        <t-descriptions-item label="字典类型" :span="2">{{ form.dictType }}</t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ form.remark }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'Dict',
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

import { addType, delType, getType, listType, refreshCache, updateType } from '@/api/system/dict/type';
import type { SysDictTypeForm, SysDictTypeQuery, SysDictTypeVo } from '@/api/system/model/dictModel';
import useDictStore from '@/store/modules/dict';
import { ArrayOps } from '@/utils/array';

const { proxy } = getCurrentInstance();

const openView = ref(false);
const openViewLoading = ref(false);
const typeList = ref<SysDictTypeVo[]>([]);
const open = ref(false);
const loading = ref(false);
const eLoading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const dateRange = ref([]);
const columnControllerVisible = ref(false);
const dictRef = ref<FormInstanceFunctions>();
const sort = ref<TableSort>();

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  dictName: [{ required: true, message: '字典名称不能为空' }],
  dictType: [{ required: true, message: '字典类型不能为空' }],
});

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `字典编号`, colKey: 'dictId', align: 'center' },
  { title: `字典名称`, colKey: 'dictName', align: 'center', ellipsis: true },
  { title: `字典类型`, colKey: 'dictType', align: 'center', ellipsis: true },
  { title: `备注`, colKey: 'remark', align: 'center', ellipsis: true },
  { title: `创建时间`, colKey: 'createTime', align: 'center', width: 180, sorter: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 提交表单对象
const form = ref<SysDictTypeForm & SysDictTypeVo>({});
// 查询对象
const queryParams = ref<SysDictTypeQuery>({
  pageNum: 1,
  pageSize: 10,
  dictName: undefined,
  dictType: undefined,
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

/** 查询字典类型列表 */
function getList() {
  loading.value = true;
  listType(proxy.addDateRange(queryParams.value, dateRange.value))
    .then((response) => {
      typeList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}
/** 表单重置 */
function reset() {
  form.value = {
    dictId: undefined,
    dictName: undefined,
    dictType: undefined,
    remark: undefined,
  };
  proxy.resetForm('dictRef');
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
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
  title.value = '添加字典类型';
}
/** 详情按钮操作 */
function handleDetail(row: SysDictTypeVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const dictId = row.dictId;
  getType(dictId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysDictTypeVo) {
  reset();
  open.value = true;
  title.value = '修改字典类型';
  eLoading.value = true;
  const dictId = row?.dictId || ids.value.at(0);
  getType(dictId).then((response) => {
    form.value = response.data;
    eLoading.value = false;
  });
}
function onConfirm() {
  dictRef.value.submit();
}
/** 提交按钮 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.dictId) {
      updateType(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        })
        .finally(() => proxy.$modal.msgClose(msgLoading));
    } else {
      addType(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('新增成功');
          open.value = false;
          getList();
        })
        .finally(() => proxy.$modal.msgClose(msgLoading));
    }
  } else {
    proxy.$modal.msgError(firstError);
  }
}
/** 删除按钮操作 */
function handleDelete(row?: SysDictTypeVo) {
  const dictIds = row?.dictId || ids.value;
  proxy.$modal.confirm(`是否确认删除字典编号为"${dictIds}"的数据项？`, () => {
    return delType(dictIds).then(() => {
      ids.value = ArrayOps.fastDeleteElement(ids.value, dictIds);
      getList();
      proxy.$modal.msgSuccess('删除成功');
    });
  });
}
/** 导出按钮操作 */
function handleExport() {
  proxy.addDateRange(queryParams.value, dateRange.value);
  proxy.download(
    'system/dict/type/export',
    {
      ...queryParams.value,
    },
    `dict_${new Date().getTime()}.xlsx`,
  );
}
/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  proxy.$modal.confirm(`是否确认刷新字典缓存？`, () => {
    return refreshCache().then(() => {
      proxy.$modal.msgSuccess('刷新成功');
      useDictStore().cleanDict();
    });
  });
}

getList();
</script>
