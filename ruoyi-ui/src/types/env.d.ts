export interface ImportMetaEnv {
  /** 打包路径 根据项目不同按需配置 */
  readonly VITE_BASE_URL: string;
  /** 页面标题 */
  readonly VITE_APP_TITLE: string;
  /** 企业名称 */
  readonly VITE_APP_COMPANY_NAME: string;
  /** 应用访问路径 例如使用前缀 /admin/ */
  readonly VITE_APP_CONTEXT_PATH: string;
  /** Admin监控地址 */
  readonly VITE_APP_MONITOR_ADMIN: string;
  /** Job监控地址 */
  readonly VITE_APP_SNAILJOB_ADMIN: string;
  /** 若依管理系统/生产环境 api前缀 */
  readonly VITE_APP_BASE_API: string;
  /** 是否在打包时开启压缩，支持 gzip 和 brotli */
  readonly VITE_BUILD_COMPRESS: string;
  /** 客户端ID */
  readonly VITE_CLIENT_ID: string;
  /** 接口加密传输 RSA */
  readonly VITE_APP_RSA_PUBLIC_KEY: string;
  /** 接口加密功能开关(如需关闭 后端也必须对应关闭) */
  readonly VITE_APP_ENCRYPT: string;
  readonly VITE_APP_RSA_PRIVATE_KEY: string;
  /** websocket 开关 */
  readonly VITE_APP_WEBSOCKET: string;
  /** 账号 */
  readonly VITE_APP_ACCOUNT: string;
  /** 密码 */
  readonly VITE_APP_PASSWORD: string;
  /** SSE开关 */
  readonly VITE_APP_SSE: string;
}
