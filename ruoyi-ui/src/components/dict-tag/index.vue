<template>
  <div>
    <template v-for="(item, index) in rowOptions">
      <span
        v-if="(!theme || theme === 'text') && !variant && (item.tagType === 'text' || !item.tagType)"
        :key="item.value"
        :class="item.tagClass"
      >
        {{ item.label }}
      </span>
      <t-tag
        v-else
        :key="`${item.value}`"
        :index="index"
        :size="size"
        :shape="shape"
        :theme="(theme || item.tagType || 'default') as any"
        :variant="variant || item.tagStyle || 'light'"
        :class="item.tagClass"
      >
        {{ item.label }}
      </t-tag>
      <template v-if="($slots.separator || separator) && rowOptions.length - 1 !== index">
        <template v-if="$slots.separator"><slot name="separator" /></template>
        <template v-else>{{ separator }}</template>
      </template>
    </template>
  </div>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'DictTag',
});

const props = defineProps({
  // 数据
  options: {
    type: Array as PropType<Array<DictModel>>,
    default: null,
  },
  size: {
    type: String as PropType<SizeEnum>,
  },
  shape: {
    type: String as PropType<'mark' | 'square' | 'round'>,
  },
  // 当前的值
  value: [Number, String, Array] as PropType<number | string | Array<string | number>>,
  // 多个值时，使用的分隔符。支持使用slot方式
  separator: [String],
  // 覆盖字典默认组件风格
  theme: {
    type: String as PropType<'default' | 'warning' | 'danger' | 'success' | 'primary' | 'text'>,
  },
  // 标签风格变体, 默认值light
  variant: {
    type: String as PropType<'outline' | 'dark' | 'light' | 'light-outline'>,
  },
  // 是否忽略类型, 默认转为string类型处理
  ignoreType: {
    type: Boolean,
    default: true,
  },
});
import { isNumber } from 'lodash';
import type { SizeEnum } from 'tdesign-vue-next';
import type { PropType } from 'vue';
import { computed } from 'vue';

import type { DictModel } from '@/utils/dict';

const values = computed<Array<number | string>>(() => {
  if (props.value !== null && props.value !== undefined && typeof props.value !== 'undefined') {
    if (Array.isArray(props.value)) {
      return props.value;
    }
    if (isNumber(props.value)) {
      return props.ignoreType ? [props.value.toString()] : [props.value];
    }
    return String(props.value).split(',');
  }
  return [];
});
// 有效选项
const rowOptions = computed<Array<DictModel>>(() => {
  if (!props.options) {
    return [];
  }
  return props.options.filter((option) => {
    return values.value.includes(props.ignoreType ? option.value.toString() : option.value);
  });
});
</script>
<style lang="less" scoped>
.t-tag + .t-tag {
  margin-left: 10px;
}
</style>
