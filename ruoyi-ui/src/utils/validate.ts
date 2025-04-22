/**
 * 路径匹配器
 * @param pattern
 * @param path
 */
export function isPathMatch(pattern: string, path: string) {
  const regexPattern = pattern.replace(/\//g, '\\/').replace(/\*\*/g, '.*').replace(/\*/g, '[^\\/]*');
  const regex = new RegExp(`^${regexPattern}$`);
  return regex.test(path);
}

/**
 * 判断url是否是http或https
 * @param url
 */
export function isHttp(url: string) {
  return url.indexOf('http://') !== -1 || url.indexOf('https://') !== -1;
}

/**
 * 判断path是否为外链
 * @param path
 */
export function isExternal(path: string) {
  return /^(https?:|mailto:|tel:)/.test(path);
}

/**
 * @param str
 */
export function validUsername(str: string) {
  const validMap = ['admin', 'editor'];
  return validMap.indexOf(str.trim()) >= 0;
}

/**
 * @param url
 */
export function validURL(url: string) {
  const reg =
    /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/;
  return reg.test(url);
}

/**
 * @param str
 */
export function validLowerCase(str: string) {
  const reg = /^[a-z]+$/;
  return reg.test(str);
}

/**
 * @param str
 */
export function validUpperCase(str: string) {
  const reg = /^[A-Z]+$/;
  return reg.test(str);
}

/**
 * @param str
 */
export function validAlphabets(str: string) {
  const reg = /^[A-Za-z]+$/;
  return reg.test(str);
}

/**
 * @param email
 */
export function validEmail(email: string) {
  const reg =
    /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return reg.test(email);
}

/**
 * @param str
 */
export function isString(str: any) {
  return typeof str === 'string' || str instanceof String;
}

/**
 * @param {Array} arg
 */
export function isArray(arg: any) {
  if (typeof Array.isArray === 'undefined') {
    return Object.prototype.toString.call(arg) === '[object Array]';
  }
  return Array.isArray(arg);
}
