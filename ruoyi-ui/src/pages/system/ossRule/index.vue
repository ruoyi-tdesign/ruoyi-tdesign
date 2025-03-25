<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form
        v-show="showSearch"
        ref="queryRef"
        :data="queryParams"
        layout="inline"
        reset-type="initial"
        label-width="calc(5em + 12px)"
      >
        <t-form-item label="规则名称" name="ruleName">
          <t-input v-model="queryParams.ruleName" placeholder="请输入规则名称" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="匹配域名" name="domain">
          <t-input v-model="queryParams.domain" placeholder="请输入匹配域名" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="媒体类型" name="mimeType">
          <t-input v-model="queryParams.mimeType" placeholder="请输入媒体类型" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="覆盖字段值" name="isOverwrite">
          <t-select v-model="queryParams.isOverwrite" placeholder="请选择覆盖字段值" clearable>
            <t-option v-for="dict in sys_yes_no" :key="dict.value" :label="dict.label" :value="dict.value" />
          </t-select>
        </t-form-item>
        <t-form-item label="是否默认" name="isDefault">
          <t-select v-model="queryParams.isDefault" placeholder="请选择是否默认" clearable>
            <t-option v-for="dict in sys_yes_no" :key="dict.value" :label="dict.label" :value="dict.value" />
          </t-select>
        </t-form-item>
        <t-form-item label="启用状态" name="status">
          <t-select v-model="queryParams.status" placeholder="请选择启用状态" clearable>
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

      <t-table
        v-model:column-controller-visible="columnControllerVisible"
        :loading="loading"
        hover
        row-key="ossRuleId"
        :data="ossRuleList"
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
              <t-button v-hasPermi="['system:ossRule:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:ossRule:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:ossRule:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button
                v-hasPermi="['system:ossRule:add', 'system:ossRule:edit', 'system:ossRule:remove']"
                theme="danger"
                variant="outline"
                @click="handleRefreshCache"
              >
                <template #icon> <refresh-icon /> </template>
                刷新缓存
              </t-button>
              <t-button v-hasPermi="['system:ossRule:export']" theme="default" variant="outline" @click="handleExport">
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
        <template #isOverwrite="{ row }">
          <t-switch
            v-model="row.isOverwrite"
            :custom-value="['Y', 'N']"
            @click.stop
            @change="handleOverwriteChange(row)"
          ></t-switch>
        </template>
        <template #isDefault="{ row }">
          <dict-tag :options="sys_yes_no" :value="row.isDefault" />
        </template>
        <template #status="{ row }">
          <dict-tag :options="sys_normal_disable" :value="row.status" />
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['system:ossRule:query']" @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link v-hasPermi="['system:ossRule:edit']" @click.stop="handleUpdate(row)">
              <template #prefix-icon><edit-icon /></template>修改
            </my-link>
            <my-link v-hasPermi="['system:ossRule:remove']" theme="danger" @click.stop="handleDelete(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 添加或修改OSS处理规则对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      placement="center"
      width="650px"
      attach="body"
      :confirm-btn="{
        loading: buttonLoading,
      }"
      @confirm="onConfirm"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="ossRuleRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(4em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitForm"
        >
          <t-form-item label="规则名称" name="ruleName" help="例如：80x80，则字段名称将输出字段名_80x80">
            <t-input v-model.trim="form.ruleName" placeholder="请输入规则名称" clearable />
          </t-form-item>
          <t-form-item label="匹配域名" name="domain" help="只有URL中包含匹配的域名，该规则才生效">
            <t-input v-model.trim="form.domain" placeholder="请输入匹配域名" clearable />
          </t-form-item>
          <t-form-item
            label="媒体类型"
            name="mimeType"
            help="规则对匹配的媒体类型或者匹配的文件后缀生效，多个使用逗号分割"
          >
            <t-input v-model.trim="form.mimeType" placeholder="请输入媒体类型" clearable />
          </t-form-item>
          <t-form-item label="规则" name="rule" help="内置domain、path、filename、url变量，使用#{#url}的方式使用">
            <t-textarea v-model.trim="form.rule" class="ruleCss" placeholder="请输入规则" clearable />
          </t-form-item>
          <t-form-item>
            <t-button
              v-for="rule in ossRules"
              :key="rule"
              theme="default"
              variant="dashed"
              size="small"
              @click="insertRuleData(rule)"
            >
              {{ rule.substring(0, 1).toUpperCase() + rule.substring(1) }}
            </t-button>
          </t-form-item>
          <t-form-item label="是否默认" name="isDefault">
            <template #help>
              使用@OssRule("800x800")的方式指定使用规则
              <br />
              不指定规则时，使用默认规则。
            </template>
            <t-switch v-model="form.isDefault" :custom-value="['Y', 'N']" />
          </t-form-item>
          <t-form-item label="启用状态" name="status">
            <t-switch v-model="form.status" :custom-value="['1', '0']" />
          </t-form-item>
          <t-form-item label="规则顺序" name="ruleSort">
            <t-input-number v-model="form.ruleSort" placeholder="请输入" />
          </t-form-item>
          <t-form-item label="备注" name="remark">
            <t-textarea v-model="form.remark" placeholder="请输入备注" />
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- OSS处理规则详情 -->
    <t-dialog
      v-model:visible="openView"
      header="OSS处理规则详情"
      placement="center"
      width="700px"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="oss规则id">{{ form.ossRuleId }}</t-descriptions-item>
        <t-descriptions-item label="规则名称">{{ form.ruleName }}</t-descriptions-item>
        <t-descriptions-item label="匹配域名">{{ form.domain }}</t-descriptions-item>
        <t-descriptions-item label="媒体类型">{{ form.mimeType }}</t-descriptions-item>
        <t-descriptions-item label="规则" :span="2">
          <span style="word-break: break-all">{{ form.rule }}</span>
        </t-descriptions-item>
        <t-descriptions-item label="覆盖默认字段值">
          <dict-tag :options="sys_yes_no" :value="form.isOverwrite" />
        </t-descriptions-item>
        <t-descriptions-item label="是否默认">
          <dict-tag :options="sys_yes_no" :value="form.isDefault" />
        </t-descriptions-item>
        <t-descriptions-item label="启用状态">
          <dict-tag :options="sys_normal_disable" :value="form.status" />
        </t-descriptions-item>
        <t-descriptions-item label="规则顺序">{{ form.ruleSort }}</t-descriptions-item>
        <t-descriptions-item label="创建部门">{{ form.createDept }}</t-descriptions-item>
        <t-descriptions-item label="更新者">{{ form.updateBy }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建者">{{ form.createBy }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ form.remark }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'OssRule',
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
import { computed, getCurrentInstance, nextTick, ref } from 'vue';

import type { SysOssRuleForm, SysOssRuleQuery, SysOssRuleVo } from '@/api/system/model/ossRuleModel';
import {
  addOssRule,
  changeOssRuleOverwrite,
  delOssRule,
  getOssRule,
  listOssRule,
  refreshOssRuleCache,
  updateOssRule,
} from '@/api/system/ossRule';
import { ArrayOps } from '@/utils/array';

const { proxy } = getCurrentInstance();
const { sys_yes_no, sys_normal_disable } = proxy.useDict('sys_yes_no', 'sys_normal_disable');

const ossRuleList = ref<SysOssRuleVo[]>([]);
const ossRuleRef = ref<FormInstanceFunctions>();
const open = ref(false);
const openView = ref(false);
const openViewLoading = ref(false);
const buttonLoading = ref(false);
const loading = ref(false);
const columnControllerVisible = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const sort = ref<TableSort>();
const ossRules = ['domain', 'path', 'fullPath', 'filename', 'url', 'fullUrl', 'query'];

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  ruleName: [{ required: true, message: '规则名称不能为空' }],
  domain: [{ required: true, message: '匹配域名不能为空' }],
  mimeType: [{ required: true, message: '媒体类型不能为空' }],
  rule: [{ required: true, message: '规则不能为空' }],
  isOverwrite: [{ required: true, message: '是否覆盖默认字段值不能为空' }],
  isDefault: [{ required: true, message: '是否默认不能为空' }],
  status: [{ required: true, message: '启用状态不能为空' }],
  ruleSort: [{ required: true, message: '规则顺序不能为空' }],
});

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `规则名称`, colKey: 'ruleName', align: 'center' },
  { title: `匹配域名`, colKey: 'domain', align: 'center', ellipsis: true },
  { title: `媒体类型`, colKey: 'mimeType', align: 'center' },
  // { title: `规则`, colKey: 'rule', align: 'center', ellipsis: true },
  { title: `覆盖字段值`, colKey: 'isOverwrite', align: 'center' },
  { title: `是否默认`, colKey: 'isDefault', align: 'center' },
  { title: `启用状态`, colKey: 'status', align: 'center' },
  { title: `规则顺序`, colKey: 'ruleSort', align: 'center', sorter: true },
  { title: `更新时间`, colKey: 'updateTime', align: 'center' },
  { title: `创建时间`, colKey: 'createTime', align: 'center' },
  { title: `备注`, colKey: 'remark', align: 'center', ellipsis: true },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 提交表单对象
const form = ref<SysOssRuleVo & SysOssRuleForm>({
  isOverwrite: 'N',
  isDefault: 'N',
  status: '1',
});
// 查询对象
const queryParams = ref<SysOssRuleQuery>({
  pageNum: 1,
  pageSize: 10,
  ruleName: undefined,
  domain: undefined,
  mimeType: undefined,
  isOverwrite: undefined,
  isDefault: undefined,
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

/** 插入规则数据 */
function insertRuleData(value: string) {
  value = `#{#${value}}`;
  const element = document
    .getElementsByClassName('ruleCss')
    .item(0)
    .getElementsByTagName('textarea')
    .item(0) as HTMLTextAreaElement;
  if (element.selectionStart || element.selectionStart === 0) {
    // 正常浏览器
    const startPos = element.selectionStart;
    const endPos = element.selectionEnd;
    const startStr = form.value.rule?.substring(0, startPos) || '';
    const endStr = form.value.rule?.substring(endPos, form.value.rule.length) || '';
    form.value.rule = startStr + value + endStr;
    const index = startPos + value.length;
    nextTick(() => {
      element.setSelectionRange?.(index, index);
      element.focus();
    });
  } else {
    form.value.rule = (form.value.rule ?? '') + value;
    element.focus();
  }
}

/** 查询OSS处理规则列表 */
function getList() {
  loading.value = true;
  listOssRule(queryParams.value)
    .then((response) => {
      ossRuleList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}

// 表单重置
function reset() {
  form.value = {
    isOverwrite: 'N',
    isDefault: 'N',
    status: '1',
    ruleSort: 0,
  };
  proxy.resetForm('ossRuleRef');
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
  open.value = true;
  title.value = '添加OSS处理规则';
}

/** 详情按钮操作 */
function handleDetail(row: SysOssRuleVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const ossRuleId = row.ossRuleId;
  getOssRule(ossRuleId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysOssRuleVo) {
  buttonLoading.value = true;
  reset();
  open.value = true;
  title.value = '修改OSS处理规则';
  const ossRuleId = row?.ossRuleId || ids.value.at(0);
  getOssRule(ossRuleId).then((response) => {
    buttonLoading.value = false;
    form.value = response.data;
  });
}

/** 提交按钮 */
function onConfirm() {
  ossRuleRef.value.submit();
}

/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.ossRuleId) {
      updateOssRule(form.value)
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
      addOssRule(form.value)
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
function handleDelete(row?: SysOssRuleVo) {
  const ossRuleIds = row?.ossRuleId || ids.value;
  proxy.$modal.confirm(`是否确认删除OSS处理规则编号为${ossRuleIds}的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delOssRule(ossRuleIds)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, ossRuleIds);
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => {
        proxy.$modal.msgClose(msgLoading);
      });
  });
}

/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  proxy.$modal.confirm(`是否确认刷新OSS处理规则缓存？`, () => {
    return refreshOssRuleCache().then(() => {
      proxy.$modal.msgSuccess('刷新成功');
    });
  });
}

/** 规则覆盖默认字段值修改  */
function handleOverwriteChange(row: SysOssRuleVo) {
  const rule = ossRuleList.value.find((value) => value.ossRuleId === row.ossRuleId);
  const text = rule.isOverwrite === 'Y' ? '覆盖默认字段值' : '不覆盖默认字段值';
  proxy.$modal.confirm(
    `确认要设置该规则为"${text}"吗?`,
    () => {
      const msgLoading = proxy.$modal.msgLoading('提交中...');
      return changeOssRuleOverwrite(rule.ossRuleId, rule.isOverwrite)
        .then(() => {
          getList();
          proxy.$modal.msgSuccess(`已设置规则为"${text}"`);
        })
        .catch(() => {
          rule.isOverwrite = rule.isOverwrite === 'N' ? 'Y' : 'N';
        })
        .finally(() => proxy.$modal.msgClose(msgLoading));
    },
    () => {
      rule.isOverwrite = rule.isOverwrite === 'N' ? 'Y' : 'N';
    },
  );
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'system/ossRule/export',
    {
      ...queryParams.value,
    },
    `ossRule_${new Date().getTime()}.xlsx`,
  );
}

getList();
</script>
