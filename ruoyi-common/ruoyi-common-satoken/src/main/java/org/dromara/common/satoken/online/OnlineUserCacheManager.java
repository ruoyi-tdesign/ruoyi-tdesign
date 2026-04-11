package org.dromara.common.satoken.online;

import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import org.dromara.common.core.enums.UserType;

/**
 * 在线用户管理
 *
 * @author hexm
 * @date 2023/07/14 17:14
 */
public interface OnlineUserCacheManager {

    /**
     * 保存在线用户
     *
     * @param userType       用户类型
     * @param tokenValue     token值
     * @param loginParameter 登录对象
     */
    void setCache(UserType userType, String tokenValue, SaLoginParameter loginParameter);

    /**
     * 保存在线用户
     *
     * @param userType       用户类型
     * @param tokenValue     token值
     * @param loginParameter 登录对象
     * @param timeout        超时时间
     */
    void setCache(UserType userType, String tokenValue, SaLoginParameter loginParameter, Long timeout);

    /**
     * 删除缓存
     *
     * @param loginType  登录类型
     * @param loginId    用户id
     * @param tokenValue token值
     */
    void deleteCache(String loginType, Object loginId, String tokenValue);
}
