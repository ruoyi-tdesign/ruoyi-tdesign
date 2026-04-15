<template>
  <t-loading :loading="loading" size="small">
    <t-row :gutter="[0, 24]">
      <t-col :span="6">
        <t-form-item label="表名称" name="info.tableName">
          <t-input v-model="info.tableName" placeholder="请输入仓库名称" />
        </t-form-item>
      </t-col>
      <t-col :span="6">
        <t-form-item label="表描述" name="info.tableComment">
          <t-input v-model="info.tableComment" placeholder="请输入" />
        </t-form-item>
      </t-col>
      <t-col :span="6">
        <t-form-item label="实体类名称" name="info.className">
          <t-input v-model="info.className" placeholder="请输入" />
        </t-form-item>
      </t-col>
      <t-col :span="6">
        <t-form-item label="作者" name="info.functionAuthor">
          <t-input v-model="info.functionAuthor" placeholder="请输入" />
        </t-form-item>
      </t-col>
      <t-col :span="12">
        <t-form-item label="备注" name="info.remark">
          <t-textarea v-model="info.remark" :autosize="{ minRows: 3, maxRows: 3 }"></t-textarea>
        </t-form-item>
      </t-col>
    </t-row>
  </t-loading>
</template>
<script lang="ts" setup>
import { toRefs } from 'vue';

const props = defineProps({
  info: {
    type: Object,
    default: null,
  },
  loading: {
    type: Boolean,
    default: false,
  },
});
const { info } = toRefs(props);
</script>
