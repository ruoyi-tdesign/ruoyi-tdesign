package org.dromara.system.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 菜单权限查询对象 sys_menu
 *
 * @author hexm
 * @date 2023-04-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenuQuery extends BaseEntity {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 组件名称
     */
    private String componentName;

    /**
     * 显示状态
     */
    private String visible;

    /**
     * 菜单状态
     */
    private String status;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    private String menuType;

    /**
     * 父菜单ID
     */
    private Long parentId;

}
