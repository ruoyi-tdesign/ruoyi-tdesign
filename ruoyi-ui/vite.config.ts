import vue from '@vitejs/plugin-vue';
import vueJsx from '@vitejs/plugin-vue-jsx';
import path from 'path';
import AutoImport from 'unplugin-auto-import/vite';
import { TDesignResolver } from 'unplugin-vue-components/resolvers';
import Components from 'unplugin-vue-components/vite';
import { ConfigEnv, loadEnv, UserConfig } from 'vite';
import viteCompression from 'vite-plugin-compression';
import { viteMockServe } from 'vite-plugin-mock';
import VueDevTools from 'vite-plugin-vue-devtools';
import svgLoader from 'vite-svg-loader';

const CWD = process.cwd();

// https://vitejs.dev/config/
export default ({ mode }: ConfigEnv): UserConfig => {
  const { VITE_APP_CONTEXT_PATH, VITE_BUILD_COMPRESS } = loadEnv(mode, CWD);
  return {
    base: VITE_APP_CONTEXT_PATH,
    resolve: {
      alias: {
        // 设置路径
        '~': path.resolve(__dirname, './'),
        // 设置别名
        '@': path.resolve(__dirname, './src'),
      },
      // https://cn.vitejs.dev/config/#resolve-extensions
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue'],
    },

    css: {
      preprocessorOptions: {
        less: {
          modifyVars: {
            hack: `true; @import (reference) "${path.resolve('src/style/variables.less')}";`,
          },
          math: 'strict',
          javascriptEnabled: true,
        },
      },
    },

    plugins: [
      {
        name: 'singleHMR',
        handleHotUpdate({ modules }) {
          modules.forEach((m) => {
            m.importedModules?.clear();
            m.importers = new Set();
          });
          return modules;
        },
      },
      vue(),
      vueJsx(),
      VueDevTools(),
      viteMockServe({
        mockPath: 'mock',
        enable: false,
      }),
      AutoImport({
        resolvers: [
          TDesignResolver({
            library: 'vue-next',
          }),
        ],
      }),
      Components({
        resolvers: [
          TDesignResolver({
            library: 'vue-next',
          }),
        ],
      }),
      svgLoader(),
      viteCompression({
        // threshold: 1024000, // 对大于 1mb 的文件进行压缩
        algorithm: VITE_BUILD_COMPRESS === 'brotli' ? 'brotliCompress' : 'gzip',
        ext: VITE_BUILD_COMPRESS === 'brotli' ? '.br' : '.gz',
        deleteOriginFile: false,
        compressionOptions: {
          level: 9,
          memLevel: 9,
        },
      }),
    ],

    server: {
      port: 3002,
      host: '0.0.0.0',
      proxy: {
        '/api': 'http://127.0.0.1:3000/',
        // https://cn.vitejs.dev/config/#server-proxy
        '/dev-api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          rewrite: (p) => p.replace(/^\/dev-api/, ''),
        },
      },
      open: true,
    },
  };
};
