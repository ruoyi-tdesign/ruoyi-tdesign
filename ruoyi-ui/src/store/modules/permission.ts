import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { RouteRecordRaw } from 'vue-router';

import { getRouters } from '@/api/menu';
import InnerLink from '@/layouts/components/InnerLink.vue';
import Layout from '@/layouts/index.vue';
import auth from '@/plugins/auth';
import router, { defaultRouterList, dynamicRoutes } from '@/router';
import { store } from '@/store';

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
  const routers = ref<RouteRecordRaw[]>([]);
  const addRoutes = ref<RouteRecordRaw[]>([]);
  const defaultRoutes = ref<RouteRecordRaw[]>([]);
  const topbarRouters = ref<RouteRecordRaw[]>([]);
  const sidebarRouters = ref<RouteRecordRaw[]>([]);

  function setRouters(newRoutes: RouteRecordRaw[]) {
    addRoutes.value = newRoutes;
    routers.value = defaultRouterList.concat(newRoutes);
  }
  function setDefaultRoutes(routes: RouteRecordRaw[]) {
    defaultRoutes.value = defaultRouterList.concat(routes);
  }
  function setTopbarRoutes(routes: RouteRecordRaw[]) {
    topbarRouters.value = routes;
  }

  function setSidebarRouters(routes: RouteRecordRaw[]) {
    sidebarRouters.value = routes;
  }

  async function generateRoutes() {
    // 向后端请求路由数据
    const res = await getRouters();
    const sdata = JSON.parse(JSON.stringify(res.data));
    const rdata = JSON.parse(JSON.stringify(res.data));
    const defaultData = JSON.parse(JSON.stringify(res.data));
    const sidebarRoutes = filterAsyncRouter(sdata);
    const rewriteRoutes = filterAsyncRouter(rdata, undefined, true);
    const defaultRoutes = filterAsyncRouter(defaultData);
    const asyncRoutes = filterDynamicRoutes(dynamicRoutes);
    asyncRoutes.forEach((route) => {
      router.addRoute(route);
    });
    setRouters(rewriteRoutes);
    setSidebarRouters(defaultRouterList.concat(sidebarRoutes));
    setDefaultRoutes(sidebarRoutes);
    setTopbarRoutes(defaultRouterList.concat(defaultRoutes));
    return defaultRoutes;
  }

  return {
    whiteListRouters,
    routers,
    setRouters,
    generateRoutes,
    setSidebarRouters,
    topbarRouters,
    sidebarRouters,
    defaultRoutes,
  };
});

/**
 * 遍历后台传来的路由字符串，转换为组件对象
 * @param asyncRouterMap 后台传来的路由字符串
 * @param lastRouter 上一级路由
 * @param type 是否是重写路由
 */
function filterAsyncRouter(asyncRouterMap: RouteRecordRaw[], lastRouter?: RouteRecordRaw, type = false) {
  return asyncRouterMap.filter((route) => {
    if (type && route.children) {
      route.children = filterChildren(route.children);
    }
    if (route.component) {
      // Layout ParentView 组件特殊处理
      if (route.component?.toString() === 'Layout') {
        route.component = Layout;
      } else if (route.component?.toString() === 'ParentView') {
        // route.component = ParentView;
        route.component = null;
      } else if (route.component?.toString() === 'InnerLink') {
        route.component = InnerLink;
      } else {
        route.component = loadView(route.component);
      }
    }
    if (route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children, route, type);
    } else {
      delete route.children;
      delete route.redirect;
    }
    return true;
  });
}

function filterChildren(childrenMap: RouteRecordRaw[], lastRouter?: RouteRecordRaw) {
  let children: RouteRecordRaw[] = [];
  childrenMap.forEach((el) => {
    if (el.children && el.children.length) {
      if (el.component?.toString() === 'ParentView' && !lastRouter) {
        el.children.forEach((c) => {
          c.path = `${el.path}/${c.path}`;
          if (c.children && c.children.length) {
            children = children.concat(filterChildren(c.children, c));
            return;
          }
          children.push(c);
        });
        return;
      }
    }
    if (lastRouter) {
      el.path = `${lastRouter.path}/${el.path}`;
    }
    children = children.concat(el);
  });
  return children;
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

// 动态路由遍历，验证是否具备权限
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
