<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="字典名称" name="dictType">
          <t-select v-model="queryParams.dictType" style="width: 200px">
            <t-option v-for="item in typeOptions" :key="item.dictId" :label="item.dictName" :value="item.dictType" />
          </t-select>
        </t-form-item>
        <t-form-item label="字典标签" name="dictLabel">
          <t-input
            v-model="queryParams.dictLabel"
            placeholder="请输入字典标签"
            clearable
            style="width: 200px"
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
        v-model:column-controller-visible="columnControllerVisible"
        :loading="loading"
        hover
        row-key="dictCode"
        :data="dataList"
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
              <t-button theme="warning" variant="outline" @click="handleClose">
                <template #icon> <close-icon /> </template>
                关闭
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
        <template #dictLabel="{ row }">
          <dict-tag :options="dictOptions" :value="row.dictValue" />
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
        <t-form
          ref="dataRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(4em + 41px)"
          @submit="submitForm"
        >
          <t-form-item label="字典类型">
            <t-input v-model="form.dictType" :disabled="true" />
          </t-form-item>
          <t-form-item label="数据标签" name="dictLabel">
            <t-input v-model="form.dictLabel" placeholder="请输入数据标签" />
          </t-form-item>
          <t-form-item label="数据键值" name="dictValue">
            <t-input v-model="form.dictValue" placeholder="请输入数据键值" />
          </t-form-item>
          <t-form-item label="样式属性" name="cssClass">
            <t-input v-model="form.cssClass" placeholder="请输入样式属性" />
          </t-form-item>
          <t-form-item label="显示排序" name="dictSort">
            <t-input-number v-model="form.dictSort" :min="0" />
          </t-form-item>
          <t-form-item label="回显样式" name="listClass">
            <t-select v-model="form.listClass" placeholder="请选择回显样式">
              <t-option v-for="item in listClassOptions" :key="item.value" :label="item.label" :value="item.value" />
            </t-select>
          </t-form-item>
          <t-form-item label="回显风格" name="tagStyle">
            <t-select v-model="form.tagStyle" placeholder="请选择回显风格" clearable>
              <t-option v-for="item in tagStyleOptions" :key="item.value" :label="item.label" :value="item.value" />
            </t-select>
          </t-form-item>
          <t-form-item label="备注" name="remark">
            <t-textarea v-model="form.remark" placeholder="请输入备注"></t-textarea>
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 字典数据详情 -->
    <t-dialog
      v-model:visible="openView"
      header="字典数据详情"
      placement="center"
      width="700px"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="字典编码">{{ form.dictCode }}</t-descriptions-item>
        <t-descriptions-item label="字典排序">{{ form.dictSort }}</t-descriptions-item>
        <t-descriptions-item label="字典标签">
          <dict-tag :options="dictOptions" :value="form.dictValue" />
        </t-descriptions-item>
        <t-descriptions-item label="字典键值">{{ form.dictValue }}</t-descriptions-item>
        <t-descriptions-item label="字典类型">{{ form.dictType }}</t-descriptions-item>
        <t-descriptions-item label="样式属性">{{ form.cssClass }}</t-descriptions-item>
        <t-descriptions-item label="表格回显样式">
          <dict-tag :options="listClassOptions" :value="form.listClass" />
        </t-descriptions-item>
        <t-descriptions-item label="回显风格">{{ form.tagStyle }}</t-descriptions-item>
        <t-descriptions-item label="是否默认">
          <dict-tag :options="sys_yes_no" :value="form.isDefault" />
        </t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ form.remark }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'DictData',
});

import {
  AddIcon,
  BrowseIcon,
  CloseIcon,
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
import { useRoute } from 'vue-router';

import { addData, delData, getData, listData, updateData } from '@/api/system/dict/data';
import { getType, optionselect as getDictOptionselect } from '@/api/system/dict/type';
import type { SysDictDataForm, SysDictDataQuery, SysDictDataVo, SysDictTypeVo } from '@/api/system/model/dictModel';
import { useTabsRouterStore } from '@/store';
import useDictStore from '@/store/modules/dict';
import { ArrayOps } from '@/utils/array';
import type { DictModel } from '@/utils/dict';
import { dictConvert } from '@/utils/dict';

const { proxy } = getCurrentInstance();
const { sys_yes_no } = proxy.useDict('sys_yes_no');

const openView = ref(false);
const openViewLoading = ref(false);
const dataList = ref<SysDictDataVo[]>([]);
const open = ref(false);
const loading = ref(false);
const eLoading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const defaultDictType = ref('');
const typeOptions = ref<SysDictTypeVo[]>([]);
const columnControllerVisible = ref(false);
const dataRef = ref<FormInstanceFunctions>();
const removeCurrentTab = useTabsRouterStore().useRemoveCurrentTab();
const route = useRoute();
const sort = ref<TableSort>();
const dictOptions = computed(() => {
  return dictConvert(dataList.value);
});
// 数据标签回显样式
const listClassOptions = ref<DictModel[]>([
  { value: 'default', tagType: 'default', label: '默认(default)' },
  { value: 'primary', tagType: 'primary', label: '主要(primary)' },
  { value: 'success', tagType: 'success', label: '成功(success)' },
  { value: 'warning', tagType: 'warning', label: '警告(warning)' },
  { value: 'danger', tagType: 'danger', label: '危险(danger)' },
  { value: 'text', tagType: 'text', label: '文本(text)' },
]);

const tagStyleOptions = ref([
  { value: 'outline', label: 'outline' },
  { value: 'dark', label: 'dark' },
  { value: 'light', label: 'light' },
  { value: 'light-outline', label: 'light-outline' },
]);

const rules = ref<Record<string, Array<FormRule>>>({
  dictLabel: [{ required: true, message: '数据标签不能为空' }],
  dictValue: [{ required: true, message: '数据键值不能为空' }],
  dictSort: [{ required: true, message: '数据顺序不能为空' }],
});
// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `字典编码`, colKey: 'dictCode', align: 'center' },
  { title: `字典标签`, colKey: 'dictLabel', align: 'center' },
  { title: `字典键值`, colKey: 'dictValue', align: 'center' },
  { title: `字典排序`, colKey: 'dictSort', align: 'center', sorter: true },
  { title: `备注`, colKey: 'remark', align: 'center', ellipsis: true },
  { title: `创建时间`, colKey: 'createTime', align: 'center', width: 180, sorter: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 提交表单对象
const form = ref<SysDictDataForm & SysDictDataVo>({
  listClass: 'default',
  dictSort: 0,
});
// 查询对象
const queryParams = ref<SysDictDataQuery>({
  pageNum: 1,
  pageSize: 10,
  dictLabel: undefined,
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

/** 查询字典类型详细 */
function getTypes(dictId: string | number | string[]) {
  getType(dictId as number).then((response) => {
    queryParams.value.dictType = response.data.dictType;
    defaultDictType.value = response.data.dictType;
    getList();
  });
}

/** 查询字典类型列表 */
function getTypeList() {
  getDictOptionselect().then((response) => {
    typeOptions.value = response.data;
  });
}
/** 查询字典数据列表 */
function getList() {
  loading.value = true;
  listData(queryParams.value)
    .then((response) => {
      dataList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}
/** 表单重置 */
function reset() {
  form.value = {
    dictCode: undefined,
    dictLabel: undefined,
    dictValue: undefined,
    dictType: undefined,
    cssClass: undefined,
    listClass: 'text',
    dictSort: 0,
    remark: undefined,
  };
  proxy.resetForm('dataRef');
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 返回按钮操作 */
function handleClose() {
  removeCurrentTab('/system/dict');
}
/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
  queryParams.value.dictType = defaultDictType.value;
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
  title.value = '添加字典数据';
  form.value.dictType = queryParams.value.dictType;
}

/** 详情按钮操作 */
function handleDetail(row: SysDictDataVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const dictCode = row.dictCode;
  getData(dictCode).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysDictDataVo) {
  reset();
  open.value = true;
  title.value = '修改字典数据';
  const dictCode = row?.dictCode || ids.value.at(0);
  eLoading.value = true;
  getData(dictCode).then((response) => {
    form.value = response.data;
    eLoading.value = false;
  });
}
function onConfirm() {
  dataRef.value.submit();
}
/** 提交按钮 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.dictCode) {
      updateData(form.value)
        .then(() => {
          useDictStore().removeDict(queryParams.value.dictType);
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        })
        .finally(() => proxy.$modal.msgClose(msgLoading));
    } else {
      addData(form.value)
        .then(() => {
          useDictStore().removeDict(queryParams.value.dictType);
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
function handleDelete(row?: SysDictDataVo) {
  const dictCodes = row?.dictCode || ids.value;
  proxy.$modal.confirm(`是否确认删除字典编码为"${dictCodes}"的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delData(dictCodes)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, dictCodes);
        getList();
        proxy.$modal.msgSuccess('删除成功');
        useDictStore().removeDict(queryParams.value.dictType);
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  });
}
/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'system/dict/data/export',
    {
      ...queryParams.value,
    },
    `dict_data_${new Date().getTime()}.xlsx`,
  );
}

getTypes(route.params && route.params.dictId);
getTypeList();
</script>
