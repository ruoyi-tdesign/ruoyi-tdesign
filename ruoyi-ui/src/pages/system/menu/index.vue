<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="菜单名称" name="menuName">
          <t-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="组件名称" name="componentName">
          <t-input v-model="queryParams.componentName" placeholder="请输入组件名称" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="queryParams.status" placeholder="菜单状态" clearable>
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

      <t-enhanced-table
        ref="tableRef"
        v-model:column-controller-visible="columnControllerVisible"
        v-model:expandedTreeNodes="expandedTreeNodes"
        hover
        :loading="loading"
        :data="menuList"
        row-key="menuId"
        :columns="columns"
        :column-controller="{
          hideTriggerButton: true,
        }"
        :tree="{ childrenKey: 'children', expandTreeNodeOnClick: true }"
        :sort="sort"
        show-sort-column-bg-color
        @sort-change="handleSortChange"
      >
        <template #topContent>
          <t-row>
            <t-col flex="auto">
              <t-button v-hasPermi="['system:menu:add']" theme="primary" @click="handleAdd()">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button theme="default" variant="outline" @click="toggleExpandAll">
                <template #icon> <unfold-less-icon v-if="isExpand" /> <unfold-more-icon v-else /> </template>
                全部{{ isExpand ? '折叠' : '展开' }}
              </t-button>
              <t-button v-hasPermi="['system:menu:export']" theme="default" variant="outline" @click="handleExport">
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
        <template #icon="{ row }">
          <r-icon :name="row.icon" />
        </template>
        <template #visible="{ row }">
          <dict-tag :options="sys_show_hide" :value="row.visible" />
        </template>
        <template #status="{ row }">
          <dict-tag :options="sys_normal_disable" :value="row.status" />
        </template>
        <template #menuType="{ row }">
          <dict-tag :options="menuTypeOptions" :value="row.menuType" />
        </template>
        <template #operation="{ row }">
          <t-space :size="4" break-line>
            <my-link v-hasPermi="['system:menu:query']" @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link v-hasPermi="['system:menu:edit']" @click.stop="handleUpdate(row)">
              <template #prefix-icon><edit-icon /></template>修改
            </my-link>
            <my-link v-hasPermi="['system:menu:edit']" @click.stop="handleCopyAdd(row)">
              <template #prefix-icon><copy-icon /></template>复制
            </my-link>
            <my-link v-hasPermi="['system:menu:add']" @click.stop="handleAdd(row)">
              <template #prefix-icon><add-icon /></template>新增
            </my-link>
            <my-link v-hasPermi="['system:menu:remove']" theme="danger" @click.stop="handleDelete(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-enhanced-table>
    </t-space>

    <!-- 添加或修改菜单权限对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      placement="center"
      width="min(780px, 100%)"
      attach="body"
      :confirm-btn="{
        loading: eLoading,
      }"
      @confirm="onConfirm"
    >
      <t-loading :loading="eLoading" size="small">
        <t-form
          ref="menuRef"
          label-align="right"
          label-width="calc(5em + 41px)"
          scroll-to-first-error="smooth"
          :data="form"
          :rules="rules"
          @submit="submitForm"
        >
          <t-row :gutter="[0, 20]">
            <t-col :span="12">
              <t-form-item label="上级菜单">
                <t-tree-select
                  v-model="form.parentId"
                  :data="menuOptions"
                  :tree-props="{
                    keys: { value: 'menuId', label: 'menuName', children: 'children' },
                    checkStrictly: true,
                  }"
                  placeholder="选择上级菜单"
                />
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="菜单类型" name="menuType">
                <t-radio-group v-model="form.menuType" :options="menuTypeOptions" />
              </t-form-item>
            </t-col>
            <t-col v-if="form.menuType !== 'F'" :span="12">
              <t-form-item label="菜单图标" name="icon">
                <icon-select v-model="form.icon" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="菜单名称" name="menuName">
                <t-input v-model="form.menuName" placeholder="请输入菜单名称" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="显示排序" name="orderNum">
                <t-input-number v-model="form.orderNum" :min="0" />
              </t-form-item>
            </t-col>
            <t-col v-if="form.menuType !== 'F'" :span="6">
              <t-form-item>
                <template #label>
                  <span>
                    <t-tooltip content="选择是外链则路由地址需要以`http(s)://`开头" placement="top">
                      <help-circle-filled-icon />
                    </t-tooltip>
                    是否外链
                  </span>
                </template>
                <t-radio-group v-model="form.isFrame" :options="yesNoOptions" />
              </t-form-item>
            </t-col>
            <t-col v-if="form.menuType !== 'F'" :span="6">
              <t-form-item name="path">
                <template #label>
                  <span>
                    <t-tooltip
                      content="访问的路由地址，如：`user`，如外网地址需内链访问则以`http(s)://`开头"
                      placement="top"
                    >
                      <help-circle-filled-icon />
                    </t-tooltip>
                    路由地址
                  </span>
                </template>
                <t-input v-model="form.path" placeholder="请输入路由地址" />
              </t-form-item>
            </t-col>
            <t-col v-if="form.menuType === 'C'" :span="6">
              <t-form-item name="componentName">
                <template #label>
                  <span>
                    <t-tooltip content="声明组件的名称，不填默认使用路由地址，如 /menu => Menu" placement="top">
                      <help-circle-filled-icon />
                    </t-tooltip>
                    组件名称
                  </span>
                </template>
                <t-input v-model="form.componentName" placeholder="请输入组件名称" clearable />
              </t-form-item>
            </t-col>
            <t-col v-if="form.menuType === 'C'" :span="6">
              <t-form-item name="component">
                <template #label>
                  <span>
                    <t-tooltip content="访问的组件路径，如：`system/user/index`，默认在`pages`目录下" placement="top">
                      <help-circle-filled-icon />
                    </t-tooltip>
                    组件路径
                  </span>
                </template>
                <t-input v-model="form.component" placeholder="请输入组件路径" />
              </t-form-item>
            </t-col>
            <t-col v-if="form.menuType !== 'M'" :span="6">
              <t-form-item>
                <t-input v-model="form.perms" placeholder="请输入权限标识" :maxlength="100" />
                <template #label>
                  <span>
                    <t-tooltip
                      content="控制器中定义的权限字符，如：@SaCheckPermission('system:user:list')"
                      placement="top"
                    >
                      <help-circle-filled-icon />
                    </t-tooltip>
                    权限字符
                  </span>
                </template>
              </t-form-item>
            </t-col>
            <t-col v-if="form.menuType === 'C'" :span="6">
              <t-form-item>
                <t-input v-model="form.queryParam" placeholder="请输入路由参数" :maxlength="255" />
                <template #label>
                  <span>
                    <t-tooltip content='访问路由的默认传递参数，如：`{"id": 1, "name": "ry"}`' placement="top">
                      <help-circle-filled-icon />
                    </t-tooltip>
                    路由参数
                  </span>
                </template>
              </t-form-item>
            </t-col>
            <t-col v-if="form.menuType === 'C'" :span="6">
              <t-form-item>
                <template #label>
                  <span>
                    <t-tooltip
                      content="选择是则会被`keep-alive`缓存，需要匹配组件的`name`和地址保持一致"
                      placement="top"
                    >
                      <help-circle-filled-icon />
                    </t-tooltip>
                    是否缓存
                  </span>
                </template>
                <t-radio-group v-model="form.isCache" :options="cacheOptions" />
              </t-form-item>
            </t-col>
            <t-col v-if="form.menuType !== 'F'" :span="6">
              <t-form-item>
                <template #label>
                  <span>
                    <t-tooltip content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问" placement="top">
                      <help-circle-filled-icon />
                    </t-tooltip>
                    显示状态
                  </span>
                </template>
                <t-radio-group v-model="form.visible">
                  <t-radio v-for="dict in sys_show_hide" :key="dict.value" :value="dict.value">
                    {{ dict.label }}
                  </t-radio>
                </t-radio-group>
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item>
                <template #label>
                  <span>
                    <t-tooltip content="选择停用则路由将不会出现在侧边栏，也不能被访问" placement="top">
                      <help-circle-filled-icon />
                    </t-tooltip>
                    菜单状态
                  </span>
                </template>
                <t-radio-group v-model="form.status">
                  <t-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">
                    {{ dict.label }}
                  </t-radio>
                </t-radio-group>
              </t-form-item>
            </t-col>
            <toggle-advanced v-if="form.menuType !== 'F'">
              <t-col :span="12">
                <t-form-item
                  label="隐藏表达式"
                  name="hiddenExpression"
                  help="使用SpEL表达式注入变量，返回值只能为true或false"
                >
                  <t-input v-model="form.hiddenExpression" placeholder="请输入隐藏表达式" clearable />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item
                  label="停用表达式"
                  name="shopExpression"
                  help="使用SpEL表达式注入变量，返回值只能为true或false"
                >
                  <t-input v-model="form.shopExpression" placeholder="请输入停用表达式" clearable />
                </t-form-item>
              </t-col>
            </toggle-advanced>
          </t-row>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 菜单权限详情 -->
    <t-dialog
      v-model:visible="openView"
      header="菜单权限详情"
      placement="center"
      width="700px"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="菜单ID">{{ form.menuId }}</t-descriptions-item>
        <t-descriptions-item label="菜单名称">{{ form.menuName }}</t-descriptions-item>
        <t-descriptions-item label="父菜单ID">{{ form.parentId }}</t-descriptions-item>
        <t-descriptions-item label="显示顺序">{{ form.orderNum }}</t-descriptions-item>
        <t-descriptions-item label="路由地址">{{ form.path }}</t-descriptions-item>
        <t-descriptions-item label="组件名称">{{ form.componentName }}</t-descriptions-item>
        <t-descriptions-item label="组件路径">{{ form.component }}</t-descriptions-item>
        <t-descriptions-item label="路由参数">{{ form.queryParam }}</t-descriptions-item>
        <t-descriptions-item label="是否为外链">
          <dict-tag :options="yesNoOptions" theme="primary" :value="form.isFrame" />
        </t-descriptions-item>
        <t-descriptions-item label="是否缓存">
          <dict-tag :options="cacheOptions" theme="primary" :value="form.isCache" />
        </t-descriptions-item>
        <t-descriptions-item label="菜单类型">
          <dict-tag :options="menuTypeOptions" :value="form.menuType" />
        </t-descriptions-item>
        <t-descriptions-item label="显示状态">
          <dict-tag :options="sys_show_hide" :value="form.visible" />
        </t-descriptions-item>
        <t-descriptions-item label="菜单状态">
          <dict-tag :options="sys_normal_disable" :value="form.status" />
        </t-descriptions-item>
        <t-descriptions-item label="权限标识">{{ form.perms }}</t-descriptions-item>
        <t-descriptions-item label="菜单图标">{{ form.icon }}</t-descriptions-item>
        <t-descriptions-item label="隐藏表达式">
          <t-tag v-if="form.hiddenExpression">{{ form.hiddenExpression }}</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="停用表达式">
          <t-tag v-if="form.shopExpression">{{ form.shopExpression }}</t-tag>
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
  name: 'Menus',
});

import {
  AddIcon,
  BrowseIcon,
  CopyIcon,
  DeleteIcon,
  DownloadIcon,
  EditIcon,
  HelpCircleFilledIcon,
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
  TableSort,
} from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import { addMenu, delMenu, getMenu, listMenu, updateMenu } from '@/api/system/menu';
import type { SysMenuForm, SysMenuQuery, SysMenuVo } from '@/api/system/model/menuModel';
import IconSelect from '@/components/icon-select/index.vue';
import type { DictModel } from '@/utils/dict';

const { proxy } = getCurrentInstance();
const { sys_normal_disable, sys_show_hide } = proxy.useDict('sys_normal_disable', 'sys_show_hide');

const openView = ref(false);
const openViewLoading = ref(false);
const menuList = ref<SysMenuVo[]>([]);
const open = ref(false);
const loading = ref(true);
const eLoading = ref(true);
const showSearch = ref(true);
const columnControllerVisible = ref(false);
const title = ref('');
const menuOptions = ref<SysMenuVo[]>([]);
const sort = ref<TableSort>();
const tableRef = ref<EnhancedTableInstanceFunctions>();
const menuRef = ref<FormInstanceFunctions>();
const expandedTreeNodes = ref([]);
/** 是否 */
const yesNoOptions = ref([
  { value: 0, label: '否' },
  { value: 1, label: '是' },
]);
// 缓存状态
const cacheOptions = ref([
  { value: 0, label: '不缓存' },
  { value: 1, label: '缓存' },
]);
// 菜单类型
const menuTypeOptions = ref<DictModel[]>([
  { value: 'M', tagType: 'primary', label: '目录' },
  { value: 'C', tagType: 'success', label: '菜单' },
  { value: 'F', tagType: 'warning', label: '按钮' },
]);

const rules = ref<Record<string, Array<FormRule>>>({
  menuName: [{ required: true, message: '菜单名称不能为空' }],
  orderNum: [{ required: true, message: '菜单顺序不能为空' }],
  path: [{ required: true, message: '路由地址不能为空' }],
});
// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `菜单名称`, colKey: 'menuName', align: 'left', width: '20%', ellipsis: true },
  { title: `图标`, colKey: 'icon', align: 'center', width: 80 },
  { title: `排序`, colKey: 'orderNum', align: 'center', width: 100, sorter: true },
  { title: `权限标识`, colKey: 'perms', align: 'center', ellipsis: true },
  { title: `组件路径`, colKey: 'component', align: 'center', ellipsis: true },
  { title: `组件名称`, colKey: 'componentName', align: 'center', ellipsis: true },
  { title: `显示状态`, colKey: 'visible', align: 'center', width: 88 },
  { title: `菜单类型`, colKey: 'menuType', align: 'center', width: 88 },
  { title: `状态`, colKey: 'status', align: 'center', width: 70 },
  { title: `创建时间`, colKey: 'createTime', align: 'center', width: '10%', sorter: true, minWidth: 112 },
  { title: `操作`, colKey: 'operation', align: 'center', width: '20%', minWidth: 125 },
]);
// 提交表单对象
const form = ref<SysMenuForm & SysMenuVo>({
  menuId: undefined,
  parentId: 0,
  menuName: undefined,
  icon: undefined,
  menuType: 'M',
  orderNum: 0,
  isFrame: 0,
  isCache: 1,
  visible: '1',
  status: '1',
});
// 查询对象
const queryParams = ref<SysMenuQuery>({
  menuName: undefined,
  visible: undefined,
});
const isExpand = computed(() => {
  return expandedTreeNodes.value.length !== 0;
});

/** 查询菜单列表 */
function getList() {
  loading.value = true;
  listMenu(queryParams.value)
    .then((response) => {
      menuList.value = proxy.handleTree(response.data, 'menuId');
    })
    .finally(() => (loading.value = false));
}
/** 查询菜单下拉树结构 */
async function getTreeselect() {
  menuOptions.value = [];
  return listMenu().then((response) => {
    menuOptions.value = [{ menuId: 0, menuName: '主类目', children: proxy.handleTree(response.data, 'menuId') }];
  });
}
/** 表单重置 */
function reset() {
  form.value = {
    menuId: undefined,
    parentId: 0,
    menuName: undefined,
    icon: undefined,
    menuType: 'M',
    orderNum: 0,
    isFrame: 0,
    isCache: 1,
    visible: '1',
    status: '1',
  };
  proxy.resetForm('menuRef');
}

/** 搜索按钮操作 */
function handleQuery() {
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
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
/** 展开/折叠操作 */
function toggleExpandAll() {
  if (isExpand.value) {
    tableRef.value.foldAll();
  } else {
    tableRef.value.expandAll();
  }
}
/** 详情按钮操作 */
function handleDetail(row: SysMenuVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const menuId = row.menuId;
  getMenu(menuId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}
/** 新增按钮操作 */
function handleAdd(row?: SysMenuVo) {
  reset();
  open.value = true;
  title.value = '添加菜单';
  eLoading.value = true;
  getTreeselect().then(() => {
    if (row != null && row.menuId) {
      form.value.parentId = row.menuId;
    } else {
      form.value.parentId = 0;
    }
    eLoading.value = false;
  });
}
/** 修改按钮操作 */
async function handleUpdate(row: SysMenuVo) {
  reset();
  open.value = true;
  eLoading.value = true;
  title.value = '修改菜单';
  await getTreeselect();
  getMenu(row.menuId).then((response) => {
    form.value = response.data;
    eLoading.value = false;
  });
}
/** 复制新增操作 */
async function handleCopyAdd(row: SysMenuVo) {
  reset();
  open.value = true;
  title.value = '添加菜单';
  eLoading.value = true;
  await getTreeselect();
  getMenu(row.menuId).then((response) => {
    form.value = response.data;
    form.value.menuId = undefined;
    eLoading.value = false;
  });
}

/** 提交按钮 */
function onConfirm() {
  menuRef.value.submit();
}

/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.menuId) {
      updateMenu(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        })
        .finally(() => proxy.$modal.msgClose(msgLoading));
    } else {
      addMenu(form.value)
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
function handleDelete(row: SysMenuVo) {
  proxy.$modal.confirm(`是否确认删除名称为"${row.menuName}"的数据项?`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delMenu(row.menuId)
      .then(() => {
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  });
}
/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'system/menu/export',
    {
      ...queryParams.value,
    },
    `menu_${new Date().getTime()}.xlsx`,
  );
}

getList();
</script>
