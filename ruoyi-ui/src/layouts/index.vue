<template>
  <t-watermark
    :watermark-content="watermarkContent"
    :y="180"
    :x="80"
    :width="Math.max(watermarkContent[0].text.length * 20, watermarkContent[1].text.length * 20)"
    :height="40"
    :z-index="1000"
    :alpha="0.5"
  >
    <div>
      <template v-if="setting.layout.value === 'side'">
        <t-layout key="side" :class="mainLayoutCls">
          <t-aside><layout-side-nav /></t-aside>
          <t-layout>
            <t-header><layout-header /></t-header>
            <t-content><layout-content /></t-content>
          </t-layout>
        </t-layout>
      </template>

      <template v-else>
        <t-layout key="no-side">
          <t-header><layout-header /> </t-header>
          <t-layout :class="mainLayoutCls">
            <layout-side-nav />
            <layout-content />
          </t-layout>
        </t-layout>
      </template>
      <setting-com />
    </div>
  </t-watermark>
</template>

<script lang="ts" setup>
import '@/style/layout.less';

import { storeToRefs } from 'pinia';
import { computed, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';

import { prefix } from '@/config/global';
import { useSettingStore, useTabsRouterStore, useUserStore } from '@/store';
import { dateFormat } from '@/utils/date';

import LayoutContent from './components/LayoutContent.vue';
import LayoutHeader from './components/LayoutHeader.vue';
import LayoutSideNav from './components/LayoutSideNav.vue';
import SettingCom from './setting.vue';

const route = useRoute();
const settingStore = useSettingStore();
const tabsRouterStore = useTabsRouterStore();
const setting = storeToRefs(settingStore);
const { name } = storeToRefs(useUserStore());

const mainLayoutCls = computed(() => [
  {
    't-layout--with-sider': settingStore.showSidebar,
  },
]);

const watermarkContent = computed(() => [
  {
    text: name.value,
    fontColor: 'rgba(174,174,174,0.5)',
  },
  {
    text: dateFormat(new Date(), 'YYYY-MM-DD HH:mm:ss'),
    fontColor: 'rgba(174,174,174,0.5)',
  },
]);

const appendNewRoute = () => {
  const {
    path,
    query,
    meta: { title },
    name,
  } = route;
  tabsRouterStore.appendTabRouterList({ path, query, title: title as string, name, isAlive: true, meta: route.meta });
};

onMounted(() => {
  appendNewRoute();
});

watch(
  () => route.path,
  () => {
    appendNewRoute();
    document.querySelector(`.${prefix}-layout`).scrollTo({ top: 0, behavior: 'smooth' });
  },
);
</script>

<style lang="less" scoped></style>
