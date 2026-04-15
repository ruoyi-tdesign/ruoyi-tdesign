<template>
  <t-loading :loading="loading" :style="`height:${height}`">
    <iframe :src="url" frameborder="no" style="width: 100%; height: 100%" scrolling="auto" />
  </t-loading>
</template>
<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';

const props = defineProps({
  src: {
    type: String,
    required: true,
  },
});

const height = ref(`${document.documentElement.clientHeight - 215}px;`);
const loading = ref(true);
const url = computed(() => props.src);

onMounted(() => {
  setTimeout(() => {
    loading.value = false;
  }, 300);
  window.onresize = function temp() {
    height.value = `${document.documentElement.clientHeight - 215}px;`;
  };
});
</script>
