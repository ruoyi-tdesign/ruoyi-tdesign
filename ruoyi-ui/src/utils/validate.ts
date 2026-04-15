/**
 * 路径匹配器
 * @param pattern
 * @param path
 */
export function isPathMatch(pattern: string, path: string) {
  // eslint-disable-next-line e18e/prefer-static-regex
  const regexPattern = pattern.replace(/\//g, '\\/').replace(/\*\*/g, '.*').replace(/\*/g, '[^\\/]*');
  const regex = new RegExp(`^${regexPattern}$`);
  return regex.test(path);
}

/**
 * 判断url是否是http或https
 * @param url
 */
export function isHttp(url: string) {
  return url.startsWith('http://') || url.startsWith('https://');
}

/**
 * 判断path是否为外链
 * @param path
 */
export function isExternal(path: string) {
  // eslint-disable-next-line e18e/prefer-static-regex
  return /^(?:https?:|mailto:|tel:)/.test(path);
}

/**
 * @param str
 */
export function validUsername(str: string) {
  const validMap = ['admin', 'editor'];
  return validMap.includes(str.trim());
}

/**
 * @param url
 */
export function validURL(url: string) {
  const reg =
    // eslint-disable-next-line e18e/prefer-static-regex
    /^(?:https?|ftp):\/\/(?:[a-zA-Z0-9.-]+(?::[a-zA-Z0-9.&%$-]+)*@)*(?:(?:25[0-5]|2[0-4]\d|1\d{2}|[1-9]\d?)(?:\.(?:25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)){3}|(?:[a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(?:com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(?::\d+)*(?:\/(?:$|[\w.,?'\\+&%$#=~-]+))*$/;
  return reg.test(url);
}

/**
 * @param str
 */
export function validLowerCase(str: string) {
  // eslint-disable-next-line e18e/prefer-static-regex
  const reg = /^[a-z]+$/;
  return reg.test(str);
}

/**
 * @param str
 */
export function validUpperCase(str: string) {
  // eslint-disable-next-line e18e/prefer-static-regex
  const reg = /^[A-Z]+$/;
  return reg.test(str);
}

/**
 * @param str
 */
export function validAlphabets(str: string) {
  // eslint-disable-next-line e18e/prefer-static-regex
  const reg = /^[A-Z]+$/i;
  return reg.test(str);
}

/**
 * @param email
 */
export function validEmail(email: string) {
  const reg =
    // eslint-disable-next-line e18e/prefer-static-regex
    /^(?:[^<>()[\]\\.,;:\s@"]+(?:\.[^<>()[\]\\.,;:\s@"]+)*|".+")@(?:\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\]|(?:[a-z\-0-9]+\.)+[a-z]{2,})$/i;
  return reg.test(email);
}

/**
 * @param str
 */
export function isString(str: any) {
  // eslint-disable-next-line unicorn/no-instanceof-builtins
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
