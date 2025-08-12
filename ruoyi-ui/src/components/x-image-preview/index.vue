<template>
  <t-image-viewer
    :key="props.src"
    :default-index="0"
    :mode="mode"
    :viewer-scale="viewerScale"
    :images="realPreviewSrcList"
    :z-index="zIndex"
    :title="title"
  >
    <template #trigger="{ open }">
      <t-image
        :key="realSrc"
        :src="realSrc"
        :style="{
          display: 'inline-block',
          width: realWidth,
          height: realHeight,
        }"
        :alt="alt"
        :fit="fit"
        :shape="shape"
        :position="position"
        :referrerpolicy="referrerPolicy"
        class="image-preview"
        :class="{ image_animation: animation }"
        :lazy="lazy"
        :gallery="realPreviewSrcList.length > 1"
        overlay-trigger="hover"
        @mouseenter="hover = true"
        @mouseleave="hover = false"
      >
        <template v-if="preview" #overlayContent>
          <div
            class="overlay"
            style="
              background: rgb(0 0 0 / 40%);
              color: #fff;
              height: 100%;
              display: flex;
              align-items: center;
              justify-content: center;
              border-radius: inherit;
              cursor: pointer;
            "
            @click.stop="open"
          >
            <t-tag style="background: transparent; color: #fff">
              <slot name="previewSlot">
                <browse-icon size="16" /> <span class="preview-text">{{ previewText }}</span>
              </slot>
            </t-tag>
          </div>
        </template>
        <template #error>
          <image-error-icon />
        </template>
      </t-image>
    </template>
  </t-image-viewer>
</template>

<script lang="tsx" setup>
import { BrowseIcon, ImageErrorIcon } from 'tdesign-icons-vue-next';
import type { TdImageProps, TdImageViewerProps } from 'tdesign-vue-next';
import type { PropType } from 'vue';
import { computed, ref, watchEffect } from 'vue';

import { listByIds } from '@/api/system/file';
import type { SysFileVo } from '@/api/system/model/fileModel';
import { getVisitUrl } from '@/utils/ruoyi';

const props = defineProps({
  // 显示地址
  src: {
    type: String,
  },
  // 是否可预览
  preview: {
    type: Boolean,
    default: true,
  },
  // 预览地址，为空则使用src显示地址
  previewSrc: {
    type: String,
  },
  // 预览文本，previewSlot插槽会覆盖它
  previewText: {
    type: String,
    default: '预览',
  },
  width: {
    type: [Number, String],
    default: '',
  },
  height: {
    type: [Number, String],
    default: '',
  },
  // 是否懒加载
  lazy: {
    type: Boolean,
    default: true,
  },
  // 是否运行动画
  animation: {
    type: Boolean,
    default: true,
  },
  zIndex: {
    type: Number,
    default: 4100,
  },
  // 图片描述
  alt: [String],
  // 图片填充模式
  fit: {
    type: String as PropType<TdImageProps['fit']>,
    default: 'cover',
  },
  // 图片圆角类型
  shape: {
    type: String as PropType<TdImageProps['shape']>,
    default: 'round',
  },
  // 等同于原生的 object-position 属性，可选值为 top right bottom left 或 string，可以自定义任何单位，px 或者 百分比
  position: {
    type: String as PropType<TdImageProps['position']>,
    default: 'center',
  },
  // img标签的原生属性，<a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Referrer-Policy">MDN 定义</a>
  referrerPolicy: {
    type: String as PropType<TdImageProps['referrerpolicy']>,
    default: 'strict-origin-when-cross-origin',
  },
  // 模态预览（modal）和非模态预览（modeless)。可选项：modal/modeless
  mode: {
    type: String as PropType<TdImageViewerProps['mode']>,
    default: 'modal',
  },
  // 限制预览器缩放的最小宽度和最小高度，仅 mode=modeless 时有效
  viewerScale: {
    type: Object as PropType<TdImageViewerProps['viewerScale']>,
  },
  // 预览标题。
  title: {
    type: String,
  },
});

const realSrc = ref('');
const realPreviewSrcList = ref<string[]>([]);
const hover = ref(false);
const scale = computed(() => (hover.value ? 1.1 : 1));

watchEffect(() => {
  const src = props.src;
  const previewSrc = props.previewSrc || props.src;
  const srcIdMode = src && /^([0-9],?)+$/.test(src);
  const previewSrcIdMode = previewSrc && /^([0-9],?)+$/.test(previewSrc);
  let ids: string[] = [];
  let srcFirst: string = '';
  let previewSrcArr: string[] = [];
  if (srcIdMode) {
    srcFirst = src.split(',')[0];
    ids.push(srcFirst);
  } else if (src) {
    if (src.search(/http(s)?:\/\//) === -1) {
      // 相对路径
      realSrc.value = src.split(/,(?=\/)/).map((value) => getVisitUrl(value))[0];
    } else {
      // http模式
      realSrc.value = src.split(/,(?=http)/)[0];
    }
  } else {
    realSrc.value = '';
  }
  if (previewSrcIdMode) {
    previewSrcArr = previewSrc.split(',');
    ids = ids.concat(previewSrcArr);
  } else if (previewSrc) {
    if (previewSrc.search(/http(s)?:\/\//) === -1) {
      // 相对路径
      realPreviewSrcList.value = previewSrc.split(/,(?=\/)/).map((value) => getVisitUrl(value));
    } else {
      // http模式
      realPreviewSrcList.value = previewSrc.split(/,(?=http)/);
    }
  } else {
    realPreviewSrcList.value = [];
  }
  // 使用id查询集中查询
  if (ids.length > 0) {
    // 使用set去重，此处不要求顺序性
    ids = Array.from(new Set(ids));
    // 使用去重后的id查询
    listByIds(ids).then((res) => {
      const dataMap = new Map<string, SysFileVo>();
      res.data.forEach((value) => {
        dataMap.set(value.fileId.toString(), value);
      });
      // src是id模式，则从结果中获取url
      if (srcIdMode) {
        const item = dataMap.get(srcFirst);
        if (item) {
          realSrc.value = getVisitUrl(item.previewUrl);
        } else {
          realSrc.value = '';
        }
      }
      // previewSrc是id模式，则从结果中获取url
      if (previewSrcIdMode) {
        const arr: string[] = [];
        previewSrcArr.forEach((id) => {
          const item = dataMap.get(id);
          if (item) arr.push(getVisitUrl(item.previewUrl));
        });
        realPreviewSrcList.value = arr;
      }
    });
  }
});

const realWidth = computed(() => (typeof props.width === 'string' ? props.width : `${props.width}px`));

const realHeight = computed(() => (typeof props.height === 'string' ? props.height : `${props.height}px`));
</script>
<style lang="less" scoped>
.image_animation:deep {
  img,
  .overlay {
    transition: all 0.3s;
    transform: scale(v-bind(scale));
  }
}

.image-preview {
  cursor: unset;
  :deep(img) {
    vertical-align: top;
  }
  .overlay {
    container: overlay-container / size;
    .preview-text {
      display: inline;
    }
  }
  @container overlay-container (width <= 50px) {
    .overlay .preview-text {
      display: none;
    }
    .t-tag .t-icon {
      margin-right: 0;
    }
  }
}

:deep .t-image__overlay-content {
  border-radius: inherit;
}
</style>
