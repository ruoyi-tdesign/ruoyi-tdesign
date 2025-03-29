<template>
  <div ref="container" class="w-full h-[calc(100vh-88px)]">
    <iframe ref="iframe" :src="iframeUrl" frameborder="0" height="100%" style="height: 100%; width: inherit"></iframe>
  </div>
</template>

<script lang="ts" setup>
defineOptions({
  name: 'WarmFlow',
});
import { onMounted } from 'vue';

import { useTabsRouterStore, useUserStore } from '@/store';

const route = useRoute();
const removeCurrentTab = useTabsRouterStore().useRemoveCurrentTab();

// definitionId为需要查询的流程定义id，
// disabled为是否可编辑, 例如：查看的时候不可编辑，不可保存
const iframeUrl = ref('');
const baseUrl = import.meta.env.VITE_APP_BASE_API;
const iframeLoaded = () => {
  // iframe监听组件内设计器保存事件
  window.onmessage = (event) => {
    if (event.data.method === 'close') {
      close();
    }
  };
};
const open = async (definitionId: string | number, disabled: boolean | string) => {
  const url = `${baseUrl}/warm-flow-ui/index.html?id=${definitionId}&disabled=${disabled}`;
  iframeUrl.value = `${url}&Authorization=Bearer ${useUserStore().token}&clientid=${import.meta.env.VITE_APP_CLIENT_ID}`;
};
/** 关闭按钮 */
function close() {
  removeCurrentTab('/workflow/processDefinition');
}

onMounted(() => {
  iframeLoaded();
  open(route.query.definitionId as string, route.query.disabled as string);
});
/**
 * 对外暴露子组件方法
 */
defineExpose({
  open,
});
</script>
