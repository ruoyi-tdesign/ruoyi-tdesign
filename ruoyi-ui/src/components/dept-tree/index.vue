<template>
  <div>
    <div class="head-container">
      <t-row style="width: 100%" :gutter="20">
        <t-col :span="10">
          <t-input v-model="deptName" placeholder="请输入部门名称" clearable style="margin-bottom: 20px">
            <template #prefixIcon>
              <search-icon />
            </template>
          </t-input>
        </t-col>
        <t-col :span="2">
          <t-button shape="square" variant="outline" @click="getDeptTree">
            <template #icon><refresh-icon /></template>
          </t-button>
        </t-col>
      </t-row>
    </div>
    <my-scrollbar>
      <div class="head-container h70vh">
        <t-loading :loading="loadingDept" size="small">
          <t-tree
            ref="deptTreeRef"
            v-model:actived="deptActived"
            v-model:expanded="expandedDept"
            class="t-tree--block-node"
            :data="deptOptions"
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
import { ref } from 'vue';

import type { TreeModel } from '@/api/model/resultModel';
import { deptTreeSelect } from '@/api/system/user';

const loadingDept = ref(false);
const deptName = ref('');
// const deptActived = ref<number[]>([]);
const expandedDept = ref<number[]>([]);
const deptOptions = ref<Array<TreeModel<number>>>([]);
const deptActived = defineModel<number[]>({
  default: () => [],
});
const emit = defineEmits(['active']);

/** 查询部门下拉树结构 */
async function getDeptTree() {
  loadingDept.value = true;
  return deptTreeSelect()
    .then((response) => {
      deptOptions.value = response.data;
    })
    .finally(() => (loadingDept.value = false));
}

/** 通过条件过滤节点  */
const filterNode = computed(() => {
  const value = deptName.value;
  return (node: TreeNodeModel) => {
    if (!node.value || !value) return true;
    return node.label.indexOf(value) >= 0;
  };
});

function triggerExpandedDept() {
  expandedDept.value = deptOptions.value
    .flatMap((value) => value.children?.concat([value]) ?? [value])
    .map((value) => value.id);
}

onMounted(() => {
  getDeptTree().then(() => triggerExpandedDept());
});
</script>

<style scoped lang="less"></style>
