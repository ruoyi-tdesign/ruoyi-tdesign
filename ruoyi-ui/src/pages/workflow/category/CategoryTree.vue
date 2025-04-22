<template>
  <div>
    <div class="head-container">
      <t-row style="width: 100%" :gutter="20">
        <t-col :span="10">
          <t-input v-model="categoryName" placeholder="请输入流程分类名称" clearable style="margin-bottom: 20px">
            <template #prefixIcon>
              <search-icon />
            </template>
          </t-input>
        </t-col>
        <t-col :span="2">
          <t-button shape="square" variant="outline" @click="getTreeselect">
            <template #icon><refresh-icon /></template>
          </t-button>
        </t-col>
      </t-row>
    </div>
    <my-scrollbar>
      <div class="head-container h70vh">
        <t-loading :loading="loadingTree" size="small">
          <t-tree
            ref="deptTreeRef"
            v-model:actived="treeActived"
            v-model:expanded="expandedTree"
            class="t-tree--block-node"
            :data="categoryOptions"
            :keys="{ value: 'id', label: 'label', children: 'children' }"
            :filter="filterNode"
            activable
            hover
            line
            check-strictly
            allow-fold-node-on-filter
            transition
            @active="emit('active')"
          />
        </t-loading>
      </div>
    </my-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { RefreshIcon, SearchIcon } from 'tdesign-icons-vue-next';
import type { TreeNodeModel } from 'tdesign-vue-next';
import { computed, ref } from 'vue';

import type { TreeModel } from '@/api/model/resultModel';
import { flowCategoryTree } from '@/api/workflow/category';

const loadingTree = ref(false);
const categoryOptions = ref<TreeModel<string>[]>([]);
const categoryName = ref('');
const expandedTree = ref<string[]>([]);
const treeActived = defineModel<string[]>({
  default: () => [] as string[],
});
const emit = defineEmits(['active']);

function triggerExpandedTree() {
  expandedTree.value = categoryOptions.value
    .flatMap((value) => value.children?.concat([value]) ?? [value])
    .map((value) => value.id);
}
/** 通过条件过滤节点  */
const filterNode = computed(() => {
  const value = categoryName.value;
  return (node: TreeNodeModel) => {
    if (!node.value || !value || node.data.id === '0') return true;
    return node.label.indexOf(value) >= 0;
  };
});

/** 查询流程分类下拉树结构 */
async function getTreeselect() {
  return flowCategoryTree().then((response) => {
    categoryOptions.value = response.data;
  });
}

onMounted(() => {
  getTreeselect().then(() => triggerExpandedTree());
});
</script>

<style scoped lang="less"></style>
