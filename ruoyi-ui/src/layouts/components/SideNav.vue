<template>
  <div :class="sideNavCls">
    <t-menu
      v-model:expanded="expanded"
      :class="menuCls"
      :theme="theme"
      :value="activeMenu"
      :collapsed="collapsed"
      :expand-mutex="menuAutoCollapsed"
    >
      <template #logo>
        <span v-if="showLogo" :class="`${prefix}-side-nav-logo-wrapper`" @click="goHome">
          <component :is="getLogo()" :class="logoCls" />
        </span>
      </template>
      <menu-content :nav-data="menu" />
      <template #operations>
        <span :class="versionCls"> {{ !collapsed ? t('common.appName') : '' }} {{ pgk.version }} </span>
      </template>
    </t-menu>
    <div :class="`${prefix}-side-nav-placeholder${collapsed ? '-hidden' : ''}`"></div>
  </div>
</template>
<script lang="ts" setup>
import { union } from 'lodash';
import type { MenuValue } from 'tdesign-vue-next';
import type { PropType } from 'vue';
import { computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import AssetLogoFull from '@/assets/icons/assets-logo-full.svg?component'; // 全
import AssetLogo from '@/assets/icons/assets-t-logo.svg?component'; // 简
import { prefix } from '@/config/global';
import { t } from '@/locales';
import { getRoutesExpanded } from '@/router';
import { useSettingStore } from '@/store';
import type { MenuRoute, ModeType } from '@/types/interface';

import pgk from '../../../package.json';
import MenuContent from './MenuContent.vue';

const props = defineProps({
  menu: {
    type: Array as PropType<MenuRoute[]>,
    default: () => [] as MenuRoute[],
  },
  showLogo: {
    type: Boolean as PropType<boolean>,
    default: true,
  },
  isFixed: {
    type: Boolean as PropType<boolean>,
    default: true,
  },
  layout: {
    type: String as PropType<string>,
    default: '',
  },
  headerHeight: {
    type: String as PropType<string>,
    default: '64px',
  },
  theme: {
    type: String as PropType<ModeType>,
    default: 'light',
  },
  isCompact: {
    type: Boolean as PropType<boolean>,
    default: false,
  },
});

const MIN_POINT = 992 - 1;

const route = useRoute();

const activeMenu = computed(() => {
  const { meta, path } = route;
  // if set path, the sidebar will highlight the path you set
  if (meta.activeMenu) {
    return meta.activeMenu as string;
  }
  return path;
});

const collapsed = computed(() => useSettingStore().isSidebarCompact);
const menuAutoCollapsed = computed(() => useSettingStore().menuAutoCollapsed);

// const active = computed(() => getActive());

const defaultExpanded = () => {
  const path = activeMenu.value;
  const expandedParent: string[] = [];
  const filter = path.split('/').filter((item) => item);
  filter.forEach((value, index) => {
    if (index === 0) {
      expandedParent.push(`/${value}`);
    } else if (index < filter.length - 1) {
      expandedParent.push(`${expandedParent[index - 1]}/${value}`);
    }
  });
  const expanded = getRoutesExpanded();
  return union(expanded, expandedParent);
};

const expanded = ref<MenuValue[]>(defaultExpanded());

const sideMode = computed(() => {
  const { theme } = props;
  return theme === 'dark';
});
const sideNavCls = computed(() => {
  const { isCompact } = props;
  return [
    `${prefix}-sidebar-layout`,
    {
      [`${prefix}-sidebar-compact`]: isCompact,
    },
  ];
});
const logoCls = computed(() => {
  return [
    `${prefix}-side-nav-logo-${collapsed.value ? 't' : 'tdesign'}-logo`,
    {
      [`${prefix}-side-nav-dark`]: sideMode.value,
    },
  ];
});
const versionCls = computed(() => {
  return [
    `version-container`,
    {
      [`${prefix}-side-nav-dark`]: sideMode.value,
    },
  ];
});
const menuCls = computed(() => {
  const { showLogo, isFixed, layout } = props;
  return [
    `${prefix}-side-nav`,
    {
      [`${prefix}-side-nav-no-logo`]: !showLogo,
      [`${prefix}-side-nav-no-fixed`]: !isFixed,
      [`${prefix}-side-nav-mix-fixed`]: layout === 'mix' && isFixed,
    },
  ];
});

const router = useRouter();
const settingStore = useSettingStore();

const autoCollapsed = () => {
  const isCompact = window.innerWidth <= MIN_POINT;
  settingStore.updateConfig({
    isSidebarCompact: isCompact,
  });
};

onMounted(() => {
  autoCollapsed();
  window.onresize = () => {
    autoCollapsed();
  };
});

const goHome = () => {
  router.push('/');
};

const getLogo = () => {
  if (collapsed.value) return AssetLogo;
  return AssetLogoFull;
};
</script>
<style lang="less" scoped></style>
