<template>
  <div class="container">
    <t-dialog
      v-model:visible="visible"
      header="审批记录"
      :width="props.width"
      :height="props.height"
      placement="center"
      :close-on-overlay-click="false"
    >
      <t-tabs v-model="tabActiveName" class="demo-tabs">
        <t-tab-panel label="流程图" value="image" style="height: 68vh">
          <t-loading :loading="loading">
            <flow-chart v-if="insId" :ins-id="insId" />
          </t-loading>
        </t-tab-panel>
        <t-tab-panel label="审批信息" value="info">
          <t-loading :loading="loading">
            <t-table :data="historyList" row-key="id" :columns="columns" style="width: 100%">
              <template #approveName="{ row }">
                <template v-if="row.approveName">
                  <t-tag
                    v-for="(item, index) in row.approveName.split(',')"
                    :key="index"
                    theme="success"
                    variant="light"
                  >
                    {{ item }}
                  </t-tag>
                </template>
                <template v-else> <t-tag type="success" variant="light">无</t-tag></template>
              </template>
              <template #flowStatus="{ row }">
                <dict-tag :options="wf_task_status" :value="row.flowStatus"></dict-tag>
              </template>
              <template #attachmentList="{ row }">
                <t-popup
                  v-if="row.attachmentList && row.attachmentList.length > 0"
                  placement="right"
                  :overlay-style="{ width: '310px' }"
                  trigger="click"
                >
                  <t-button style="margin-right: 16px">附件</t-button>
                  <template #content>
                    <t-table :data="row.attachmentList" :columns="attachmentListColumns">
                      <template #name="scope">
                        <t-button variant="text" @click="handleDownload(scope.row.ossId)">下载</t-button>
                      </template>
                    </t-table>
                  </template>
                </t-popup>
              </template>
            </t-table>
          </t-loading>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>
<script lang="ts" setup>
import type { PrimaryTableCol } from 'tdesign-vue-next';
import { ref } from 'vue';

import { listByIds } from '@/api/system/oss';
import { flowHisTaskList } from '@/api/workflow/instance';
import FlowChart from '@/components/Process/flowChart.vue';

const props = defineProps({
  width: {
    type: String,
    default: '80%',
  },
  height: {
    type: String,
    default: '100%',
  },
});
const { proxy } = getCurrentInstance();
const { wf_task_status } = proxy.useDict('wf_task_status');
const loading = ref(false);
const visible = ref(false);
const historyList = ref<Array<any>>([]);
const tabActiveName = ref('image');
const insId = ref(null);

// 列显隐信息
const columns = computed<Array<PrimaryTableCol>>(() => [
  { title: `序号`, colKey: 'serial-number', width: 70 },
  { title: `任务名称`, colKey: 'nodeName', align: 'center' },
  { title: `办理人`, colKey: 'approveName', align: 'center', ellipsis: true },
  { title: `状态`, colKey: 'flowStatus', align: 'center', width: 80 },
  { title: `审批意见`, colKey: 'message', align: 'center', ellipsis: true },
  { title: `开始时间`, colKey: 'createTime', align: 'center', width: '10%', minWidth: 112 },
  { title: `结束时间`, colKey: 'updateTime', align: 'center', width: '10%', minWidth: 112 },
  { title: `运行时长`, colKey: 'runDuration', align: 'center' },
  { title: `附件`, colKey: 'attachmentList', align: 'center' },
]);
// 列显隐信息
const attachmentListColumns = computed<Array<PrimaryTableCol>>(() => [
  { title: `附件名称`, colKey: 'originalName', align: 'center', width: 202, ellipsis: true },
  { title: `操作`, colKey: 'name', align: 'center', width: 80 },
]);

// 初始化查询审批记录
const init = async (businessId: string | number) => {
  visible.value = true;
  loading.value = true;
  tabActiveName.value = 'image';
  historyList.value = [];
  flowHisTaskList(businessId).then((resp) => {
    if (resp.data) {
      historyList.value = resp.data.list;
      insId.value = resp.data.instanceId;
      if (historyList.value.length > 0) {
        historyList.value.forEach((item) => {
          if (item.ext) {
            getIds(item.ext).then((res) => {
              item.attachmentList = res.data;
            });
          } else {
            item.attachmentList = [];
          }
        });
      }
      loading.value = false;
    }
  });
};
const getIds = async (ids: string | string[]) => {
  return listByIds(ids);
};

/** 下载按钮操作 */
const handleDownload = (ossId: string) => {
  proxy?.$download.oss(ossId);
};

/**
 * 对外暴露子组件方法
 */
defineExpose({
  init,
});
</script>
<style lang="less" scoped>
.container {
  :deep(.t-dialog__ctx .t-dialog__body) {
    max-height: calc(100vh - 170px) !important;
    min-height: calc(100vh - 170px) !important;
  }
}
</style>
