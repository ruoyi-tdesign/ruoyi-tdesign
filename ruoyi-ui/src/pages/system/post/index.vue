<template>
  <t-card>
    <t-row :gutter="20">
      <!--部门数据-->
      <t-col :sm="2" :xs="12">
        <dept-tree v-model="deptActived" @active="handleQuery" />
      </t-col>
      <!--用户数据-->
      <t-col :sm="10" :xs="12">
        <t-space direction="vertical" style="width: 100%">
          <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
            <t-form-item label="岗位名称" name="postName">
              <t-input
                v-model="queryParams.postName"
                placeholder="请输入岗位名称"
                clearable
                style="width: 200px"
                @enter="handleQuery"
              />
            </t-form-item>
            <t-form-item label="岗位编码" name="postCode">
              <t-input
                v-model="queryParams.postCode"
                placeholder="请输入岗位编码"
                clearable
                style="width: 200px"
                @enter="handleQuery"
              />
            </t-form-item>
            <t-form-item label="类别编码" name="postCategory">
              <t-input v-model="queryParams.postCategory" placeholder="请输入类别编码" clearable @enter="handleQuery" />
            </t-form-item>
            <t-form-item label="状态" name="status">
              <t-select v-model="queryParams.status" placeholder="岗位状态" clearable style="width: 200px">
                <t-option
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
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
            row-key="postId"
            :data="postList"
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
                  <t-button v-hasPermi="['system:post:add']" theme="primary" @click="handleAdd">
                    <template #icon> <add-icon /></template>
                    新增
                  </t-button>
                  <t-button
                    v-hasPermi="['system:post:edit']"
                    theme="default"
                    variant="outline"
                    :disabled="single"
                    @click="handleUpdate()"
                  >
                    <template #icon> <edit-icon /> </template>
                    修改
                  </t-button>
                  <t-button
                    v-hasPermi="['system:post:remove']"
                    theme="danger"
                    variant="outline"
                    :disabled="multiple"
                    @click="handleDelete()"
                  >
                    <template #icon> <delete-icon /> </template>
                    删除
                  </t-button>
                  <t-button v-hasPermi="['system:post:export']" theme="default" variant="outline" @click="handleExport">
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
              <dict-tag :options="sys_normal_disable" :value="row.status" />
            </template>
            <template #operation="{ row }">
              <t-space :size="8" break-line>
                <my-link v-hasPermi="['system:post:query']" @click.stop="handleDetail(row)">
                  <template #prefix-icon><browse-icon /></template>详情
                </my-link>
                <my-link v-hasPermi="['system:post:edit']" @click.stop="handleUpdate(row)">
                  <template #prefix-icon><edit-icon /></template>修改
                </my-link>
                <my-link v-hasPermi="['system:post:remove']" theme="danger" @click.stop="handleDelete(row)">
                  <template #prefix-icon><delete-icon /></template>删除
                </my-link>
              </t-space>
            </template>
          </t-table>
        </t-space>
      </t-col>
    </t-row>

    <!-- 添加或修改岗位信息对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      width="min(500px, 100%)"
      attach="body"
      :confirm-btn="{
        loading: eLoading,
      }"
      @confirm="onConfirm"
    >
      <t-loading :loading="eLoading" size="small">
        <t-form
          ref="postRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(4em + 41px)"
          @submit="submitForm"
        >
          <t-form-item label="岗位名称" name="postName">
            <t-input v-model="form.postName" placeholder="请输入岗位名称" />
          </t-form-item>
          <t-form-item label="归属部门" name="deptId">
            <t-tree-select
              v-model="form.deptId"
              :data="deptOptions"
              :tree-props="{
                keys: { value: 'id', label: 'label', children: 'children' },
                checkStrictly: true,
              }"
              placeholder="请选择归属部门"
            />
          </t-form-item>
          <t-form-item label="岗位编码" name="postCode">
            <t-input v-model="form.postCode" placeholder="请输入编码名称" />
          </t-form-item>
          <t-form-item label="类别编码" name="postCategory">
            <t-input v-model="form.postCategory" placeholder="请输入岗位类别编码" clearable />
          </t-form-item>
          <t-form-item label="岗位顺序" name="postSort">
            <t-input-number v-model="form.postSort" :min="0" />
          </t-form-item>
          <t-form-item label="岗位状态" name="status">
            <t-radio-group v-model="form.status">
              <t-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">
                {{ dict.label }}
              </t-radio>
            </t-radio-group>
          </t-form-item>
          <t-form-item label="备注" name="remark">
            <t-textarea v-model="form.remark" placeholder="请输入备注" />
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 岗位信息详情 -->
    <t-dialog
      v-model:visible="openView"
      header="岗位信息详情"
      placement="center"
      width="min(700px, 100%)"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="岗位ID">{{ form.postId }}</t-descriptions-item>
        <t-descriptions-item label="部门">{{ form.deptName }}</t-descriptions-item>
        <t-descriptions-item label="岗位编码">{{ form.postCode }}</t-descriptions-item>
        <t-descriptions-item label="类别编码">{{ form.postCategory }}</t-descriptions-item>
        <t-descriptions-item label="岗位名称">{{ form.postName }}</t-descriptions-item>
        <t-descriptions-item label="显示顺序">{{ form.postSort }}</t-descriptions-item>
        <t-descriptions-item label="状态">
          <dict-tag :options="sys_normal_disable" :value="form.status" />
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
  name: 'Post',
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

import type { TreeModel } from '@/api/model/resultModel';
import type { SysPostForm, SysPostQuery, SysPostVo } from '@/api/system/model/postModel';
import { addPost, delPost, getPost, listPost, updatePost } from '@/api/system/post';
import { deptTreeSelect } from '@/api/system/user';
import { ArrayOps } from '@/utils/array';

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const openView = ref(false);
const openViewLoading = ref(false);
const postList = ref<SysPostVo[]>([]);
const open = ref(false);
const loading = ref(false);
const eLoading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const postRef = ref<FormInstanceFunctions>();
const columnControllerVisible = ref(false);
const sort = ref<TableSort>();
const deptActived = ref<number[]>([]);
const deptOptions = ref<Array<TreeModel<number>>>([]);

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  deptId: [{ required: true, message: '部门不能为空' }],
  postName: [{ required: true, message: '岗位名称不能为空' }],
  postCode: [{ required: true, message: '岗位编码不能为空' }],
  postSort: [{ required: true, message: '岗位顺序不能为空' }],
});

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `岗位编码`, colKey: 'postCode', align: 'center' },
  { title: `类别编码`, colKey: 'postCategory', align: 'center' },
  { title: `岗位名称`, colKey: 'postName', align: 'center' },
  { title: `部门`, colKey: 'deptName', align: 'center' },
  { title: `岗位排序`, colKey: 'postSort', align: 'center', sorter: true },
  { title: `状态`, colKey: 'status', align: 'center' },
  { title: `创建时间`, colKey: 'createTime', align: 'center', width: 180, sorter: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 提交表单对象
const form = ref<SysPostForm & SysPostVo>({});
// 查询对象
const queryParams = ref<SysPostQuery>({
  pageNum: 1,
  pageSize: 10,
  postCode: undefined,
  postName: undefined,
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

/** 查询部门下拉树结构 */
async function getDeptTree() {
  return deptTreeSelect().then((response) => {
    deptOptions.value = response.data;
  });
}

/** 查询岗位信息列表 */
function getList() {
  loading.value = true;
  queryParams.value.deptId = deptActived.value.at(0);
  listPost(queryParams.value)
    .then((response) => {
      postList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}
/** 表单重置 */
function reset() {
  form.value = {
    postId: undefined,
    postCode: undefined,
    postName: undefined,
    postSort: 0,
    status: '1',
    remark: undefined,
  };
  deptActived.value = [];
  proxy.resetForm('postRef');
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
  getDeptTree();
  open.value = true;
  title.value = '添加岗位';
}

/** 详情按钮操作 */
function handleDetail(row: SysPostVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const postId = row.postId;
  getDeptTree();
  getPost(postId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysPostVo) {
  reset();
  open.value = true;
  title.value = '修改岗位';
  eLoading.value = true;
  const postId = row?.postId || ids.value.at(0);
  getPost(postId).then((response) => {
    form.value = response.data;
    eLoading.value = false;
  });
}

/** 提交按钮 */
function onConfirm() {
  postRef.value.submit();
}

/** 提交按钮 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.postId) {
      updatePost(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        })
        .finally(() => proxy.$modal.msgClose(msgLoading));
    } else {
      addPost(form.value)
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
function handleDelete(row?: SysPostVo) {
  const postIds = row?.postId || ids.value;
  proxy.$modal.confirm(`是否确认删除岗位编号为"${postIds}"的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delPost(postIds)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, postIds);
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  });
}

/** 导出按钮操作 */
function handleExport() {
  queryParams.value.deptId = deptActived.value.at(0);
  proxy.download(
    'system/post/export',
    {
      ...queryParams.value,
    },
    `post_${new Date().getTime()}.xlsx`,
  );
}

getList();
</script>
