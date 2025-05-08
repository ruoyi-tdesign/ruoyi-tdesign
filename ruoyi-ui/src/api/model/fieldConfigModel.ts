import type { FormRule, InputNumberProps, InputProps, SelectProps, SwitchProps, TextareaProps } from 'tdesign-vue-next';

export interface FieldOption {
  label: string;
  value: string | number;
}
/** 字段基本配置对象 */
export interface FieldConfig<T extends string | number | boolean | Array<string | number> = string> {
  /** 字段默认值 */
  value?: T;
  /** 字段名称 */
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
