package org.dromara.common.satoken.utils;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.dromara.common.core.domain.model.BaseUser;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.enums.DeviceType;
import org.dromara.common.satoken.context.SaSecurityContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static org.dromara.common.satoken.utils.MultipleStpUtil.*;

/**
 * 登录鉴权助手的基本类
 * <p>
 * user_type 为 用户类型 同一个用户表 可以有多种用户类型 例如 pc,app
 * deivce 为 设备类型 同一个用户类型 可以有 多种设备类型 例如 web,ios
 * 可以组成 用户类型与设备类型多对多的 权限灵活控制
 * <p>
 * 多用户体系 针对 多种用户类型 但权限控制不一致
 * 可以组成 多用户类型表与多设备类型 分别控制权限
 *
 * @author hexm
 * @date 2024/03/07 10:10
 */
public class MultipleLoginBaseHelper {

    /**
     * 获取登录类型
     */
    public static String getLoginType(StpLogic logic) {
        return logic.getLoginType();
    }

    /**
     * 当前是否登录
     */
    public static boolean isLogin(StpLogic logic) {
        return logic.isLogin();
    }

    /**
     * 登录系统 基于 设备类型
     * 针对相同用户体系不同设备
     *
     * @param baseUser 基本登录用户信息
     */
    public static void loginByDevice(StpLogic logic, BaseUser baseUser, DeviceType deviceType) {
        SaLoginModel model = new SaLoginModel();
        if (ObjectUtil.isNotNull(deviceType)) {
            model.setDevice(deviceType.getDevice());
        }
        if (StrUtil.isBlank(baseUser.getDeviceType())) {
            baseUser.setDeviceType(deviceType.getDevice());
        }
        login(logic, baseUser, model);
    }

    /**
     * 登录系统 基于 设备类型
     * 针对相同用户体系不同设备
     *
     * @param baseUser 登录用户信息
     */
    public static void login(StpLogic logic, BaseUser baseUser, SaLoginModel model) {
        login(logic, baseUser, baseUser.getUserId(), model);
    }

    /**
     * 登录系统 基于 设备类型
     * 针对相同用户体系不同设备
     *
     * @param baseUser 登录用户信息
     */
    public static void login(StpLogic logic, BaseUser baseUser, Object loginId, SaLoginModel model) {
        String loginType = getLoginType(logic);
        baseUser.setLoginType(loginType);
//        SaStorage storage = SaHolder.getStorage();
//        storage.set(loginType + LOGIN_USER_KEY, baseUser);
//        storage.set(loginType + TENANT_KEY, baseUser.getTenantId());
//        storage.set(loginType + USER_KEY, baseUser.getUserId());
        model = ObjectUtil.defaultIfNull(model, new SaLoginModel());
        SaSecurityContext.setContext(baseUser);
        logic.login(loginId, model);
        SaSession tokenSession = logic.getTokenSession();
        tokenSession.updateTimeout(model.getTimeout());
        tokenSession.set(LOGIN_USER_KEY, baseUser);
    }

    /**
     * 获取用户
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseUser> T getUser(StpLogic logic) {
        try {
            SaSession session = logic.getTokenSession();
            if (ObjectUtil.isNull(session)) {
                return null;
            }
            return (T) session.get(LOGIN_USER_KEY);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取用户(多级缓存)
     */
    public static <T extends BaseUser> Optional<T> getUserOptional(StpLogic logic) {
        return Optional.ofNullable(getUser(logic));
    }

    /**
     * 获取用户基于token
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseUser> T getUser(StpLogic logic, String token) {
        SaSession session = logic.getTokenSessionByToken(token, false);
        if (session != null) {
            return (T) session.get(LOGIN_USER_KEY);
        }
        return null;
    }

    /**
     * 获取用户基于token
     */
    public static <T extends BaseUser> Optional<T> getUserOptional(StpLogic logic, String token) {
        return Optional.ofNullable(getUser(logic, token));
    }

    /**
     * 更新用户
     *
     * @param updateBy 更新回调
     */
    public static <T extends BaseUser> void updateUser(StpLogic logic, Consumer<T> updateBy) {
        Object loginId = logic.getLoginId();
        if (loginId != null) {
            updateUser(logic, loginId, updateBy);
        }
    }

    /**
     * 更新用户
     *
     * @param loginId  登录id
     * @param updateBy 更新回调
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseUser> void updateUser(StpLogic logic, Object loginId, Consumer<T> updateBy) {
        List<String> tokens = logic.getTokenValueListByLoginId(loginId);
        String tokenValue = null;
        try {
            tokenValue = logic.getTokenValue();
        } catch (Exception ignore) {
            // 不做处理
        }
        if (CollUtil.isNotEmpty(tokens)) {
            for (String token : tokens) {
                SaSession session = logic.getTokenSessionByToken(token, false);
                if (session == null) {
                    continue;
                }
                T tokenUser = (T) session.get(LOGIN_USER_KEY);
                if (tokenUser == null) {
                    continue;
                }
                updateBy.accept(tokenUser);
                session.set(LOGIN_USER_KEY, tokenUser);
                if (Objects.equals(tokenValue, token)) {
                    SaStorage storage = SaHolder.getStorage();
                    if (storage != null) {
                        storage.set(getLoginType(logic) + LOGIN_USER_KEY, tokenUser);
                    }
                }
            }
        }
    }

    /**
     * 获取用户id
     */
    public static Long getUserId(StpLogic logic) {
        return getUserOptional(logic).map(baseUser -> Convert.toLong(baseUser.getUserId())).orElse(null);
    }

    /**
     * 获取租户ID
     */
    public static String getTenantId(StpLogic logic) {
        return getUserOptional(logic).map(BaseUser::getTenantId).orElse(null);
    }

    /**
     * 获取用户账户
     */
    public static String getUsername(StpLogic logic) {
        return getUserOptional(logic).map(BaseUser::getUsername).orElse(null);
    }
}
