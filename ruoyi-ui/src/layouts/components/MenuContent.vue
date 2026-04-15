<template>
  <template v-for="item in list" :key="item.path">
    <template v-if="!item.children || !item.children.length || item.meta?.single">
      <t-menu-item v-if="getHref(item)" :value="item.path" @click="openHref(getHref(item)[0])">
        <template #icon>
          <component :is="menuIcon(item)" class="t-icon"></component>
        </template>
        {{ item.title }}
      </t-menu-item>
      <t-menu-item v-else :value="item.path" :to="item">
        <template #icon>
          <component :is="menuIcon(item)" class="t-icon"></component>
        </template>
        {{ item.title }}
      </t-menu-item>
    </template>
    <t-submenu v-else :value="item.path" :title="item.meta?.title">
      <template #icon>
        <component :is="menuIcon(item)" class="t-icon"></component>
      </template>
      <menu-content v-if="item.children" :nav-data="item.children" />
    </t-submenu>
  </template>
</template>
<script setup lang="tsx">
import type { PropType } from 'vue';
import { computed } from 'vue';

import RIcon from '@/components/r-icon/index.vue';
import type { ComplexRoute, MenuRoute } from '@/types/interface';

type ListItemType = MenuRoute & { icon?: string };

const props = defineProps({
  navData: {
    type: Array as PropType<ComplexRoute[]>,
    default: () => [] as ComplexRoute[],
  },
});

const list = computed(() => {
  const { navData } = props;
  return getMenuList(navData);
});

const menuIcon = (item: ListItemType) => {
  if (typeof item.icon === 'string') {
    return <RIcon name={item.icon} style={{ width: '18px', height: '18px' }} />;
  }
  return item.icon;
};

const getMenuList = (list: ComplexRoute[], basePath?: string): ListItemType[] => {
  if (!list || list.length === 0) {
    return [];
  }
  // 如果meta中有orderNo则按照从小到大排序
  list.sort((a, b) => {
    return (a.meta?.orderNo || 0) - (b.meta?.orderNo || 0);
  });
  return list
    .map((item) => {
      const path = basePath && !item.path?.includes(basePath) ? `${basePath}/${item.path}` : item.path;

      return {
        path,
        title: item.meta?.title,
        icon: item.meta?.icon,
        children: getMenuList(item.children, path),
        meta: item.meta,
        redirect: item.redirect,
        query: item.query,
      } as ListItemType;
    })
    .filter((item) => item.meta && item.meta.hidden !== true);
};

const regExp = /(http|https):\/\/([\w.]+\/?)\S*/;
const getHref = (item: MenuRoute) => {
  const { path } = item;
  const { frameSrc, frameBlank } = item.meta;
  if (frameSrc && frameBlank) {
    return frameSrc.match(regExp);
  }
  return path.match(regExp);
};

const openHref = (url: string) => {
  window.open(url);
};
</script>
