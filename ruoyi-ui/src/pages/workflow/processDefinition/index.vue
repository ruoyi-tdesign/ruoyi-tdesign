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
            <t-form-item label="流程定义名称" name="name">
              <t-input v-model="queryParams.name" placeholder="请输入流程定义名称" clearable @enter="handleQuery" />
            </t-form-item>
            <t-form-item label="流程定义KEY" name="key">
              <t-input v-model="queryParams.key" placeholder="请输入流程定义KEY" clearable @enter="handleQuery" />
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
                  <t-button theme="danger" :disabled="multiple" @click="handleDelete()">
                    <template #icon><delete-icon /></template>
                    删除
                  </t-button>
                  <t-button theme="primary" @click="uploadDialog.visible = true">
                    <template #icon> <cloud-upload-icon /></template>
                    部署流程文件
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
            <template #resourceName="{ row }">
              <t-link theme="primary" hover="color" @click.stop="clickPreview(row.id, 'xml')">
                {{ row.resourceName }}
              </t-link>
            </template>
            <template #diagramResourceName="{ row }">
              <t-link theme="primary" hover="color" @click.stop="clickPreview(row.id, 'bpmn')">
                {{ row.resourceName }}
              </t-link>
            </template>
            <template #suspensionState="{ row }">
              <t-tag v-if="row.suspensionState === 1" theme="success" variant="light">激活</t-tag>
              <t-tag v-else theme="danger" variant="light">挂起</t-tag>
            </template>
            <template #tableName="{ row }">
              <span v-if="row.wfDefinitionConfigVo">{{ row.wfDefinitionConfigVo.tableName }}</span>
            </template>
            <template #operation="{ row }">
              <t-space :size="8" break-line>
                <t-link theme="primary" hover="color" size="small" @click.stop="handleProcessDefState(row)">
                  <lock-on-icon v-if="row.suspensionState === 1" />
                  <lock-off-icon v-else />
                  {{ row.suspensionState === 1 ? '挂起流程' : '激活流程' }}
                </t-link>
                <t-link
                  theme="primary"
                  size="small"
                  hover="color"
                  @click.stop="getProcessDefinitionHistoryList(row.id, row.key)"
                >
                  <template #prefix-icon><history-icon /></template>历史版本
                </t-link>
                <t-link theme="danger" size="small" hover="color" @click.stop="handleDelete(row)">
                  <template #prefix-icon><delete-icon /></template>删除
                </t-link>
                <t-link theme="primary" size="small" hover="color" @click.stop="handleConvertToModel(row)">
                  <template #prefix-icon><swap-icon /></template>转换模型
                </t-link>
                <t-link theme="primary" size="small" hover="color" @click.stop="handleDefinitionConfigOpen(row)">
                  <template #prefix-icon><catalog-icon /></template>绑定业务
                </t-link>
              </t-space>
            </template>
          </t-table>
        </t-space>
      </t-col>
    </t-row>
    <!-- 预览图片或xml -->
    <process-preview ref="previewRef" />

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
                keys: { value: 'categoryCode', label: 'categoryName', children: 'children' },
                checkStrictly: true,
              }"
              placeholder="请选择父级分类"
            />
          </t-form-item>
          <t-form-item label-width="0px">
            <t-upload
              class="upload-bpmn"
              multiple
              accept="application/zip,application/xml,.bpmn"
              :request-method="handlerDeployProcessFile"
              theme="file"
              :before-upload="handlerBeforeUpload"
              draggable
              tips="仅允许导入xls、xlsx格式文件。"
            >
              <template #tips>
                选择BPMN流程文件<br />
                仅支持 .zip、.bpmn20.xml、bpmn 格式文件<br />
                PS:如若部署请部署从本项目模型管理导出的数据
              </template>
            </t-upload>
          </t-form-item>
        </t-form>
      </t-loading>
    </t-dialog>

    <!-- 历史版本 -->
    <t-dialog
      v-if="processDefinitionDialog.visible"
      v-model:visible="processDefinitionDialog.visible"
      :header="processDefinitionDialog.title"
      width="70%"
    >
      <t-table
        :loading="loading"
        :columns="historyColumns"
        :data="processDefinitionHistoryList"
        @selection-change="handleSelectionChange"
      >
        <template #version="{ row }">v{{ row.version }}.0</template>
        <template #resourceName="{ row }">
          <t-link theme="primary" hover="color" @click.stop="clickPreview(row.id, 'xml')">
            {{ row.resourceName }}
          </t-link>
        </template>
        <template #diagramResourceName="{ row }">
          <t-link theme="primary" hover="color" @click.stop="clickPreview(row.id, 'bpmn')">
            {{ row.resourceName }}
          </t-link>
        </template>
        <template #suspensionState="{ row }">
          <t-tag v-if="row.suspensionState === 1" theme="success" variant="light">激活</t-tag>
          <t-tag v-else theme="danger" variant="light">挂起</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <t-link theme="primary" hover="color" size="small" @click.stop="handleProcessDefState(row)">
              <lock-on-icon v-if="row.suspensionState === 1" />
              <lock-off-icon v-else />
              {{ row.suspensionState === 1 ? '挂起流程' : '激活流程' }}
            </t-link>
            <t-link theme="primary" size="small" hover="color" @click.stop="handleDefinitionConfigOpen(row)">
              <template #prefix-icon><catalog-icon /></template>绑定业务
            </t-link>
            <t-link theme="primary" size="small" hover="color" @click.stop="handleConvertToModel(row)">
              <template #prefix-icon><swap-icon /></template>转换模型
            </t-link>
            <t-link theme="danger" size="small" hover="color" @click.stop="handleDelete(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </t-link>
          </t-space>
        </template>
      </t-table>
    </t-dialog>

    <!-- 表单配置 -->
    <t-dialog
      v-model:visible="definitionConfigDialog.visible"
      :title="definitionConfigDialog.title"
      width="650px"
      attach="body"
      :close-on-overlay-click="false"
    >
      <t-form :data="definitionConfigForm" label-width="auto">
        <t-form-item label="流程KEY">
          <t-input v-model="definitionConfigForm.processKey" disabled />
        </t-form-item>
        <t-form-item label="表名" name="formId">
          <t-input v-model="definitionConfigForm.tableName" placeholder="示例:test_leave" />
        </t-form-item>
        <t-form-item label="备注">
          <t-textarea v-model="definitionConfigForm.remark" />
        </t-form-item>
      </t-form>

      <template #footer>
        <div class="dialog-footer">
          <t-button variant="outline" @click="definitionConfigDialog.visible = false">取消</t-button>
          <t-button theme="primary" @click="handlerSaveForm">保存</t-button>
        </div>
      </template>
    </t-dialog>
  </t-card>
</template>

<script lang="ts" setup>
defineOptions({
  name: 'ProcessDefinition',
});

import {
  CatalogIcon,
  CloudUploadIcon,
  DeleteIcon,
  HistoryIcon,
  LockOffIcon,
  LockOnIcon,
  RefreshIcon,
  SearchIcon,
  SettingIcon,
  SwapIcon,
} from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol, TableProps, UploadProps } from 'tdesign-vue-next';
import { computed, ref } from 'vue';

import { getByDefId, getByTableNameNotDefId, saveOrUpdate } from '@/api/workflow/definitionConfig';
import type { DefinitionConfigForm } from '@/api/workflow/definitionConfig/types';
import type { WfCategoryVo } from '@/api/workflow/model/categoryModel';
import {
  convertToModel,
  definitionXml,
  deleteProcessDefinition,
  deployProcessFile,
  getListByKey,
  listProcessDefinition,
  updateDefinitionState,
} from '@/api/workflow/processDefinition';
import type { ProcessDefinitionQuery, ProcessDefinitionVo } from '@/api/workflow/processDefinition/types';
import CategoryTree from '@/pages/workflow/category/CategoryTree.vue';

import ProcessPreview from './components/processPreview.vue';

const { proxy } = getCurrentInstance();

const previewRef = ref<InstanceType<typeof ProcessPreview>>();
const definitionConfigForm = ref<DefinitionConfigForm>({});

const treeActived = ref<string[]>([]);
const columnControllerVisible = ref(false);

const loading = ref(true);
const ids = ref<Array<any>>([]);
const deploymentIds = ref<Array<any>>([]);
const keys = ref<Array<any>>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const uploadDialogLoading = ref(false);
const processDefinitionList = ref<ProcessDefinitionVo[]>([]);
const processDefinitionHistoryList = ref<ProcessDefinitionVo[]>([]);
const categoryOptions = ref<WfCategoryVo[]>([]);
/** 部署文件分类选择 */
const selectCategory = ref();

const uploadDialog = reactive({
  visible: false,
  title: '部署流程文件',
});

const processDefinitionDialog = reactive({
  visible: false,
  title: '历史版本',
});

const definitionConfigDialog = reactive({
  visible: false,
  title: '流程定义配置',
});

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 30, align: 'center' },
  { title: `序号`, colKey: 'serial-number', width: 60 },
  { title: `流程定义名称`, colKey: 'name', align: 'center', ellipsis: true },
  { title: `标识KEY`, colKey: 'key', align: 'center', ellipsis: true },
  { title: `版本号`, colKey: 'version', align: 'center' },
  { title: `流程XML`, colKey: 'resourceName', align: 'center', ellipsis: true },
  { title: `流程图片`, colKey: 'diagramResourceName', align: 'center', ellipsis: true },
  { title: `状态`, colKey: 'suspensionState', align: 'center' },
  { title: `部署时间`, colKey: 'deploymentTime', align: 'center', width: '10%', minWidth: 112 },
  { title: `表名/表单KEY`, colKey: 'tableName', align: 'center' },
  { title: `操作`, colKey: 'operation', align: 'center', fixed: 'right' },
]);

// 历史版本列显隐信息
const historyColumns = ref<Array<PrimaryTableCol>>([
  // { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 30, align: 'center' },
  { title: `序号`, colKey: 'serial-number', width: 60 },
  { title: `流程定义名称`, colKey: 'name', align: 'center', ellipsis: true },
  { title: `标识KEY`, colKey: 'key', align: 'center', ellipsis: true },
  { title: `版本号`, colKey: 'version', align: 'center' },
  { title: `流程XML`, colKey: 'resourceName', align: 'center', ellipsis: true },
  { title: `流程图片`, colKey: 'diagramResourceName', align: 'center', ellipsis: true },
  { title: `状态`, colKey: 'suspensionState', align: 'center' },
  { title: `部署时间`, colKey: 'deploymentTime', align: 'center', sorter: true, width: '10%', minWidth: 112 },
  { title: `操作`, colKey: 'operation', align: 'center', fixed: 'right' },
]);

// 查询参数
const queryParams = ref<ProcessDefinitionQuery>({
  pageNum: 1,
  pageSize: 10,
  name: undefined,
  key: undefined,
  categoryCode: undefined,
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
  getList();
});

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
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
  deploymentIds.value = selectList.value.map((item) => item.deploymentId);
  keys.value = selectList.value.map((item) => item.key);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};
// 分页
const getList = async () => {
  loading.value = true;
  const resp = await listProcessDefinition(queryParams.value);
  processDefinitionList.value = resp.rows;
  total.value = resp.total;
  loading.value = false;
};
// 获取历史流程定义
const getProcessDefinitionHistoryList = async (id: string, key: string) => {
  processDefinitionDialog.visible = true;
  loading.value = true;
  const resp = await getListByKey(key);
  if (resp.data && resp.data.length > 0) {
    processDefinitionHistoryList.value = resp.data.filter((item: any) => item.id !== id);
  }
  loading.value = false;
};

type PreviewType = 'xml' | 'bpmn';
// 预览 公共方法
const clickPreview = async (id: string, type: PreviewType) => {
  loading.value = true;
  const resp = await definitionXml(id);
  if (previewRef.value) {
    const xmlStr = resp.data.xmlStr;
    loading.value = false;
    previewRef.value.openDialog(xmlStr, type);
  }
};

/** 删除按钮操作 */
const handleDelete = async (row?: ProcessDefinitionVo) => {
  const id = row?.id || ids.value;
  const deployIds = row?.deploymentId || deploymentIds.value;
  const defKeys = row?.key || keys.value;
  proxy?.$modal.confirm(`是否确认删除流程定义KEY为【${defKeys}】的数据项？`, async () => {
    loading.value = true;
    await deleteProcessDefinition(deployIds, id).finally(() => (loading.value = false));
    await getList();
    await proxy?.$modal.msgSuccess('删除成功');
  });
};
/** 挂起/激活 */
const handleProcessDefState = async (row: ProcessDefinitionVo) => {
  let msg: string;
  if (row.suspensionState === 1) {
    msg = `暂停后，此流程下的所有任务都不允许往后流转，您确定挂起【${row.name || row.key}】吗？`;
  } else {
    msg = `启动后，此流程下的所有任务都允许往后流转，您确定激活【${row.name || row.key}】吗？`;
  }
  proxy?.$modal.confirm(msg, async () => {
    loading.value = true;
    await updateDefinitionState(row.id).finally(() => (loading.value = false));
    await getList();
    proxy?.$modal.msgSuccess('操作成功');
  });
};
/** 流程定义转换为模型 */
const handleConvertToModel = async (row: ProcessDefinitionVo) => {
  proxy?.$modal.confirm(`是否确认转换流程定义key为【${row.key}】的数据项？`, async () => {
    await convertToModel(row.id).finally(() => (loading.value = false));
    getList();
    await proxy?.$modal.msgSuccess('操作成功');
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
const handlerDeployProcessFile: UploadProps['requestMethod'] = (files) => {
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
    formData.append('categoryCode', selectCategory.value);
    deployProcessFile(formData)
      .then(() => {
        uploadDialog.visible = false;
        proxy?.$modal.msgSuccess('部署成功');
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

// 打开流程定义配置
const handleDefinitionConfigOpen = async (row: ProcessDefinitionVo) => {
  definitionConfigDialog.visible = true;
  definitionConfigForm.value.processKey = row.key;
  definitionConfigForm.value.definitionId = row.id;
  definitionConfigForm.value.version = row.version;
  const resp = await getByDefId(row.id);
  if (resp.data) {
    definitionConfigForm.value = resp.data;
  } else {
    definitionConfigForm.value.tableName = undefined;
    definitionConfigForm.value.remark = undefined;
  }
};
// 保存表单
const handlerSaveForm = async () => {
  getByTableNameNotDefId(definitionConfigForm.value.tableName, definitionConfigForm.value.definitionId).then((res) => {
    if (res.data && res.data.length > 0) {
      proxy.$modal.confirm(
        `表名已被【${res.data[0].processKey}】版本v${res.data[0].version}.0绑定确认后将会删除绑定的流程KEY!`,
        () => {
          saveOrUpdate(definitionConfigForm.value).then((resp) => {
            if (resp.code === 200) {
              proxy?.$modal.msgSuccess('操作成功');
              definitionConfigDialog.visible = false;
              getList();
            }
          });
        },
        null,
        {
          theme: 'warning',
        },
      );
    } else {
      saveOrUpdate(definitionConfigForm.value).then((resp) => {
        if (resp.code === 200) {
          proxy?.$modal.msgSuccess('操作成功');
          definitionConfigDialog.visible = false;
          getList();
        }
      });
    }
  });
};
</script>
<style lang="less" scoped>
:global(.upload-bpmn) {
  width: 100%;
}
:global(.upload-bpmn .t-upload__dragger) {
  width: 100%;
}
</style>
