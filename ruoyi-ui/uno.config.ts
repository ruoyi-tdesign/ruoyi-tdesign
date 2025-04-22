import { defineConfig, presetUno, transformerVariantGroup } from 'unocss';

export default defineConfig({
  // ...UnoCSS options
  // presets: [], // 清除默认预设
  // 预设，默认包含 UnoCSS 的预设，可以添加其他预设
  presets: [presetUno()],
  // 变体组，允许使用类似 tailwind 的变体组
  transformers: [transformerVariantGroup()],
});
