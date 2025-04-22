import { defineStore } from 'pinia';
import { ref } from 'vue';

import { getTenantList } from '@/api/login';
import type { TenantListVo } from '@/api/model/loginModel';

export const useTenantStore = defineStore('tenant', () => {
  // 租户开关
  const tenantEnabled = ref(false);

  // 租户列表
  const tenantList = ref<TenantListVo[]>([]);

  // 租户列表
  function initTenantList() {
    getTenantList(true).then((res) => {
      const vo = res.data;
      tenantEnabled.value = !!vo.tenantEnabled;
      if (tenantEnabled.value) {
        tenantList.value = vo.voList;
      }
    });
  }

  initTenantList();

  return {
    tenantEnabled,
    tenantList,
    initTenantList,
  };
});
