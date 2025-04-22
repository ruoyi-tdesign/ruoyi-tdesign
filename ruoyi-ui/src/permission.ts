import 'nprogress/nprogress.css'; // progress bar style

import NProgress from 'nprogress'; // progress bar

import router from '@/router';
import { usePermissionStoreHook, useUserStore } from '@/store';
import { useTitle } from '@/utils/doc';
import { isRelogin } from '@/utils/request';
import { isPathMatch } from '@/utils/validate';

let errorRetry = 0;

NProgress.configure({ showSpinner: false });

// 路由白名单
const isWhiteList = (path: string, whiteList: string[]) => {
  return whiteList.some((pattern) => isPathMatch(pattern, path));
};

router.beforeEach(async (to, from, next) => {
  NProgress.start();

  const permissionStore = usePermissionStoreHook();
  const { whiteListRouters } = permissionStore;

  const userStore = useUserStore();
  const { token } = userStore;
  if (token) {
    if (to.meta.title) {
      useTitle(to.meta.title as string);
    }

    // 错误重试超出限制
    if (errorRetry >= 3) {
      errorRetry = 0;
      next({
        path: '/500',
        query: { redirect: encodeURIComponent(to.fullPath) },
      });
      return;
    }

    if (isWhiteList(to.path, whiteListRouters)) {
      next();
      return;
    }

    const { roles } = userStore;

    if (roles && roles.length > 0) {
      next();
    } else {
      isRelogin.show = true;
      try {
        await userStore.getUserInfo();

        isRelogin.show = false;

        await permissionStore.generateRoutes();
        next({ ...to, replace: true }); // hack方法 确保addRoutes已完成
        errorRetry = 0;
      } catch (error) {
        console.error(error);
        errorRetry++;
        // await userStore.logout();
        next({
          path: '/login',
          query: { redirect: encodeURIComponent(to.fullPath) },
        });
        NProgress.done();
      }
    }
  } else {
    /* white list router */
    if (whiteListRouters.indexOf(to.path) !== -1) {
      next();
    } else {
      next({
        path: '/login',
        query: { redirect: encodeURIComponent(to.fullPath) },
      });
    }
    NProgress.done();
  }
});

router.afterEach(async (to) => {
  if (to.path === '/login') {
    const userStore = useUserStore();
    const { token } = userStore;
    const isLogin = await userStore.isLogin();
    if (isLogin) {
      const redirect = to.query.redirect as string;
      const redirectUrl = redirect ? decodeURIComponent(redirect) : '/';
      await router.push(redirectUrl);
    } else if (token) {
      await userStore.logout();
    }
  }
  NProgress.done();
});
