package org.dromara.common.satoken.online;

import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import org.dromara.common.core.constant.CacheConstants;
import org.dromara.common.core.domain.dto.UserOnlineDTO;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.utils.OnlineUserUtil;

import java.time.Duration;

/**
 * 默认在线登录缓存管理
 *
 * @author hexm
 * @date 2023/07/14 17:22
 */
public class DefaultOnlineUserCacheManager implements OnlineUserCacheManager {

    /**
     * 保存在线用户
     *
     * @param userType       用户类型
     * @param tokenValue     token值
     * @param loginParameter 登录对象
     */
    @Override
    public void setCache(UserType userType, String tokenValue, SaLoginParameter loginParameter) {
        UserOnlineDTO dto = OnlineUserUtil.getOnlineDTO(tokenValue);
        RedisUtils.setObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, dto);
    }

    /**
     * 保存在线用户
     *
     * @param userType       用户类型
     * @param tokenValue     token值
     * @param loginParameter 登录对象
     * @param timeout        超时时间
     */
    @Override
    public void setCache(UserType userType, String tokenValue, SaLoginParameter loginParameter, Long timeout) {
        UserOnlineDTO dto = OnlineUserUtil.getOnlineDTO(tokenValue);
        RedisUtils.setObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, dto, Duration.ofSeconds(timeout));
    }

    /**
     * 删除缓存
     *
     * @param loginType  登录类型
     * @param loginId    用户id
     * @param tokenValue token值
     */
    @Override
    public void deleteCache(String loginType, Object loginId, String tokenValue) {
        RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
    }
}
