<template>
  <t-dialog
    v-model:visible="visible"
    header="级联删除菜单"
    destroy-on-close
    attach="body"
    width="750px"
    :close-on-overlay-click="false"
    @before-open="getTreeselect()"
  >
    <t-tree
      v-model="menuIds"
      class="tree-border"
      :data="menuOptions"
      :keys="{ value: 'menuId', label: 'menuName', children: 'children' }"
      hover
      line
      transition
      checkable
      empty="加载中，请稍候"
      value-mode="all"
    />
    <template #footer>
      <div class="dialog-footer">
        <t-button theme="primary" :loading="deleteLoading" @click="submitDeleteForm">确 定</t-button>
        <t-button variant="outline" @click="visible = false">取 消</t-button>
      </div>
    </template>
  </t-dialog>
</template>
<script setup lang="ts">
import { getCurrentInstance, ref } from 'vue';

import { cascadeDelMenu, listMenu } from '@/api/system/menu';
import type { SysMenuVo } from '@/api/system/model/menuModel';

defineOptions({
  name: 'CascadDeleteMenuDialog',
});

const emit = defineEmits(['submit']);

const visible = defineModel('visible', {
  type: Boolean,
  default: false,
});

const { proxy } = getCurrentInstance();
const menuOptions = ref<SysMenuVo[]>([]);

/** 查询菜单下拉树结构 */
async function getTreeselect() {
  menuOptions.value = [];
  return listMenu().then((response) => {
    menuOptions.value = [{ menuId: 0, menuName: '主类目', children: proxy.handleTree(response.data, 'menuId') }];
  });
}

const deleteLoading = ref<boolean>(false);
const menuIds = ref<Array<number | string>>([]);

/** 删除提交按钮 */
const submitDeleteForm = async () => {
  if (menuIds.value.length <= 0) {
    proxy?.$modal.msgWarning('请选择要删除的菜单');
    return;
  }
  deleteLoading.value = true;
  await cascadeDelMenu(menuIds.value).finally(() => (deleteLoading.value = false));
  emit('submit');
  proxy?.$modal.msgSuccess('删除成功');
  visible.value = false;
};
</script>
<style scoped lang="less">
.tree-border {
  height: 300px;
  overflow: auto;
}
</style>
