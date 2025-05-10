<!-- 代替my-descriptions组件，渲染字段 -->
<template>
  <my-descriptions v-bind="$attrs">
    <slot name="prefix" />
    <template v-for="(fieldConfig, key) in fieldConfigs" :key="fieldConfig + key">
      <t-descriptions-item
        v-if="!fieldConfig.visible || effectConfigValue[fieldConfig.visible]"
        :label="fieldConfig.label"
      >
        <template v-if="fieldConfig.component === 'select'">
          <dict-tag
            :options="getOptions(fieldConfig.selectProps.options as Array<FieldOption | FieldOptionGroup>)"
            :value="getValue(fieldConfig, key)"
          />
        </template>
        <!--        <template v-if="['select', 'radio', 'checkbox', 'tree-select'].includes(fieldConfig.component)">
          <dict-tag :options="fieldConfig.options" :value="configValue[key]" />
        </template>-->
        <template v-else-if="['image-upload'].includes(fieldConfig.component)">
          <image-preview :src="getValue(fieldConfig, key)" width="60px" height="60px" />
        </template>
        <template v-else>
          {{ getValue(fieldConfig, key) }}
        </template>
      </t-descriptions-item>
    </template>
    <slot name="suffix" />
  </my-descriptions>
</template>

<script setup lang="ts">
import isString from 'lodash/isString';

import type { FieldConfig, FieldOption, FieldOptionGroup } from '@/api/model/fieldConfigModel';
import { getAndSetLastObject } from '@/components/field-config/util';

const props = defineProps({
  // 配置值对象
  configValue: {
    type: [String, Object] as PropType<string | Record<string, any>>,
    required: true,
  },
  // 表单字段配置
  fieldConfigs: {
    type: Object as PropType<Record<string, FieldConfig<any>>>,
    required: true,
  },
});

const effectConfigValue = computed(() => {
  if (isString(props.configValue)) {
    return JSON.parse(props.configValue) ?? {};
  }
  return props.configValue ?? {};
});

/**
 * 获取值
 * @param fieldConfig
 * @param key
 */
function getValue(fieldConfig: FieldConfig<any>, key: string) {
  const names = fieldConfig.name?.split('.') ?? [key];
  return getAndSetLastObject(effectConfigValue.value, names)[names.at(-1)];
}

/**
 * 获取选项
 * @param options
 */
function getOptions(options: Array<FieldOption | FieldOptionGroup>): FieldOption[] {
  return options.flatMap((option) => {
    // @ts-ignore
    if (option.children) {
      // @ts-ignore
      return option.children.map((child) => {
        return child;
      });
    }
    return option;
  });
}
</script>
