import type { FormRule, InputNumberProps, InputProps, SelectProps, SwitchProps, TextareaProps } from 'tdesign-vue-next';

export interface FieldOption {
  /** 当前选项是否为全选，全选可以在顶部，也可以在底部。点击当前选项会选中禁用态除外的全部选项，即使是分组选择器也会选中全部选项 */
  checkAll?: boolean;
  /** 是否禁用该选项 */
  disabled?: boolean;
  /** 选项标题，在选项过长时hover选项展示 */
  title?: string;
  /** 标签 */
  label: string;
  /** 值 */
  value: string | number;
}
export type FieldOptionGroup = {
  /** 是否显示分隔线，默认为true */
  divider?: boolean;
  /** 分组别名 */
  label?: string;
  /** 分组名称 */
  group: string;
  /** 子选项 */
  children?: FieldOption[];
};
/** 字段基本配置对象 */
export interface FieldConfig<T extends string | number | boolean | Array<string | number> = string> {
  /** 字段默认值 */
  value?: T;
  /** 标签名称 */
  label: string;
  /** 自定义字段名称，默认为对象的属性名称。可以使用.作为嵌套对象的属性，例如：items.item */
  name: string;
  /** 组件 */
  component: string;
  /** 帮助信息 */
  help?: string;
  /** 是否必填 */
  required: boolean;
  /** 占位符 */
  placeholder?: string;
  /** 占用栅格数 */
  span?: number;
  /** 可见性依赖字段。例如a=true，则b设置visible为a */
  visible?: string;
  /** 其他校验规则 */
  rules?: FormRule[];
  /** 输入框组件属性 */
  inputProps: InputProps;
  /** 数字输入框组件属性 */
  inputNumberProps: InputNumberProps;
  /** 开关组件属性 */
  switchProps: SwitchProps;
  /** 文本域组件属性 */
  textareaProps: TextareaProps;
  /** 选择器组件属性 */
  selectProps: SelectProps;
}

/**
 * 评估字段配置
 * @param config 字段配置对象
 */
export function evalFieldConfig(config: FieldConfig<any>) {
  config.rules?.forEach((rule) => {
    if (rule.pattern) {
      rule.pattern = new RegExp(rule.pattern);
    }
    if (rule.validator) {
      // @ts-ignore
      // eslint-disable-next-line no-new-func
      rule.validator = new Function(rule.validator);
    }
  });
}

/**
 * 评估字段配置对象
 * @param configs 字段配置对象
 */
export function evalFieldConfigs(configs: Record<string, FieldConfig<any>>) {
  Object.values(configs).forEach((config) => {
    evalFieldConfig(config);
  });
}
