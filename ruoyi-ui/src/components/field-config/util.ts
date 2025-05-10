/**
 * 获取并设置最后一级对象
 * @param obj 需要获取的对象源
 * @param keys 字段名称数组
 */
export const getAndSetLastObject = (obj: Record<string, any>, keys: string[]) => {
  if (keys.length > 1) {
    obj[keys[0]] ??= {};
  }
  return keys.reduce((pre, cur, index) => {
    // 如果当前不是最后一个，则创建对象
    if (index !== keys.length - 1) {
      pre[cur] ??= {};
      return pre[cur];
    }
    return pre;
  }, obj);
};
