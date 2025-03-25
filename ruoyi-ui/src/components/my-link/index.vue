<template>
  <t-link :size="size" :theme="theme" :hover="hover" v-bind="$attrs">
    <template #prefix-icon><slot name="prefix-icon" /></template><slot />
  </t-link>
</template>

<script lang="ts" setup>
import type { LinkProps } from 'tdesign-vue-next';

defineOptions({
  name: 'MyLink',
});

defineProps({
  size: {
    type: String as PropType<LinkProps['size']>,
    default: 'small',
  },
  theme: {
    type: String as PropType<LinkProps['theme']>,
    default: 'primary',
  },
  hover: {
    type: String as PropType<LinkProps['hover']>,
    default: 'color',
  },
  download: {
    type: Boolean,
    default: false,
  },
  href: {
    type: String,
    default: '',
  },
  target: {
    type: String,
  },
  underline: {
    type: Boolean,
    default: true,
  },
});
</script>
