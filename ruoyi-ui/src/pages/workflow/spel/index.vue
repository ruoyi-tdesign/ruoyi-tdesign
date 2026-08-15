<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="组件名称" name="componentName">
          <t-input v-model="queryParams.componentName" placeholder="请输入组件名称" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="方法名" name="methodName">
          <t-input v-model="queryParams.methodName" placeholder="请输入方法名" clearable @enter="handleQuery" />
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
        row-key="id"
        :data="spelList"
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
              <t-button v-hasPermi="['workflow:spel:add']" theme="primary" @click="handleAdd()">
                <template #icon> <add-icon /></template>
                新增
              </t-button>
              <t-button
                v-hasPermi="['workflow:spel:edit']"
                theme="default"
                variant="outline"
                :disabled="single"
                @click="handleUpdate()"
              >
                <template #icon> <edit-icon /> </template>
                修改
              </t-button>
              <t-button
                v-hasPermi="['workflow:spel:remove']"
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
        <template #status="{ row }">
          <t-tag v-if="row.status === '0'" theme="success" variant="light">正常</t-tag>
          <t-tag v-else theme="danger" variant="light">停用</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['workflow:spel:edit']" @click.stop="handleUpdate(row)">
              <template #prefix-icon><edit-icon /></template>修改
            </my-link>
            <my-link v-hasPermi="['workflow:spel:remove']" theme="danger" @click.stop="handleDelete(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 添加或修改流程spel表达式定义对话框 -->
    <t-dialog
      v-model:visible="dialog.visible"
      :header="dialog.title"
      width="min(600px, 100%)"
      attach="body"
      :close-on-overlay-click="false"
      :confirm-btn="{
        loading: buttonLoading,
      }"
      @confirm="spelRef.submit()"
    >
      <t-loading :loading="buttonLoading" size="small">
        <t-form
          ref="spelRef"
          :data="form"
          :rules="rules"
          label-align="right"
          label-width="calc(4em + 41px)"
          scroll-to-first-error="smooth"
          @submit="submitForm"
        >
          <t-form-item label="组件名称" name="componentName">
            <t-input v-model="form.componentName" placeholder="请输入组件名称" @input="updateViewSpel" />
          </t-form-item>
          <t-form-item label="方法名称" name="methodName">
            <t-input v-model="form.methodName" placeholder="请输入方法名称" @input="updateViewSpel" />
          </t-form-item>
          <t-form-item label="方法参数" name="methodParams">
            <t-input v-model="form.methodParams" placeholder="请输入方法参数" @input="updateViewSpel" />
          </t-form-item>
          <t-form-item label="SPEL表达式">
            <span class="preview-box">
              {{ form.viewSpel || '例如：#{@组件名.方法名(#方法参数)} 或 ${方法参数}' }}
            </span>
          </t-form-item>
          <t-form-item label="状态" name="status">
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
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'Spel',
});

import { AddIcon, DeleteIcon, EditIcon, RefreshIcon, SearchIcon, SettingIcon } from 'tdesign-icons-vue-next';
import type { FormInstanceFunctions, FormRule, PageInfo, PrimaryTableCol, SubmitContext } from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref, watch } from 'vue';

import type { SpelForm, SpelQuery, SpelVo } from '@/api/workflow/model/spelModel';
import { addSpel, delSpel, getSpel, listSpel, updateSpel } from '@/api/workflow/spel';

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict('sys_normal_disable');

const spelList = ref<SpelVo[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const columnControllerVisible = ref(false);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryRef = ref<FormInstanceFunctions>();
const spelRef = ref<FormInstanceFunctions>();

const dialog = reactive({
  visible: false,
  title: '',
});

const initFormData: SpelForm = {
  id: undefined,
  componentName: undefined,
  methodName: undefined,
  methodParams: undefined,
  viewSpel: undefined,
  status: '0',
  remark: undefined,
};

// 提交表单对象
const form = ref<SpelForm>({ ...initFormData });
// 查询对象
const queryParams = ref<SpelQuery>({
  pageNum: 1,
  pageSize: 10,
  componentName: undefined,
  methodName: undefined,
  methodParams: undefined,
  viewSpel: undefined,
  status: undefined,
});

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  status: [{ required: true, message: '状态不能为空' }],
});

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 30, align: 'center' },
  { title: `序号`, colKey: 'serial-number', width: 60 },
  { title: `组件名称`, colKey: 'componentName', align: 'center', ellipsis: true },
  { title: `方法名称`, colKey: 'methodName', align: 'center', ellipsis: true },
  { title: `参数名称`, colKey: 'methodParams', align: 'center', ellipsis: true },
  { title: `SPEL表达式`, colKey: 'viewSpel', align: 'center', ellipsis: true },
  { title: `状态`, colKey: 'status', align: 'center' },
  { title: `备注`, colKey: 'remark', align: 'center', ellipsis: true },
  { title: `操作`, colKey: 'operation', align: 'center', fixed: 'right', width: 140 },
]);

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

/** 查询流程spel表达式定义列表 */
const getList = async () => {
  loading.value = true;
  const res = await listSpel(queryParams.value);
  spelList.value = res.rows;
  total.value = res.total;
  loading.value = false;
};

/** 表单重置 */
const reset = () => {
  form.value = { ...initFormData };
  spelRef.value?.reset();
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryRef.value?.reset();
  handleQuery();
};

/** 多选框选中数据 */
const handleSelectionChange = (selection: Array<string | number>) => {
  ids.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加流程spel表达式定义';
};

/** 修改按钮操作 */
const handleUpdate = async (row?: SpelVo) => {
  reset();
  const _id = row?.id || ids.value[0];
  const res = await getSpel(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = '修改流程spel表达式定义';
};

/** 提交表单 */
function submitForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.id) {
      updateSpel(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('操作成功');
          dialog.visible = false;
          getList();
        })
        .finally(() => {
          buttonLoading.value = false;
          proxy.$modal.msgClose(msgLoading);
        });
    } else {
      addSpel(form.value)
        .then(() => {
          proxy.$modal.msgSuccess('操作成功');
          dialog.visible = false;
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
const handleDelete = (row?: SpelVo) => {
  const _ids = row?.id || ids.value;
  proxy.$modal.confirm(`是否确认删除流程spel表达式定义编号为"${_ids}"的数据项？`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delSpel(_ids)
      .then(() => {
        ids.value = ids.value.filter((id) => !(Array.isArray(_ids) ? _ids : [_ids]).includes(id));
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => {
        proxy.$modal.msgClose(msgLoading);
      });
  });
};

/** 更新 spel 预览值 */
const updateViewSpel = () => {
  const comp = (form.value.componentName || '').trim();
  const method = (form.value.methodName || '').trim();
  const paramStr = (form.value.methodParams || '').trim();

  if (!comp && !method && !paramStr) {
    form.value.viewSpel = '';
    return;
  }

  // 替换变量值：只有参数存在，组件和方法都不存在
  if (!comp && !method && paramStr) {
    const paramList = paramStr
      .split(',')
      .map((p) => p.trim())
      .filter((p) => p.length > 0);

    if (paramList.length === 1) {
      form.value.viewSpel = `\${${paramList[0]}}`;
      return;
    }
  }

  // 如果缺少组件或方法，提示填写
  if (!comp || !method) {
    form.value.viewSpel = '请填写组件名称和方法名';
    return;
  }

  const paramList: string[] = [];

  if (paramStr) {
    // 分割并过滤掉空参数
    paramList.push(
      ...paramStr
        .split(',')
        .map((p) => p.trim())
        .filter((p) => p.length > 0),
    );
  }

  const paramPart = paramList.length > 0 ? `(${paramList.map((p) => `#${p}`).join(',')})` : '()';

  form.value.viewSpel = `#{@${comp}.${method}${paramPart}}`;
};

/** 监听所有字段变化 */
watch(() => [form.value.componentName, form.value.methodName, form.value.methodParams], updateViewSpel);

onMounted(() => {
  getList();
});
</script>
<style lang="less" scoped>
.preview-box {
  width: 100%;
  padding: 10px 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  color: #333;
  font-family: monospace;
  white-space: nowrap;
  overflow-x: auto;
  min-height: 36px;
  line-height: 1.5;
}
</style>
