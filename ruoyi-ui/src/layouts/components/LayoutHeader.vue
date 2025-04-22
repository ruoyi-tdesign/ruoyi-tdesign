<template>
  <l-header
    v-if="settingStore.showHeader"
    :show-logo="settingStore.showHeaderLogo"
    :theme="settingStore.displayMode"
    :layout="settingStore.layout"
    :is-fixed="settingStore.isHeaderFixed"
    :menu="headerMenu"
    :is-compact="settingStore.isSidebarCompact"
  />
</template>

<script lang="ts" setup>
import { storeToRefs } from 'pinia';
import { computed } from 'vue';

import { usePermissionStore, useSettingStore } from '@/store';
import type { ComplexRoute } from '@/types/interface';

import LHeader from './Header.vue';

const permissionStore = usePermissionStore();
const settingStore = useSettingStore();
const { menus: menuRouters } = storeToRefs(permissionStore);
const headerMenu = computed<ComplexRoute[]>(() => {
  if (settingStore.layout === 'mix') {
    if (settingStore.splitMenu) {
      return menuRouters.value.map((menu: any) => {
        const newMenu: ComplexRoute = {
          query: undefined,
          ...menu,
          children: [],
        };
        // 这里设置子路由的参数
        if (menu.children && menu.children.length > 0) {
          newMenu.query = (menu.children[0] as any).query;
        }
        return newMenu;
      });
    }
    return [];
  }
  return menuRouters.value as ComplexRoute[];
});
</script>
