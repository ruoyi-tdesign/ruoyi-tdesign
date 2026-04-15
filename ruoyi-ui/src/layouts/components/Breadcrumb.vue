<template>
  <t-breadcrumb max-item-width="150" class="tdesign-breadcrumb" separator="&nbsp;/&nbsp;">
    <t-breadcrumbItem
      v-for="(item, index) in crumbs"
      :key="item.to + index"
      :disabled="crumbs.length - 1 === index"
      :to="{ path: item.to, query: item.query }"
      :router="router"
    >
      {{ item.title }}
    </t-breadcrumbItem>
  </t-breadcrumb>
</template>
<script lang="ts" setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();

const crumbs = computed(() => {
  const pathArray = route.path.split('/');
  pathArray.shift();

  return pathArray.reduce((breadcrumbArray, path, idx) => {
    const routeLocationMatched = route.matched[idx];
    // 如果路由是空的，则跳过
    if (!routeLocationMatched) {
      return breadcrumbArray;
    }
    // 如果路由下有hiddenBreadcrumb或当前遍历到参数则隐藏
    if (routeLocationMatched?.meta?.hiddenBreadcrumb || Object.values(route.params).includes(path)) {
      return breadcrumbArray;
    }
    const to =
      routeLocationMatched?.meta?.breadcrumbRedirect ??
      // 如果子路由的激活菜单存在，则使用激活菜单地址
      routeLocationMatched.children?.at(0)?.meta?.activeMenu ??
      routeLocationMatched.path;
    const title =
      routeLocationMatched?.meta?.title ?? router.getRoutes().find((value) => value.path === to)?.meta?.title;
    if (!title) {
      return breadcrumbArray;
    }
    breadcrumbArray.push({
      // @ts-expect-error 设置子路由的参数
      query: routeLocationMatched.children?.at(0)?.query,
      to,
      title,
    });
    return breadcrumbArray;
  }, []);
});
</script>
<style scoped>
.tdesign-breadcrumb {
  margin-bottom: 24px;
}
</style>
