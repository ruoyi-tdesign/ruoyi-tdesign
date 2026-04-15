<template>
  <pre :class="`language-${language}${isShowLineNumbers ? ' line-numbers' : ''}`">
<code ref="html" :class="`language-${ language}`" v-text="code" />
  </pre>
</template>
<script lang="ts" setup>
import Prism from 'prismjs';
import { onMounted, onUpdated, ref } from 'vue';

defineProps({
  // 代码
  code: {
    type: String,
    default: '',
  },
  // 语言
  language: {
    type: String,
    default: 'html',
  },
  // 是否显示行号
  isShowLineNumbers: {
    type: Boolean,
    default: true,
  },
});

const html = ref<HTMLElement>();

// 注册语言别名
function register() {
  // vue使用markup解析
  Prism.languages.vue = Prism.languages.markup;
}

onMounted(() => {
  register();
  Prism.highlightElement(html.value); // 切换菜单重新渲染
});

onUpdated(() => {
  Prism.highlightElement(html.value); // 切换菜单重新渲染
});
</script>
