<template>
  <div v-if="layout === 'side'" class="header-menu-search">
    <t-auto-complete
      v-model="searchData"
      :class="['header-search', { 'hover-active': isSearchFocus }]"
      :placeholder="t('layout.searchPlaceholder')"
      :options="options"
      :filterable="false"
      :popup-props="{
        overlayInnerClassName: ['search-popup'],
      }"
      @blur="changeSearchFocus(false)"
      @focus="changeSearchFocus(true)"
      @select="handleClick"
    >
      <template #prefix-icon>
        <search-icon class="icon" size="16" />
      </template>
      <template #option="{ option }">
        <div class="t-select-option__highlight-item" v-html="option.label" />
      </template>
    </t-auto-complete>
  </div>

  <div v-else class="header-menu-search-left">
    <t-button
      :class="{ 'search-icon-hide': isSearchFocus }"
      theme="default"
      shape="square"
      variant="text"
      @click="changeSearchFocus(true)"
    >
      <search-icon />
    </t-button>
    <t-auto-complete
      v-model="searchData"
      :class="['header-search', { 'width-zero': !isSearchFocus }]"
      :placeholder="t('layout.searchPlaceholder')"
      :autofocus="isSearchFocus"
      :options="options"
      :filterable="false"
      :popup-props="{
        overlayInnerClassName: ['search-popup'],
      }"
      @blur="changeSearchFocus(false)"
      @select="handleClick"
    >
      <template #prefix-icon>
        <search-icon size="16" />
      </template>
      <template #option="{ option }">
        <div class="t-select-option__highlight-item" v-html="option.label" />
      </template>
    </t-auto-complete>
  </div>
</template>

<script lang="ts" setup>
import Fuse from 'fuse.js';
import { storeToRefs } from 'pinia';
import { SearchIcon } from 'tdesign-icons-vue-next';
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

import { t } from '@/locales';
import { usePermissionStore } from '@/store';
import type { MenuRoute } from '@/types/interface';

defineProps({
  layout: String,
});

interface SearchOption {
  text: string;
  value: string;
  label: string;
}

const permissionStore = usePermissionStore();
const { defaultRoutes: menuRouters } = storeToRefs(permissionStore);
const router = useRouter();

const isSearchFocus = ref(false);
const searchData = ref('');
const changeSearchFocus = (value: boolean) => {
  isSearchFocus.value = value;
};
const menus = computed<SearchOption[]>(() => {
  const newMenuRouters = menuRouters.value as Array<MenuRoute>;
  return getLeftMenus(newMenuRouters)
    .filter((value) => value?.title && !value?.meta?.hidden)
    .map((value) => ({ text: value.title, value: value.path, label: value.title }));
});

const fuse = computed(() => {
  const options = {
    includeScore: true,
    threshold: 0.7,
    fieldNormWeight: 0.5,
    includeMatches: true,
    keys: ['label'],
  };
  return new Fuse(menus.value, options);
});

const options = computed(() => {
  if (!searchData.value) {
    return [];
  }
  const search = fuse.value.search(searchData.value);
  return search.map((value) => {
    const item: SearchOption = { ...value.item };
    value.matches.forEach((value1) => {
      const key = value1.key as keyof SearchOption;
      value1.indices.toReversed().forEach((value2) => {
        item[key] = replaceWord(item[key], value2[0], value2[1] + 1, (sub) => `<b class="t-is-highlight">${sub}</b>`);
      });
    });
    return item;
  });
});

/**
 * 字符串替换
 * @param str 字符串
 * @param startIndex 起始下标
 * @param endIndex 结束下标（不包含）
 * @param newWord 替换字符串
 */
function replaceWord(str: string, startIndex: number, endIndex: number, newWord: (sub: string) => string) {
  const s1 = str.substring(0, startIndex);
  const s2 = str.substring(startIndex, endIndex);
  const s3 = str.substring(endIndex);
  return s1 + newWord(s2) + s3;
}

// 处理跳转
function handleClick(text: string) {
  const option = menus.value.find((menu) => menu.text === text);
  router.push({ path: option.value, force: true }).then(() => {
    searchData.value = '';
  });
}

/** 获取叶子节点的菜单结构 */
function getLeftMenus(menus: Array<MenuRoute>, parent?: MenuRoute): MenuRoute[] {
  return menus.flatMap((value) => {
    // 浅克隆对象
    value = { ...value };
    function getTitle(title: string) {
      if (title && parent?.title) {
        return `${parent.title} / ${title}`;
      }
      return title;
    }
    if (value?.meta?.title) {
      value.title = getTitle(value.meta.title);
    }
    value.path = parent?.path && !value.path.startsWith('http') ? `${parent?.path}/${value.path}` : value.path;
    if (value.children && value.children.length > 0) {
      return getLeftMenus(value.children, value);
    }
    return value;
  });
}
</script>
<style lang="less" scoped>
:global(.search-popup) {
  width: auto !important;
}

.header-menu-search {
  display: flex;
  margin-left: 16px;

  .hover-active {
    background: var(--td-bg-color-secondarycontainer);
  }

  .t-icon {
    color: var(--td-text-color-primary) !important;
  }

  .header-search {
    :deep(.t-input) {
      border: none;
      outline: none;
      box-shadow: none;
      transition: background @anim-duration-base linear;

      .t-input__inner {
        transition: background @anim-duration-base linear;
        background: none;
      }

      &:hover {
        background: var(--td-bg-color-secondarycontainer);

        .t-input__inner {
          background: var(--td-bg-color-secondarycontainer);
        }
      }
    }
  }
}

.t-button {
  margin: 0 8px;
  transition: opacity @anim-duration-base @anim-time-fn-easing;

  .t-icon {
    font-size: 20px;

    &.general {
      margin-right: 16px;
    }
  }
}

.search-icon-hide {
  opacity: 0;
}

.header-menu-search-left {
  display: flex;
  align-items: center;

  .header-search {
    width: 200px;
    transition: width @anim-duration-base @anim-time-fn-easing;

    :deep(.t-input) {
      border: 0;

      &:focus {
        box-shadow: none;
      }
    }

    &.width-zero {
      width: 0;
      opacity: 0;
    }
  }
}
</style>
