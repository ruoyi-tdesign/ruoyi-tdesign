<template>
  <div>
    <t-table :data="devices" :columns="columns" :loading="loading" hover row-key="id" style="font-size: 14px">
      <template #deviceType="{ row }">
        <dict-tag :options="sys_device_type" :value="row.deviceType" />
      </template>
      <template #loginTime="{ row }">{{ parseTime(row.loginTime) }}</template>
      <template #operation="{ row }">
        <t-link size="small" theme="danger" hover="color" @click.stop="handleDelOnline(row)">
          <template #prefix-icon><delete-icon /></template>删除
        </t-link>
      </template>
    </t-table>
  </div>
</template>

<script lang="ts" setup>
import type { PrimaryTableCol } from 'tdesign-vue-next';

defineOptions({
  name: 'OnlineDevice',
});

import { DeleteIcon } from 'tdesign-icons-vue-next';

import type { SysUserOnline } from '@/api/monitor/model/userOnlineModel';
import { delOnline, getOnline } from '@/api/monitor/online';

const { proxy } = getCurrentInstance();
const { sys_device_type } = toRefs<any>(proxy?.useDict('sys_device_type'));

const devices = ref<SysUserOnline[]>([]);
const loading = ref(false);

// 列显隐信息
const columns = ref<Array<PrimaryTableCol>>([
  { title: `设备类型`, colKey: 'deviceType', align: 'center' },
  { title: `主机`, colKey: 'ipaddr', align: 'center', ellipsis: true },
  { title: `登录地点`, colKey: 'loginLocation', align: 'center', ellipsis: true },
  { title: `操作系统`, colKey: 'os', align: 'center', ellipsis: true },
  { title: `浏览器`, colKey: 'browser', align: 'center', ellipsis: true },
  { title: `登录时间`, colKey: 'loginTime', align: 'center', width: '10%', minWidth: 112 },
  { title: `操作`, colKey: 'operation', align: 'center' },
]);

const getOnlineList = async () => {
  loading.value = true;
  const res = await getOnline();
  loading.value = false;
  devices.value = res.rows;
};

/** 删除按钮操作 */
const handleDelOnline = (row: any) => {
  proxy.$modal.confirm('删除设备后，在该设备登录需要重新进行验证', () => {
    return delOnline(row.tokenId).then((res) => {
      if (res.code === 200) {
        proxy?.$modal.msgSuccess('删除成功');
      } else {
        proxy?.$modal.msgError(res.msg);
      }
    });
  });
};

onMounted(() => {
  getOnlineList();
});
</script>
