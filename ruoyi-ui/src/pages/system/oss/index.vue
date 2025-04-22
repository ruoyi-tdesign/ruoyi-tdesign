<template>
  <t-card>
    <t-space direction="vertical" style="width: 100%">
      <t-form v-show="showSearch" ref="queryRef" :data="queryParams" layout="inline" label-width="calc(4em + 12px)">
        <t-form-item label="文件名" name="fileName">
          <t-input v-model="queryParams.fileName" placeholder="请输入文件名" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="原名" name="originalName">
          <t-input v-model="queryParams.originalName" placeholder="请输入原名" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="文件后缀" name="fileSuffix">
          <t-input v-model="queryParams.fileSuffix" placeholder="请输入文件后缀" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="创建时间">
          <t-date-range-picker
            v-model="daterangeCreateTime"
            style="width: 240px"
            allow-input
            clearable
            separator="-"
            :placeholder="['开始日期', '结束日期']"
          />
        </t-form-item>
        <t-form-item label="上传人" name="createByName">
          <t-input v-model="queryParams.createByName" placeholder="请输入上传人" clearable @enter="handleQuery" />
        </t-form-item>
        <t-form-item label="服务商" name="service">
          <t-input v-model="queryParams.service" placeholder="请输入服务商" clearable @enter="handleQuery" />
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
        row-key="ossId"
        :data="ossList"
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
              <t-button v-hasPermi="['system:oss:upload']" theme="primary" @click="handleFile">
                <template #icon> <cloud-upload-icon /></template>
                上传文件
              </t-button>
              <t-button v-hasPermi="['system:oss:upload']" theme="primary" @click="handleImage">
                <template #icon> <cloud-upload-icon /></template>
                上传图片
              </t-button>
              <t-button
                v-hasPermi="['system:oss:remove']"
                theme="danger"
                variant="outline"
                :disabled="multiple"
                @click="handleDelete()"
              >
                <template #icon> <delete-icon /> </template>
                删除
              </t-button>
              <t-button
                v-if="isSystem"
                v-hasPermi="['system:oss:edit']"
                :theme="previewListResource ? 'danger' : 'warning'"
                variant="outline"
                @click="handlePreviewListResource(!previewListResource)"
              >
                预览开关 : {{ previewListResource ? '禁用' : '启用' }}
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

        <template #url="{ row }">
          <image-preview
            v-if="previewListResource && checkFileSuffix(row.fileSuffix)"
            :src="row.url"
            width="60px"
            height="60px"
          />
          <t-tooltip v-else :content="row.url" placement="top">
            <div class="t-text-ellipsis" v-text="row.url" />
          </t-tooltip>
        </template>
        <template #size="{ row }">
          <span>{{ bytesToSize(row.size) }}</span>
        </template>
        <template #operation="{ row }">
          <t-space :size="8" break-line>
            <my-link v-hasPermi="['system:oss:download']" v-copyText="row.url" v-copyText:callback="copyTextSuccess">
              <template #prefix-icon><relativity-icon /></template>复制
            </my-link>
            <my-link v-hasPermi="['system:oss:download']" @click.stop="handleDownload(row)">
              <template #prefix-icon><download-icon /></template>下载
            </my-link>
            <my-link v-hasPermi="['system:oss:remove']" theme="danger" @click.stop="handleDelete(row)">
              <template #prefix-icon><delete-icon /></template>删除
            </my-link>
          </t-space>
        </template>
      </t-table>
    </t-space>

    <!-- 添加或修改OSS对象存储对话框 -->
    <t-dialog
      v-model:visible="open"
      :header="title"
      destroy-on-close
      :close-on-overlay-click="false"
      width="700px"
      attach="body"
      @confirm="submitForm"
    >
      <t-form ref="ossRef" :data="form" label-align="right" :rules="rules" label-width="calc(3em + 41px)">
        <t-form-item label="文件名">
          <file-upload
            v-if="type === 0"
            v-model="form.file"
            theme="file-flow"
            :support-select-file="false"
            :support-url="false"
          />
          <image-upload
            v-if="type === 1"
            v-model="form.file"
            theme="image-flow"
            :support-select-file="false"
            :support-url="false"
          />
        </t-form-item>
      </t-form>
    </t-dialog>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'Oss',
});

import {
  CloudUploadIcon,
  DeleteIcon,
  DownloadIcon,
  RefreshIcon,
  RelativityIcon,
  SearchIcon,
  SettingIcon,
} from 'tdesign-icons-vue-next';
import type { FormRule, PageInfo, PrimaryTableCol, TableSort } from 'tdesign-vue-next';
import { computed, getCurrentInstance, ref, toRefs } from 'vue';

import { getPreviewListResourceConfig } from '@/api/system/config';
import type { SysOssQuery, SysOssVo } from '@/api/system/model/ossModel';
import { delOss, listOss } from '@/api/system/oss';
import FileUpload from '@/components/file-upload/index.vue';
import ImagePreview from '@/components/image-preview/index.vue';
import ImageUpload from '@/components/image-upload/index.vue';
import { DEFAULT_TENANT_ID } from '@/constants';
import { useUserStore } from '@/store';
import { ArrayOps } from '@/utils/array';

const { proxy } = getCurrentInstance();
const { tenantId } = toRefs(useUserStore());
const isSystem = computed(() => tenantId.value === DEFAULT_TENANT_ID);

const ossList = ref<SysOssVo[]>([]);
const open = ref(false);
const columnControllerVisible = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const type = ref(0);
const previewListResource = ref(true);
const daterangeCreateTime = ref([]);
// 默认排序
const sort = ref<TableSort>();

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `选择列`, colKey: 'row-select', type: 'multiple', width: 50, align: 'center' },
  { title: `文件名`, colKey: 'fileName', align: 'center', ellipsis: true },
  { title: `原名`, colKey: 'originalName', align: 'center', ellipsis: true },
  { title: `文件后缀`, colKey: 'fileSuffix', align: 'center' },
  { title: `文件展示`, colKey: 'url', align: 'center' },
  { title: `大小`, colKey: 'size', align: 'center', sorter: true },
  { title: `创建时间`, colKey: 'createTime', align: 'center', width: 180, sorter: true },
  { title: `上传人`, colKey: 'createByName', align: 'center' },
  { title: `服务商`, colKey: 'service', align: 'center' },
  { title: `操作`, colKey: 'operation', align: 'center', width: 180 },
]);

const rules = ref<Record<string, Array<FormRule>>>({
  file: [{ required: true, message: '文件不能为空' }],
});

const form = ref<{ file?: any }>({});
const queryParams = ref<SysOssQuery>({
  pageNum: 1,
  pageSize: 10,
  fileName: undefined,
  originalName: undefined,
  fileSuffix: undefined,
  url: undefined,
  createTime: undefined,
  createByName: undefined,
  service: undefined,
  orderByColumn: undefined,
  isAsc: undefined,
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

/** 查询OSS对象存储列表 */
function getList() {
  loading.value = true;
  getPreviewListResourceConfig().then((response) => {
    previewListResource.value = response.data === undefined ? true : response.data;
  });
  listOss(proxy.addDateRange(queryParams.value, daterangeCreateTime.value, 'CreateTime'))
    .then((response) => {
      ossList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    })
    .finally(() => (loading.value = false));
}
function checkFileSuffix(fileSuffix: string | string[]) {
  const arr = ['png', 'jpg', 'jpeg', 'ico', 'bmp', 'webp', 'gif', 'svg'];
  return arr.some((type) => {
    return fileSuffix.indexOf(type) > -1;
  });
}
/** 表单重置 */
function reset() {
  form.value = {
    file: undefined,
  };
  proxy.resetForm('ossRef');
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  daterangeCreateTime.value = [];
  proxy.resetForm('queryRef');
  queryParams.value.pageNum = 1;
  handleSortChange(null);
}
/** 选择条数  */
function handleSelectionChange(selection: Array<string | number>) {
  ids.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
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
/** 文件按钮操作 */
function handleFile() {
  reset();
  open.value = true;
  title.value = '上传文件';
  type.value = 0;
}
/** 图片按钮操作 */
function handleImage() {
  reset();
  open.value = true;
  title.value = '上传图片';
  type.value = 1;
}
/** 提交按钮 */
function submitForm() {
  open.value = false;
  reset();
  getList();
}
/** 复制成功 */
function copyTextSuccess() {
  proxy.$modal.msgSuccess('复制成功');
}
/** 下载按钮操作 */
function handleDownload(row: SysOssVo) {
  proxy.$download.oss(row.ossId);
}
/** 预览状态修改  */
function handlePreviewListResource(preview: boolean) {
  const text = preview ? '启用' : '停用';
  proxy.$modal.confirm(`确认要"${text}""预览列表图片"配置吗?`, () => {
    const msgLoading = proxy.$modal.msgLoading('提交中...');
    return proxy
      .updateConfigByKey('sys.oss.previewListResource', `${preview}`, 1)
      .then(() => {
        getList();
        proxy.$modal.msgSuccess(`${text}成功`);
      })
      .catch(() => {
        previewListResource.value = previewListResource.value !== true;
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  });
}
/** 删除按钮操作 */
function handleDelete(row?: SysOssVo) {
  const ossIds = row?.ossId || ids.value;
  proxy.$modal.confirm(`是否确认删除OSS对象存储编号为"${ossIds}"的数据项?`, () => {
    const msgLoading = proxy.$modal.msgLoading('正在删除中...');
    return delOss(ossIds)
      .then(() => {
        ids.value = ArrayOps.fastDeleteElement(ids.value, ossIds);
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .finally(() => proxy.$modal.msgClose(msgLoading));
  });
}

getList();
</script>
