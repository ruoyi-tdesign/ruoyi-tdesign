<template>
  <t-dialog
    v-model:visible="modelVisible"
    :header="title"
    :close-on-overlay-click="false"
    width="min(90%, 1400px)"
    top="5vh"
    attach="body"
    :confirm-btn="null"
  >
    <t-loading :loading="loading">
      <t-tabs v-model="activeName" placement="top">
        <t-tab-panel
          v-for="(value, key) in data"
          :key="value"
          :value="getLabel(key)"
          :label="getLabel(key)"
          class="content-scrollbar"
          style="max-height: calc(100vh - 5vh - 119px - 96px - 48px)"
        >
          <preview-code :code="value" :language="getLanguage(getLabel(key))" />
          <t-tooltip content="复制" placement="top">
            <t-button
              v-copyText="value"
              v-copyText:callback="copyTextSuccess"
              variant="text"
              theme="primary"
              style="position: absolute; top: 8px; right: 12px"
            >
              <template #icon> <file-copy-icon /></template>
              复制
            </t-button>
          </t-tooltip>
        </t-tab-panel>
      </t-tabs>
    </t-loading>
  </t-dialog>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'GenPreview',
});
const props = defineProps({
  visible: {
    type: Boolean,
    default: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  data: [Object] as PropType<Record<string, string>>,
});
const emit = defineEmits(['update:visible']);
import { FileCopyIcon } from 'tdesign-icons-vue-next';
import type { PropType } from 'vue';
import { computed, getCurrentInstance, ref, watch } from 'vue';

const { proxy } = getCurrentInstance();
const title = ref('代码预览');
const activeName = ref('domain.java');

const modelVisible = computed({
  get() {
    return props.visible;
  },
  set(val) {
    emit('update:visible', val);
  },
});
watch(
  () => props.data,
  (data) => {
    if (!data) return;
    const keys = Object.keys(data);
    const includes = keys.find((value) => value.includes(activeName.value));
    if (!includes) {
      activeName.value = getLabel(keys[0]);
    }
  },
);

// 获取vm名称
function getLabel(key: string) {
  return key.substring(key.lastIndexOf('/') + 1, key.indexOf('.vm'));
}

// 获取代码语言
function getLanguage(label: string) {
  return label.substring(label.lastIndexOf('.') + 1);
}

/** 复制代码成功 */
function copyTextSuccess() {
  proxy.$modal.msgSuccess('复制成功');
}
</script>
<style scoped lang="less"></style>
