package org.dromara.common.satoken.utils;

import cn.dev33.satoken.stp.StpLogic;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dromara.common.core.domain.model.BaseUser;
import org.dromara.common.core.enums.DeviceType;

import java.util.Optional;
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
 * @author hexm
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginUserHelper {

    /**
     * 获取支持有的登录操作对象
     */
    public static StpLogic getStpLogic() {
        return MultipleStpUtil.USER;
    }

    /**
     * 获取登录类型
     */
    public static String getLoginType() {
        return MultipleStpUtil.USER.getLoginType();
    }

    /**
     * 当前是否登录
     */
    public static boolean isLogin() {
        return MultipleStpUtil.USER.isLogin();
    }

    /**
     * 登录系统
     *
     * @param tokenUser 登录用户信息
     */
    public static void login(BaseUser tokenUser) {
        loginByDevice(tokenUser, null);
    }

    /**
     * 登录系统 基于 设备类型
     * 针对相同用户体系不同设备
     *
     * @param baseUser 登录用户信息
     */
    public static void loginByDevice(BaseUser baseUser, DeviceType deviceType) {
        DynamicLoginHelper.loginByDevice(MultipleStpUtil.USER, baseUser, deviceType);
    }

    /**
     * 获取用户(多级缓存)
     */
    public static <T extends BaseUser> T getUser() {
        return DynamicLoginHelper.getUser(MultipleStpUtil.USER);
    }

    /**
     * 获取用户(多级缓存)
     */
    public static <T extends BaseUser> Optional<T> getUserOptional() {
        return DynamicLoginHelper.getUserOptional(MultipleStpUtil.USER);
    }

    /**
     * 获取用户基于token
     */
    public static <T extends BaseUser> T getUser(String token) {
        return DynamicLoginHelper.getUser(MultipleStpUtil.USER, token);
    }

    /**
     * 获取用户基于token
     */
    public static <T extends BaseUser> Optional<T> getUserOptional(String token) {
        return DynamicLoginHelper.getUserOptional(MultipleStpUtil.USER, token);
    }

    /**
     * 更新用户
     *
     * @param updateBy 更新回调
     */
    public static <T extends BaseUser> void updateUser(Consumer<T> updateBy) {
        DynamicLoginHelper.updateUser(MultipleStpUtil.USER, updateBy);
    }

    /**
     * 更新用户
     *
     * @param loginId  登录id
     * @param updateBy 更新回调
     */
    public static <T extends BaseUser> void updateUser(Object loginId, Consumer<T> updateBy) {
        DynamicLoginHelper.updateUser(MultipleStpUtil.USER, loginId, updateBy);
    }

    /**
     * 获取用户id
     */
    public static Long getUserId() {
        return DynamicLoginHelper.getUserId(MultipleStpUtil.USER);
    }

    /**
     * 获取租户ID
     */
    public static String getTenantId() {
        return DynamicLoginHelper.getTenantId(MultipleStpUtil.USER);
    }

    /**
     * 获取用户账户
     */
    public static String getUsername() {
        return DynamicLoginHelper.getUsername(MultipleStpUtil.USER);
    }

}
