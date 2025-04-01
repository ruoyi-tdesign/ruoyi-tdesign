<template>
  <result title="404 Not Found" :tip="t('pages.result.404.subtitle')" type="404">
    <t-button @click="() => $router.push('/')">{{ t('pages.result.404.back') }}</t-button>
  </result>
</template>

<script lang="ts" setup>
defineOptions({
  name: 'Result404',
});
import Result from '@/components/result/index.vue';
import { t } from '@/locales';
</script>
