<template>
  <t-card>
    <t-row :gutter="20">
      <!--分类数据-->
      <t-col :sm="2" :xs="12">
        <div class="head-container">
          <t-space>
            <t-input v-model="categoryName" placeholder="搜索分类名称" clearable style="margin-bottom: 20px">
              <template #prefixIcon>
                <search-icon />
              </template>
            </t-input>
            <t-button shape="square" variant="outline" @click="getCategoryTree">
              <template #icon><refresh-icon /></template>
            </t-button>
          </t-space>
        </div>
        <div class="head-container">
          <t-skeleton animation="gradient" :loading="loadingOptions">
            <t-tree
              ref="categoryTreeRef"
              v-model:actived="categoryActived"
              v-model:expanded="expandedCategoryArray"
              class="left-tree t-tree--block-node"
              :data="fileCategoryTree"
              :keys="{ value: 'fileCategoryId', label: 'categoryName', children: 'children' }"
              :filter="filterNode"
              activable
              hover
              line
              allow-fold-node-on-filter
              transition
            >
              <template #label="{ node }"> {{ node.data.categoryName }} ({{ node.data.fileCount ?? 0 }})</template>
              <template #empty>
                <my-link v-hasPermi="['system:fileCategory:add']" @click.stop="handleCategoryAdd()"> 新建分类 </my-link>
              </template>
              <template #operations="{ node }">
                <t-dropdown
                  v-hasPermi="[
                    'system:fileCategory:query',
                    'system:fileCategory:add',
                    'system:fileCategory:edit',
                    'system:fileCategory:remove',
                  ]"
                  placement="bottom"
                  :max-column-width="120"
                  :popup-props="{ showArrow: true }"
                >
                  <t-button size="small" theme="primary" variant="text"> 操作 </t-button>
                  <t-dropdown-menu>
                    <t-dropdown-item
                      v-if="node.value !== 0"
                      v-hasPermi="['system:fileCategory:query']"
                      content="查看详情"
                      @click="handleCategoryDetail(node.data)"
                    >
                      <template #prefix-icon><browse-icon /></template>
                    </t-dropdown-item>
                    <t-dropdown-item
                      v-hasPermi="['system:fileCategory:add']"
                      content="新增子分类"
                      @click="handleCategoryAdd(node.data)"
                    >
                      <template #prefix-icon><add-icon /></template>
                    </t-dropdown-item>
                    <t-dropdown-item
                      v-if="node.value !== 0"
                      v-hasPermi="['system:fileCategory:add']"
                      content="前插分类"
                      @click="handleCategoryAdd(node.getParent()?.data, node.data.orderNum - 1)"
                    >
                      <template #prefix-icon><arrow-up-icon /></template>
                    </t-dropdown-item>
                    <t-dropdown-item
                      v-if="node.value !== 0"
                      v-hasPermi="['system:fileCategory:add']"
                      content="后插分类"
                      @click="handleCategoryAdd(node.getParent()?.data, node.data.orderNum + 1)"
                    >
                      <template #prefix-icon><arrow-down-icon /></template>
                    </t-dropdown-item>
                    <t-dropdown-item
                      v-if="node.value !== 0"
                      v-hasPermi="['system:fileCategory:edit']"
                      content="编辑分类"
                      @click="handleCategoryUpdate(node.data)"
                    >
                      <template #prefix-icon><edit-icon /></template>
                    </t-dropdown-item>
                    <t-dropdown-item
                      v-if="node.value !== 0"
                      v-hasPermi="['system:fileCategory:remove']"
                      content="删除分类"
                      theme="error"
                      @click="handleCategoryDelete(node.data)"
                    >
                      <template #prefix-icon><delete-icon /></template>
                    </t-dropdown-item>
                  </t-dropdown-menu>
                </t-dropdown>
              </template>
            </t-tree>
          </t-skeleton>
        </div>
      </t-col>
      <!-- 文件数据 -->
      <t-col :sm="10" :xs="12">
        <file-list
          :category-id="categoryActived[0]"
          :query-param="queryParam"
          :multiple="multiple"
          :image-upload="imageUpload"
          :file-upload="fileUpload"
          :file-upload-props="fileUploadProps"
          :image-upload-props="imageUploadProps"
          :thumbnail-size="thumbnailSize"
          :rect-max-height="rectMaxHeight"
          @change="(selectValues) => emit('change', selectValues)"
          @update="getCategoryTree"
        />
      </t-col>
    </t-row>

    <!-- 添加或修改文件分类对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      width="500px"
      :confirm-btn="{
        loading: buttonLoading,
      }"
      @confirm="onConfirmCategory"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="fileCategoryRef"
          :data="form"
          :rules="rules"
          label-align="right"
          label-width="calc(4em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitCategoryForm"
        >
          <t-form-item label="分类名称" name="categoryName">
            <t-input v-model="form.categoryName" placeholder="请输入分类名称" clearable :maxlength="50" />
          </t-form-item>
          <t-form-item label="父级分类" name="parentId">
            <t-tree-select
              v-model="form.parentId"
              :data="fileCategoryOptions"
              :tree-props="{
                keys: { value: 'fileCategoryId', label: 'categoryName', children: 'children' },
                checkStrictly: true,
              }"
              placeholder="请选择父级分类"
            />
          </t-form-item>
          <t-form-item label="显示顺序" name="orderNum">
            <t-input-number v-model="form.orderNum" placeholder="请输入" />
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 文件分类详情 -->
    <t-dialog v-model:visible="openView" header="文件分类详情" width="700px" :footer="false">
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="文件分类id">{{ form.fileCategoryId }}</t-descriptions-item>
        <t-descriptions-item label="分类名称">{{ form.categoryName }}</t-descriptions-item>
        <t-descriptions-item label="文件数量">{{ form.fileCount }}</t-descriptions-item>
        <t-descriptions-item label="父级分类id">{{ form.parentId }}</t-descriptions-item>
        <t-descriptions-item label="父级分类">{{ form.parentCategoryName }}</t-descriptions-item>
        <t-descriptions-item label="分类路径" :span="2">{{ form.categoryPath }}</t-descriptions-item>
        <t-descriptions-item label="层级">{{ form.level }}</t-descriptions-item>
        <t-descriptions-item label="显示顺序">{{ form.orderNum }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'FileCategory',
});
import {
  AddIcon,
  ArrowDownIcon,
  ArrowUpIcon,
  BrowseIcon,
  DeleteIcon,
  EditIcon,
  RefreshIcon,
  SearchIcon,
} from 'tdesign-icons-vue-next';
import type { FormInstanceFunctions, FormRule, SubmitContext, TreeNodeModel, TreeNodeValue } from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref, watch } from 'vue';

import {
  addFileCategory,
  delFileCategory,
  getFileCategory,
  listFileCategory,
  updateFileCategory,
} from '@/api/system/fileCategory';
import type {
  SysFileCategoryForm,
  SysFileCategoryQuery,
  SysFileCategoryVo,
} from '@/api/system/model/fileCategoryModel';
import type { SysFileVo } from '@/api/system/model/fileModel';

import type { FileListProps } from './FileList.vue';
import FileList from './FileList.vue';

export interface FileCategoryProps extends Omit<FileListProps, 'categoryId'> {}
const props = withDefaults(defineProps<FileCategoryProps>(), {
  imageUpload: true,
  fileUpload: true,
  multiple: true,
  thumbnailSize: 120,
});
watch(
  () => props.queryParam,
  () => getCategoryTree(),
);

const { proxy } = getCurrentInstance();

const openView = ref(false);
const openViewLoading = ref(false);
const loadingOptions = ref(false);
const fileCategoryTree = ref<SysFileCategoryVo[]>([]);
const fileCategoryOptions = ref<SysFileCategoryVo[]>([]);
const expandedCategoryArray = ref<TreeNodeValue[]>([]);
const open = ref(false);
const buttonLoading = ref(false);
const title = ref('');
const fileCategoryRef = ref<FormInstanceFunctions>();
const categoryName = ref('');
const categoryActived = ref<number[]>([]);

const rules = ref<Record<string, Array<FormRule>>>({
  categoryName: [
    { required: true, message: '分类名称不能为空' },
    { pattern: /^[^/%_*]*$/, message: '分类名不能包含下列任何字符：/%_*' },
  ],
  parentId: [{ required: true, message: '父级分类不能为空' }],
  orderNum: [{ required: true, message: '显示顺序不能为空' }],
});
// 提交表单对象
const form = ref<SysFileCategoryVo & SysFileCategoryForm>({});
const emit = defineEmits<{
  (e: 'change', selectValues: SysFileVo[]): void;
}>();
const queryParams = computed<SysFileCategoryQuery>(() => ({
  suffixes: props.queryParam?.suffixes?.map((value) => `.${value}`),
  maxSize: props.queryParam?.maxSize,
  contentTypes: props.queryParam?.contentTypes,
}));

/** 通过条件过滤节点  */
function filterNode(node: TreeNodeModel) {
  if (!node.value || !categoryName.value) return true;
  return node.label.indexOf(categoryName.value) >= 0;
}

// 表单重置
function reset() {
  form.value = {
    orderNum: 0,
  };
  proxy.resetForm('fileCategoryRef');
}

/** 新增分类按钮操作 */
function handleCategoryAdd(row?: SysFileCategoryVo, orderNum?: number) {
  reset();
  getCategoryOptions();
  if (row != null && row.fileCategoryId) {
    form.value.parentId = row.fileCategoryId;
  } else {
    form.value.parentId = 0;
  }
  if (orderNum) {
    form.value.orderNum = orderNum;
  }
  open.value = true;
  title.value = '添加文件分类';
}

/** 分类详情按钮操作 */
function handleCategoryDetail(row: SysFileCategoryVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const fileCategoryId = row.fileCategoryId;
  getFileCategory({ ...queryParams.value, fileCategoryId }).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改分类按钮操作 */
async function handleCategoryUpdate(row?: SysFileCategoryVo) {
  buttonLoading.value = true;
  reset();
  open.value = true;
  title.value = '修改文件分类';
  await getCategoryOptions();
  if (row != null) {
    form.value.parentId = row.fileCategoryId;
  }
  getFileCategory({ ...queryParams.value, fileCategoryId: row.fileCategoryId }).then((response) => {
    buttonLoading.value = false;
    form.value = response.data;
  });
}

/** 查询文件分类下拉树选项结构 */
async function getCategoryOptions() {
  const response = await listFileCategory();
  fileCategoryOptions.value = [
    {
      fileCategoryId: 0,
      categoryName: '根分类',
      children: proxy.handleTree(response.data, 'fileCategoryId', 'parentId'),
    },
  ];
}

/** 查询文件分类树结构 */
function getCategoryTree() {
  loadingOptions.value = true;
  listFileCategory(queryParams.value)
    .then((response) => {
      fileCategoryTree.value = proxy.handleTree(response.data, 'fileCategoryId', 'parentId');
    })
    .finally(() => (loadingOptions.value = false));
}

/** 提交分类按钮 */
function onConfirmCategory() {
  fileCategoryRef.value.submit();
}

/** 提交分类表单 */
function submitCategoryForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.fileCategoryId) {
      updateFileCategory(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getCategoryTree();
        })
        .finally(() => {
          buttonLoading.value = false;
          proxy.$modal.msgClose(msgLoading);
        });
    } else {
      addFileCategory(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('新增成功');
          open.value = false;
          getCategoryTree();
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

/** 删除分类按钮操作 */
function handleCategoryDelete(row: SysFileCategoryVo) {
  proxy.$modal.confirm(`是否确认删除【${row.categoryName}】分类？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delFileCategory(row.fileCategoryId)
      .then(() => {
        categoryActived.value = categoryActived.value.filter((value) => value !== row.fileCategoryId);
        getCategoryTree();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => {
        proxy.$modal.msgClose(msgLoading);
      });
  });
}

getCategoryTree();
</script>
<style lang="less" scoped>
.left-tree {
  :deep(.t-tree__empty) {
    display: flex;
    justify-content: center;
    min-height: 200px;
  }
}
</style>
