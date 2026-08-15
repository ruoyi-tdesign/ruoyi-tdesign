package org.dromara.common.satoken.core.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.model.BaseUser;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.service.PermissionService;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.dromara.common.satoken.context.SaSecurityContext;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.satoken.utils.MultipleStpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * sa-token 权限管理实现类
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
public class SaPermissionImpl implements StpInterface {

    private final PermissionService permissionService;

    /**
     * 获取菜单权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (MultipleStpUtil.SYSTEM.isLogin()) {
            LoginUser loginUser = LoginHelper.getUser();
            if (ObjectUtil.isNull(loginUser) || !loginUser.getLoginId().equals(loginId)) {
                if (ObjectUtil.isNotNull(permissionService)) {
                    List<String> list = StringUtils.splitList(loginId.toString(), ":");
                    return new ArrayList<>(permissionService.getMenuPermission(Long.parseLong(list.get(1))));
                } else {
                    throw new ServiceException("PermissionService 实现类不存在");
                }
            }
            UserType userType = UserType.getUserType(loginUser.getUserType());
            if (userType == UserType.APP_USER) {
                // 其他端 自行根据业务编写
            }
            // SYS_USER 默认返回权限
            if (CollUtil.isNotEmpty(loginUser.getMenuPermission())) {
                // SYS_USER 默认返回权限
                return new ArrayList<>(loginUser.getMenuPermission());
            } else {
                return new ArrayList<>();
            }
        }
        BaseUser user = SaSecurityContext.getContext();
        if (user != null) {
            return new ArrayList<>(user.getMenuPermission());
        }
        return new ArrayList<>();
    }

    /**
     * 获取角色权限列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (MultipleStpUtil.SYSTEM.isLogin()) {
            LoginUser loginUser = LoginHelper.getUser();
            if (ObjectUtil.isNull(loginUser) || !loginUser.getLoginId().equals(loginId)) {
                if (ObjectUtil.isNotNull(permissionService)) {
                    List<String> list = StringUtils.splitList(loginId.toString(), ":");
                    return new ArrayList<>(permissionService.getRolePermission(Long.parseLong(list.get(1))));
                } else {
                    throw new ServiceException("PermissionService 实现类不存在");
                }
            }
            UserType userType = UserType.getUserType(loginUser.getUserType());
            if (userType == UserType.APP_USER) {
                // 其他端 自行根据业务编写
            }
            // SYS_USER 默认返回权限
            if (CollUtil.isNotEmpty(loginUser.getRolePermission())) {
                // SYS_USER 默认返回权限
                return new ArrayList<>(loginUser.getRolePermission());
            } else {
                return new ArrayList<>();
            }
        }
        BaseUser user = SaSecurityContext.getContext();
        if (user != null) {
            return new ArrayList<>(user.getRolePermission());
        }
        return new ArrayList<>();
    }
}
