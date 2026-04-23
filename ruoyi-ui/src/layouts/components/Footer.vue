<template>
  <div :class="`${prefix}-footer`">{{ t('common.copyright') }}</div>
</template>
<script lang="ts" setup>
import { prefix } from '@/config/global';
import { t } from '@/locales';
</script>
<style lang="less" scoped>
.@{starter-prefix}-footer {
  color: var(--td-text-color-placeholder);
  line-height: 20px;
  text-align: center;
}
</style>
