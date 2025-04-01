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
        <t-form-item label="敏感词" name="word">
          <t-input v-model="queryParams.word" placeholder="请输入敏感词" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="敏感词类别" name="category">
          <t-select v-model="queryParams.category" placeholder="请选择敏感词类别" clearable>
            <t-option
              v-for="dict in sensitive_words_category"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
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
        row-key="wordId"
        :data="sensitiveWordList"
        :columns="columns"
        :selected-row-keys="ids"
        select-on-row-click
        :pagination="pagination"
        :column-controller="{
          hideTriggerButton: true,
        }"
        @select-change="handleSelectionChange"
      >
        <template #topContent>
          <t-row>
            <t-col flex="auto">
              <t-button v-hasPermi="['system:sensitiveWord:add']" theme="primary" @click="handleAdd">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['system:sensitiveWord:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['system:sensitiveWord:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button
                v-hasPermi="['system:sensitiveWord:import']"
                theme="default"
                variant="outline"
                @click="openImportExcel = true"
              >
                <template #icon> <upload-icon /> </template>
                导入
              </t-button>
              <t-button
                v-hasPermi="['system:sensitiveWord:export']"
                theme="default"
                variant="outline"
                @click="handleExport"
              >
                <template #icon> <download-icon /> </template>
                导出
              </t-button>
              <t-button
                v-hasPermi="['system:sensitiveWord:remove']"
                theme="danger"
                variant="outline"
                @click="handleRefreshCache"
              >
                <template #icon> <refresh-icon /> </template>
                刷新缓存
              </t-button>
              <t-button theme="default" variant="outline" @click="openTest = true">
                <template #icon> <filter2-icon /> </template>
                测试文本
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
        <template #category="{ row }">
          <dict-tag :options="sensitive_words_category" :value="row.category" />
        </template>
        <template #status="{ row }">
          <dict-tag :options="sys_normal_disable" :value="row.status" />
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['system:sensitiveWord:query']" @click.stop="handleDetail(row)">
              <template #prefix-icon><browse-icon /></template>详情
            </my-link>
            <my-link v-hasPermi="['system:sensitiveWord:edit']" @click.stop="handleUpdate(row)">
              <template #prefix-icon><edit-icon /></template>修改
            </my-link>
            <my-link v-hasPermi="['system:sensitiveWord:remove']" theme="danger" @click.stop="handleDelete(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 添加或修改敏感词对话框 -->
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
      @confirm="sensitiveWordRef.submit()"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="sensitiveWordRef"
          label-align="right"
          :data="form"
          :rules="rules"
          label-width="calc(5em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitForm"
        >
          <t-form-item label="敏感词" name="word">
            <t-input v-model="form.word" placeholder="请输入敏感词" clearable />
          </t-form-item>
          <t-form-item label="敏感词类别" name="category">
            <t-select v-model="form.category" placeholder="请选择敏感词类别" clearable>
              <t-option
                v-for="dict in sensitive_words_category"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </t-select>
          </t-form-item>
          <t-form-item label="描述" name="description">
            <t-textarea v-model="form.description" placeholder="请输入描述" />
          </t-form-item>
          <t-form-item label="启用状态" name="status">
            <t-select v-model="form.status" placeholder="请选择启用状态" clearable>
              <t-option
                v-for="dict in sys_normal_disable"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
              />
            </t-select>
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 敏感词详情 -->
    <t-dialog
      v-model:visible="openView"
      header="敏感词详情"
      placement="center"
      width="min(700px, 100%)"
      attach="body"
      :footer="false"
    >
      <my-descriptions :loading="openViewLoading">
        <t-descriptions-item label="敏感词id">{{ form.wordId }}</t-descriptions-item>
        <t-descriptions-item label="敏感词">{{ form.word }}</t-descriptions-item>
        <t-descriptions-item label="敏感词类别">
          <dict-tag :options="sensitive_words_category" :value="form.category" />
        </t-descriptions-item>
        <t-descriptions-item label="启用状态">
          <dict-tag :options="sys_normal_disable" :value="form.status" />
        </t-descriptions-item>
        <t-descriptions-item label="描述" :span="2">{{ form.description }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ parseTime(form.updateTime) }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ parseTime(form.createTime) }}</t-descriptions-item>
      </my-descriptions>
    </t-dialog>

    <!-- 敏感词导入对话框 -->
    <upload-excel
      v-model:visible="openImportExcel"
      title="敏感词导入"
      upload-api="system/sensitiveWord/importData"
      download-template-api="/system/sensitiveWord/importTemplate"
      template-filename="sensitiveWord_template"
      @refresh="getList()"
    />

    <!--  敏感词测试  -->
    <t-dialog
      v-model:visible="openTest"
      header="敏感词测试"
      destroy-on-close
      :close-on-overlay-click="false"
      width="min(600px, 100%)"
      attach="body"
      :confirm-btn="{
        content: '测试',
        loading: buttonTestLoading,
      }"
      @confirm="sensitiveWordTestRef.submit()"
    >
      <t-loading :loading="buttonTestLoading" size="small">
        <t-form
          ref="sensitiveWordTestRef"
          label-align="right"
          :data="testForm"
          :rules="testRules"
          label-width="calc(5em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitTestForm"
        >
          <t-form-item label="敏感词类别" name="category">
            <t-select
              v-model="testForm.category"
              auto-width
              placeholder="请选择敏感词类别"
              clearable
              multiple
              :min-collapsed-num="3"
              :tag-props="{ theme: 'primary', variant: 'light' }"
            >
              <t-option
                v-for="dict in sensitive_words_category"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </t-select>
          </t-form-item>
          <t-form-item label="测试文本" name="text">
            <t-textarea v-model="testForm.text" placeholder="请输入测试文本" />
          </t-form-item>
          <template v-if="testResult">
            <t-divider>敏感词测试结果</t-divider>
            <my-descriptions :column="1" :label-style="{ width: '150px' }">
              <t-descriptions-item label="是否包含敏感词">{{ testResult.containsSensitiveWord }}</t-descriptions-item>
              <t-descriptions-item label="替换后文本">{{ testResult.sensitiveWordReplace }}</t-descriptions-item>
              <t-descriptions-item v-if="testResult.sensitiveWords?.length" label="包含的敏感词">
                {{ testResult.sensitiveWords?.join(' ') }}
              </t-descriptions-item>
            </my-descriptions>
          </template>
        </t-form>
      </t-loading>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'SensitiveWord',
});
import {
  AddIcon,
  BrowseIcon,
  DeleteIcon,
  DownloadIcon,
  EditIcon,
  Filter2Icon,
  RefreshIcon,
  SearchIcon,
  SettingIcon,
  UploadIcon,
} from 'tdesign-icons-vue-next';
import type { FormInstanceFunctions, FormRule, PageInfo, PrimaryTableCol, SubmitContext } from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref } from 'vue';

import type {
  SysSensitiveWordForm,
  SysSensitiveWordQuery,
  SysSensitiveWordTest,
  SysSensitiveWordTestVo,
  SysSensitiveWordVo,
} from '@/api/system/model/sensitiveWordModel';
import {
  addSensitiveWord,
  delSensitiveWord,
  getSensitiveWord,
  listSensitiveWord,
  refreshSensitiveWordCache,
  sensitiveWordTest,
  updateSensitiveWord,
} from '@/api/system/sensitiveWord';
import { ArrayOps } from '@/utils/array';

const { proxy } = getCurrentInstance();
const { sys_normal_disable, sensitive_words_category } = proxy.useDict(
  'sys_normal_disable',
  'sensitive_words_category',
);

const openView = ref(false);
const openViewLoading = ref(false);
const sensitiveWordRef = ref<FormInstanceFunctions>();
const open = ref(false);
const buttonLoading = ref(false);
const title = ref('');
const sensitiveWordList = ref<SysSensitiveWordVo[]>([]);
const loading = ref(false);
const columnControllerVisible = ref(false);
const showSearch = ref(true);
const total = ref(0);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const openImportExcel = ref(false);
// 测试
const openTest = ref(false);
const buttonTestLoading = ref(false);
const sensitiveWordTestRef = ref<FormInstanceFunctions>();
const testResult = ref<SysSensitiveWordTestVo>();

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  word: [
    { required: true, message: '敏感词不能为空' },
    { max: 255, message: '敏感词不能超过255个字符' },
  ],
  category: [
    { required: true, message: '敏感词类别不能为空' },
    { max: 20, message: '敏感词类别不能超过20个字符' },
  ],
  description: [{ max: 500, message: '描述不能超过500个字符' }],
  status: [{ required: true, message: '启用状态不能为空' }],
});
const testRules = ref<Record<string, Array<FormRule>>>({
  text: [{ required: true, message: '测试文本不能为空' }],
});

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `敏感词`, colKey: 'word', align: 'center' },
  { title: `敏感词类别`, colKey: 'category', align: 'center' },
  { title: `描述`, colKey: 'description', align: 'center', ellipsis: true },
  { title: `启用状态`, colKey: 'status', align: 'center' },
  { title: `更新时间`, colKey: 'updateTime', align: 'center', minWidth: 112, width: 180 },
  { title: `创建时间`, colKey: 'createTime', align: 'center', minWidth: 112, width: 180 },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);
// 提交表单对象
const form = ref<SysSensitiveWordVo & SysSensitiveWordForm>({});
const testForm = ref<SysSensitiveWordTest>({ text: '' });
// 查询对象
const queryParams = ref<SysSensitiveWordQuery>({
  pageNum: 1,
  pageSize: 10,
  word: undefined,
  category: undefined,
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

/** 查询敏感词列表 */
function getList() {
  loading.value = true;
  listSensitiveWord(queryParams.value)
    .then((response) => {
      sensitiveWordList.value = response.rows;
      total.value = response.total;
    })
    .finally(() => (loading.value = false));
}

// 表单重置
function reset() {
  form.value = {
    status: 1,
  };
  proxy.resetForm('sensitiveWordRef');
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
  handleQuery();
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
  title.value = '添加敏感词';
}

/** 详情按钮操作 */
function handleDetail(row: SysSensitiveWordVo) {
  reset();
  openView.value = true;
  openViewLoading.value = true;
  const wordId = row.wordId;
  getSensitiveWord(wordId).then((response) => {
    form.value = response.data;
    openViewLoading.value = false;
  });
}

/** 修改按钮操作 */
function handleUpdate(row?: SysSensitiveWordVo) {
  buttonLoading.value = true;
  reset();
  open.value = true;
  title.value = '修改敏感词';
  const wordId = row?.wordId || ids.value.at(0);
  getSensitiveWord(wordId).then((response) => {
    buttonLoading.value = false;
    form.value = response.data;
  });
}

/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.wordId) {
      updateSensitiveWord(form.value)
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
      addSensitiveWord(form.value)
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

/** 提交测试表单 */
function submitTestForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonTestLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('数据请求中...');
    sensitiveWordTest(testForm.value)
      .then((res) => {
        testResult.value = res.data;
      })
      .finally(() => {
        buttonTestLoading.value = false;
        proxy.$modal.msgClose(msgLoading);
      });
  } else {
    proxy.$modal.msgError(firstError);
  }
}

/** 删除按钮操作 */
function handleDelete(row?: SysSensitiveWordVo) {
  const wordIds = row?.wordId || ids.value;
  proxy.$modal.confirm(`是否确认删除敏感词编号为${wordIds}的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delSensitiveWord(wordIds)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, wordIds);
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
    'system/sensitiveWord/export',
    {
      ...queryParams.value,
    },
    `sensitiveWord_${new Date().getTime()}.xlsx`,
  );
}

/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  proxy.$modal.confirm(`是否确认刷新缓存？`, () => {
    return refreshSensitiveWordCache().then(() => {
      proxy.$modal.msgSuccess('刷新成功');
    });
  });
}

getList();
</script>
