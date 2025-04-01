import path from 'node:path';

import vue from '@vitejs/plugin-vue';
import vueJsx from '@vitejs/plugin-vue-jsx';
import copy from 'rollup-plugin-copy';
import UnoCSS from 'unocss/vite';
import AutoImport from 'unplugin-auto-import/vite';
import { TDesignResolver } from 'unplugin-vue-components/resolvers';
import Components from 'unplugin-vue-components/vite';
import type { ConfigEnv, UserConfig } from 'vite';
import { loadEnv } from 'vite';
import viteCompression from 'vite-plugin-compression';
import { viteMockServe } from 'vite-plugin-mock';
import prismjs from 'vite-plugin-prismjs';
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
      UnoCSS(),
      copy({
        targets: [
          {
            src: 'node_modules/tinymce/skins/ui/oxide/*.css',
            dest: 'public/tinymce/skins/ui/oxide/', // 目标public目录下的子目录
          },
          {
            src: 'node_modules/tinymce/skins/ui/oxide-dark/*.css',
            dest: 'public/tinymce/skins/ui/oxide-dark/', // 目标public目录下的子目录
          },
        ],
        copyOnce: true,
        verbose: true,
      }),
      prismjs({
        // 放置常用的代码
        languages: [
          'markup',
          'bash',
          'javascript',
          'java',
          'json',
          'css',
          'c',
          'csharp',
          'cpp',
          'diff',
          'dart',
          'php',
          'python',
          'r',
          'rust',
          'ruby',
          'go',
          'graphql',
          'ini',
          'less',
          'lua',
          'kotlin',
          'objectivec',
          'scss',
          'shell',
          'sql',
          'swift',
          'yaml',
          'typescript',
        ],
        // languages: 'all',
        plugins: ['line-numbers'], // 配置显示行号插件
        theme: 'default', // 主题名称
        css: true,
      }),
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
      vue({
        script: {
          defineModel: true,
        },
      }),
      vueJsx(),
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
        imports: ['vue-router', 'vue', 'pinia'],
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
          ws: true,
          rewrite: (p) => p.replace(/^\/dev-api/, ''),
        },
      },
      open: true,
    },
    // 预编译
    optimizeDeps: {
      include: ['vue', 'vue-router', 'pinia', 'axios', '@vueuse/core', 'vue-i18n', 'image-conversion'],
    },
  };
};
