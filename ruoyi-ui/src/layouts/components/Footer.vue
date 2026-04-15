<template>
  <div :class="`${prefix}-footer`">
    Copyright © 2021-{{ new Date().getFullYear() }} {{ companyName }} All Rights Reserved
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue';

import { prefix } from '@/config/global';

const companyName = ref(import.meta.env.VITE_APP_COMPANY_NAME);
</script>
<style lang="less" scoped>
.@{starter-prefix}-footer {
  color: var(--td-text-color-placeholder);
  line-height: 20px;
  text-align: center;
}
</style>
