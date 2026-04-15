<template>
  <html-image-preview v-if="imagePreview">
    <component :is="content" />
  </html-image-preview>
  <template v-else>
    <component :is="content" />
  </template>
</template>
<script lang="tsx" setup>
import Prism from 'prismjs';
import { nextTick, onMounted, onUpdated, ref } from 'vue';

const props = defineProps({
  // html文本
  htmlText: {
    type: String,
    required: true,
  },
  // 是否显示代码块行号
  isShowLineNumbers: {
    type: Boolean,
    default: true,
  },
  // pre代码块最大高度
  codeMaxHeight: {
    type: String,
  },
  // 是否开启图片预览
  imagePreview: {
    type: Boolean,
    default: false,
  },
  // 图片懒加载 (使用浏览器原生懒加载)
  imageLazy: {
    type: Boolean,
    default: false,
  },
});

const html = ref<HTMLElement>();

const searchImg = /<img [^>]*>/g;
const htmlText = computed(() => {
  if (props.imageLazy && props.htmlText) {
    // 使用正则，兼容非客户端环境
    return props.htmlText?.replaceAll(searchImg, (substring) => {
      if (substring.search('loading=') === -1) {
        return `${substring.slice(0, 5)} loading="lazy" ${substring.slice(5)}`;
      }
      return substring;
    });
  }
  return props.htmlText;
});

const content = () => {
  return (
    <div class="editor_preview line-numbers">
      <div ref={html} v-html={htmlText.value}></div>
    </div>
  );
};

/**
 * 渲染元素
 */
function renderHtml() {
  nextTick(() => {
    Prism.highlightAllUnder(html.value!);
    html.value?.querySelectorAll('pre[class*="language-"]').forEach((value) => {
      if (props.isShowLineNumbers) {
        if (!value.classList.contains('line-numbers')) {
          value.classList.add('line-numbers');
        }
      } else if (value.classList.contains('line-numbers')) {
        value.classList.remove('line-numbers');
      }
      if (props.codeMaxHeight) {
        if (!value.classList.contains('content-scrollbar')) {
          value.classList.add('content-scrollbar');
        }
      }
    });
  });
}

// 注册语言别名
function register() {
  // vue使用markup解析
  Prism.languages.vue = Prism.languages.markup;
}

onMounted(() => {
  register();
  renderHtml();
});
onUpdated(() => {
  renderHtml();
});
</script>
<style scoped lang="less">
.editor_preview {
  :deep(ul),
  :deep(dl),
  :deep(li),
  :deep(dd),
  :deep(dt) {
    margin: revert;
    padding: revert;
    list-style: revert;
  }

  :deep(figure),
  :deep(h1),
  :deep(h2),
  :deep(h3),
  :deep(h4),
  :deep(h5),
  :deep(h6),
  :deep(p) {
    margin: revert;
  }

  color: var(--td-text-color-primary);

  > :deep(div) {
    font-family:
      -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue',
      sans-serif;
    line-height: 1.4;
    margin: 1rem;

    pre.content-scrollbar {
      max-height: v-bind(codeMaxHeight);
    }

    pre.line-numbers {
      white-space: pre-wrap;
    }

    table {
      border-collapse: collapse;
    }

    table:not([cellpadding]) td,
    table:not([cellpadding]) th {
      padding: 0.4rem;
    }

    table[border]:not([border='0'], [style*='border-width']) td,
    table[border]:not([border='0'], [style*='border-width']) th {
      border-width: 1px;
    }

    table[border]:not([border='0'], [style*='border-style']) td,
    table[border]:not([border='0'], [style*='border-style']) th {
      border-style: solid;
    }

    table[border]:not([border='0'], [style*='border-color']) td,
    table[border]:not([border='0'], [style*='border-color']) th {
      border-color: #ccc;
    }

    figure {
      display: table;
      margin: 1rem auto;
    }

    figure figcaption {
      color: #999;
      display: block;
      margin-top: 0.25rem;
      text-align: center;
    }

    hr {
      border-color: #ccc;
      border-style: solid;
      border-width: 1px 0 0;
    }

    code {
      border-radius: 3px;
      padding: 0.1rem 0.2rem;
    }

    blockquote p:not([dir='rtl']) {
      border-left: 2px solid #ccc;
      margin-left: 1.5rem;
      padding-left: 1rem;
    }

    blockquote p[dir='rtl'] {
      border-right: 2px solid #ccc;
      margin-right: 1.5rem;
      padding-right: 1rem;
    }
  }
}
</style>
