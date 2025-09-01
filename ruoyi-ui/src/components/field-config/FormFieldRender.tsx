import { Col, FormItem, Input, InputNumber, Select, Switch } from 'tdesign-vue-next';

import type { FieldConfig } from '@/api/model/fieldConfigModel';

export const FormFieldRenderProps = {
  modelValue: {
    type: [String, Number, Boolean, Array, Object],
  },
  // 表单字段名称
  name: {
    type: String,
    required: true,
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
};

export default defineComponent({
  name: 'FormFieldRender',
  props: FormFieldRenderProps,
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const modelValue = computed({
      get() {
        if (props.fieldConfig.component === 'input') {
          return (props.modelValue as string)?.trim();
        }
        return props.modelValue;
      },
      set(value) {
        emit('update:modelValue', value);
      },
    });

    onMounted(() => {
      if (props.initValue) {
        modelValue.value = props.fieldConfig.value;
      }
    });

    return () => {
      const fieldConfig = props.fieldConfig;

      /**
       * 渲染帮助信息
       */
      const renderHelp = () => {
        if (fieldConfig.help) {
          return <span v-html={fieldConfig.help!}></span>;
        }
        return '';
      };

      /**
       * 渲染组件
       */
      const renderComponent = () => {
        if (fieldConfig.component === 'input') {
          return (
            <Input
              v-model={modelValue.value}
              placeholder={fieldConfig.placeholder ?? `请输入${fieldConfig.label}`}
              {...fieldConfig.inputProps}
            ></Input>
          );
        }
        if (fieldConfig.component === 'input-number') {
          return <InputNumber v-model={modelValue.value} {...fieldConfig.inputNumberProps}></InputNumber>;
        }
        if (fieldConfig.component === 'switch') {
          return <Switch v-model={modelValue.value} {...fieldConfig.switchProps}></Switch>;
        }
        if (fieldConfig.component === 'select') {
          return <Select v-model={modelValue.value} {...fieldConfig.selectProps}></Select>;
        }
        return '';
      };

      if (!fieldConfig.visible || props.configValue[fieldConfig.visible]) {
        return (
          <Col span={fieldConfig.span ?? 12}>
            <FormItem
              label={fieldConfig.label}
              name={props.name}
              rules={[
                { required: fieldConfig.required, message: `${fieldConfig.label}不能为空` },
                ...(fieldConfig.rules ?? []),
              ]}
              help={() => renderHelp()}
            >
              {renderComponent()}
            </FormItem>
          </Col>
        );
      }
      return '';
    };
  },
});
