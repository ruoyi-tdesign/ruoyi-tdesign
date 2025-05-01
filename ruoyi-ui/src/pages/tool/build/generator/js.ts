import { titleCase } from '@/utils/ruoyi';

import { trigger } from './config';

// 定义表单配置类型
interface FormConfig {
  formRef?: string;
  formModel?: string;
  size?: string;
  labelPosition?: string;
  labelWidth?: number;
  formRules?: string;
  gutter?: number;
  disabled?: boolean;
  span?: number;
  formBtns?: boolean;
  fields?: Field[];
}

// 定义字段对象的类型
interface Field {
  layout: 'colFormItem' | 'rowFormItem';
  span: number;
  labelWidth?: number;
  label: string;
  vModel: string;
  required?: boolean;
  tag?: string;
  type?: string;
  justify?: string;
  align?: string;
  children?: Field[];
  // 其他可能的属性
  [key: string]: any;
}

const exportDefault: string = 'export default ';

const units: { [key: string]: string } = {
  KB: '1024',
  MB: '1024 / 1024',
  GB: '1024 / 1024 / 1024',
};

let confGlobal: FormConfig | null;

const inheritAttrs: { [key: string]: string } = {
  file: '',
  dialog: 'inheritAttrs: false,',
};

export function makeUpJs(conf: FormConfig, type: string): string {
  // eslint-disable-next-line no-multi-assign
  confGlobal = conf = JSON.parse(JSON.stringify(conf));
  const dataList: string[] = [];
  const ruleList: string[] = [];
  const optionsList: string[] = [];
  const propsList: string[] = [];
  const methodList: string[] = mixinMethod(type);
  const uploadVarList: string[] = [];

  conf.fields.forEach((el: Field) => {
    buildAttributes(el, dataList, ruleList, optionsList, methodList, propsList, uploadVarList);
  });

  const script: string = buildexport(
    conf,
    type,
    dataList.join('\n'),
    ruleList.join('\n'),
    optionsList.join('\n'),
    uploadVarList.join('\n'),
    propsList.join('\n'),
    methodList.join('\n'),
  );
  confGlobal = null;
  return script;
}

function buildAttributes(
  el: Field,
  dataList: string[],
  ruleList: string[],
  optionsList: string[],
  methodList: string[],
  propsList: string[],
  uploadVarList: string[],
): void {
  buildData(el, dataList);
  buildRules(el, ruleList);

  if (el.options && el.options.length) {
    buildOptions(el, optionsList);
    if (el.dataType === 'dynamic') {
      const model: string = `${el.vModel}Options`;
      const options: string = titleCase(model);
      buildOptionMethod(`get${options}`, model, methodList);
    }
  }

  if (el.props && el.props.props) {
    buildProps(el, propsList);
  }

  if (el.action && el.tag === 'el-upload') {
    uploadVarList.push(
      `${el.vModel}Action: '${el.action}',
      ${el.vModel}fileList: [],`,
    );
    methodList.push(buildBeforeUpload(el));
    if (!el['auto-upload']) {
      methodList.push(buildSubmitUpload(el));
    }
  }

  if (el.children) {
    el.children.forEach((el2: Field) => {
      buildAttributes(el2, dataList, ruleList, optionsList, methodList, propsList, uploadVarList);
    });
  }
}

function mixinMethod(type: string): string[] {
  const list: string[] = [];
  const minxins: {
    [key: string]: { [key: string]: string } | null;
  } = {
    file: confGlobal?.formBtns
      ? {
          submitForm: `submitForm() {
          this.$refs['${confGlobal.formRef}'].validate(valid => {
            if(!valid) return
            // TODO 提交表单
          })
        },`,
          resetForm: `resetForm() {
          this.$refs['${confGlobal.formRef}'].resetFields()
        },`,
        }
      : null,
    dialog: {
      onOpen: 'onOpen() {},',
      onClose: `onClose() {
        this.$refs['${confGlobal.formRef}'].resetFields()
      },`,
      close: `close() {
        this.$emit('update:visible', false)
      },`,
      handleConfirm: `handleConfirm() {
        this.$refs['${confGlobal.formRef}'].validate(valid => {
          if(!valid) return
          this.close()
        })
      },`,
    },
  };

  const methods = minxins[type];
  if (methods) {
    Object.keys(methods).forEach((key: string) => {
      list.push(methods[key]);
    });
  }

  return list;
}

function buildData(conf: Field, dataList: string[]): void {
  if (conf.vModel === undefined) return;
  let defaultValue: string;
  if (typeof conf.defaultValue === 'string' && !conf.multiple) {
    defaultValue = `'${conf.defaultValue}'`;
  } else {
    defaultValue = `${JSON.stringify(conf.defaultValue)}`;
  }
  dataList.push(`${conf.vModel}: ${defaultValue},`);
}

function buildRules(conf: Field, ruleList: string[]): void {
  if (conf.vModel === undefined) return;
  const rules: string[] = [];
  if (trigger[conf.tag]) {
    if (conf.required) {
      const type: string = Array.isArray(conf.defaultValue) ? "type: 'array'," : '';
      let message: string = Array.isArray(conf.defaultValue) ? `请至少选择一个${conf.vModel}` : conf.placeholder;
      if (message === undefined) message = `${conf.label}不能为空`;
      rules.push(`{ required: true, ${type} message: '${message}', trigger: '${trigger[conf.tag]}' }`);
    }
    if (conf.regList && Array.isArray(conf.regList)) {
      conf.regList.forEach((item: { pattern?: string; message: string }) => {
        if (item.pattern) {
          // eslint-disable-next-line no-eval
          rules.push(`{ pattern: ${eval(item.pattern)}, message: '${item.message}', trigger: '${trigger[conf.tag]}' }`);
        }
      });
    }
    ruleList.push(`${conf.vModel}: [${rules.join(',')}],`);
  }
}

function buildOptions(conf: Field, optionsList: string[]): void {
  if (conf.vModel === undefined) return;
  if (conf.dataType === 'dynamic') {
    conf.options = [];
  }
  const str: string = `${conf.vModel}Options: ${JSON.stringify(conf.options)},`;
  optionsList.push(str);
}

function buildProps(conf: Field, propsList: string[]): void {
  if (conf.dataType === 'dynamic') {
    // eslint-disable-next-line no-unused-expressions
    conf.valueKey !== 'value' && (conf.props.props.value = conf.valueKey);
    // eslint-disable-next-line no-unused-expressions
    conf.labelKey !== 'label' && (conf.props.props.label = conf.labelKey);
    // eslint-disable-next-line no-unused-expressions
    conf.childrenKey !== 'children' && (conf.props.props.children = conf.childrenKey);
  }
  const str = `${conf.vModel}Props: ${JSON.stringify(conf.props.props)},`;
  propsList.push(str);
}

function buildBeforeUpload(conf: Field): string {
  const unitNum: string = units[conf.sizeUnit];
  let rightSizeCode: string = '';
  let acceptCode: string = '';
  const returnList: string[] = [];
  if (conf.fileSize) {
    rightSizeCode = `let isRightSize = file.size / ${unitNum} < ${conf.fileSize}
    if(!isRightSize){
      this.$message.error('文件大小超过 ${conf.fileSize}${conf.sizeUnit}')
    }`;
    returnList.push('isRightSize');
  }
  if (conf.accept) {
    acceptCode = `let isAccept = new RegExp('${conf.accept}').test(file.type)
    if(!isAccept){
      this.$message.error('应该选择${conf.accept}类型的文件')
    }`;
    returnList.push('isAccept');
  }
  const str: string = `${conf.vModel}BeforeUpload(file) {
    ${rightSizeCode}
    ${acceptCode}
    return ${returnList.join('&&')}
  },`;
  return returnList.length ? str : '';
}

function buildSubmitUpload(conf: Field): string {
  const str: string = `submitUpload() {
    this.$refs['${conf.vModel}'].submit()
  },`;
  return str;
}

function buildOptionMethod(methodName: string, model: string, methodList: string[]): void {
  const str: string = `${methodName}() {
    // TODO 发起请求获取数据
    this.${model}
  },`;
  methodList.push(str);
}

function buildexport(
  conf: FormConfig,
  type: string,
  data: string,
  rules: string,
  selectOptions: string,
  uploadVar: string,
  props: string,
  methods: string,
): string {
  const str: string = `${exportDefault}{
  ${inheritAttrs[type]}
  components: {},
  props: [],
  data () {
    return {
      ${conf.formModel}: {
        ${data}
      },
      ${conf.formRules}: {
        ${rules}
      },
      ${uploadVar}
      ${selectOptions}
      ${props}
    }
  },
  computed: {},
  watch: {},
  created () {},
  mounted () {},
  methods: {
    ${methods}
  }
}`;
  return str;
}
