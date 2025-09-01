import isString from 'lodash/isString';

import type { FieldConfig } from '@/api/model/fieldConfigModel';
import FormFieldRender from '@/components/field-config/FormFieldRender';
import { getAndSetLastObject } from '@/components/field-config/util';

export const FormFieldRendersProps = {
  /** 表单对象 */
  form: {
    type: Object,
    required: true,
  },
  // 配置值绑定属性
  modelValue: {
    type: [String, Object] as PropType<string | Record<string, any>>,
  },
  // 表单字段名称
  name: {
    type: String,
    required: true,
  },
  // 字段配置
  configs: {
    type: Object as PropType<Record<string, FieldConfig<any>>>,
    required: true,
  },
  // 绑定值的类型，object为对象，string为json字符串
  type: {
    type: String as PropType<'object' | 'string'>,
    default: 'object',
  },
};

export default defineComponent({
  name: 'FormFieldRenders',
  props: FormFieldRendersProps,
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const effectValue = ref({});

    // 更新了值后，同步到本地变量中
    watchEffect(() => {
      if (isString(props.modelValue)) {
        effectValue.value = JSON.parse(props.modelValue) || {};
      } else {
        effectValue.value = props.modelValue || {};
      }
    });

    const modelValue = computed({
      get() {
        return effectValue.value;
      },
      set(value) {
        if (props.type === 'string') {
          emit('update:modelValue', JSON.stringify(value));
        } else {
          emit('update:modelValue', value);
        }
      },
    });

    // 如果变更了字段配置，则重置值
    watch(
      () => props.configs,
      () => {
        restoreDefaultConfigValue();
      },
    );

    // 恢复配置默认值
    const restoreDefaultConfigValue = () => {
      const data = {};
      for (const key in props.configs) {
        const item = props.configs[key];
        const names = item.name?.split('.') ?? [key];
        getAndSetLastObject(data, names)[names.at(-1)] = item.value;
      }
      modelValue.value = data;
    };

    // 渲染多个字段
    const renderItems = () => {
      const items = [];
      for (const key in props.configs) {
        const item = props.configs[key];
        items.push(renderItem(key, item));
      }
      return items;
    };

    // 渲染字段
    const renderItem = (key: string, item: FieldConfig<any>) => {
      const names = item.name?.split('.') ?? [key];
      const val = computed({
        get() {
          return getAndSetLastObject(modelValue.value, names)[names.at(-1)];
        },
        set(value) {
          getAndSetLastObject(modelValue.value, names)[names.at(-1)] = value;
        },
      });

      return (
        <FormFieldRender
          v-model={val.value}
          config-value={modelValue.value}
          name={`${props.name}[${names.join('][')}]`}
          field-config={item}
        />
      );
    };

    return () => {
      return renderItems();
    };
  },
});
