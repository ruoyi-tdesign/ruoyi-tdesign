<template>
  <t-card>
    <t-row :gutter="[20, 20]">
      <t-col :span="12">
        <t-card hover-shadow>
          <template #header>
            <t-space :size="4"><desktop-icon />基本信息</t-space>
          </template>
          <t-form layout="inline">
            <t-row>
              <t-col :span="3">
                <t-form-item label="Redis版本">
                  <div v-if="cache.info">{{ cache.info.redis_version }}</div>
                </t-form-item>
              </t-col>
              <t-col :span="3">
                <t-form-item label="运行模式">
                  <div v-if="cache.info">{{ cache.info.redis_mode === 'standalone' ? '单机' : '集群' }}</div>
                </t-form-item>
              </t-col>
              <t-col :span="3">
                <t-form-item label="端口">
                  <div v-if="cache.info">{{ cache.info.tcp_port }}</div>
                </t-form-item>
              </t-col>
              <t-col :span="3">
                <t-form-item label="客户端数">
                  <div v-if="cache.info">{{ cache.info.connected_clients }}</div>
                </t-form-item>
              </t-col>
              <t-col :span="3">
                <t-form-item label="运行时间(天)">
                  <div v-if="cache.info">{{ cache.info.uptime_in_days }}</div>
                </t-form-item>
              </t-col>
              <t-col :span="3">
                <t-form-item label="使用内存">
                  <div v-if="cache.info">{{ cache.info.used_memory_human }}</div>
                </t-form-item>
              </t-col>
              <t-col :span="3">
                <t-form-item label="使用CPU">
                  <div v-if="cache.info">{{ parseFloat(cache.info.used_cpu_user_children).toFixed(2) }}</div>
                </t-form-item>
              </t-col>
              <t-col :span="3">
                <t-form-item label="内存配置">
                  <div v-if="cache.info">{{ cache.info.maxmemory_human }}</div>
                </t-form-item>
              </t-col>
              <t-col :span="3">
                <t-form-item label="AOF是否开启">
                  <div v-if="cache.info">{{ cache.info.aof_enabled === '0' ? '否' : '是' }}</div>
                </t-form-item>
              </t-col>
              <t-col :span="3">
                <t-form-item label="RDB是否成功">
                  <div v-if="cache.info">{{ cache.info.rdb_last_bgsave_status }}</div>
                </t-form-item>
              </t-col>
              <t-col :span="3">
                <t-form-item label="Key数量">
                  <div v-if="cache.dbSize">{{ cache.dbSize }}</div>
                </t-form-item>
              </t-col>
              <t-col :span="3">
                <t-form-item label="网络入口/出口">
                  <div v-if="cache.info">
                    {{ cache.info.instantaneous_input_kbps }}kps/{{ cache.info.instantaneous_output_kbps }}kps
                  </div>
                </t-form-item>
              </t-col>
            </t-row>
          </t-form>
        </t-card>
      </t-col>

      <t-col :span="6">
        <t-card hover-shadow>
          <template #header>
            <t-space :size="4"><chart-pie-icon />命令统计</t-space>
          </template>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <div ref="commandstats" style="height: 420px" />
          </div>
        </t-card>
      </t-col>

      <t-col :span="6">
        <t-card hover-shadow>
          <template #header>
            <t-space :size="4"><dashboard-icon />内存信息</t-space>
          </template>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <div ref="usedmemory" style="height: 420px" />
          </div>
        </t-card>
      </t-col>
    </t-row>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'Cache',
});
import type { ECharts } from 'echarts';
import * as echarts from 'echarts';
import { ChartPieIcon, DashboardIcon, DesktopIcon } from 'tdesign-icons-vue-next';
import { getCurrentInstance, onDeactivated, onMounted, ref } from 'vue';

import { getCache } from '@/api/monitor/cache';
import type { SysCacheInfo } from '@/api/monitor/model/cacheModel';

const cache = ref<SysCacheInfo>({});
const commandstats = ref<HTMLDivElement>();
const usedmemory = ref<HTMLDivElement>();
const { proxy } = getCurrentInstance();
const commandstatsIntance = ref<ECharts>();
const usedmemoryInstance = ref<ECharts>();

function getList() {
  proxy.$modal.loading('正在加载缓存监控数据，请稍候！');
  getCache().then((response) => {
    proxy.$modal.closeLoading();
    cache.value = response.data;

    commandstatsIntance.value = echarts.init(commandstats.value, 'macarons');
    commandstatsIntance.value.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)',
      },
      series: [
        {
          name: '命令',
          type: 'pie',
          roseType: 'radius',
          radius: [15, 95],
          center: ['50%', '38%'],
          data: response.data.commandStats,
          animationEasing: 'cubicInOut',
          animationDuration: 1000,
        },
      ],
    });

    usedmemoryInstance.value = echarts.init(usedmemory.value, 'macarons');
    usedmemoryInstance.value.setOption({
      tooltip: {
        formatter: `{b} <br/>{a} : ${cache.value.info.used_memory_human}`,
      },
      series: [
        {
          name: '峰值',
          type: 'gauge',
          min: 0,
          max: 1000,
          detail: {
            formatter: cache.value.info.used_memory_human,
          },
          data: [
            {
              value: Number.parseFloat(cache.value.info.used_memory_human),
              name: '内存消耗',
            },
          ],
        },
      ],
    });
  });
}

const listener = () => {
  commandstatsIntance.value.resize();
  usedmemoryInstance.value.resize();
};

onMounted(() => {
  window.addEventListener('resize', listener);
});

onDeactivated(() => {
  window.removeEventListener('resize', listener);
});

getList();
</script>
