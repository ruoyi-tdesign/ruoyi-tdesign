package org.dromara.system.domain.bo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.dromara.common.core.constant.RegexConstants;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.system.domain.SysMenu;

import java.io.Serial;
import java.io.Serializable;

/**
 * 菜单权限业务对象 sys_menu
 *
 * @author Michelle.Chung
 */
@Data
@AutoMapper(target = SysMenu.class, reverseConvertGenerate = false)
public class SysMenuBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @NotNull(message = "菜单ID不能为空", groups = {EditGroup.class})
    private Long menuId;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空", groups = {AddGroup.class, EditGroup.class})
    @Size(max = 50, message = "菜单名称长度不能超过{max}个字符", groups = {AddGroup.class, EditGroup.class})
    private String menuName;

    /**
     * 父菜单id
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer orderNum;

    /**
     * 路由地址
     */
    @Size(max = 250, message = "路由地址不能超过{max}个字符", groups = {AddGroup.class, EditGroup.class})
    private String path;

    /**
     * 组件路径
     */
    @Size(max = 250, message = "组件路径不能超过{max}个字符", groups = {AddGroup.class, EditGroup.class})
    private String component;

    /**
     * 组件名称
     */
    @Size(max = 250, message = "组件组件名称不能超过{max}个字符", groups = {AddGroup.class, EditGroup.class})
    private String componentName;

    /**
     * 路由参数
     */
    private String queryParam;

    /**
     * 是否为外链（1是 0否）
     */
    private Integer isFrame;

    /**
     * 是否缓存（1缓存 0不缓存）
     */
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @NotBlank(message = "菜单类型不能为空", groups = {AddGroup.class, EditGroup.class})
    private String menuType;

    /**
     * 显示状态（1显示 0隐藏）
     */
    private String visible;

    /**
     * 菜单状态（1正常 0停用）
     */
    private String status;

    /**
     * 权限标识
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(max = 100, message = "权限标识长度不能超过{max}个字符", groups = {AddGroup.class, EditGroup.class})
    @Pattern(regexp = RegexConstants.PERMISSION_STRING, message = "权限标识必须符合 tool:build:list 格式")
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 隐藏表达式
     */
    private String hiddenExpression;

    /**
     * 停用表达式
     */
    private String shopExpression;

    /**
     * 备注
     */
    private String remark;


}
