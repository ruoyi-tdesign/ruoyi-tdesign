/**
 * 通用js方法封装处理
 * Copyright (c) 2019 ruoyi
 */
import modal from '@/plugins/modal';
import type { DictModel } from '@/utils/dict';

// 日期格式化
export function parseTime(time: Date | string | number, pattern?: string) {
  if (arguments.length === 0 || !time) {
    return null;
  }
  const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}';
  let date;
  if (typeof time === 'object') {
    date = time;
  } else {
    if (typeof time === 'string' && /^[0-9]+$/.test(time)) {
      time = parseInt(time, 10);
    } else if (typeof time === 'string') {
      time = time
        .replace(/-/gm, '/')
        .replace('T', ' ')
        .replace(/\.[\d]{3}/gm, '');
    }
    if (typeof time === 'number' && time.toString().length === 10) {
      time *= 1000;
    }
    date = new Date(time);
  }
  const formatObj: Record<string, number> = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay(),
  };
  return format.replace(/{([ymdhisa])+}/g, (result: string, key: string) => {
    let value: string | number = formatObj[key];
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') {
      return ['日', '一', '二', '三', '四', '五', '六'][value];
    }
    if (result.length > 0 && value < 10) {
      value = `0${value}`;
    }
    return String(value || 0);
  });
}

// 表单重置
export function resetForm(refName: string | number) {
  if (this.$refs[refName]) {
    this.$refs[refName].reset();
  }
}

/**
 * 添加日期范围,时间会自动填充时间后缀
 * @param params
 * @param dateRange
 * @param propName
 */
export function addDateRange(params: any, dateRange: Array<Date | string | number>, propName?: any) {
  const search = params;
  search.params =
    typeof search.params === 'object' && search.params !== null && !Array.isArray(search.params) ? search.params : {};
  dateRange = Array.isArray(dateRange) ? dateRange : [];
  if (typeof propName === 'undefined') {
    [search.params.beginTime, search.params.endTime] = dateRange.map(
      (item, index) => item && `${parseTime(item, '{y}-{m}-{d}')} ${index === 0 ? '00:00:00' : '23:59:59'}`,
    );
  } else {
    [search.params[`begin${propName}`], search.params[`end${propName}`]] = dateRange.map(
      (item, index) => item && `${parseTime(item, '{y}-{m}-{d}')} ${index === 0 ? '00:00:00' : '23:59:59'}`,
    );
  }
  return search;
}

/**
 * 添加日期范围（时间不会自动填充）
 * @param params
 * @param dateRange
 * @param propName
 */
export function addDateRangeTime(params: any, dateRange: Array<any>, propName?: any) {
  const search = params;
  search.params =
    typeof search.params === 'object' && search.params !== null && !Array.isArray(search.params) ? search.params : {};
  dateRange = Array.isArray(dateRange) ? dateRange : [];
  if (typeof propName === 'undefined') {
    [search.params.beginTime, search.params.endTime] = dateRange;
  } else {
    [search.params[`begin${propName}`], search.params[`end${propName}`]] = dateRange;
  }
  return search;
}

// 回显数据字典
export function selectDictLabel(datas: DictModel[], value: any) {
  if (value === undefined) {
    return '';
  }
  const actions = [];
  datas.some((dict) => {
    if (dict.value === `${value}`) {
      actions.push(dict.label);
      return true;
    }
    return false;
  });
  if (actions.length === 0) {
    actions.push(value);
  }
  return actions.join('');
}

// 回显数据字典（字符串数组）
export function selectDictLabels(datas: DictModel[], value?: string | string[], separator = ',') {
  if (value === undefined || value.length === 0) {
    return '';
  }
  if (Array.isArray(value)) {
    value = value.join(',');
  }
  const actions: string[] = [];
  value.split(separator).forEach((val) => {
    let match = false;
    datas.some((dict) => {
      if (dict.value === val) {
        actions.push(dict.label + separator);
        match = true;
      }
      return false;
    });
    if (!match) {
      actions.push(val + separator);
    }
  });
  return actions.join('').substring(0, actions.join('').length - 1);
}

// 字符串格式化(%s )
export function sprintf(str: string) {
  // eslint-disable-next-line prefer-rest-params
  const args = arguments;
  let flag = true;
  let i = 1;
  str = str.replace(/%s/g, () => {
    const arg = args[i++];
    if (typeof arg === 'undefined') {
      flag = false;
      return '';
    }
    return arg;
  });
  return flag ? str : '';
}

// 转换字符串，undefined,null等转化为""
export function parseStrEmpty(str: string | number) {
  if (!str || str === 'undefined' || str === 'null') {
    return '';
  }
  return str;
}

// 数据合并
export function mergeRecursive(source: { [x: string]: any }, target: { [x: string]: any }) {
  for (const p in target) {
    try {
      if (target[p].constructor === Object) {
        source[p] = mergeRecursive(source[p], target[p]);
      } else {
        source[p] = target[p];
      }
    } catch (e) {
      source[p] = target[p];
    }
  }
  return source;
}

/**
 * 构造树型结构数据
 * @param {*} data 数据源
 * @param {*} id id字段 默认 'id'
 * @param {*} parentId 父节点字段 默认 'parentId'
 * @param {*} children 孩子节点字段 默认 'children'
 */
export function handleTree<T, ID extends keyof T & string, PID extends keyof T & string, C extends keyof T & string>(
  data: T[],
  id?: ID,
  parentId?: PID,
  children?: C,
): T[] {
  const config = {
    id: id || ('id' as ID),
    parentId: parentId || ('parentId' as PID),
    childrenList: children || ('children' as C),
  };

  const childrenListMap: any = {};
  const tree: T[] = [];
  for (const d of data) {
    const id = d[config.id];
    childrenListMap[id] = d;
    if (!d[config.childrenList]) {
      d[config.childrenList] = [] as T[C];
    }
  }

  for (const d of data) {
    const parentId = d[config.parentId];
    const parentObj = childrenListMap[parentId];
    if (!parentObj) {
      tree.push(d);
    } else {
      parentObj[config.childrenList].push(d);
    }
  }
  return tree;
}

/**
 * 参数处理
 * @param {*} params  参数
 */
export function tansParams(params: { [x: string]: any }) {
  let result = '';
  for (const propName of Object.keys(params)) {
    const value = params[propName];
    const part = `${encodeURIComponent(propName)}=`;
    if (value !== null && value !== '' && typeof value !== 'undefined') {
      if (typeof value === 'object') {
        for (const key of Object.keys(value)) {
          if (value[key] !== null && value[key] !== '' && typeof value[key] !== 'undefined') {
            const params = `${propName}[${key}]`;
            const subPart = `${encodeURIComponent(params)}=`;
            result += `${subPart + encodeURIComponent(value[key])}&`;
          }
        }
      } else {
        result += `${part + encodeURIComponent(value)}&`;
      }
    }
  }
  return result;
}

// 返回项目路径
export function getNormalPath(p: string) {
  if (p.length === 0 || !p || p === 'undefined' || p === null) {
    return p;
  }
  const res = p.replace('//', '/');
  if (res[res.length - 1] === '/') {
    return res.slice(0, res.length - 1);
  }
  return res;
}

// 验证是否为blob格式
export function blobValidate(data: { type: string }) {
  return data.type !== 'application/json';
}

// 过滤tree数据
export function treeFilter<S extends { children: S[]; label: string }>(data: S[], searchValue: string) {
  if (data && data.length > 0) {
    data = Object.assign([], data);
    return data
      .map((item) => ({ ...item }))
      .filter((item) => {
        if (item.children && item.children.length > 0) {
          item.children = treeFilter(item.children, searchValue);
        }
        return (item.children && item.children.length > 0) || item.label.indexOf(searchValue) !== -1;
      });
  }
  return data;
}

/** tree中的所有id */
export function treeId<T extends { children: T[]; id: string | number }>(data: T[]): Array<string | number> {
  if (data && data.length > 0) {
    return data.flatMap((item) => {
      let ids: Array<string | number> = [];
      if (item.children && item.children.length > 0) {
        ids = treeId(item.children);
      }
      return [...ids, item.id];
    });
  }
  return [];
}

/** byte格式化 */
export function bytesToSize(bytes: number) {
  if (bytes === 0) return '0 B';
  if (!bytes) return '';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return `${(bytes / k ** i).toFixed(3)} ${sizes[i]}`;
  // toPrecision(3) 后面保留两位小数，如1.00GB
}

/**
 * 处理状态改变
 * @param list 列表数据，用来从列表中获取相同id的对象
 * @param obj 对象
 * @param id 对象id属性
 * @param status 对象状态属性
 * @param title 提示标题
 * @param ok 选择确定后的回调
 * @param yesValue 启用值
 * @param noValue 停用值
 */
export function handleChangeStatus<T>(
  list: T[],
  obj: T,
  id: keyof T,
  status: keyof T,
  title: string,
  ok: (row: T) => Promise<any>,
  yesValue = '1',
  noValue = '0',
) {
  const rowObj: T = list.find((value) => value[id] === obj[id]);
  const text = rowObj[status] === yesValue ? '启用' : '停用';
  modal.confirm(
    `确认要${text}"${title}"吗?`,
    () => {
      return ok(rowObj)
        .then(() => {
          modal.msgSuccess(`${text}成功`);
        })
        .catch(() => {
          // @ts-ignore
          rowObj[status] = rowObj[status] === noValue ? yesValue : noValue;
        });
    },
    () => {
      // @ts-ignore
      rowObj[status] = rowObj[status] === noValue ? yesValue : noValue;
    },
  );
}

/**
 * 是否是有效的json
 * @param json
 */
export function isJson(json: string) {
  try {
    const obj = JSON.parse(json);
    return typeof obj === 'object' && obj;
  } catch (e) {
    return false;
  }
}

/**
 * 提取http链接文件后缀名
 * @param http
 */
export function getHttpFileSuffix(http: string) {
  const index = http.indexOf('?');
  if (index !== -1) {
    http = http.substring(0, http.indexOf('?'));
  }
  return http.substring(http.lastIndexOf('.') + 1);
}

/**
 * 提取http链接文件名
 * @param http
 */
export function getHttpFileName(http: string) {
  const index = http.indexOf('?');
  if (index !== -1) {
    http = http.substring(0, http.indexOf('?'));
  }
  return http.substring(http.lastIndexOf('/') + 1);
}

/**
 * 首字母大小
 * @param str
 */
export function titleCase(str: string) {
  return str.replace(/( |^)[a-z]/g, (L) => L.toUpperCase());
}

/**
 * 下划转驼峰
 * @param str
 */
export function camelCase(str: string) {
  return str.replace(/_[a-z]/g, (str1) => str1.slice(-1).toUpperCase());
}

export function makeMap(str: string, expectsLowerCase?: boolean) {
  const map = Object.create(null);
  const list = str.split(',');
  for (let i = 0; i < list.length; i++) {
    map[list[i]] = true;
  }
  return expectsLowerCase ? (val: string) => map[val.toLowerCase()] : (val: string) => map[val];
}
