package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.constant.UserConstants;
import org.dromara.common.core.enums.MenuTypeEnum;
import org.dromara.common.core.enums.YesNoFrameEnum;
import org.dromara.common.core.utils.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 菜单权限表 sys_menu
 *
 * @author Lion Li
 */
@Data
@TableName("sys_menu")
public class SysMenu implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @TableId(value = "menu_id")
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父菜单id
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 组件名称
     */
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
     * 权限字符串
     */
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
     * 创建部门
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createDept;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 父菜单名称
     */
    @TableField(exist = false)
    private String parentName;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<>();


    /**
     * 获取路由名称
     */
    public String getRouteName() {
        String routerName = StringUtils.capitalize(path);
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame()) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * 获取路由地址
     */
    public String getRouterPath() {
        String routerPath = this.path;
        // 内链打开外网方式
        if (getParentId() != 0L && isInnerLink()) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0L == getParentId() && MenuTypeEnum.DIRECTORY.getType().equals(getMenuType())
            && YesNoFrameEnum.NO.getCode().equals(getIsFrame())) {
            routerPath = "/" + this.path;
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame()) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     */
    public String getComponentInfo() {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(this.component) && !isMenuFrame()) {
            component = this.component;
        } else if (StringUtils.isEmpty(this.component) && getParentId() != 0L && isInnerLink()) {
            component = UserConstants.INNER_LINK;
        } else if (StringUtils.isEmpty(this.component) && isParentView()) {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     */
    public boolean isMenuFrame() {
        return getParentId() == 0L && MenuTypeEnum.MENU.getType().equals(menuType) && YesNoFrameEnum.NO.getCode().equals(isFrame);
    }

    /**
     * 是否为内链组件
     */
    public boolean isInnerLink() {
        return YesNoFrameEnum.NO.getCode().equals(isFrame) && StringUtils.ishttp(path);
    }

    /**
     * 是否为parent_view组件
     */
    public boolean isParentView() {
        return getParentId() != 0L && MenuTypeEnum.DIRECTORY.getType().equals(menuType);
    }

    /**
     * 内链域名特殊字符替换
     */
    public static String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(path, new String[]{Constants.HTTP, Constants.HTTPS, Constants.WWW, ".", ":"},
            new String[]{"", "", "", "/", "/"});
    }
}
