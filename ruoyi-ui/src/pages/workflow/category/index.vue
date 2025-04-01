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
        <t-form-item label="分类名称" name="categoryName">
          <t-input v-model="queryParams.categoryName" placeholder="请输入分类名称" clearable @enter="handleQuery" />
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

      <t-enhanced-table
        ref="tableRef"
        v-model:column-controller-visible="columnControllerVisible"
        v-model:expandedTreeNodes="expandedTreeNodes"
        hover
        :loading="loading"
        :data="categoryList"
        row-key="categoryId"
        :columns="columns"
        :column-controller="{
          hideTriggerButton: true,
        }"
        :tree="{ childrenKey: 'children', expandTreeNodeOnClick: true }"
      >
        <template #topContent>
          <t-row>
            <t-col flex="auto">
              <t-button v-hasPermi="['workflow:category:add']" theme="primary" @click="handleAdd()">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button theme="default" variant="outline" @click="toggleExpandAll">
                <template #icon> <unfold-less-icon v-if="isExpand" /> <unfold-more-icon v-else /> </template>
                全部{{ isExpand ? '折叠' : '展开' }}
              </t-button>
              <t-button
                v-hasPermi="['workflow:category:export']"
                theme="default"
                variant="outline"
                @click="handleExport"
              >
                <template #icon> <download-icon /> </template>
                导出
              </t-button>
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
          <t-space :size="8" break-line>
            <t-link
              v-hasPermi="['workflow:category:query']"
              theme="primary"
              hover="color"
              @click.stop="handleDetail(row)"
            >
              <browse-icon />详情
            </t-link>
            <t-link
              v-hasPermi="['workflow:category:edit']"
              theme="primary"
              hover="color"
              @click.stop="handleUpdate(row)"
            >
              <edit-icon />修改
            </t-link>
            <t-link v-hasPermi="['workflow:category:add']" theme="primary" hover="color" @click.stop="handleAdd(row)">
              <add-icon />新增
            </t-link>
            <t-link
              v-hasPermi="['workflow:category:remove']"
              theme="danger"
              hover="color"
              @click.stop="handleDelete(row)"
            >
              <delete-icon />删除
            </t-link>
          </t-space>
        </template>
      </t-enhanced-table>
    </t-space>

    <!-- 添加或修改流程分类对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      width="min(500px, 100%)"
      attach="body"
      :confirm-btn="{
        loading: buttonLoading,
      }"
      @confirm="categoryRef.submit()"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="categoryRef"
          :data="form"
          :rules="rules"
          label-align="right"
          label-width="calc(4em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitForm"
        >
          <t-form-item label="分类名称" name="categoryName">
            <t-input v-model="form.categoryName" placeholder="请输入分类名称" clearable />
          </t-form-item>
          <t-form-item label="上级分类" name="parentId">
            <t-tree-select
              v-model="form.parentId"
              :data="categoryOptions"
              :tree-props="{
                keys: { value: 'categoryId', label: 'categoryName', children: 'children' },
                checkStrictly: true,
              }"
              placeholder="请选择上级分类"
            />
          </t-form-item>
          <t-form-item label="排序" name="orderNum">
            <t-input-number v-model="form.orderNum" placeholder="请输入" :min="0" :allow-input-over-limit="false" />
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 流程分类详情 -->
    <t-dialog
      v-model:visible="openView"
      header="流程分类详情"
      placement="center"
      width="min(700px, 100%)"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="分类名称">{{ form.categoryName }}</t-descriptions-item>
        <t-descriptions-item label="上级分类">{{ form.parentId }}</t-descriptions-item>
        <t-descriptions-item label="排序">{{ form.orderNum }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'Category',
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
  UnfoldLessIcon,
  UnfoldMoreIcon,
} from 'tdesign-icons-vue-next';
import type {
  EnhancedTableInstanceFunctions,
  FormInstanceFunctions,
  FormRule,
  PrimaryTableCol,
  SubmitContext,
} from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import { addCategory, delCategory, getCategory, listCategory, updateCategory } from '@/api/workflow/category';
import type { CategoryForm, CategoryQuery, CategoryVO } from '@/api/workflow/model/categoryModel';

const { proxy } = getCurrentInstance();

const openView = ref(false);
const openViewLoading = ref(false);
const categoryList = ref<CategoryVO[]>([]);
const loading = ref(false);
const columnControllerVisible = ref(false);
const showSearch = ref(true);
const tableRef = ref<EnhancedTableInstanceFunctions>();
const categoryOptions = ref<CategoryVO[]>([]);
const open = ref(false);
const buttonLoading = ref(false);
const title = ref('');
const categoryRef = ref<FormInstanceFunctions>();
const expandedTreeNodes = ref([]);

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  parentId: [{ required: true, message: '请选择上级分类' }],
  categoryName: [
    { required: true, message: '请输入分类名称' },
    { max: 255, message: '分类名称不能超过255个字符' },
  ],
});

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `分类名称`, colKey: 'categoryName', width: 260 },
  { title: `上级分类`, colKey: 'parentId', align: 'center' },
  { title: `显示顺序`, colKey: 'orderNum', align: 'center', width: 200 },
  { title: `创建时间`, colKey: 'createTime', align: 'center', minWidth: 112, width: 180 },
  { title: `更新时间`, colKey: 'updateTime', align: 'center', minWidth: 112, width: 180 },
  { title: `操作`, colKey: 'operation', align: 'center', width: 240, fixed: 'right' },
]);
// 提交表单对象
const form = ref<CategoryVO & CategoryForm>({});
// 查询对象
const queryParams = ref<CategoryQuery>({
  categoryName: undefined,
});
const isExpand = computed(() => {
  return expandedTreeNodes.value.length !== 0;
});

/** 查询流程分类列表 */
function getList() {
  loading.value = true;
  listCategory(queryParams.value)
    .then((response) => {
      categoryList.value = proxy.handleTree(response.data, 'categoryId', 'parentId');
    })
    .finally(() => (loading.value = false));
}

// 表单重置
function reset() {
  form.value = {
    orderNum: 0,
  };
  proxy.resetForm('categoryRef');
}

/** 搜索按钮操作 */
function handleQuery() {
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
  handleQuery();
}

/** 展开/折叠操作 */
function toggleExpandAll() {
  if (isExpand.value) {
    tableRef.value.foldAll();
  } else {
    tableRef.value.expandAll();
  }
}

/** 详情按钮操作 */
function handleDetail(row: CategoryVO) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const id = row.categoryId;
  getCategory(id).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 新增按钮操作 */
function handleAdd(row?: CategoryVO) {
  reset();
  getTreeselect();
  if (row != null && row.categoryId) {
    form.value.parentId = row.categoryId;
  } else {
    form.value.parentId = 0;
  }
  open.value = true;
  title.value = '添加流程分类';
}

/** 修改按钮操作 */
async function handleUpdate(row?: CategoryVO) {
  buttonLoading.value = true;
  reset();
  open.value = true;
  title.value = '修改流程分类';
  await getTreeselect();
  getCategory(row.categoryId).then((response) => {
    buttonLoading.value = false;
    form.value = response.data;
  });
}

/** 查询流程分类下拉树结构 */
async function getTreeselect() {
  return listCategory().then((response) => {
    categoryOptions.value = [
      { categoryId: 0, categoryName: '顶级节点', children: proxy.handleTree(response.data, 'categoryId', 'parentId') },
    ];
  });
}

/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.categoryId) {
      updateCategory(form.value)
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
      addCategory(form.value)
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
function handleDelete(row: CategoryVO) {
  proxy.$modal.confirm(`是否确认删除"${row.categoryName}"的分类？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delCategory(row.categoryId)
      .then(() => {
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
    'workflow/category/export',
    {
      ...queryParams.value,
    },
    `category_${new Date().getTime()}.xlsx`,
  );
}

getList();
</script>
