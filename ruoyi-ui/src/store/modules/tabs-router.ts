import { defineStore } from 'pinia';
import { nextTick } from 'vue';
import type { RouteRecordRaw } from 'vue-router';

import { store, unfoldRoutesPath, usePermissionStore } from '@/store';
import type { TRouterInfo, TTabRouterType } from '@/types/interface';

const homeRoute: Array<TRouterInfo> = [
  {
    path: '/',
    routeIdx: 0,
    title: '仪表盘',
    name: 'DashboardBase',
    isHome: true,
  },
];

const state = {
  tabRouterList: homeRoute,
  isRefreshing: false,
};

// 不需要做多标签tabs页缓存的列表 值为每个页面对应的name 如 DashboardDetail
// const ignoreCacheRoutes = ['DashboardDetail'];
const ignoreCacheRoutes = ['login'];

export const useTabsRouterStore = defineStore('tabsRouter', {
  state: () => state,
  getters: {
    tabRouters: (state: TTabRouterType) => state.tabRouterList,
    refreshing: (state: TTabRouterType) => state.isRefreshing,
  },
  actions: {
    // 处理刷新
    toggleTabRouterAlive(routeIdx: number) {
      this.isRefreshing = !this.isRefreshing;
      this.tabRouters[routeIdx].isAlive = !this.tabRouters[routeIdx].isAlive;
    },
    // 处理新增
    appendTabRouterList(newRoute: TRouterInfo) {
      const needAlive = !ignoreCacheRoutes.includes(newRoute.name as string);
      if (!this.tabRouters.find((route: TRouterInfo) => route.path === newRoute.path)) {
        this.tabRouterList = this.tabRouterList.concat({ ...newRoute, isAlive: needAlive });
      }
    },
    // 处理关闭当前
    subtractCurrentTabRouter(newRoute: TRouterInfo) {
      const { routeIdx } = newRoute;
      this.tabRouterList = this.tabRouterList.slice(0, routeIdx).concat(this.tabRouterList.slice(routeIdx + 1));
    },
    // 处理关闭右侧
    subtractTabRouterBehind(newRoute: TRouterInfo) {
      const { routeIdx } = newRoute;
      const homeIdx: number = this.tabRouters.findIndex((route: TRouterInfo) => route.isHome);
      let tabRouterList: Array<TRouterInfo> = this.tabRouterList.slice(0, routeIdx + 1);
      if (routeIdx < homeIdx) {
        tabRouterList = tabRouterList.concat(homeRoute);
      }
      this.tabRouterList = tabRouterList;
    },
    // 处理关闭左侧
    subtractTabRouterAhead(newRoute: TRouterInfo) {
      const { routeIdx } = newRoute;
      const homeIdx: number = this.tabRouters.findIndex((route: TRouterInfo) => route.isHome);
      let tabRouterList: Array<TRouterInfo> = this.tabRouterList.slice(routeIdx);
      if (routeIdx > homeIdx) {
        tabRouterList = homeRoute.concat(tabRouterList);
      }
      this.tabRouterList = tabRouterList;
    },
    // 处理关闭其他
    subtractTabRouterOther(newRoute: TRouterInfo) {
      const { routeIdx } = newRoute;
      const homeIdx: number = this.tabRouters.findIndex((route: TRouterInfo) => route.isHome);
      this.tabRouterList = routeIdx === homeIdx ? homeRoute : homeRoute.concat([this.tabRouterList?.[routeIdx]]);
    },
    removeTabRouterList() {
      this.tabRouterList = homeRoute;
    },
    initTabRouterList(newRoutes: TRouterInfo[]) {
      newRoutes?.forEach((route: TRouterInfo) => this.appendTabRouterList(route));
    },
    /**
     * 关闭当前路由，并跳转到目标路由
     */
    useRemoveCurrentTab() {
      const route = useRoute();
      const router = useRouter();
      return (to?: Parameters<typeof router.push>[0]) => {
        const index = this.tabRouterList.findIndex((item: TRouterInfo) => item.path === route.path);
        if (index !== -1) {
          const nextRouter = this.tabRouterList[index + 1] || this.tabRouterList[index - 1];
          this.tabRouterList = this.tabRouterList.slice(0, index).concat(this.tabRouterList.slice(index + 1));
          if (!to) {
            router?.go(-1);
          } else if (typeof to === 'string') {
            if (to === route.path) {
              router?.push({ path: nextRouter.path, query: nextRouter.query });
            } else {
              router.push({ path: to });
            }
          } else {
            router.push(to);
          }
        }
      };
    },
  },
  persist: {
    afterRestore: (ctx) => {
      const permissionStore = usePermissionStore();
      const routerList = ctx.store.tabRouterList as Array<TRouterInfo>;
      const routesPath = unfoldRoutesPath(permissionStore.defaultRoutes as RouteRecordRaw[]);
      routerList.forEach((routerInfo) => {
        for (const route of routesPath) {
          // 地址相同，更新meta和query信息
          if (routerInfo.path === route.path) {
            const { query, meta } = route as any;
            routerInfo.meta = meta;
            routerInfo.query = query;
            break;
          }
        }
      });
      // 重新刷新
      nextTick(() => ctx.store.$reset());
    },
  },
});

export function getTabsRouterStore() {
  return useTabsRouterStore(store);
}
