<template>
  <t-card size="small">
    <div class="container">
      <div class="left-board">
        <div class="logo-wrapper">
          <div class="logo">Form Generator</div>
        </div>
        <my-scrollbar class="left-scrollbar">
          <div class="components-list">
            <div class="components-title font-bold">输入型组件</div>
            <draggable
              class="components-draggable"
              :list="inputComponents"
              :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
              :clone="cloneComponent"
              :sort="false"
              @end="onEnd"
            >
              <template #item="{ element }">
                <div class="components-item" @click="addComponent(element)">
                  <div class="components-body">
                    <r-icon :name="element.tagIcon" />
                    {{ element.label }}
                  </div>
                </div>
              </template>
            </draggable>
            <div class="components-title font-bold">选择型组件</div>
            <draggable
              class="components-draggable"
              :list="selectComponents"
              :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
              :clone="cloneComponent"
              :sort="false"
              @end="onEnd"
            >
              <template #item="{ element }">
                <div class="components-item" @click="addComponent(element)">
                  <div class="components-body">
                    <r-icon :name="element.tagIcon" />
                    {{ element.label }}
                  </div>
                </div>
              </template>
            </draggable>
            <div class="components-title font-bold">布局型组件</div>
            <draggable
              class="components-draggable"
              :list="layoutComponents"
              :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
              :clone="cloneComponent"
              :sort="false"
              @end="onEnd"
            >
              <template #item="{ element }">
                <div class="components-item" @click="addComponent(element)">
                  <div class="components-body">
                    <r-icon :name="element.tagIcon" />
                    {{ element.label }}
                  </div>
                </div>
              </template>
            </draggable>
          </div>
        </my-scrollbar>
      </div>

      <div class="center-board">
        <t-space class="flex! justify-end p-x">
          <my-link @click="download">
            <template #prefix-icon><download1-icon /></template>导出vue文件
          </my-link>
          <my-link @click="openCopy">
            <template #prefix-icon><copy-icon /></template>复制代码
          </my-link>
          <my-link @click="empty">
            <template #prefix-icon><delete-icon /></template>清空
          </my-link>
        </t-space>
        <my-scrollbar class="center-scrollbar">
          <t-row class="center-board-row" :gutter="formConf.gutter">
            <t-form
              :size="formConf.size"
              :label-position="formConf.labelPosition"
              :disabled="formConf.disabled"
              :label-width="formConf.labelWidth + 'px'"
            >
              <draggable class="drawing-board" :list="drawingList" :animation="340" group="componentsGroup">
                <template #item="{ element, index }">
                  <draggable-item
                    :key="element.renderKey"
                    :drawing-list="drawingList"
                    :element="element"
                    :index="index"
                    :active-id="activeId"
                    :form-conf="formConf"
                    @active-item="activeFormItem"
                    @copy-item="drawingItemCopy"
                    @delete-item="drawingItemDelete"
                  />
                </template>
              </draggable>
              <div v-show="!drawingList.length" class="empty-info">从左侧拖入或点选组件进行表单设计</div>
            </t-form>
          </t-row>
        </my-scrollbar>
      </div>

      <!--    <right-panel
      :active-data="activeData"
      :form-conf="formConf"
      :show-field="!!drawingList.length"
      @tag-change="tagChange"
    />-->

      <code-type-dialog
        v-model:visible="dialogVisible"
        title="选择生成类型"
        :show-file-name="showFileName"
        @confirm="generate"
      />
      <input id="copyNode" type="hidden" />
    </div>
  </t-card>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'Build',
});
import { useClipboard } from '@vueuse/core';
// import beautifier from 'js-beautify';
import { CopyIcon, DeleteIcon, Download1Icon } from 'tdesign-icons-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import draggable from 'vuedraggable';

import { titleCase } from '@/utils/ruoyi';

import CodeTypeDialog from './CodeTypeDialog.vue';
import DraggableItem from './DraggableItem.vue';
import { beautifierConf, formConf, inputComponents, layoutComponents, selectComponents } from './generator/config';
import { makeUpCss } from './generator/css';
import drawingDefault from './generator/drawingDefault';
import { cssStyle, makeUpHtml, vueScript, vueTemplate } from './generator/html';
import { makeUpJs } from './generator/js';
// import RightPanel from './RightPanel.vue';

const { proxy } = getCurrentInstance();

const oldActiveId = ref();
const tempActiveData = ref();

// const logo;
const operationType = ref<string>('');
const idGlobal = ref(100);
const drawingList = ref(drawingDefault);
const activeId = ref(drawingDefault[0].formId);
const drawerVisible = ref(false);
const formData = ref({});
const dialogVisible = ref(false);
const generateConf = ref(null);
const showFileName = ref(false);
const activeData = ref(drawingDefault[0]);

watch(
  () => activeData.value.label,
  (val, oldVal) => {
    if (activeData.value.placeholder === undefined || !activeData.value.tag || oldActiveId.value !== activeId.value) {
      return;
    }
    activeData.value.placeholder = activeData.value.placeholder.replace(oldVal, '') + val;
  },
);
watchEffect(() => {
  oldActiveId.value = activeId.value;
});

const handleCopy = () => {
  const sourceText = generateCode();
  const { copy } = useClipboard({ source: sourceText });
  copy()
    .then(() => {
      MessagePlugin.closeAll();
      MessagePlugin.success('代码已复制到剪切板，可粘贴。');
    })
    .catch(() => {
      MessagePlugin.closeAll();
      MessagePlugin.error('代码复制失败');
    });
};

function activeFormItem(element) {
  activeData.value = element;
  activeId.value = element.formId;
}

function onEnd(obj, a) {
  if (obj.from !== obj.to) {
    activeData.value = tempActiveData.value;
    activeId.value = idGlobal.value;
  }
}

function addComponent(item) {
  const clone = cloneComponent(item);
  drawingList.value.push(clone);
  activeFormItem(clone);
}

function cloneComponent(origin) {
  const clone = JSON.parse(JSON.stringify(origin));
  clone.formId = ++idGlobal.value;
  clone.span = formConf.span;
  clone.renderKey = +new Date(); // 改变renderKey后可以实现强制更新组件
  if (!clone.layout) clone.layout = 'colFormItem';
  if (clone.layout === 'colFormItem') {
    clone.vModel = `field${idGlobal.value}`;
    // eslint-disable-next-line no-unused-expressions
    clone.placeholder !== undefined && (clone.placeholder += clone.label);
    tempActiveData.value = clone;
  } else if (clone.layout === 'rowFormItem') {
    delete clone.label;
    clone.componentName = `row${idGlobal.value}`;
    clone.gutter = formConf.gutter;
    tempActiveData.value = clone;
  }
  return tempActiveData.value;
}

function assembleFormData() {
  formData.value = {
    fields: JSON.parse(JSON.stringify(drawingList.value)),
    ...formConf,
  };
}

function execRun(data) {
  assembleFormData();
  drawerVisible.value = true;
}

function execDownload(data) {
  const codeStr = generateCode();
  const blob = new Blob([codeStr], { type: 'text/plain;charset=utf-8' });
  proxy.$download.saveAs(blob, data.fileName);
}

function execCopy(data) {
  document.getElementById('copyNode').click();
}

function generate(data) {
  const execFunStr = `exec${titleCase(operationType.value)}`;
  let func: Function;
  if (execFunStr === 'execRun') {
    func = execRun;
  } else if (execFunStr === 'execDownload') {
    func = execDownload;
  } else if (execFunStr === 'execCopy') {
    func = execCopy;
  }
  generateConf.value = data;
  func?.(data);
}

function empty() {
  proxy.$modal.confirm('确定要清空所有组件吗？', () => {
    drawingList.value = [];
  });
}

function drawingItemCopy(item, parent) {
  let clone = JSON.parse(JSON.stringify(item));
  clone = createIdAndKey(clone);
  parent.push(clone);
  activeFormItem(clone);
}

function createIdAndKey(item) {
  item.formId = ++idGlobal.value;
  item.renderKey = +new Date();
  if (item.layout === 'colFormItem') {
    item.vModel = `field${idGlobal.value}`;
  } else if (item.layout === 'rowFormItem') {
    item.componentName = `row${idGlobal.value}`;
  }
  if (Array.isArray(item.children)) {
    item.children = item.children.map((childItem) => createIdAndKey(childItem));
  }
  return item;
}

function drawingItemDelete(index, parent) {
  parent.splice(index, 1);
  nextTick(() => {
    const len = drawingList.value.length;
    if (len) {
      activeFormItem(drawingList.value[len - 1]);
    }
  });
}

function generateCode() {
  const { type } = generateConf.value;
  assembleFormData();
  const script = vueScript(makeUpJs(formData.value, type));
  const html = vueTemplate(makeUpHtml(formData.value, type));
  const css = cssStyle(makeUpCss(formData.value));
  return html + script + css;
  // return beautifier.html(html + script + css, beautifierConf.html);
}

function download() {
  dialogVisible.value = true;
  showFileName.value = true;
  operationType.value = 'download';
}

function run() {
  dialogVisible.value = true;
  showFileName.value = false;
  operationType.value = 'run';
}

function openCopy() {
  dialogVisible.value = true;
  showFileName.value = false;
  operationType.value = 'copy';
}

function tagChange(newTag) {
  newTag = cloneComponent(newTag);
  newTag.vModel = activeData.value.vModel;
  newTag.formId = activeId.value;
  newTag.span = activeData.value.span;
  delete activeData.value.tag;
  delete activeData.value.tagIcon;
  // delete activeData.value.document;
  Object.keys(newTag).forEach((key) => {
    if (activeData.value[key] !== undefined && typeof activeData.value[key] === typeof newTag[key]) {
      newTag[key] = activeData.value[key];
    }
  });
  activeData.value = newTag;
  updateDrawingList(newTag, drawingList.value);
}

function updateDrawingList(newTag, list) {
  const index = list.findIndex((item) => item.formId === activeId.value);
  if (index > -1) {
    list.splice(index, 1, newTag);
  } else {
    list.forEach((item) => {
      if (Array.isArray(item.children)) updateDrawingList(newTag, item.children);
    });
  }
}

onBeforeMount(() => {
  // 防止 firefox 下 拖拽 会新打卡一个选项卡
  document.body.ondrop = (event) => {
    event.preventDefault();
    event.stopPropagation();
  };
});
</script>

<style lang="less" scoped>
.editor-tabs {
  background: #121315;
  .el-tabs__header {
    margin: 0;
    border-bottom-color: #121315;
    .el-tabs__nav {
      border-color: #121315;
    }
  }
  .el-tabs__item {
    height: 32px;
    line-height: 32px;
    color: #888a8e;
    border-left: 1px solid #121315 !important;
    background: #363636;
    margin-right: 5px;
    user-select: none;
  }
  .el-tabs__item.is-active {
    background: #1e1e1e;
    border-bottom-color: #1e1e1e !important;
    color: #fff;
  }
  .el-icon-edit {
    color: #f1fa8c;
  }
  .el-icon-document {
    color: #a95812;
  }
}

// home
.right-scrollbar {
  .el-scrollbar__view {
    padding: 12px 18px 15px 15px;
  }
}
.left-scrollbar .el-scrollbar__wrap {
  box-sizing: border-box;
  overflow-x: hidden !important;
  margin-bottom: 0 !important;
}
.center-tabs {
  .el-tabs__header {
    margin-bottom: 0 !important;
  }
  .el-tabs__item {
    width: 50%;
    text-align: center;
  }
  .el-tabs__nav {
    width: 100%;
  }
}
.reg-item {
  padding: 12px 6px;
  background: #f8f8f8;
  position: relative;
  border-radius: 4px;
  .close-btn {
    position: absolute;
    right: -6px;
    top: -6px;
    display: block;
    width: 16px;
    height: 16px;
    line-height: 16px;
    background: rgba(0, 0, 0, 0.2);
    border-radius: 50%;
    color: #fff;
    text-align: center;
    z-index: 1;
    cursor: pointer;
    font-size: 12px;
    &:hover {
      background: rgba(210, 23, 23, 0.5);
    }
  }
  & + .reg-item {
    margin-top: 18px;
  }
}

.custom-tree-node {
  width: 100%;
  font-size: 14px;
  .node-operation {
    float: right;
  }
  i[class*='el-icon'] + i[class*='el-icon'] {
    margin-left: 6px;
  }
  .el-icon-plus {
    color: #409eff;
  }
  .el-icon-delete {
    color: #157a0c;
  }
}

.left-scrollbar .el-scrollbar__view {
  overflow-x: hidden;
}

.el-rate {
  display: inline-block;
  vertical-align: text-top;
}
.el-upload__tip {
  line-height: 1.2;
}

@selectedColor: #f6f7ff;
@lighterBlue: #409eff;

.container {
  position: relative;
  width: 100%;
  height: 100%;
}

.components-list {
  padding: 8px;
  box-sizing: border-box;
  height: 100%;
  .components-item {
    display: inline-block;
    width: 48%;
    margin: 1%;
    transition: transform 0ms !important;
  }
}
.components-draggable {
  padding-bottom: 20px;
}
.components-title {
  font-size: 14px;
  color: #222;
  margin: 6px 2px;
  .svg-icon {
    color: #666;
    font-size: 18px;
  }
}

.components-body {
  padding: 8px 10px;
  background: @selectedColor;
  font-size: 12px;
  cursor: move;
  border: 1px dashed @selectedColor;
  border-radius: 3px;
  .svg-icon {
    color: #777;
    font-size: 15px;
  }
  &:hover {
    border: 1px dashed #787be8;
    color: #787be8;
    .svg-icon {
      color: #787be8;
    }
  }
}

.left-board {
  width: 260px;
  position: absolute;
  left: 0;
  top: 0;
  height: 100vh;
}
.left-scrollbar {
  height: calc(100vh - 42px);
  overflow: hidden;
}
.center-scrollbar {
  height: calc(100vh - 42px);
  overflow: hidden;
  border-left: 1px solid #f1e8e8;
  border-right: 1px solid #f1e8e8;
  box-sizing: border-box;
}
.center-board {
  height: 100vh;
  width: auto;
  margin: 0 350px 0 260px;
  box-sizing: border-box;
}
.empty-info {
  position: absolute;
  top: 46%;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 18px;
  color: #ccb1ea;
  letter-spacing: 4px;
}
.logo-wrapper {
  position: relative;
  height: 42px;
  background: #fff;
  border-bottom: 1px solid #f1e8e8;
  box-sizing: border-box;
}
.logo {
  position: absolute;
  left: 12px;
  top: 6px;
  line-height: 30px;
  color: #00afff;
  font-weight: 600;
  font-size: 17px;
  white-space: nowrap;
  > img {
    width: 30px;
    height: 30px;
    vertical-align: top;
  }
  .github {
    display: inline-block;
    vertical-align: sub;
    margin-left: 15px;
    > img {
      height: 22px;
    }
  }
}

.center-board-row {
  padding: 12px 12px 15px 12px;
  box-sizing: border-box;
  & > .el-form {
    // 69 = 12+15+42
    height: calc(100vh - 69px);
  }
}
.drawing-board {
  height: 100%;
  position: relative;
  .components-body {
    padding: 0;
    margin: 0;
    font-size: 0;
  }
  .sortable-ghost {
    position: relative;
    display: block;
    overflow: hidden;
    &::before {
      content: ' ';
      position: absolute;
      left: 0;
      right: 0;
      top: 0;
      height: 3px;
      background: rgb(89, 89, 223);
      z-index: 2;
    }
  }
  .components-item.sortable-ghost {
    width: 100%;
    height: 60px;
    background-color: @selectedColor;
  }
  .active-from-item {
    & > .el-form-item {
      background: @selectedColor;
      border-radius: 6px;
    }
    & > .drawing-item-copy,
    & > .drawing-item-delete {
      display: initial;
    }
    & > .component-name {
      color: @lighterBlue;
    }
  }
  .el-form-item {
    margin-bottom: 15px;
  }
}
.drawing-item {
  position: relative;
  cursor: move;
  &.unfocus-bordered:not(.activeFromItem) > div:first-child {
    border: 1px dashed #ccc;
  }
  .el-form-item {
    padding: 12px 10px;
  }
}
.drawing-row-item {
  position: relative;
  cursor: move;
  box-sizing: border-box;
  border: 1px dashed #ccc;
  border-radius: 3px;
  padding: 0 2px;
  margin-bottom: 15px;
  .drawing-row-item {
    margin-bottom: 2px;
  }
  .el-col {
    margin-top: 22px;
  }
  .el-form-item {
    margin-bottom: 0;
  }
  .drag-wrapper {
    min-height: 80px;
  }
  &.active-from-item {
    border: 1px dashed @lighterBlue;
  }
  .component-name {
    position: absolute;
    top: 0;
    left: 0;
    font-size: 12px;
    color: #bbb;
    display: inline-block;
    padding: 0 6px;
  }
}
.drawing-item,
.drawing-row-item {
  &:hover {
    & > .el-form-item {
      background: @selectedColor;
      border-radius: 6px;
    }
    & > .drawing-item-copy,
    & > .drawing-item-delete {
      display: initial;
    }
  }
  & > .drawing-item-copy,
  & > .drawing-item-delete {
    display: none;
    position: absolute;
    top: -10px;
    width: 22px;
    height: 22px;
    line-height: 22px;
    text-align: center;
    border-radius: 50%;
    font-size: 12px;
    border: 1px solid;
    cursor: pointer;
    z-index: 1;
  }
  & > .drawing-item-copy {
    right: 56px;
    border-color: @lighterBlue;
    color: @lighterBlue;
    background: #fff;
    &:hover {
      background: @lighterBlue;
      color: #fff;
    }
  }
  & > .drawing-item-delete {
    right: 24px;
    border-color: #f56c6c;
    color: #f56c6c;
    background: #fff;
    &:hover {
      background: #f56c6c;
      color: #fff;
    }
  }
}
</style>
