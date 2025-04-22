<template>
  <div>
    <i-frame v-model:src="url"></i-frame>
  </div>
</template>

<script lang="ts" setup>
defineOptions({
  name: 'Admin',
});
import { ref } from 'vue';

import IFrame from '@/components/i-frame/index.vue';

const url = ref(import.meta.env.VITE_APP_MONITOR_ADMIN);
</script>
