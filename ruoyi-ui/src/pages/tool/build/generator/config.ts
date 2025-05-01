// 表单配置类型
interface FormConfig {
  formRef: string;
  formModel: string;
  size: string;
  labelPosition: string;
  labelWidth: number;
  formRules: string;
  gutter: number;
  disabled: boolean;
  span: number;
  formBtns: boolean;
}

// 输入组件配置类型
interface InputComponent {
  label: string;
  tag: string;
  tagIcon: string;
  placeholder: string;
  defaultValue: any;
  span: number;
  labelWidth?: number;
  style?: { width: string };
  clearable?: boolean;
  prepend?: string;
  append?: string;
  'prefix-icon'?: string;
  'suffix-icon'?: string;
  maxlength?: number;
  'show-word-limit'?: boolean;
  readonly?: boolean;
  disabled?: boolean;
  required?: boolean;
  regList: any[];
  changeTag: boolean;
  document: string;
  type?: string;
  autosize?: { minRows: number; maxRows: number };
  'show-password'?: boolean;
  min?: number;
  max?: number;
  step?: number;
  'step-strictly'?: boolean;
  precision?: number;
  'controls-position'?: string;
}

// 选择组件配置类型
interface SelectComponent {
  label: string;
  tag: string;
  tagIcon: string;
  placeholder?: string;
  defaultValue?: any;
  span?: number;
  labelWidth?: number | null;
  style?: { width?: string };
  optionType?: string;
  border?: boolean;
  size?: string;
  clearable?: boolean;
  disabled?: boolean;
  required?: boolean;
  filterable?: boolean;
  multiple?: boolean;
  options?: { id?: number; label: string; value: number; children?: { id?: number; label: string; value: number }[] }[];
  regList?: any[];
  changeTag?: boolean;
  document?: string;
  props?: { props: { multiple: boolean } };
  'show-all-levels'?: boolean;
  dataType?: string;
  labelKey?: string;
  valueKey?: string;
  childrenKey?: string;
  separator?: string;
  'active-text'?: string;
  'inactive-text'?: string;
  'active-color'?: string;
  'inactive-color'?: string;
  'active-value'?: boolean;
  'inactive-value'?: boolean;
  min?: number;
  max?: number;
  step?: number;
  'show-stops'?: boolean;
  range?: boolean;
  'picker-options'?: { selectableRange: string };
  format?: string;
  'value-format'?: string;
  'is-range'?: boolean;
  'range-separator'?: string;
  'start-placeholder'?: string;
  'end-placeholder'?: string;
  'allow-half'?: boolean;
  'show-text'?: boolean;
  'show-score'?: boolean;
  'show-alpha'?: boolean;
  'color-format'?: string;
  action?: string;
  accept?: string;
  name?: string;
  'auto-upload'?: boolean;
  showTip?: boolean;
  buttonText?: string;
  fileSize?: number;
  sizeUnit?: string;
  'list-type'?: string;
  type?: string;
  readonly?: boolean;
}

// 布局组件配置类型
interface LayoutComponent {
  layout: string;
  tagIcon: string;
  type: string;
  justify?: string;
  align?: string;
  label?: string;
  layoutTree?: boolean;
  children?: any[];
  document?: string;
  changeTag?: boolean;
  labelWidth?: number | null;
  tag?: string;
  span?: number;
  default?: string;
  icon?: string;
  size?: string;
  disabled?: boolean;
}

// 触发方式类型
interface Trigger {
  [key: string]: string;
}

// 美化器配置类型
interface BeautifierConfig {
  html: {
    indent_size: string;
    indent_char: string;
    max_preserve_newlines: string;
    preserve_newlines: boolean;
    keep_array_indentation: boolean;
    break_chained_methods: boolean;
    indent_scripts: string;
    brace_style: string;
    space_before_conditional: boolean;
    unescape_strings: boolean;
    jslint_happy: boolean;
    end_with_newline: boolean;
    wrap_line_length: string;
    indent_inner_html: boolean;
    comma_first: boolean;
    e4x: boolean;
    indent_empty_lines: boolean;
  };
  js: {
    indent_size: string;
    indent_char: string;
    max_preserve_newlines: string;
    preserve_newlines: boolean;
    keep_array_indentation: boolean;
    break_chained_methods: boolean;
    indent_scripts: string;
    brace_style: string;
    space_before_conditional: boolean;
    unescape_strings: boolean;
    jslint_happy: boolean;
    end_with_newline: boolean;
    wrap_line_length: string;
    indent_inner_html: boolean;
    comma_first: boolean;
    e4x: boolean;
    indent_empty_lines: boolean;
  };
}

// 表单配置
export const formConf: FormConfig = {
  formRef: 'elForm',
  formModel: 'formData',
  size: 'medium',
  labelPosition: 'right',
  labelWidth: 100,
  formRules: 'rules',
  gutter: 15,
  disabled: false,
  span: 12,
  formBtns: true,
};

// 输入组件列表
export const inputComponents: InputComponent[] = [
  {
    label: '单行文本',
    tag: 't-input',
    tagIcon: 'input',
    placeholder: '请输入',
    defaultValue: undefined,
    span: 12,
    labelWidth: null,
    style: { width: '100%' },
    clearable: true,
    prepend: '',
    append: '',
    'prefix-icon': '',
    'suffix-icon': '',
    maxlength: null,
    'show-word-limit': false,
    readonly: false,
    disabled: false,
    required: true,
    regList: [],
    changeTag: true,
    document: 'https://element.eleme.cn/#/zh-CN/component/input',
  },
  //   {
  //     label: '多行文本',
  //     tag: 'el-input',
  //     tagIcon: 'textarea',
  //     type: 'textarea',
  //     placeholder: '请输入',
  //     defaultValue: undefined,
  //     span: 24,
  //     labelWidth: null,
  //     autosize: {
  //       minRows: 4,
  //       maxRows: 4,
  //     },
  //     style: { width: '100%' },
  //     maxlength: null,
  //     'show-word-limit': false,
  //     readonly: false,
  //     disabled: false,
  //     required: true,
  //     regList: [],
  //     changeTag: true,
  //     document: 'https://element.eleme.cn/#/zh-CN/component/input',
  //   },
  //   {
  //     label: '密码',
  //     tag: 'el-input',
  //     tagIcon: 'password',
  //     placeholder: '请输入',
  //     defaultValue: undefined,
  //     span: 24,
  //     'show-password': true,
  //     labelWidth: null,
  //     style: { width: '100%' },
  //     clearable: true,
  //     prepend: '',
  //     append: '',
  //     'prefix-icon': '',
  //     'suffix-icon': '',
  //     maxlength: null,
  //     'show-word-limit': false,
  //     readonly: false,
  //     disabled: false,
  //     required: true,
  //     regList: [],
  //     changeTag: true,
  //     document: 'https://element.eleme.cn/#/zh-CN/component/input',
  //   },
  //   {
  //     label: '计数器',
  //     tag: 'el-input-number',
  //     tagIcon: 'number',
  //     placeholder: '',
  //     defaultValue: undefined,
  //     span: 24,
  //     labelWidth: null,
  //     min: undefined,
  //     max: undefined,
  //     step: undefined,
  //     'step-strictly': false,
  //     precision: undefined,
  //     'controls-position': '',
  //     disabled: false,
  //     required: true,
  //     regList: [],
  //     changeTag: true,
  //     document: 'https://element.eleme.cn/#/zh-CN/component/input-number',
  //   },
];

// 选择组件列表
export const selectComponents: SelectComponent[] = [
  {
    label: '下拉选择',
    tag: 't-select',
    tagIcon: 'select',
    placeholder: '请选择',
    defaultValue: undefined,
    span: 24,
    labelWidth: null,
    style: { width: '100%' },
    clearable: true,
    disabled: false,
    required: true,
    filterable: false,
    multiple: false,
    options: [
      {
        label: '选项一',
        value: 1,
      },
      {
        label: '选项二',
        value: 2,
      },
    ],
    regList: [],
    changeTag: true,
    document: 'https://element.eleme.cn/#/zh-CN/component/select',
  },
  // {
  //   label: '级联选择',
  //   tag: 'el-cascader',
  //   tagIcon: 'cascader',
  //   placeholder: '请选择',
  //   defaultValue: [],
  //   span: 24,
  //   labelWidth: null,
  //   style: { width: '100%' },
  //   props: {
  //     props: {
  //       multiple: false,
  //     },
  //   },
  //   'show-all-levels': true,
  //   disabled: false,
  //   clearable: true,
  //   filterable: false,
  //   required: true,
  //   options: [
  //     {
  //       id: 1,
  //       value: 1,
  //       label: '选项1',
  //       children: [
  //         {
  //           id: 2,
  //           value: 2,
  //           label: '选项1-1',
  //         },
  //       ],
  //     },
  //   ],
  //   dataType: 'dynamic',
  //   labelKey: 'label',
  //   valueKey: 'value',
  //   childrenKey: 'children',
  //   separator: '/',
  //   regList: [],
  //   changeTag: true,
  //   document: 'https://element.eleme.cn/#/zh-CN/component/cascader',
  // },
  // {
  //   label: '单选框组',
  //   tag: 'el-radio-group',
  //   tagIcon: 'radio',
  //   defaultValue: undefined,
  //   span: 24,
  //   labelWidth: null,
  //   style: {},
  //   optionType: 'default',
  //   border: false,
  //   size: 'medium',
  //   disabled: false,
  //   required: true,
  //   options: [
  //     {
  //       label: '选项一',
  //       value: 1,
  //     },
  //     {
  //       label: '选项二',
  //       value: 2,
  //     },
  //   ],
  //   regList: [],
  //   changeTag: true,
  //   document: 'https://element.eleme.cn/#/zh-CN/component/radio',
  // },
  // {
  //   label: '多选框组',
  //   tag: 'el-checkbox-group',
  //   tagIcon: 'checkbox',
  //   defaultValue: [],
  //   span: 24,
  //   labelWidth: null,
  //   style: {},
  //   optionType: 'default',
  //   border: false,
  //   size: 'medium',
  //   disabled: false,
  //   required: true,
  //   options: [
  //     {
  //       label: '选项一',
  //       value: 1,
  //     },
  //     {
  //       label: '选项二',
  //       value: 2,
  //     },
  //   ],
  //   regList: [],
  //   changeTag: true,
  //   document: 'https://element.eleme.cn/#/zh-CN/component/checkbox',
  // },
  // {
  //   label: '开关',
  //   tag: 'el-switch',
  //   tagIcon: 'switch',
  //   defaultValue: false,
  //   span: 24,
  //   labelWidth: null,
  //   style: {},
  //   disabled: false,
  //   required: true,
  //   'active-text': '',
  //   'inactive-text': '',
  //   'active-color': null,
  //   'inactive-color': null,
  //   'active-value': true,
  //   'inactive-value': false,
  //   regList: [],
  //   changeTag: true,
  //   document: 'https://element.eleme.cn/#/zh-CN/component/switch',
  // },
  // {
  //   label: '滑块',
  //   tag: 'el-slider',
  //   tagIcon: 'slider',
  //   defaultValue: null,
  //   span: 24,
  //   labelWidth: null,
  //   disabled: false,
  //   required: true,
  //   min: 0,
  //   max: 100,
  //   step: 1,
  //   'show-stops': false,
  //   range: false,
  //   regList: [],
  //   changeTag: true,
  //   document: 'https://element.eleme.cn/#/zh-CN/component/slider',
  // },
  // {
  //   label: '时间选择',
  //   tag: 'el-time-picker',
  //   tagIcon: 'time',
  //   placeholder: '请选择',
  //   defaultValue: null,
  //   span: 24,
  //   labelWidth: null,
  //   style: { width: '100%' },
  //   disabled: false,
  //   clearable: true,
  //   required: true,
  //   'picker-options': {
  //     selectableRange: '00:00:00-23:59:59',
  //   },
  //   format: 'HH:mm:ss',
  //   'value-format': 'HH:mm:ss',
  //   regList: [],
  //   changeTag: true,
  //   document: 'https://element.eleme.cn/#/zh-CN/component/time-picker',
  // },
  // {
  //   label: '时间范围',
  //   tag: 'el-time-picker',
  //   tagIcon: 'time-range',
  //   defaultValue: null,
  //   span: 24,
  //   labelWidth: null,
  //   style: { width: '100%' },
  //   disabled: false,
  //   clearable: true,
  //   required: true,
  //   'is-range': true,
  //   'range-separator': '至',
  //   'start-placeholder': '开始时间',
  //   'end-placeholder': '结束时间',
  //   format: 'HH:mm:ss',
  //   'value-format': 'HH:mm:ss',
  //   regList: [],
  //   changeTag: true,
  //   document: 'https://element.eleme.cn/#/zh-CN/component/time-picker',
  // },
  // {
  //   label: '日期选择',
  //   tag: 'el-date-picker',
  //   tagIcon: 'date',
  //   placeholder: '请选择',
  //   defaultValue: null,
  //   type: 'date',
  //   span: 24,
  //   labelWidth: null,
  //   style: { width: '100%' },
  //   disabled: false,
  //   clearable: true,
  //   required: true,
  //   format: 'yyyy-MM-dd',
  //   'value-format': 'yyyy-MM-dd',
  //   readonly: false,
  //   regList: [],
  //   changeTag: true,
  //   document: 'https://element.eleme.cn/#/zh-CN/component/date-picker',
  // },
  // {
  //   label: '日期范围',
  //   tag: 'el-date-picker',
  //   tagIcon: 'date-range',
  //   defaultValue: null,
  //   span: 24,
  //   labelWidth: null,
  //   style: { width: '100%' },
  //   type: 'daterange',
  //   'range-separator': '至',
  //   'start-placeholder': '开始日期',
  //   'end-placeholder': '结束日期',
  //   disabled: false,
  //   clearable: true,
  //   required: true,
  //   format: 'yyyy-MM-dd',
  //   'value-format': 'yyyy-MM-dd',
  //   readonly: false,
  //   regList: [],
  //   changeTag: true,
  //   document: 'https://element.eleme.cn/#/zh-CN/component/date-picker',
  // },
  // {
  //   label: '评分',
  //   tag: 'el-rate',
  //   tagIcon: 'rate',
  //   defaultValue: 0,
  //   span: 24,
  //   labelWidth: null,
  //   style: {},
  //   max: 5,
  //   'allow-half': false,
  //   'show-text': false,
  //   'show-score': false,
  //   disabled: false,
  //   required: true,
  //   regList: [],
  //   changeTag: true,
  //   document: 'https://element.eleme.cn/#/zh-CN/component/rate',
  // },
  // {
  //   label: '颜色选择',
  //   tag: 'el-color-picker',
  //   tagIcon: 'color',
  //   defaultValue: null,
  //   labelWidth: null,
  //   'show-alpha': false,
  //   'color-format': '',
  //   disabled: false,
  //   required: true,
  //   size: 'medium',
  //   regList: [],
  //   changeTag: true,
  //   document: 'https://element.eleme.cn/#/zh-CN/component/color-picker',
  // },
  // {
  //   label: '上传',
  //   tag: 'el-upload',
  //   tagIcon: 'upload',
  //   action: 'https://jsonplaceholder.typicode.com/posts/',
  //   defaultValue: null,
  //   labelWidth: null,
  //   disabled: false,
  //   required: true,
  //   accept: '',
  //   name: 'file',
  //   'auto-upload': true,
  //   showTip: false,
  //   buttonText: '点击上传',
  //   fileSize: 2,
  //   sizeUnit: 'MB',
  //   'list-type': 'text',
  //   multiple: false,
  //   regList: [],
  //   changeTag: true,
  //   document: 'https://element.eleme.cn/#/zh-CN/component/upload',
  // },
];

// 布局组件列表
export const layoutComponents: LayoutComponent[] = [
  {
    layout: 'rowFormItem',
    tagIcon: 'row',
    type: 'default',
    justify: 'start',
    align: 'top',
    label: '行容器',
    layoutTree: true,
    children: [],
    document: 'https://element.eleme.cn/#/zh-CN/component/layout',
  },
  // {
  //   layout: 'colFormItem',
  //   label: '按钮',
  //   changeTag: true,
  //   labelWidth: null,
  //   tag: 'el-button',
  //   tagIcon: 'button',
  //   span: 24,
  //   default: '主要按钮',
  //   type: 'primary',
  //   icon: 'el-icon-search',
  //   size: 'medium',
  //   disabled: false,
  //   document: 'https://element.eleme.cn/#/zh-CN/component/button',
  // },
];

// 组件rule的触发方式，无触发方式的组件不生成rule
export const trigger: Trigger = {
  'el-input': 'blur',
  'el-input-number': 'blur',
  'el-select': 'change',
  'el-radio-group': 'change',
  'el-checkbox-group': 'change',
  'el-cascader': 'change',
  'el-time-picker': 'change',
  'el-date-picker': 'change',
  'el-rate': 'change',
};

// 美化器配置
export const beautifierConf: BeautifierConfig = {
  html: {
    indent_size: '2',
    indent_char: ' ',
    max_preserve_newlines: '-1',
    preserve_newlines: false,
    keep_array_indentation: false,
    break_chained_methods: false,
    indent_scripts: 'separate',
    brace_style: 'end-expand',
    space_before_conditional: true,
    unescape_strings: false,
    jslint_happy: false,
    end_with_newline: true,
    wrap_line_length: '110',
    indent_inner_html: true,
    comma_first: false,
    e4x: true,
    indent_empty_lines: true,
  },
  js: {
    indent_size: '2',
    indent_char: ' ',
    max_preserve_newlines: '-1',
    preserve_newlines: false,
    keep_array_indentation: false,
    break_chained_methods: false,
    indent_scripts: 'normal',
    brace_style: 'end-expand',
    space_before_conditional: true,
    unescape_strings: false,
    jslint_happy: true,
    end_with_newline: true,
    wrap_line_length: '110',
    indent_inner_html: true,
    comma_first: false,
    e4x: true,
    indent_empty_lines: true,
  },
};
