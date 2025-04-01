package org.dromara.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.core.enums.MenuTypeEnum;
import org.dromara.common.core.enums.YesNoFrameEnum;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.system.domain.SysMenu;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 菜单权限视图对象 sys_menu
 *
 * @author Michelle.Chung
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysMenu.class)
public class SysMenuVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @ExcelProperty(value = "菜单ID")
    private Long menuId;

    /**
     * 菜单名称
     */
    @ExcelProperty(value = "菜单名称")
    private String menuName;

    /**
     * 父菜单ID
     */
    @ExcelProperty(value = "父菜单ID")
    private Long parentId;

    /**
     * 显示顺序
     */
    @ExcelProperty(value = "显示顺序")
    private Integer orderNum;

    /**
     * 路由地址
     */
    @ExcelProperty(value = "路由地址")
    private String path;

    /**
     * 组件路径
     */
    @ExcelProperty(value = "组件路径")
    private String component;

    /**
     * 组件名称
     */
    @ExcelProperty(value = "组件名称")
    private String componentName;

    /**
     * 路由参数
     */
    @ExcelProperty(value = "路由参数")
    private String queryParam;

    /**
     * 是否为外链（1是 0否）
     */
    @ExcelProperty(value = "是否为外链")
    private Integer isFrame;

    /**
     * 是否缓存（1缓存 0不缓存）
     */
    @ExcelProperty(value = "是否缓存")
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @ExcelProperty(value = "菜单类型")
    private String menuType;

    /**
     * 显示状态（1显示 0隐藏）
     */
    @ExcelProperty(value = "显示状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_show_hide")
    private String visible;

    /**
     * 菜单状态（1正常 0停用）
     */
    @ExcelProperty(value = "菜单状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

    /**
     * 权限标识
     */
    @ExcelProperty(value = "权限标识")
    private String perms;

    /**
     * 菜单图标
     */
    @ExcelProperty(value = "菜单图标")
    private String icon;

    /**
     * 隐藏表达式
     */
    @ExcelProperty(value = "隐藏表达式")
    private String hiddenExpression;

    /**
     * 停用表达式
     */
    @ExcelProperty(value = "停用表达式")
    private String shopExpression;

    /**
     * 创建部门
     */
    private Long createDept;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 父菜单名称
     */
    private String parentName;

    /**
     * 子菜单
     */
    private List<SysMenuVo> children = new ArrayList<>();


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
        String component = SystemConstants.LAYOUT;
        if (StringUtils.isNotEmpty(this.component) && !isMenuFrame()) {
            component = this.component;
        } else if (StringUtils.isEmpty(this.component) && getParentId() != 0L && isInnerLink()) {
            component = SystemConstants.INNER_LINK;
        } else if (StringUtils.isEmpty(this.component) && isParentView()) {
            component = SystemConstants.PARENT_VIEW;
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
