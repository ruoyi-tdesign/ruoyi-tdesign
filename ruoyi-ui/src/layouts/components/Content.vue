<template>
  <router-view v-if="!isRefreshing" v-slot="{ Component }">
    <transition name="fade" mode="out-in">
      <keep-alive :include="aliveViews">
        <component :is="Component" :key="getComponentKey(Component)" />
      </keep-alive>
    </transition>
  </router-view>
  <frame-page />
</template>

<script lang="ts" setup>
import isBoolean from 'lodash/isBoolean';
import isUndefined from 'lodash/isUndefined';
import type { ComputedRef, VNode } from 'vue';
import { computed } from 'vue';
import type { RouteLocationNormalizedLoaded } from 'vue-router';
import { useRoute } from 'vue-router';

import FramePage from '@/layouts/frame/index.vue';
import { useTabsRouterStore } from '@/store';
import { MD5 } from '@/utils/crypto';

// <suspense>标签属于实验性功能，请谨慎使用
// 如果存在需解决/page/1=> /page/2 刷新数据问题 请修改代码 使用activeRouteFullPath 作为key
// <suspense>
//  <component :is="Component" :key="activeRouteFullPath" />
// </suspense>

const route = useRoute();

function getKey(key: string | ((route: RouteLocationNormalizedLoaded) => string)) {
  if (typeof key === 'function') {
    return key(route);
  }
  return key;
}

function getComponentKey(component?: VNode) {
  if (route.meta?.key) {
    return getKey(route.meta?.key);
  }
  return route.meta?.menuId ? route.meta?.menuId : (component?.key?.toString().concat('-') ?? '') + MD5(route.path);
}

const aliveViews = computed(() => {
  const tabsRouterStore = useTabsRouterStore();
  const { tabRouters } = tabsRouterStore;
  return tabRouters
    .filter((tabRoute) => {
      const keepAliveConfig = tabRoute.meta?.keepAlive ?? !tabRoute.meta?.noCache;
      const isRouteKeepAlive = isUndefined(keepAliveConfig) || (isBoolean(keepAliveConfig) && keepAliveConfig); // 默认开启keepalive
      return tabRoute.isAlive && isRouteKeepAlive;
    })
    .map((tabRoute) => (tabRoute.meta?.componentName ? tabRoute.meta?.componentName : tabRoute.name));
}) as ComputedRef<string[]>;

const isRefreshing = computed(() => {
  const tabsRouterStore = useTabsRouterStore();
  const { refreshing } = tabsRouterStore;
  return refreshing;
});
</script>
<style lang="less" scoped>
.fade-leave-active,
.fade-enter-active {
  transition: opacity @anim-duration-slow @anim-time-fn-easing;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
