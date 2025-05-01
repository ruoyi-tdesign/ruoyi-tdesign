// 定义 styles 对象的类型
type CssStyles = {
  [key: string]: string;
};

// 定义元素对象的类型
type ElementObject = {
  tag: string;
  children?: ElementObject[];
};

// 定义配置对象的类型
type Config = {
  fields?: ElementObject[];
};

const styles: CssStyles = {
  'el-rate': '.el-rate{display: inline-block; vertical-align: text-top;}',
  'el-upload': '.el-upload__tip{line-height: 1.2;}',
};

function addCss(cssList: string[], el: ElementObject): void {
  const css = styles[el.tag];
  // eslint-disable-next-line no-unused-expressions
  css && cssList.indexOf(css) === -1 && cssList.push(css);
  if (el.children) {
    el.children.forEach((el2: ElementObject) => addCss(cssList, el2));
  }
}

export function makeUpCss(conf: Config): string {
  const cssList: string[] = [];
  conf.fields.forEach((el: ElementObject) => addCss(cssList, el));
  return cssList.join('\n');
}
