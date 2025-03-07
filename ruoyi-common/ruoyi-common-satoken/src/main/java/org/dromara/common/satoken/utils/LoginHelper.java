package org.dromara.common.satoken.utils;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpLogic;
import cn.hutool.core.convert.Convert;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dromara.common.core.constant.TenantConstants;
import org.dromara.common.core.constant.UserConstants;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.enums.UserType;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 登录鉴权助手
 * <p>
 * user_type 为 用户类型 同一个用户表 可以有多种用户类型 例如 pc,app
 * deivce 为 设备类型 同一个用户类型 可以有 多种设备类型 例如 web,ios
 * 可以组成 用户类型与设备类型多对多的 权限灵活控制
 * <p>
 * 多用户体系 针对 多种用户类型 但权限控制不一致
 * 可以组成 多用户类型表与多设备类型 分别控制权限
 *
 * @author Lion Li
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginHelper {

    public static final String CLIENT_KEY = "clientId";

    /**
     * 获取支持有的登录操作对象
     */
    public static StpLogic getStpLogic() {
        return MultipleStpUtil.SYSTEM;
    }

    /**
     * 获取登录类型
     *
     * @return
     */
    public static String getLoginType() {
        return MultipleStpUtil.SYSTEM.getLoginType();
    }

    /**
     * 当前是否登录
     *
     * @return
     */
    public static boolean isLogin() {
        return MultipleStpUtil.SYSTEM.isLogin();
    }

    /**
     * 登录系统 基于 设备类型
     * 针对相同用户体系不同设备
     *
     * @param loginUser 登录用户信息
     * @param model     配置参数
     */
    public static void login(LoginUser loginUser, SaLoginModel model) {
        MultipleLoginBaseHelper.login(MultipleStpUtil.SYSTEM, loginUser, loginUser.getLoginId(), model);
    }

    /**
     * 获取用户(多级缓存)
     */
    public static <T extends LoginUser> T getUser() {
        return MultipleLoginBaseHelper.getUser(MultipleStpUtil.SYSTEM);
    }

    /**
     * 获取用户(多级缓存)
     */
    public static <T extends LoginUser> Optional<T> getUserOptional() {
        return MultipleLoginBaseHelper.getUserOptional(MultipleStpUtil.SYSTEM);
    }

    /**
     * 获取用户基于token
     */
    public static <T extends LoginUser> T getUser(String token) {
        return MultipleLoginBaseHelper.getUser(MultipleStpUtil.SYSTEM, token);
    }

    /**
     * 获取用户基于token
     */
    public static <T extends LoginUser> Optional<T> getUserOptional(String token) {
        return MultipleLoginBaseHelper.getUserOptional(MultipleStpUtil.SYSTEM, token);
    }

    /**
     * 更新用户
     *
     * @param updateBy 更新回调
     */
    public static <T extends LoginUser> void updateUser(Consumer<T> updateBy) {
        MultipleLoginBaseHelper.updateUser(MultipleStpUtil.SYSTEM, updateBy);
    }

    /**
     * 更新用户
     *
     * @param loginId  用户id
     * @param updateBy 更新回调
     */
    public static <T extends LoginUser> void updateUser(Object loginId, Consumer<T> updateBy) {
        MultipleLoginBaseHelper.updateUser(MultipleStpUtil.SYSTEM, loginId, updateBy);
    }

    /**
     * 获取用户id
     */
    public static Long getUserId() {
        return MultipleLoginBaseHelper.getUserId(MultipleStpUtil.SYSTEM);
    }

    /**
     * 获取用户账户
     */
    public static String getUsername() {
        return getUserOptional().map(LoginUser::getUsername).orElse(null);
    }

    /**
     * 获取租户ID
     */
    public static String getTenantId() {
        return MultipleLoginBaseHelper.getTenantId(MultipleStpUtil.SYSTEM);
    }

    /**
     * 获取部门ID
     */
    public static Long getDeptId() {
        return getUserOptional().map(LoginUser::getDeptId).orElse(null);
    }

    /**
     * 获取部门名
     */
    public static String getDeptName() {
        return Convert.toStr(getUserOptional().map(LoginUser::getDeptName));
    }

    /**
     * 获取部门类别编码
     */
    public static String getDeptCategory() {
        return Convert.toStr(getUserOptional().map(LoginUser::getDeptCategory));
    }

    /**
     * 获取用户类型
     */
    public static UserType getUserType() {
        String loginType = MultipleStpUtil.SYSTEM.getLoginIdAsString();
        return UserType.getUserType(loginType);
    }

    /**
     * 是否为超级管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isSuperAdmin(Long userId) {
        return UserConstants.SUPER_ADMIN_ID.equals(userId);
    }

    /**
     * 是否为超级管理员
     *
     * @return 结果
     */
    public static boolean isSuperAdmin() {
        return isSuperAdmin(getUserId());
    }

    /**
     * 是否为租户管理员
     *
     * @param rolePermission 角色权限标识组
     * @return 结果
     */
    public static boolean isTenantAdmin(Set<String> rolePermission) {
        return rolePermission.contains(TenantConstants.TENANT_ADMIN_ROLE_KEY);
    }

    /**
     * 当前用户是否为租户管理员
     *
     * @return 结果
     */
    public static boolean isTenantAdmin() {
        return getUserOptional().map(loginUser -> isTenantAdmin(loginUser.getRolePermission())).orElse(false);
    }
}
