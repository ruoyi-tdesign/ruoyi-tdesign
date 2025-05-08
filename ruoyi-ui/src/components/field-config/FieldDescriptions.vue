<!-- 代替my-descriptions组件，渲染字段 -->
<template>
  <my-descriptions v-bind="$attrs">
    <slot name="prefix" />
    <template v-for="(fieldConfig, key) in fieldConfigs" :key="fieldConfig + key">
      <t-descriptions-item v-if="!fieldConfig.visible || configValue[fieldConfig.visible]" :label="fieldConfig.name">
        <template v-if="fieldConfig.component === 'select'">
          <dict-tag :options="fieldConfig.selectProps.options as FieldOption[]" :value="configValue[key]" />
        </template>
        <!--        <template v-if="['select', 'radio', 'checkbox', 'tree-select'].includes(fieldConfig.component)">
          <dict-tag :options="fieldConfig.options" :value="configValue[key]" />
        </template>-->
        <template v-else-if="['image-upload'].includes(fieldConfig.component)">
          <image-preview :src="configValue[key]" width="60px" height="60px" />
        </template>
        <template v-else>
          {{ configValue[key] }}
        </template>
      </t-descriptions-item>
    </template>
    <slot name="suffix" />
  </my-descriptions>
</template>

<script setup lang="ts">
import type { FieldConfig, FieldOption } from '@/api/model/fieldConfigModel';

defineProps({
  // 配置值对象
  configValue: {
    type: Object as PropType<Record<string, any>>,
    required: true,
  },
  // 表单字段配置
  fieldConfigs: {
    type: Object as PropType<Record<string, FieldConfig<any>>>,
    required: true,
  },
});
</script>
