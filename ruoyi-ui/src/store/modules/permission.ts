import cloneDeep from 'lodash/cloneDeep';
import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { RouteRecordRaw } from 'vue-router';

import { getRouters } from '@/api/menu';
import InnerLink from '@/layouts/components/InnerLink.vue';
import Layout from '@/layouts/index.vue';
import auth from '@/plugins/auth';
import router, { defaultRouterList, dynamicRoutes } from '@/router';
import { store } from '@/store';
import { isHttp } from '@/utils/validate';

const modules = import.meta.glob('./../../pages/**/*.vue');

export const usePermissionStore = defineStore('permission', () => {
  const whiteListRouters = ref([
    '/login',
    '/register',
    '/social-callback',
    '/403',
    '/500',
    '/register*',
    '/register/*',
  ]);
  const menus = ref<RouteRecordRaw[]>([]);
  const allMenus = ref<RouteRecordRaw[]>([]);

  async function generateRoutes() {
    // 向后端请求路由数据
    const res = await getRouters();
    const asyncRouter = filterAsyncRouter(cloneDeep(res.data) as any);
    menus.value = asyncRouter;
    // 动态添加可访问路由表
    const asyncRoutes = filterDynamicRoutes(dynamicRoutes);
    allMenus.value = defaultRouterList.concat(asyncRouter);

    asyncRoutes.forEach((route) => {
      router.addRoute(route);
    });

    // 根据后台路由数据生成可访问路由表
    asyncRouter.forEach((route) => {
      if (!isHttp(route.path)) {
        router.addRoute(route); // 动态添加可访问路由表
      }
    });
  }

  return {
    menus,
    allMenus,
    whiteListRouters,
    generateRoutes,
  };
});

/**
 * 遍历后台传来的路由字符串，转换为组件对象
 * @param routers 后台传来的路由字符串
 */
function filterAsyncRouter(routers: RouteRecordRaw[]) {
  return routers.filter((route) => {
    if (route.component) {
      // Layout ParentView 组件特殊处理
      if (route.component?.toString() === 'Layout') {
        route.component = Layout;
      } else if (route.component?.toString() === 'InnerLink') {
        route.component = InnerLink;
      } else {
        route.component = loadView(route.component);
      }
    }
    if (route.children?.length) {
      route.children = filterAsyncRouter(route.children);
    } else {
      delete route.children;
      delete route.redirect;
    }
    return true;
  });
}

/** 展开路由地址 */
export function unfoldRoutesPath(routes: RouteRecordRaw[], parentPath?: string) {
  const newRoutes: RouteRecordRaw[] = [];
  for (const route of routes) {
    const newRoute = { ...route };
    const path = `${parentPath?.concat('/') ?? ''}${route.path}`.replaceAll(/\/+/g, '/');
    newRoute.path = path;
    newRoutes.push(newRoute);
    if (newRoute.children && newRoute.children.length > 0) {
      unfoldRoutesPath(newRoute.children, path).forEach((value) => {
        newRoutes.push(value);
      });
    }
  }
  return newRoutes;
}

/**
 * 动态路由遍历，验证是否具备权限
 * @param routes
 */
export function filterDynamicRoutes(routes: RouteRecordRaw[]) {
  const res: RouteRecordRaw[] = [];
  routes.forEach((route) => {
    if (route.meta.permissions) {
      if (auth.hasPermiOr(route.meta.permissions as any)) {
        res.push(route);
      }
    } else if (route.meta.roles) {
      if (auth.hasRoleOr(route.meta.roles as string[])) {
        res.push(route);
      }
    }
  });
  return res;
}

/**
 * 加载组件
 * @param view 组件名称
 */
export const loadView = (view: any) => {
  let res;
  for (const path in modules) {
    const dir = path.split('pages/')[1].split('.vue')[0];
    if (dir === view) {
      res = () => modules[path]();
    }
  }
  return res;
};

// 非setup
export const usePermissionStoreHook = () => {
  return usePermissionStore(store);
};

export default usePermissionStore;

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(usePermissionStore, import.meta.hot));
}
