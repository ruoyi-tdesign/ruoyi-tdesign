<template>
  <div :class="layoutCls">
    <t-head-menu :class="menuCls" :theme="menuTheme" expand-type="popup" :value="active">
      <template #logo>
        <span v-if="showLogo" class="header-logo-container" @click="handleNav('/')">
          <logo-full class="t-logo" />
        </span>
        <div v-else class="header-operate-left">
          <t-button theme="default" shape="square" variant="text" @click="changeCollapsed">
            <view-list-icon class="collapsed-icon" />
          </t-button>
          <search :layout="layout" />
        </div>
      </template>
      <template v-if="layout !== 'side'" #default>
        <menu-content class="header-menu" :nav-data="menu" />
      </template>
      <template #operations>
        <div class="operations-container">
          <!-- 搜索框 -->
          <search v-if="layout !== 'side'" :layout="layout" />

          <!-- 选择租户 -->
          <dynamic-tenant @dynamic-change="(value) => (dynamic = value)" />

          <!-- 全局通知 -->
          <notice />

          <!-- github -->
          <t-tooltip placement="bottom" :content="t('layout.header.code')">
            <t-button theme="default" shape="square" variant="text" @click="navToGitHub">
              <logo-github-icon />
            </t-button>
          </t-tooltip>
          <!-- 帮助 -->
          <t-tooltip placement="bottom" :content="t('layout.header.help')">
            <t-button theme="default" shape="square" variant="text" @click="navToHelper">
              <help-circle-icon />
            </t-button>
          </t-tooltip>
          <!-- 多语言 -->
          <translate-select />
          <!-- 用户头像、下拉选项 -->
          <t-dropdown :min-column-width="120" :max-column-width="180" trigger="click">
            <template #dropdown>
              <t-dropdown-menu>
                <t-dropdown-item
                  v-if="!dynamic"
                  class="operations-dropdown-container-item"
                  @click="handleNav('/profile/index')"
                >
                  <user-circle-icon />{{ t('layout.header.user') }}
                </t-dropdown-item>
                <t-dropdown-item class="operations-dropdown-container-item" @click="openModifyPassword = true">
                  <secured-icon />{{ t('layout.header.modifyPassword') }}
                </t-dropdown-item>
                <t-dropdown-item class="operations-dropdown-container-item" @click="handleLogout">
                  <poweroff-icon />{{ t('layout.header.signOut') }}
                </t-dropdown-item>
              </t-dropdown-menu>
            </template>
            <!-- 用户头像 -->
            <t-button class="header-user-btn" theme="default" variant="text">
              <template #icon>
                <t-avatar
                  shape="round"
                  size="small"
                  style="margin-right: var(--td-size-2); color: var(--td-text-color-primary); background: transparent"
                  :image="userStore.avatar"
                  hide-on-load-failed
                  :alt="userStore.name"
                >
                  <template #icon><user-circle-icon class="header-user-avatar" /></template>
                </t-avatar>
              </template>
              <div class="header-user-account">{{ userStore.name }}</div>
              <template #suffix><chevron-down-icon /></template>
            </t-button>
          </t-dropdown>
          <t-tooltip placement="bottom" :content="t('layout.header.setting')">
            <t-button theme="default" shape="square" variant="text" @click="toggleSettingPanel">
              <setting-icon />
            </t-button>
          </t-tooltip>
        </div>
      </template>
    </t-head-menu>
    <modify-password v-model:visible="openModifyPassword" />
  </div>
</template>

<script lang="ts" setup>
import {
  ChevronDownIcon,
  HelpCircleIcon,
  LogoGithubIcon,
  PoweroffIcon,
  SecuredIcon,
  SettingIcon,
  UserCircleIcon,
  ViewListIcon,
} from 'tdesign-icons-vue-next';
import type { PropType } from 'vue';
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import LogoFull from '@/assets/icons/assets-logo-full.svg?component';
import TranslateSelect from '@/components/translate-select/index.vue';
import { prefix } from '@/config/global';
import ModifyPassword from '@/layouts/components/ModifyPassword.vue';
import { t } from '@/locales';
import { getActive } from '@/router';
import { useSettingStore, useUserStore } from '@/store';
import type { ComplexRoute, ModeType } from '@/types/interface';

import DynamicTenant from './DynamicTenant.vue';
import MenuContent from './MenuContent.vue';
import Notice from './Notice.vue';
import Search from './Search.vue';

const userStore = useUserStore();

const props = defineProps({
  theme: {
    type: String,
    default: 'light',
  },
  layout: {
    type: String,
    default: 'top',
  },
  showLogo: {
    type: Boolean,
    default: true,
  },
  menu: {
    type: Array as PropType<ComplexRoute[]>,
    default: () => [],
  },
  isFixed: {
    type: Boolean,
    default: false,
  },
  isCompact: {
    type: Boolean,
    default: false,
  },
  maxLevel: {
    type: Number,
    default: 3,
  },
});

const openModifyPassword = ref(false);
const router = useRouter();
const route = useRoute();
const settingStore = useSettingStore();
// 是否切换了租户
const dynamic = ref(false);

const toggleSettingPanel = () => {
  settingStore.updateConfig({
    showSettingPanel: true,
  });
};

// const active = computed(() => getActive());
const active = computed(() => {
  const { path, meta } = route;
  const effectivePath = meta.activeMenu ?? path;
  if (settingStore.layout === 'mix') {
    if (settingStore.splitMenu) {
      if (effectivePath && effectivePath.lastIndexOf('/') > 0) {
        const tmpPath = effectivePath.substring(1, effectivePath.length);
        return `/${tmpPath.substring(0, tmpPath.indexOf('/'))}`;
      }
    }
  }
  return getActive(effectivePath);
});

const layoutCls = computed(() => [`${prefix}-header-layout`]);

const menuCls = computed(() => {
  const { isFixed, layout, isCompact } = props;
  return [
    {
      [`${prefix}-header-menu`]: !isFixed,
      [`${prefix}-header-menu-fixed`]: isFixed,
      [`${prefix}-header-menu-fixed-side`]: layout === 'side' && isFixed,
      [`${prefix}-header-menu-fixed-side-compact`]: layout === 'side' && isFixed && isCompact,
    },
  ];
});
const menuTheme = computed(() => props.theme as ModeType);
const changeCollapsed = () => {
  settingStore.updateConfig({
    isSidebarCompact: !settingStore.isSidebarCompact,
  });
};

const handleNav = (url: string) => {
  router.push(url);
};

const handleLogout = async () => {
  const userStore = useUserStore();
  await userStore.logout();
  await router.push({
    path: '/login',
    query: { redirect: encodeURIComponent(router.currentRoute.value.fullPath) },
  });
};

const navToGitHub = () => {
  window.open('https://github.com/tencent/tdesign-vue-next-starter');
};

const navToHelper = () => {
  window.open('http://tdesign.tencent.com/starter/docs/get-started');
};
</script>
<style lang="less" scoped>
.@{starter-prefix}-header {
  &-menu-fixed {
    position: fixed;
    top: 0;
    z-index: 1001;

    :deep(.t-head-menu__inner) {
      padding-right: var(--td-comp-margin-xl);
    }

    &-side {
      left: 232px;
      right: 0;
      z-index: 10;
      width: auto;
      transition: all 0.3s;

      &-compact {
        left: 64px;
      }
    }
  }

  &-logo-container {
    cursor: pointer;
    display: inline-flex;
  }
}

// 菜单溢出时，显示滚动条
.@{starter-prefix}-header-menu-fixed {
  :deep(.t-menu) {
    overflow-x: auto;
    overflow-y: hidden;

    @thumbThickness: 4px;
    @trackThickness: 4px;

    &::-webkit-scrollbar {
      width: @trackThickness; /* 滚动条的宽度 */
      height: @trackThickness; /* 横向滚动条的高度 */
      background-color: transparent;
    }

    /* 滚动条轨道 */
    &::-webkit-scrollbar-track {
      background-color: var(--td-scroll-track-color); /* 轨道的背景色 */
      border-radius: @trackThickness; /* 轨道的圆角 */
    }

    &::-webkit-scrollbar-track:hover {
      background-color: var(--td-scroll-track-color); /* 轨道的背景色 */
    }

    &::-webkit-scrollbar-corner {
      width: 0;
    }

    /* 滚动条滑块 */
    &::-webkit-scrollbar-thumb {
      border-radius: calc(@trackThickness / 2); /* 滑块的圆角 */
      border: calc((@trackThickness - @thumbThickness) / 2) solid transparent;
      background-clip: padding-box;
      background-color: var(--td-scrollbar-color); /* 滑块的背景色 */
    }

    /* 滑块在悬停时的样式 */
    &::-webkit-scrollbar-thumb:hover {
      background-color: var(--td-scrollbar-hover-color); /* 滑块悬停时的背景色 */
    }
  }
}

.header-menu {
  flex: 1 1 1;
  display: inline-flex;

  :deep(.t-menu__item) {
    min-width: unset;
  }
}

.operations-container {
  display: flex;
  align-items: center;

  .t-popup__reference {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .t-button {
    margin-left: var(--td-comp-margin-l);
  }
}

.header-operate-left {
  display: flex;
  align-items: normal;
  line-height: 0;
}

.header-logo-container {
  width: 184px;
  height: 26px;
  display: flex;
  margin-left: 24px;
  color: var(--td-text-color-primary);

  .t-logo {
    width: 100%;
    height: 100%;

    &:hover {
      cursor: pointer;
    }
  }

  &:hover {
    cursor: pointer;
  }
}

.header-user-account {
  display: inline-flex;
  align-items: center;
  color: var(--td-text-color-primary);
}

:deep(.t-head-menu__inner) {
  border-bottom: 1px solid var(--td-component-stroke);
}

.t-menu--light {
  .header-user-account {
    color: var(--td-text-color-primary);
  }
}

.t-menu--dark {
  .t-head-menu__inner {
    border-bottom: 1px solid var(--td-gray-color-10);
  }

  .header-user-account {
    color: rgb(255 255 255 / 55%);
  }
}

.operations-dropdown-container-item {
  width: 100%;
  display: flex;
  align-items: center;

  :deep(.t-dropdown__item-text) {
    display: flex;
    align-items: center;
  }

  .t-icon {
    font-size: var(--td-comp-size-xxxs);
    margin-right: var(--td-comp-margin-s);
  }

  :deep(.t-dropdown__item) {
    width: 100%;
    margin-bottom: 0;
  }

  &:last-child {
    :deep(.t-dropdown__item) {
      margin-bottom: 8px;
    }
  }
}
</style>

<!-- eslint-disable-next-line vue-scoped-css/enforce-style-type -->
<style lang="less">
.operations-dropdown-container-item {
  .t-dropdown__item-text {
    display: flex;
    align-items: center;
  }
}
</style>
