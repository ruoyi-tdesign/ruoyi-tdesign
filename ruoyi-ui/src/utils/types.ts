// 定义一个工具类型，筛选出 T 中值为 string 类型的键
export type StringKeys<T> = AnyKeys<T, string>;

// 定义一个工具类型，筛选出 T 中值为 指定 类型的键
export type AnyKeys<T, U> = {
  [K in keyof T]: T[K] extends U ? K : never;
}[keyof T];
