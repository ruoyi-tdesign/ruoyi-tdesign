import type { RouterJumpVo } from '@/api/workflow/model/workflowCommonModel';

export function useRouterJump() {
  const router = useRouter();
  // const { proxy } = getCurrentInstance();
  return (routerJumpVo: RouterJumpVo) => {
    // proxy.$tab.closePage(proxy.$route);
    router.push({
      path: routerJumpVo.formPath,
      query: {
        id: routerJumpVo.businessId,
        type: routerJumpVo.type,
        taskId: routerJumpVo.taskId,
      },
    });
  };
}
