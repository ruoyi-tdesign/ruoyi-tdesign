<template>
  <div ref="container">
    <t-image-viewer
      v-model:visible="visible"
      v-model:index="index"
      :images="images"
      :close-on-overlay="closeOnOverlay"
      v-bind="$attrs"
    />
    <slot />
  </div>
</template>

<script lang="ts" setup>
import debounce from 'lodash/debounce';
import { ref, useSlots } from 'vue';

defineProps({
  // 是否在点击遮罩层时，触发预览关闭
  closeOnOverlay: {
    type: Boolean,
    default: true,
  },
});

const container = ref<HTMLElement>();
const slots = useSlots();
const index = ref(0);
const images = ref<string[]>();
const visible = ref(false);

function replaceImageWithComponent() {
  if (slots.default().length && container.value) {
    nextTick(() => {
      const element = container.value;
      const elements = element.querySelectorAll('img');
      const imageElements = Array.from(elements);
      images.value = imageElements
        .filter((img) => img.src)
        .map((img, i) => {
          img.style.cursor = 'zoom-in';
          img.addEventListener('click', () => {
            index.value = i;
            visible.value = true;
          });
          return img.src;
        });
    });
  }
}

const replaceImageWithComponentOnDebounce = debounce(replaceImageWithComponent, 200);

watch(() => slots.default(), replaceImageWithComponentOnDebounce, { immediate: true });

onMounted(() => {
  useMutationObserver(container.value, replaceImageWithComponentOnDebounce, {
    childList: true,
    subtree: true,
  });
});
</script>
