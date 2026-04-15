<template>
  <t-select
    v-if="userId === 1 && tenantEnabled"
    v-model="tenantId"
    clearable
    filterable
    reserve-keyword
    placeholder="请选择租户"
    style="max-width: 180px; margin: 0 8px"
    @change="dynamicTenantEvent"
    @clear="dynamicClearEvent"
  >
    <template #prefixIcon>
      <company class="t-icon" />
    </template>
    <t-option v-for="item in tenantList" :key="item.tenantId" :label="item.companyName" :value="item.tenantId" />
  </t-select>
</template>
<script lang="ts" setup>
import { storeToRefs } from 'pinia';
import { ref, toRefs } from 'vue';
import { useRouter } from 'vue-router';

import { dynamicClear, dynamicTenant } from '@/api/system/tenant';
import Company from '@/assets/icons/svg/company.svg?component';
import { useTabsRouterStore, useUserStore } from '@/store';
import { useTenantStore } from '@/store/modules/tenant';

const emit = defineEmits(['dynamic-change']);
const { userId } = toRefs(useUserStore());
const tenantId = ref(undefined);
const tabsRouterStore = useTabsRouterStore();
const router = useRouter();
const { tenantEnabled, tenantList } = storeToRefs(useTenantStore());

/**
 * 动态切换租户
 */
function dynamicTenantEvent() {
  if (tenantId.value != null && tenantId.value !== '') {
    dynamicTenant(tenantId.value).then(() => {
      emit('dynamic-change', true);
      tabsRouterStore.removeTabRouterList();
      router.push('/');
    });
  }
}

/**
 * 清理动态租户
 */
function dynamicClearEvent() {
  dynamicClear().then(() => {
    emit('dynamic-change', false);
    tabsRouterStore.removeTabRouterList();
    router.push('/');
  });
}
</script>
<style scoped></style>
