<template>
  <t-dialog
    v-model:visible="visible"
    header="修改表名"
    destroy-on-close
    :close-on-overlay-click="false"
    width="450px"
    attach="body"
    placement="center"
    @opened="reset"
    @confirm="updateTableRef.submit()"
  >
    <t-loading :loading="loading">
      <t-form
        ref="updateTableRef"
        label-align="right"
        :data="updateTableNameForm"
        :rules="rules"
        label-width="calc(5em + 41px)"
        scroll-to-first-error="smooth"
        @submit="submitUpdateTableNameForm"
      >
        <t-form-item label="数据源名称" name="dataName">
          <t-select v-model="updateTableNameForm.dataName" placeholder="请选择/输入数据源名称" filterable clearable>
            <t-option v-for="item in dataNameList" :key="item" :label="item" :value="item"> </t-option>
          </t-select>
        </t-form-item>
        <t-form-item label="表名称" name="tableName">
          <t-input v-model="updateTableNameForm.tableName" placeholder="请输入表名称" clearable />
        </t-form-item>
      </t-form>
      <div style="margin-top: 16px; text-align: center">提交操作成功后，表结构不会自动更新，请手动更新同步。</div>
    </t-loading>
  </t-dialog>
</template>
<script setup lang="ts">
import type { FormInstanceFunctions, FormRule, SubmitContext } from 'tdesign-vue-next';
import type { PropType } from 'vue';
import { defineModel, getCurrentInstance, onMounted, ref } from 'vue';

import { getDataNames, updateGenTableName } from '@/api/tool/gen';
import type { GenTableVo, GenUpdateTableNameForm } from '@/api/tool/model/genModel';

const props = defineProps({
  tableVo: {
    type: Object as PropType<GenTableVo>,
    required: true,
  },
});
const emit = defineEmits(['submit']);
const updateTableRef = ref<FormInstanceFunctions>();
const dataNameList = ref<Array<string>>([]);
const { proxy } = getCurrentInstance();

const visible = defineModel('visible', {
  type: Boolean,
  default: false,
});
const loading = defineModel('loading', {
  type: Boolean,
  default: false,
});

const updateTableNameForm = ref<GenUpdateTableNameForm>({});

const rules = ref<Record<string, Array<FormRule>>>({
  tableName: [{ required: true, message: '请输入表名称' }],
  dataName: [{ required: true, message: '请输入数据源' }],
});

/** 查询多数据源名称 */
async function getDataNameList() {
  const res = await getDataNames();
  dataNameList.value = res.data;
}

function reset() {
  const tableVo = props.tableVo as GenTableVo;
  updateTableNameForm.value = {
    tableId: tableVo.tableId,
    dataName: tableVo.dataName,
    tableName: tableVo.tableName,
  };
}

/** 表单提交 */
function submitUpdateTableNameForm({ validateResult, firstError }: SubmitContext) {
  if (validateResult === true) {
    proxy.$modal.confirm('是否确认修改表名？', () => {
      const msgLoading = proxy.$modal.msgLoading('正在提交中...');
      loading.value = true;
      updateGenTableName(updateTableNameForm.value)
        .then((res) => {
          proxy.$modal.msgSuccess(res.msg);
          visible.value = false;
          emit('submit');
        })
        .finally(() => {
          loading.value = false;
          proxy.$modal.msgClose(msgLoading);
        });
    });
  } else {
    proxy.$modal.msgError(firstError);
  }
}

onMounted(() => {
  getDataNameList();
});
</script>
