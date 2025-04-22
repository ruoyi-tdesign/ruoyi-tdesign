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
            <t-form-item label="用户名称" name="userName">
              <t-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable @enter="handleQuery" />
            </t-form-item>
            <t-form-item label="用户昵称" name="nickName">
              <t-input v-model="queryParams.nickName" placeholder="请输入用户昵称" clearable @enter="handleQuery" />
            </t-form-item>
            <t-form-item label="手机号码" name="phonenumber">
              <t-input v-model="queryParams.phonenumber" placeholder="请输入手机号码" clearable @enter="handleQuery" />
            </t-form-item>
            <t-form-item label="用户邮箱" name="email">
              <t-input v-model="queryParams.email" placeholder="请输入用户邮箱" clearable @enter="handleQuery" />
            </t-form-item>
            <t-form-item label="状态" name="status">
              <t-select v-model="queryParams.status" placeholder="用户状态" clearable>
                <t-option
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </t-select>
            </t-form-item>
            <t-form-item label="创建时间" style="width: 308px">
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
            row-key="userId"
            :data="userList"
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
                  <t-button v-hasPermi="['system:user:add']" theme="primary" @click="handleAdd">
                    <template #icon> <add-icon /></template>
                    新增
                  </t-button>
                  <t-button
                    v-hasPermi="['system:user:edit']"
                    theme="default"
                    variant="outline"
                    :disabled="single"
                    @click="handleUpdate()"
                  >
                    <template #icon> <edit-icon /> </template>
                    修改
                  </t-button>
                  <t-button
                    v-hasPermi="['system:user:remove']"
                    theme="danger"
                    variant="outline"
                    :disabled="multiple"
                    @click="handleDelete()"
                  >
                    <template #icon> <delete-icon /> </template>
                    删除
                  </t-button>
                  <t-button v-hasPermi="['system:user:import']" theme="default" variant="outline" @click="handleImport">
                    <template #icon> <upload-icon /> </template>
                    导入
                  </t-button>
                  <t-button v-hasPermi="['system:user:export']" theme="default" variant="outline" @click="handleExport">
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
              <t-switch v-model="row.status" :custom-value="['1', '0']" @click.stop @change="handleStatusChange(row)" />
            </template>
            <template #avatar="{ row }">
              <image-preview :src="row.avatar" width="60px" height="60px" />
            </template>
            <template #operation="{ row }">
              <t-space :size="8" break-line>
                <t-tooltip content="详情" placement="top">
                  <my-link v-hasPermi="['system:user:query']" @click.stop="handleDetail(row)">
                    <browse-icon />
                  </my-link>
                </t-tooltip>
                <t-tooltip v-if="row.userId !== 1" content="修改" placement="top">
                  <my-link v-hasPermi="['system:user:edit']" @click.stop="handleUpdate(row)">
                    <edit-icon />
                  </my-link>
                </t-tooltip>
                <t-tooltip v-if="row.userId !== 1" content="删除" placement="top">
                  <my-link v-hasPermi="['system:user:remove']" theme="danger" @click.stop="handleDelete(row)">
                    <delete-icon />
                  </my-link>
                </t-tooltip>
                <t-tooltip v-if="row.userId !== 1" content="重置密码" placement="top">
                  <my-link v-hasPermi="['system:user:resetPwd']" @click.stop="handleResetPwd(row)">
                    <user-password-icon />
                  </my-link>
                </t-tooltip>
                <t-tooltip v-if="row.userId !== 1" content="分配角色" placement="top">
                  <my-link v-hasPermi="['system:user:edit']" @click.stop="handleAuthRole(row)">
                    <user-safety-icon />
                  </my-link>
                </t-tooltip>
              </t-space>
            </template>
          </t-table>
        </t-space>
      </t-col>
    </t-row>

    <!-- 添加或修改用户配置对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      placement="center"
      width="700px"
      attach="body"
      :confirm-btn="{
        loading: dLoading,
      }"
      @close="cancel"
      @confirm="submitForm"
    >
      <t-loading :loading="dLoading" size="small">
        <t-form
          ref="userRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(4em + 41px)"
          scroll-to-first-error="smooth"
          @submit="onSubmit"
        >
          <t-row :gutter="[0, 20]">
            <t-col :span="6">
              <t-form-item label="用户昵称" name="nickName">
                <t-input v-model="form.nickName" placeholder="请输入用户昵称" :maxlength="30" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="归属部门" name="deptId">
                <t-tree-select
                  v-model="form.deptId"
                  :data="deptFormOptions"
                  :tree-props="{
                    keys: { value: 'id', label: 'label', children: 'children' },
                    checkStrictly: true,
                  }"
                  placeholder="请选择归属部门"
                  @change="handleDeptChange"
                />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="手机号码" name="phonenumber">
                <t-input v-model="form.phonenumber" placeholder="请输入手机号码" :maxlength="11" />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="邮箱" name="email">
                <t-input v-model="form.email" placeholder="请输入邮箱" :maxlength="50" />
              </t-form-item>
            </t-col>
            <t-col v-if="!form.userId" :span="6">
              <t-form-item label="用户名称" name="userName">
                <t-input v-model="form.userName" placeholder="请输入用户名称" :maxlength="30" autocomplete="off" />
              </t-form-item>
            </t-col>
            <t-col v-if="!form.userId" :span="6">
              <t-form-item label="用户密码" name="password">
                <t-input
                  v-model="form.password"
                  placeholder="请输入用户密码"
                  type="password"
                  :maxlength="20"
                  autocomplete="new-password"
                />
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="用户性别" name="sex">
                <t-select v-model="form.sex" placeholder="请选择">
                  <t-option
                    v-for="dict in sys_user_sex"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  ></t-option>
                </t-select>
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="状态" name="status">
                <t-radio-group v-model="form.status">
                  <t-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">
                    {{ dict.label }}
                  </t-radio>
                </t-radio-group>
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="岗位" name="postIds">
                <t-select
                  v-model="form.postIds"
                  :loading="loadingPost"
                  clearable
                  multiple
                  placeholder="请选择"
                  :tag-props="{ theme: 'primary', variant: 'light' }"
                >
                  <t-option
                    v-for="item in postOptions"
                    :key="item.postId"
                    :label="item.postName"
                    :value="item.postId"
                    :disabled="item.status === '0'"
                  ></t-option>
                </t-select>
              </t-form-item>
            </t-col>
            <t-col :span="6">
              <t-form-item label="角色" name="roleIds">
                <t-select
                  v-model="form.roleIds"
                  clearable
                  multiple
                  filterable
                  placeholder="请选择"
                  :tag-props="{ theme: 'primary', variant: 'light' }"
                >
                  <t-option
                    v-for="item in roleOptions"
                    :key="item.roleId"
                    :label="item.roleName"
                    :value="item.roleId"
                    :disabled="item.status === '0'"
                  ></t-option>
                </t-select>
              </t-form-item>
            </t-col>
            <t-col :span="12">
              <t-form-item label="备注" name="remark">
                <t-textarea v-model="form.remark" placeholder="请输入备注"></t-textarea>
              </t-form-item>
            </t-col>
          </t-row>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 用户导入对话框 -->
    <t-dialog
      v-model:visible="upload.open"
      :close-on-overlay-click="false"
      width="410px"
      :header="upload.title"
      attach="body"
      :footer="null"
    >
      <t-space direction="vertical" size="4px">
        <t-upload
          ref="uploadRef"
          :limit="1"
          accept=".xlsx, .xls"
          :headers="upload.headers"
          :action="upload.url + '?updateSupport=' + upload.updateSupport"
          :disabled="upload.isUploading"
          :on-progress="handleFileUploadProgress"
          :on-success="handleFileSuccess"
          :auto-upload="false"
          theme="file"
          draggable
          tips="仅允许导入xls、xlsx格式文件。"
        >
        </t-upload>
        <div><t-checkbox v-model="upload.updateSupport" />是否更新已经存在的用户数据</div>
        <t-link theme="primary" hover="color" style="font-size: 12px; vertical-align: baseline" @click="importTemplate">
          下载模板
        </t-link>
      </t-space>
    </t-dialog>

    <!-- 用户信息详情 -->
    <t-dialog
      v-model:visible="openView"
      header="用户信息详情"
      width="700px"
      attach="body"
      placement="center"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="用户ID">{{ formView.userId }}</t-descriptions-item>
        <t-descriptions-item label="部门">{{ formView.deptName }}</t-descriptions-item>
        <t-descriptions-item label="用户账号">{{ formView.userName }}</t-descriptions-item>
        <t-descriptions-item label="用户昵称">{{ formView.nickName }}</t-descriptions-item>
        <t-descriptions-item label="用户类型">{{ formView.userType }}</t-descriptions-item>
        <t-descriptions-item label="用户邮箱">{{ formView.email }}</t-descriptions-item>
        <t-descriptions-item label="手机号码">{{ formView.phonenumber }}</t-descriptions-item>
        <t-descriptions-item label="用户性别">
          <dict-tag :options="sys_user_sex" :value="formView.sex" />
        </t-descriptions-item>
        <t-descriptions-item label="头像地址">
          <image-preview :src="formView.avatar" :width="60" :height="60" />
        </t-descriptions-item>
        <t-descriptions-item label="帐号状态">
          <dict-tag :options="sys_normal_disable" :value="formView.status" />
        </t-descriptions-item>
        <t-descriptions-item label="最后登录IP">{{ formView.loginIp }}</t-descriptions-item>
        <t-descriptions-item label="最后登录时间">{{ parseTime(formView.loginDate) }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(formView.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(formView.createTime) }}</t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ formView.remark }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'User',
});

import { storeToRefs } from 'pinia';
import {
  AddIcon,
  BrowseIcon,
  DeleteIcon,
  DownloadIcon,
  EditIcon,
  RefreshIcon,
  SearchIcon,
  SettingIcon,
  UploadIcon,
  UserPasswordIcon,
  UserSafetyIcon,
} from 'tdesign-icons-vue-next';
import type {
  FormInstanceFunctions,
  FormRule,
  PageInfo,
  PrimaryTableCol,
  SubmitContext,
  SuccessContext,
  TableSort,
  TreeSelectProps,
  UploadInstanceFunctions,
} from 'tdesign-vue-next';
import { computed, createVNode, getCurrentInstance, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

import type { TreeModel } from '@/api/model/resultModel';
import type { SysPostVo } from '@/api/system/model/postModel';
import type { SysRoleVo } from '@/api/system/model/roleModel';
import type { SysUserForm, SysUserQuery, SysUserVo } from '@/api/system/model/userModel';
import { postOptionSelect } from '@/api/system/post';
import {
  addUser,
  changeUserStatus,
  delUser,
  deptTreeSelect,
  getUser,
  listUser,
  resetUserPwd,
  updateUser,
} from '@/api/system/user';
import { useUserStore } from '@/store';
import { ArrayOps } from '@/utils/array';
import { handleChangeStatus } from '@/utils/ruoyi';

const router = useRouter();
const { proxy } = getCurrentInstance();
const { sys_normal_disable, sys_user_sex } = proxy.useDict('sys_normal_disable', 'sys_user_sex');

const uploadRef = ref<UploadInstanceFunctions>();
const userRef = ref<FormInstanceFunctions>();
const userList = ref<SysUserVo[]>([]);
const open = ref(false);
const openView = ref(false);
const openViewLoading = ref(false);
const loading = ref(true);
const dLoading = ref(false);
const showSearch = ref(true);
const columnControllerVisible = ref(false);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const dateRange = ref([]);
const deptFormOptions = ref<Array<TreeModel<number>>>([]);
const initPassword = ref(undefined);
const postOptions = ref<SysPostVo[]>([]);
const roleOptions = ref<SysRoleVo[]>([]);
const deptActived = ref<number[]>([]);
const sort = ref<TableSort>();
const loadingPost = ref(false);
const { token } = storeToRefs(useUserStore());
/** 用户导入参数 */
const upload = reactive({
  // 是否显示弹出层（用户导入）
  open: false,
  // 弹出层标题（用户导入）
  title: '',
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的用户数据
  updateSupport: false,
  // 设置上传的请求头部
  headers: { Authorization: `Bearer ${token.value}` },
  // 上传的地址
  url: `${import.meta.env.VITE_APP_BASE_API}/system/user/importData`,
});
// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 30, align: 'center' },
  // { title: `用户编号`, colKey: 'userId', align: 'center' },
  { title: `用户名称`, colKey: 'userName', ellipsis: true, align: 'center' },
  { title: `用户昵称`, colKey: 'nickName', ellipsis: true, align: 'center' },
  { title: `头像`, colKey: 'avatar', align: 'center' },
  { title: `部门`, colKey: 'dept.deptName', align: 'center' },
  { title: `手机号码`, colKey: 'phonenumber', align: 'center' },
  { title: `状态`, colKey: 'status', align: 'center' },
  { title: `更新时间`, colKey: 'updateTime', align: 'center', sorter: true, width: '10%', minWidth: 112 },
  { title: `创建时间`, colKey: 'createTime', align: 'center', sorter: true, width: '10%', minWidth: 112 },
  { title: `操作`, colKey: 'operation', align: 'center' },
]);

const rules = ref<Record<string, Array<FormRule>>>({
  userName: [
    { required: true, message: '用户名称不能为空' },
    { min: 2, max: 20, message: '用户名称长度必须介于 2 和 20 之间' },
  ],
  nickName: [{ required: true, message: '用户昵称不能为空' }],
  deptId: [{ required: true, message: '部门不能为空' }],
  status: [{ required: true, message: '账号状态不能为空' }],
  password: [
    { required: true, message: '用户密码不能为空' },
    { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间' },
    { pattern: /^[^<>"'|\\]+$/, message: '不能包含非法字符：< > " \' \\ |', trigger: 'blur' },
  ],
  email: [{ email: true, message: '请输入正确的邮箱地址' }],
  phonenumber: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码' }],
  roleIds: [{ required: true, message: '用户角色不能为空' }],
});
const form = ref<SysUserForm & SysUserVo>({
  status: '1',
  postIds: [],
  roleIds: [],
});
const formView = ref<SysUserVo>({});
const queryParams = ref<SysUserQuery>({
  pageNum: 1,
  pageSize: 10,
  userName: undefined,
  nickName: undefined,
  phonenumber: undefined,
  email: undefined,
  status: undefined,
  deptId: undefined,
});

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
async function getDeptFormTree() {
  return deptTreeSelect().then((response) => {
    deptFormOptions.value = response.data;
  });
}

const handleDeptChange: TreeSelectProps['onChange'] = (value) => {
  loadingPost.value = true;
  form.value.postIds = [];
  postOptionSelect(value as number)
    .then((res) => {
      postOptions.value = res.data;
    })
    .finally(() => (loadingPost.value = false));
};

/** 查询用户列表 */
function getList() {
  loading.value = true;
  queryParams.value.deptId = deptActived.value.at(0);
  listUser(proxy.addDateRange(queryParams.value, dateRange.value)).then((res) => {
    loading.value = false;
    userList.value = res.rows;
    total.value = res.total;
    handleSelectionChange([]);
  });
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
  deptActived.value = [];
  queryParams.value.deptId = undefined;
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
/** 删除按钮操作 */
function handleDelete(row?: SysUserVo) {
  const userIds = row?.userId || ids.value;
  proxy.$modal.confirm(`是否确认删除用户编号为"${userIds}"的数据项？`, () => {
    return delUser(userIds).then(() => {
      ids.value = ArrayOps.fastDeleteElement(ids.value, userIds);
      getList();
      proxy.$modal.msgSuccess('删除成功');
    });
  });
}
/** 导出按钮操作 */
function handleExport() {
  queryParams.value.deptId = deptActived.value.at(0);
  proxy.download(
    'system/user/export',
    {
      ...queryParams.value,
    },
    `user_${new Date().getTime()}.xlsx`,
  );
}
/** 用户状态修改  */
function handleStatusChange(row: SysUserVo) {
  handleChangeStatus(userList.value, row, 'userId', 'status', `${row.userName}用户`, (user) =>
    changeUserStatus(user.userId, user.status).then(() => getList()),
  );
}
/** 跳转角色分配 */
function handleAuthRole(row: SysUserVo) {
  const { userId } = row;
  router.push(`/system/user-auth/role/${userId}`);
}
/** 重置密码按钮操作 */
function handleResetPwd(row: SysUserVo) {
  proxy.$modal.prompt(`请输入"${row.userName}"的新密码`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    closeOnClickModal: false,
    inputPattern: /^.{5,20}$/,
    inputErrorMessage: '用户密码长度必须介于 5 和 20 之间',
    onConfirm: (value) => {
      const msgLoading = proxy.$modal.msgLoading('提交中...');
      return resetUserPwd(row.userId, value)
        .then(() => {
          proxy.$modal.msgSuccess(`修改成功，新密码是：${value}`);
        })
        .finally(() => proxy.$modal.msgClose(msgLoading));
    },
  });
}
/** 选择条数  */
function handleSelectionChange(selection: Array<string | number>) {
  ids.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}
/** 导入按钮操作 */
function handleImport() {
  upload.title = '用户导入';
  upload.open = true;
}
/** 下载模板操作 */
function importTemplate() {
  proxy.download('system/user/importTemplate', {}, `user_template_${new Date().getTime()}.xlsx`);
}
/** 文件上传中处理 */
const handleFileUploadProgress = () => {
  upload.isUploading = true;
};
/** 文件上传成功处理 */
const handleFileSuccess = (context: SuccessContext) => {
  upload.open = false;
  upload.isUploading = false;
  uploadRef.value.uploadFiles([]);
  proxy.$modal.alert({
    // @ts-ignore
    body: createVNode(
      'div',
      { style: 'overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;' },
      context.response.msg,
    ),
    header: '导入结果',
  });
  getList();
};
/** 重置操作表单 */
function reset() {
  form.value = {
    userId: undefined,
    deptId: undefined,
    userName: undefined,
    nickName: undefined,
    password: undefined,
    phonenumber: undefined,
    email: undefined,
    sex: undefined,
    status: '1',
    remark: undefined,
    postIds: [],
    roleIds: [],
  };
  proxy.resetForm('userRef');
}
/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}
/** 详情按钮操作 */
function handleDetail(row: SysUserVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const userId = row.userId;
  getUser(userId).then((response) => {
    formView.value = response.data?.user;
    openViewLoading.value = false;
  });
}
/** 新增按钮操作 */
function handleAdd() {
  open.value = true;
  dLoading.value = true;
  reset();
  getDeptFormTree();
  getUser()
    .then((response) => {
      postOptions.value = response.data.posts;
      roleOptions.value = response.data.roles;
      title.value = '添加用户';
      form.value.password = initPassword.value;
    })
    .finally(() => {
      dLoading.value = false;
    });
}
/** 修改按钮操作 */
function handleUpdate(row?: SysUserVo) {
  reset();
  const userId = row?.userId || ids.value.at(0);
  open.value = true;
  dLoading.value = true;
  getDeptFormTree();
  getUser(userId)
    .then((response) => {
      Object.assign(form.value, response.data.user);
      postOptions.value = response.data.posts;
      roleOptions.value = response.data.roles;
      form.value.postIds = response.data.postIds;
      form.value.roleIds = response.data.roleIds;
      title.value = '修改用户';
      form.value.password = '';
    })
    .finally(() => {
      dLoading.value = false;
    });
}
/** 提交按钮 */
function submitForm() {
  userRef.value.submit();
}

const onSubmit = ({ validateResult, firstError }: SubmitContext) => {
  if (validateResult === true) {
    dLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.userId) {
      updateUser(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        })
        .finally(() => {
          dLoading.value = false;
          proxy.$modal.msgClose(msgLoading);
        });
    } else {
      addUser(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('新增成功');
          open.value = false;
          getList();
        })
        .finally(() => {
          dLoading.value = false;
          proxy.$modal.msgClose(msgLoading);
        });
    }
  } else {
    proxy.$modal.msgError(firstError);
  }
};

onMounted(() => {
  getList();
});
</script>
