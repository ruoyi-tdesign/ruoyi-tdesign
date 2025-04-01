package org.dromara.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.lang.tree.Tree;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.dromara.common.core.constant.TenantConstants;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.SysMenuBo;
import org.dromara.system.domain.query.SysMenuQuery;
import org.dromara.system.domain.vo.MenuTreeSelectVo;
import org.dromara.system.domain.vo.RouterVo;
import org.dromara.system.domain.vo.SysMenuVo;
import org.dromara.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单信息
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController {

    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("/getRouters")
    public R<List<RouterVo>> getRouters() {
        List<SysMenuVo> menus = menuService.selectMenuTreeByUserId(LoginHelper.getUserId());
        return R.ok(menuService.buildMenus(menus));
    }

    /**
     * 获取菜单列表
     */
    @SaCheckRole(value = {
        TenantConstants.SUPER_ADMIN_ROLE_KEY,
        TenantConstants.TENANT_ADMIN_ROLE_KEY
    }, mode = SaMode.OR)
    @SaCheckPermission("system:menu:list")
    @GetMapping("/list")
    public R<List<SysMenuVo>> list(SysMenuQuery query) {
        List<SysMenuVo> menus = menuService.selectMenuList(query, LoginHelper.getUserId());
        return R.ok(menus);
    }

    /**
     * 导出菜单权限列表
     */
    @SaCheckPermission("system:menu:export")
    @Log(title = "菜单权限", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysMenuQuery query, HttpServletResponse response) {
        List<SysMenuVo> list = menuService.selectMenuList(query, LoginHelper.getUserId());
        ExcelUtil.exportExcel(list, "菜单权限", SysMenuVo.class, response);
    }

    /**
     * 根据菜单编号获取详细信息
     *
     * @param menuId 菜单ID
     */
    @SaCheckRole(value = {
        TenantConstants.SUPER_ADMIN_ROLE_KEY,
        TenantConstants.TENANT_ADMIN_ROLE_KEY
    }, mode = SaMode.OR)
    @SaCheckPermission("system:menu:query")
    @GetMapping(value = "/{menuId}")
    public R<SysMenuVo> getInfo(@NotNull(message = "菜单id不能为空") @PathVariable Long menuId) {
        return R.ok(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @SaCheckPermission("system:menu:query")
    @GetMapping("/treeSelect")
    public R<List<Tree<Long>>> treeSelect(SysMenuQuery query) {
        List<SysMenuVo> menus = menuService.selectMenuList(query, LoginHelper.getUserId());
        return R.ok(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     *
     * @param roleId 角色ID
     */
    @SaCheckPermission("system:menu:query")
    @GetMapping(value = "/roleMenuTreeSelect/{roleId}")
    public R<MenuTreeSelectVo> roleMenuTreeSelect(@PathVariable("roleId") Long roleId) {
        List<SysMenuVo> menus = menuService.selectMenuList(LoginHelper.getUserId());
        MenuTreeSelectVo selectVo = new MenuTreeSelectVo();
        selectVo.setCheckedKeys(menuService.selectMenuListByRoleId(roleId));
        selectVo.setMenus(menuService.buildMenuTreeSelect(menus));
        return R.ok(selectVo);
    }

    /**
     * 加载对应租户套餐菜单列表树
     *
     * @param packageId 租户套餐ID
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:query")
    @GetMapping(value = "/tenantPackageMenuTreeSelect/{packageId}")
    public R<MenuTreeSelectVo> tenantPackageMenuTreeSelect(@PathVariable("packageId") Long packageId) {
        List<SysMenuVo> menus = menuService.selectMenuList(LoginHelper.getUserId());
        MenuTreeSelectVo selectVo = new MenuTreeSelectVo();
        selectVo.setCheckedKeys(menuService.selectMenuListByPackageId(packageId));
        selectVo.setMenus(menuService.buildMenuTreeSelect(menus));
        return R.ok(selectVo);
    }

    /**
     * 新增菜单
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysMenuBo menu) {
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysMenuBo menu) {
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:remove")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public R<Void> remove(@NotNull(message = "主键不能为空") @PathVariable("menuId") Long menuId) {
        return toAjax(menuService.deleteMenuById(menuId));
    }

}
