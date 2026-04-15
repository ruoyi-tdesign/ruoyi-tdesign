<template>
  <t-select
    :value="modelValue"
    placeholder="请选择"
    :style="{ width: '400px' }"
    :popup-props="{ overlayInnerStyle: { width: '400px' } }"
    @change="onChange($event as string)"
  >
    <t-option v-for="item in options" :key="item.stem" :value="item.stem" class="overlay-options">
      <div>
        <r-icon :name="item.stem" />
      </div>
    </t-option>
    <template #valueDisplay><r-icon :name="modelValue" :style="{ marginRight: '8px' }" />{{ modelValue }}</template>
  </t-select>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'IconSelect',
});
defineProps({
  modelValue: String,
});

const emit = defineEmits(['update:modelValue', 'change']);

import { manifest } from 'tdesign-icons-vue-next';

// 获取全部图标的列表
const options = manifest;
const onChange = (value: string) => {
  emit('update:modelValue', value);
  emit('change', value);
};
</script>
