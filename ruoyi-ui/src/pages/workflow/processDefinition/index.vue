<template>
  <t-card>
    <t-row :gutter="20">
      <!--模型分类-->
      <t-col :sm="2" :xs="12">
        <category-tree v-model="treeActived" @active="handleQuery" />
      </t-col>
      <!--模型数据-->
      <t-col :sm="10" :xs="12">
        <t-space direction="vertical" style="width: 100%">
          <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(6em + 12px)">
            <t-form-item label="流程定义名称" name="flowName">
              <t-input v-model="queryParams.flowName" placeholder="请输入流程定义名称" clearable @enter="handleQuery" />
            </t-form-item>
            <t-form-item label="流程定义KEY" name="flowCode">
              <t-input v-model="queryParams.flowCode" placeholder="请输入流程定义KEY" clearable @enter="handleQuery" />
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

          <t-tabs v-model="activeName" class="demo-tabs" @change="handleClick">
            <t-tab-panel label="已发布" value="0"></t-tab-panel>
            <t-tab-panel label="未发布" value="1"></t-tab-panel>
            <t-table
              v-model:column-controller-visible="columnControllerVisible"
              hover
              :loading="loading"
              row-key="id"
              :data="processDefinitionList"
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
                    <t-button theme="primary" @click="handleAdd">
                      <template #icon> <add-icon /></template>
                      新增
                    </t-button>
                    <t-button theme="default" variant="outline" :disabled="single" @click="handleUpdate()">
                      <template #icon> <edit-icon /> </template>
                      修改
                    </t-button>
                    <t-button theme="danger" :disabled="multiple" @click="handleDelete()">
                      <template #icon><delete-icon /></template>
                      删除
                    </t-button>
                    <t-button theme="primary" @click="uploadDialog.visible = true">
                      <template #icon> <cloud-upload-icon /></template>
                      部署流程文件
                    </t-button>
                    <t-button theme="default" variant="outline" @click="handleExport">
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
              <template #version="{ row }">v{{ row.version }}.0</template>
              <template #activityStatus="{ row }">
                <t-switch
                  v-model="row.activityStatus"
                  :active-value="1"
                  :inactive-value="0"
                  @change="(status) => handleProcessDefState(row, status)"
                />
              </template>
              <template #isPublish="{ row }">
                <t-tag v-if="row.isPublish == 0" theme="danger" variant="light">未发布</t-tag>
                <t-tag v-else-if="row.isPublish == 1" theme="success" variant="light">已发布</t-tag>
                <t-tag v-else theme="danger" variant="light">失效</t-tag>
              </template>
              <template #operation="{ row }">
                <t-space :size="8" break-line>
                  <my-link theme="danger" @click.stop="handleDelete(row)">
                    <template #prefix-icon><delete-icon /></template>删除流程
                  </my-link>
                  <my-link @click.stop="handleCopyDef(row)">
                    <template #prefix-icon><copy-icon /></template>复制流程
                  </my-link>
                  <my-link v-if="row.isPublish === 0" @click.stop="design(row)">
                    <template #prefix-icon><gesture-typing-icon /></template>流程设计
                  </my-link>
                  <my-link @click.stop="designView(row)">
                    <template #prefix-icon><browse-icon /></template>查看流程
                  </my-link>
                  <my-link v-if="row.isPublish !== 1" @click.stop="handlePublish(row)">
                    <template #prefix-icon><rocket-icon /></template>发布流程
                  </my-link>
                </t-space>
              </template>
            </t-table>
          </t-tabs>
        </t-space>
      </t-col>
    </t-row>

    <!-- 部署文件 -->
    <t-dialog
      v-if="uploadDialog.visible"
      v-model:visible="uploadDialog.visible"
      :header="uploadDialog.title"
      :close-on-overlay-click="false"
      width="30%"
    >
      <t-loading :loading="uploadDialogLoading">
        <t-form colon label-width="calc(8em + 41px)">
          <t-form-item label="请选择部署流程分类" required-mark>
            <t-tree-select
              v-model="selectCategory"
              :data="categoryOptions"
              filterable
              :tree-props="{
                keys: { value: 'id', label: 'label', children: 'children' },
                checkStrictly: true,
              }"
              placeholder="请选择父级分类"
            />
          </t-form-item>
          <t-form-item label-width="0px">
            <t-upload
              class="upload-model"
              multiple
              accept="application/json,application/text"
              :request-method="handlerImportDefinition"
              theme="file"
              :before-upload="handlerBeforeUpload"
              draggable
              tips="仅允许导入json格式文件。"
            >
              <template #tips>
                选择JSON流程文件<br />
                仅支持 .json 格式文件<br />
                PS:如若部署请部署从本项目模型管理导出的数据
              </template>
            </t-upload>
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 新增/编辑流程定义 -->
    <t-dialog
      v-model:visible="modelDialog.visible"
      :header="modelDialog.title"
      width="650px"
      attach="body"
      :close-on-overlay-click="false"
      :confirm-btn="{
        loading: buttonLoading,
      }"
      @confirm="defFormRef.submit()"
    >
      <t-form
        ref="defFormRef"
        :data="form"
        :rules="rules"
        label-align="right"
        label-width="calc(5em + 41px)"
        scroll-to-first-error="smooth"
        @submit="handleSubmit"
      >
        <t-form-item label="流程类别" name="category">
          <t-tree-select
            v-model="form.category"
            :data="categoryOptions"
            :tree-props="{
              keys: { value: 'id', label: 'label', children: 'children' },
              checkStrictly: true,
            }"
            placeholder="选择流程类别"
          />
        </t-form-item>
        <t-form-item label="流程编码" name="flowCode">
          <t-input v-model="form.flowCode" placeholder="请输入流程编码" maxlength="40" show-limit-number />
        </t-form-item>
        <t-form-item label="流程名称" name="flowName">
          <t-input v-model="form.flowName" placeholder="请输入流程名称" maxlength="100" show-limit-number />
        </t-form-item>
        <t-form-item label="表单路径" name="flowName">
          <t-input v-model="form.formPath" placeholder="请输入表单路径" maxlength="100" show-limit-number />
        </t-form-item>
      </t-form>
    </t-dialog>
  </t-card>
</template>

<script lang="ts" setup>
defineOptions({
  name: 'ProcessDefinition',
});

import {
  AddIcon,
  BrowseIcon,
  CloudUploadIcon,
  CopyIcon,
  DeleteIcon,
  DownloadIcon,
  EditIcon,
  GestureTypingIcon,
  RefreshIcon,
  RocketIcon,
  SearchIcon,
  SettingIcon,
} from 'tdesign-icons-vue-next';
import type {
  FormInstanceFunctions,
  FormProps,
  FormRule,
  PageInfo,
  PrimaryTableCol,
  TableProps,
  TabsProps,
  UploadProps,
} from 'tdesign-vue-next';
import { computed, ref } from 'vue';

import type { TreeModel } from '@/api/model/resultModel';
import {
  active,
  add,
  copy,
  deleteDefinition,
  edit,
  getInfo,
  importDef,
  listDefinition,
  publish,
  unPublishList,
} from '@/api/workflow/definition';
import type { FlowDefinitionForm, FlowDefinitionQuery, FlowDefinitionVo } from '@/api/workflow/model/definitionModel';
import CategoryTree from '@/pages/workflow/category/CategoryTree.vue';

const { proxy } = getCurrentInstance();
const router = useRouter();

const treeActived = ref<string[]>([]);
const columnControllerVisible = ref(false);

const buttonLoading = ref(true);
const loading = ref(true);
const ids = ref<Array<any>>([]);
const flowCodeList = ref<Array<any>>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const uploadDialogLoading = ref(false);
const processDefinitionList = ref<FlowDefinitionVo[]>([]);
const categoryOptions = ref<TreeModel<number | string>[]>([]);
/** 部署文件分类选择 */
const selectCategory = ref();
const defFormRef = ref<FormInstanceFunctions>();
const activeName = ref<number | string>('0');
const uploadDialog = reactive({
  visible: false,
  title: '部署流程文件',
});

const processDefinitionDialog = reactive({
  visible: false,
  title: '历史版本',
});

const modelDialog = reactive({
  visible: false,
  title: '',
});

// 校验规则
const rules = ref<Record<string, Array<FormRule>>>({
  category: [{ required: true, message: '分类名称不能为空' }],
  flowName: [{ required: true, message: '流程定义名称不能为空' }],
  flowCode: [{ required: true, message: '流程定义编码不能为空' }],
});

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 30, align: 'center' },
  { title: `序号`, colKey: 'serial-number', width: 60 },
  { title: `流程定义名称`, colKey: 'flowName', align: 'center', ellipsis: true },
  { title: `标识KEY`, colKey: 'flowCode', align: 'center', ellipsis: true },
  { title: `版本号`, colKey: 'version', align: 'center' },
  { title: `激活状态`, colKey: 'activityStatus', align: 'center' },
  { title: `发布状态`, colKey: 'isPublish', align: 'center' },
  { title: `操作`, colKey: 'operation', align: 'center', fixed: 'right', width: 170 },
]);

// 流程定义参数
const form = ref<FlowDefinitionVo & FlowDefinitionForm>({
  id: '',
  flowName: '',
  flowCode: '',
  category: '',
  formPath: '',
});

// 查询参数
const queryParams = ref<FlowDefinitionQuery>({
  pageNum: 1,
  pageSize: 10,
  flowName: undefined,
  flowCode: undefined,
  category: undefined,
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

onMounted(() => {
  handleQuery();
});
const handleClick: TabsProps['onChange'] = (value) => {
  // v-model处理有延迟 需要手动处理
  activeName.value = value;
  handleQuery();
};
/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  if (activeName.value === '0') {
    getList();
  } else {
    getUnPublishList();
  }
};
/** 重置按钮操作 */
const resetQuery = () => {
  proxy.resetForm('queryRef');
  treeActived.value = [];
  handleQuery();
};
const selectList = ref<any[]>();
// 多选框选中数据
const handleSelectionChange: TableProps['onSelectChange'] = (selection, options) => {
  if (options.type === 'uncheck' && options.currentRowKey === 'CHECK_ALL_BOX') {
    // 取消全选. 注：此处组件有bug，无法获取到取消全选的数据，只能通过获取到全部数据再做对比
    const ids = processDefinitionList.value.map((value) => value.id);
    selectList.value = selectList.value.filter((value) => !ids.includes(value.id));
  } else {
    selectList.value = options.selectedRowData;
  }
  ids.value = selection;
  flowCodeList.value = selectList.value.map((item) => item.flowCode);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};
// 分页
const getList = async () => {
  loading.value = true;
  queryParams.value.category = treeActived.value.at(0);
  const resp = await listDefinition(queryParams.value);
  processDefinitionList.value = resp.rows;
  total.value = resp.total;
  loading.value = false;
};
// 查询未发布的流程定义列表
const getUnPublishList = async () => {
  loading.value = true;
  queryParams.value.category = treeActived.value.at(0);
  const resp = await unPublishList(queryParams.value);
  processDefinitionList.value = resp.rows;
  total.value = resp.total;
  loading.value = false;
};

/** 删除按钮操作 */
const handleDelete = (row?: FlowDefinitionVo) => {
  const id = row?.id || ids.value;
  const defList = processDefinitionList.value.filter((x) => id.indexOf(x.id) !== -1).map((x) => x.flowCode);
  proxy?.$modal.confirm(`是否确认删除流程定义KEY为【${defList}】的数据项？`, async () => {
    loading.value = true;
    await deleteDefinition(id).finally(() => (loading.value = false));
    handleQuery();
    await proxy?.$modal.msgSuccess('删除成功');
  });
};

/** 发布流程定义 */
const handlePublish = (row?: FlowDefinitionVo) => {
  proxy?.$modal.confirm(
    `是否确认发布流程定义KEY为【${row.flowCode}】版本为【${row.version}】的数据项？，发布后会将已发布流程定义改为失效！`,
    async () => {
      loading.value = true;
      await publish(row.id).finally(() => (loading.value = false));
      processDefinitionDialog.visible = false;
      activeName.value = '0';
      handleQuery();
      await proxy?.$modal.msgSuccess('发布成功');
    },
  );
};
/** 挂起/激活 */
const handleProcessDefState = async (row: FlowDefinitionVo, status: number | string | boolean) => {
  let msg: string;
  if (status === 0) {
    msg = `暂停后，此流程下的所有任务都不允许往后流转，您确定挂起【${row.flowName || row.flowCode}】吗？`;
  } else {
    msg = `启动后，此流程下的所有任务都允许往后流转，您确定激活【${row.flowName || row.flowCode}】吗？`;
  }

  loading.value = true;
  proxy?.$modal.confirm(msg, async () => {
    try {
      await active(row.id, !!status);
      await handleQuery();
      proxy?.$modal.msgSuccess('操作成功');
    } catch (error) {
      row.activityStatus = status === 0 ? 1 : 0;
      console.error(error);
    } finally {
      loading.value = false;
    }
  });
};
// 上传文件前的钩子
const handlerBeforeUpload = () => {
  if (selectCategory.value === 'ALL') {
    proxy?.$modal.msgError('顶级节点不可作为分类！');
    return false;
  }
  if (!selectCategory.value) {
    proxy?.$modal.msgError('请选择左侧要上传的分类！');
    return false;
  }
  return true;
};
// 部署文件
const handlerImportDefinition: UploadProps['requestMethod'] = (files) => {
  return new Promise((resolve) => {
    const file = Array.isArray(files) ? files.at(0) : files;
    const formData = new FormData();
    if (selectCategory.value === 'ALL') {
      proxy?.$modal.msgError('顶级节点不可作为分类！');
      resolve({ status: 'fail', error: '顶级节点不可作为分类！', response: {} });
      return;
    }
    if (!selectCategory.value) {
      proxy?.$modal.msgError('请选择部署流程分类！');
      resolve({ status: 'fail', error: '请选择部署流程分类！', response: {} });
      return;
    }
    uploadDialogLoading.value = true;
    formData.append('file', file.raw);
    formData.append('category', selectCategory.value);
    importDef(formData)
      .then(() => {
        uploadDialog.visible = false;
        proxy?.$modal.msgSuccess('部署成功');
        activeName.value = '1';
        handleQuery();
        resolve({ status: 'success', response: {} });
      })
      .catch((e) => {
        resolve({ status: 'fail', error: e, response: {} });
      })
      .finally(() => {
        uploadDialogLoading.value = false;
      });
  });
};
/**
 * 设计流程
 * @param row
 */
const design = async (row: FlowDefinitionVo) => {
  router.push({
    path: `/workflow/design/index`,
    query: {
      definitionId: row.id,
      disabled: 'false',
    },
  });
};

/**
 * 查看流程
 * @param row
 */
const designView = async (row: FlowDefinitionVo) => {
  router.push({
    path: `/workflow/design/index`,
    query: {
      definitionId: row.id,
      disabled: 'true',
    },
  });
};
/** 表单重置 */
const reset = () => {
  form.value = {
    id: '',
    flowName: '',
    flowCode: '',
    category: '',
    formPath: '',
  };
  proxy.resetForm('defFormRef');
};
/**
 * 新增
 */
const handleAdd = async () => {
  reset();
  modelDialog.visible = true;
  modelDialog.title = '新增流程';
};
/** 修改按钮操作 */
const handleUpdate = async (row?: FlowDefinitionVo) => {
  buttonLoading.value = true;
  reset();
  const id = row?.id || ids.value[0];
  modelDialog.visible = true;
  modelDialog.title = '修改流程';
  const res = await getInfo(id);
  buttonLoading.value = false;
  Object.assign(form.value, res.data);
};

const handleSubmit: FormProps['onSubmit'] = ({ validateResult, firstError }) => {
  if (validateResult === true) {
    buttonLoading.value = true;
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    if (form.value.id) {
      edit(form.value)
        .then(() => {
          proxy?.$modal.msgSuccess('操作成功');
          modelDialog.visible = false;
          handleQuery();
        })
        .finally(() => {
          buttonLoading.value = false;
          proxy.$modal.msgClose(msgLoading);
        });
    } else {
      add(form.value)
        .then(() => {
          proxy?.$modal.msgSuccess('操作成功');
          modelDialog.visible = false;
          handleQuery();
        })
        .finally(() => {
          buttonLoading.value = false;
          proxy.$modal.msgClose(msgLoading);
        });
    }
  } else {
    proxy.$modal.msgError(firstError);
  }
};
// 复制
const handleCopyDef = async (row: FlowDefinitionVo) => {
  proxy.$modal.confirm(`是否确认复制【${row.flowCode}】版本为【${row.version}】的流程定义！`, () => {
    loading.value = true;
    copy(row.id)
      .then((resp) => {
        if (resp.code === 200) {
          proxy?.$modal.msgSuccess('操作成功');
          activeName.value = '1';
          handleQuery();
        }
      })
      .finally(() => (loading.value = false));
  });
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy.download(
    `/workflow/definition/exportDef/${ids.value[0]}`,
    {
      ...queryParams.value,
    },
    `${flowCodeList.value[0]}.json`,
  );
};
</script>
<style lang="less" scoped>
:global(.upload-model) {
  width: 100%;
}
:global(.upload-model .t-upload__dragger) {
  width: 100%;
}
</style>
