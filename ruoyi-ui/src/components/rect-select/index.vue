<template>
  <div
    ref="boxRef"
    class="content-scrollbar rect-box"
    :style="{
      maxHeight: boxMaxHeight,
    }"
    v-bind="$attrs"
    @mousedown.left="handleMousedown($event)"
    @scroll="handleScroll"
  >
    <slot />
    <div
      v-if="effectiveActive"
      class="rect-select"
      :class="{ 'rect-select-animate': borderAnimate }"
      :style="{
        width: `${rect.width}px`,
        height: `${rect.height}px`,
        top: `${rect.top}px`,
        left: `${rect.left}px`,
      }"
    />
  </div>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'RectSelect',
});
const props = defineProps({
  borderAnimate: {
    type: Boolean,
    default: false,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  boxMaxHeight: {
    type: String,
    default: '',
  },
});

const emit = defineEmits<{
  (e: 'change', checkedIndexes: number[]): void;
}>();

import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue';

// 矩形选取的相对距离
const rect = computed(() => {
  return {
    width: Math.abs(current.value.x - start.value.x),
    height: Math.abs(current.value.y - start.value.y),
    top: Math.min(start.value.y, current.value.y),
    left: Math.min(start.value.x, current.value.x),
  };
});
const domRects = ref([]);
const active = ref(false);
// xy：相对距离；pageXY:相对浏览器左上角距离
const start = ref({ x: 0, y: 0, pageX: 0, pageY: 0 });
const current = ref({ x: 0, y: 0, pageX: 0, pageY: 0 });
const boxRef = ref<HTMLElement>();
const distance = ref(5);
const scrollPadding = ref(40);
// 盒子的绝对距离
const boxRect = ref({ left: 0, top: 0, right: 0, bottom: 0 });
const effectiveActive = computed(() => {
  return active.value && !props.disabled;
});

let scrollInterval: ReturnType<typeof setInterval> = null;

/**
 * 鼠标按下事件
 * @param event
 */
function handleMousedown(event: MouseEvent) {
  if (props.disabled) {
    return;
  }
  active.value = true;
  initBoxRect();
  // 启用区域选择时，获取一次dom元素的区域
  nextTick(() => {
    renderChildrenRects();
  });
  start.value.pageX = event.x;
  start.value.pageY = event.y;
  // 绝对距离转为相对距离
  start.value.x = event.x - boxRect.value.left;
  start.value.y = event.y - boxRect.value.top + boxRef.value.scrollTop;
  handleMouseMove(event);
}

/**
 * 鼠标移动事件
 * @param event
 */
function handleMouseMove(event: MouseEvent) {
  if (effectiveActive.value) {
    // 绝对距离转为相对距离
    current.value.pageX = event.x;
    current.value.pageY = event.y;
    current.value.x = range(current.value.pageX - boxRect.value.left, 0, boxRect.value.right - boxRect.value.left);
    current.value.y = range(
      current.value.pageY - boxRect.value.top + boxRef.value.scrollTop,
      boxRef.value.scrollTop,
      boxRect.value.bottom - boxRect.value.top + boxRef.value.scrollTop,
    );
    triggerChange();
    // 触发滚动定时器
    if (isTriggerScrollTop() || isTriggerScrollBottom()) {
      // 开启计时器，如果计时器不存在
      if (scrollInterval === null) {
        scrollInterval = setInterval(() => {
          triggerScroll();
        }, 20);
      }
    } else {
      clearInterval(scrollInterval);
      scrollInterval = null;
    }
  }
}

/**
 * 处理滚动事件
 */
function handleScroll() {
  if (effectiveActive.value) {
    // 重新计算相对距离
    current.value.x = range(current.value.pageX - boxRect.value.left, 0, boxRect.value.right - boxRect.value.left);
    current.value.y = range(
      current.value.pageY - boxRect.value.top + boxRef.value.scrollTop,
      boxRef.value.scrollTop,
      boxRect.value.bottom - boxRect.value.top + boxRef.value.scrollTop,
    );
    // 滚动时，重新获取一次dom元素的区域
    // renderChildrenRects();
    triggerChange();
  }
}

/** 控制触碰到顶部或底部时的滚动 */
function triggerScroll() {
  const rate = 10;
  const scrollTop = boxRef.value.scrollTop;
  const top = Math.abs(current.value.y - boxRef.value.scrollTop);
  if (isTriggerScrollTop()) {
    const speed = distance.value * Math.max((scrollPadding.value - top) / rate, 1);
    boxRef.value.scrollTo({
      top: Math.max(scrollTop - speed, 0),
      left: boxRef.value.scrollLeft,
      behavior: 'instant',
    });
  } else if (isTriggerScrollBottom()) {
    const bottom = Math.abs(current.value.y + boxRect.value.top - boxRef.value.scrollTop - boxRect.value.bottom);
    const speed = distance.value * Math.max((scrollPadding.value - bottom) / rate, 1);
    boxRef.value.scrollTo({
      top: Math.min(scrollTop + speed, boxRef.value.scrollHeight - boxRef.value.clientHeight),
      left: boxRef.value.scrollLeft,
      behavior: 'instant',
    });
  }
}

/** 是否触发触顶滚动 */
function isTriggerScrollTop() {
  const scrollTop = boxRef.value.scrollTop;
  const top = Math.abs(current.value.y - boxRef.value.scrollTop);
  return top < scrollPadding.value && scrollTop !== 0;
}

/** 是否触发触底滚动 */
function isTriggerScrollBottom() {
  const scrollTop = boxRef.value.scrollTop;
  const bottom = Math.abs(current.value.y + boxRect.value.top - boxRef.value.scrollTop - boxRect.value.bottom);
  return bottom <= scrollPadding.value && scrollTop + boxRef.value.clientHeight < boxRef.value.scrollHeight;
}

/** 限制一个数在范围内 */
function range(num: number, min: number, max: number) {
  if (num < min) {
    return min;
  }
  if (num > max) {
    return max;
  }
  return num;
}

/**
 * 鼠标松开事件
 */
function handleMouseUp() {
  clearInterval(scrollInterval);
  scrollInterval = null;
  active.value = false;
}

/**
 * 触发变更事件
 */
function triggerChange() {
  const rec1 = [
    rect.value.left,
    rect.value.top,
    rect.value.left + rect.value.width,
    rect.value.top + rect.value.height,
  ];
  const checked: number[] = [];
  domRects.value.forEach((rec2, index) => {
    if (isRectangleOverlap(rec1, rec2)) {
      checked.push(index);
    }
  });
  emit('change', checked);
}

/** 获取子元素的区域 */
function renderChildrenRects() {
  const rects = [];
  const children: HTMLCollection = boxRef.value!.children;
  for (let i = 0; i < children.length - 1; i++) {
    const element = children.item(i);
    const domRect = element.getBoundingClientRect();
    // 转为相对距离矩形框
    const rec = [
      domRect.left - boxRect.value.left,
      domRect.top - boxRect.value.top + boxRef.value.scrollTop,
      domRect.right - boxRect.value.left,
      domRect.bottom - boxRect.value.top + boxRef.value.scrollTop,
    ];
    rects.push(rec);
  }
  domRects.value = rects;
}

/** 初始化可选区的区域 */
function initBoxRect() {
  const clientRect = boxRef.value.getBoundingClientRect();
  boxRect.value.left = clientRect.left;
  boxRect.value.top = clientRect.top;
  boxRect.value.right = clientRect.right;
  boxRect.value.bottom = clientRect.bottom;
}

/**
 * 判断两个矩形是否重叠
 * 参数类型[x1,y1,x2,y2]，表示左上角与右下角坐标
 * @param rec1
 * @param rec2
 */
function isRectangleOverlap(rec1: number[], rec2: number[]) {
  // 判断矩形区域长度不小于0
  if (rec1[0] === rec1[2] || rec1[1] === rec1[3] || rec2[0] === rec2[2] || rec2[1] === rec2[3]) {
    return false;
  }
  // 判断矩形1是否在矩形2的四周，如果不在四周则存在重叠区域
  return !(
    rec1[2] < rec2[0] || // left
    rec1[3] < rec2[1] || // bottom
    rec1[0] > rec2[2] || // right
    rec1[1] > rec2[3]
  ); // top
}

onMounted(() => {
  window.addEventListener('mouseup', handleMouseUp);
  window.addEventListener('mousemove', handleMouseMove);
});
onUnmounted(() => {
  window.removeEventListener('mouseup', handleMouseUp);
  window.removeEventListener('mousemove', handleMouseMove);
});
</script>
<style scoped lang="less">
.rect-box {
  max-height: calc(100vh - 316px);
  //overflow: hidden;
  overflow-x: hidden;
  position: relative;
}

.rect-select {
  //z-index: 999999; //根据情况自己拿捏
  position: absolute;
  box-sizing: border-box;
  border: 1px solid var(--td-brand-color);
  background-color: var(--td-brand-color-hover);
  opacity: 0.3;

  /* 边框虚线滚动动画特效 */
  &-animate {
    border: none;
    background:
      linear-gradient(90deg, var(--td-brand-color) 60%, transparent 60%) repeat-x left top/10px 1px,
      linear-gradient(0deg, var(--td-brand-color) 60%, transparent 60%) repeat-y right top/1px 10px,
      linear-gradient(90deg, var(--td-brand-color) 60%, transparent 60%) repeat-x right bottom/10px 1px,
      linear-gradient(0deg, var(--td-brand-color) 60%, transparent 60%) repeat-y left bottom/1px 10px,
      var(--td-brand-color-hover);
    animation: border-animate 0.382s infinite linear;
  }

  @keyframes border-animate {
    0% {
      background-position:
        left top,
        right top,
        right bottom,
        left bottom;
    }

    100% {
      background-position:
        left 10px top,
        right top 10px,
        right 10px bottom,
        left bottom 10px;
    }
  }
}
</style>
