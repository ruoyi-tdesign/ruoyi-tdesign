<template>
  <t-col v-if="!fieldConfig.visible || configValue[fieldConfig.visible]" :span="fieldConfig.span ?? 12">
    <t-form-item
      :label="fieldConfig.name"
      :name="name"
      :rules="[
        { required: fieldConfig.required, message: `${fieldConfig.name}不能为空` },
        ...(fieldConfig.rules ?? []),
      ]"
    >
      <template v-if="fieldConfig.help" #help><span v-html="fieldConfig.help"></span></template>
      <t-input
        v-if="fieldConfig.component === 'input'"
        v-model.trim="modelValue"
        :placeholder="`请输入${fieldConfig.name}`"
        :type="fieldConfig.type"
      />
      <t-input-number
        v-else-if="fieldConfig.component === 'input-number'"
        v-model="modelValue"
        :allow-input-over-limit="false"
        :min="fieldConfig.min ?? -Infinity"
        :max="fieldConfig.max ?? Infinity"
        :decimal-places="0"
        auto-width
      />
      <t-switch v-else-if="fieldConfig.component === 'switch'" v-model="modelValue" />
      <t-select
        v-else-if="fieldConfig.component === 'select'"
        v-model="modelValue"
        placeholder="请选择"
        :multiple="fieldConfig.multiple"
        clearable
        filterable
      >
        <t-option
          v-for="option in fieldConfig.options"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        />
      </t-select>
    </t-form-item>
  </t-col>
</template>

<script setup lang="ts">
import type { FieldConfig } from '@/api/model/fieldConfigModel';

const props = defineProps({
  // 表单字段名称
  name: {
    type: String,
  },
  // 配置值对象
  configValue: {
    type: Object as PropType<Record<string, any>>,
    required: true,
  },
  // 表单字段配置
  fieldConfig: {
    type: Object as PropType<FieldConfig<any>>,
    required: true,
  },
  // 是否允许初始化值
  initValue: {
    type: Boolean,
    default: false,
  },
});

const modelValue = defineModel<any>();

onMounted(() => {
  if (props.initValue && modelValue.value === undefined) {
    modelValue.value = props.fieldConfig.value;
  }
});
</script>
