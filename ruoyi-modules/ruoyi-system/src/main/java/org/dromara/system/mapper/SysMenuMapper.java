package org.dromara.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.common.core.enums.MenuTypeEnum;
import org.dromara.common.core.enums.NormalDisableEnum;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.SysMenu;
import org.dromara.system.domain.query.SysMenuQuery;
import org.dromara.system.domain.vo.SysMenuVo;

import java.util.List;

/**
 * 菜单表 数据层
 *
 * @author Lion Li
 */
public interface SysMenuMapper extends BaseMapperPlus<SysMenu, SysMenuVo> {

    /**
     * 查询菜单权限列表
     *
     * @param menu
     * @return {@link SysMenu}
     */
    List<SysMenuVo> queryList(SysMenuQuery menu);

    /**
     * 根据用户查询系统菜单列表
     *
     * @param query 查询条件
     * @return 菜单列表
     */
    List<SysMenuVo> selectMenuListByUserId(SysMenuQuery query);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询菜单
     *
     * @return 菜单列表
     */
    default List<SysMenuVo> selectMenuTreeAll() {
        LambdaQueryWrapper<SysMenu> lqw = new LambdaQueryWrapper<SysMenu>()
            .in(SysMenu::getMenuType, MenuTypeEnum.DIRECTORY.getType(), MenuTypeEnum.MENU.getType())
            .eq(SysMenu::getStatus, NormalDisableEnum.NORMAL.getCode())
            .orderByAsc(SysMenu::getParentId)
            .orderByAsc(SysMenu::getOrderNum);
        return this.selectVoList(lqw);
    }

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenuVo> selectMenuTreeByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId            角色ID
     * @param menuCheckStrictly 菜单树选择项是否关联显示
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);
}
