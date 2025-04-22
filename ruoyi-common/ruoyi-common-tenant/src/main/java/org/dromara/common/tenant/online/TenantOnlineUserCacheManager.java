package org.dromara.common.tenant.online;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.hutool.core.util.StrUtil;
import org.dromara.common.core.constant.CacheConstants;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.domain.dto.UserOnlineDTO;
import org.dromara.common.core.domain.model.BaseUser;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.context.SaSecurityContext;
import org.dromara.common.satoken.online.OnlineUserCacheManager;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.satoken.utils.OnlineUserUtil;
import org.dromara.common.tenant.helper.TenantHelper;

import java.time.Duration;

/**
 * 租户在线用户缓存管理
 *
 * @author hexm
 * @date 2023/07/14 17:29
 */
public class TenantOnlineUserCacheManager implements OnlineUserCacheManager {

    /**
     * 保存在线用户
     *
     * @param userType   用户类型
     * @param tokenValue token值
     * @param loginModel 登录对象
     */
    @Override
    public void setCache(UserType userType, String tokenValue, SaLoginModel loginModel) {
        UserOnlineDTO dto = OnlineUserUtil.getOnlineDTO(tokenValue);
        LoginUser user = LoginHelper.getUser();
        RedisUtils.setObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, dto);
        RedisUtils.setObject(GlobalConstants.ONLINE_TOKEN_TENANT_ID_KEY + tokenValue, user.getTenantId());
    }

    /**
     * 保存在线用户
     *
     * @param userType   用户类型
     * @param tokenValue token值
     * @param loginModel 登录对象
     * @param timeout    超时时间
     */
    @Override
    public void setCache(UserType userType, String tokenValue, SaLoginModel loginModel, Long timeout) {
        BaseUser baseUser = SaSecurityContext.getContext();
        UserOnlineDTO dto = OnlineUserUtil.getOnlineDTO(tokenValue);
        RedisUtils.setObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, dto, Duration.ofSeconds(timeout));
        RedisUtils.setObject(GlobalConstants.ONLINE_TOKEN_TENANT_ID_KEY + tokenValue, baseUser.getTenantId(), Duration.ofSeconds(timeout));
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
        String tenantId = RedisUtils.getObject(GlobalConstants.ONLINE_TOKEN_TENANT_ID_KEY + tokenValue);
        if (StrUtil.isNotBlank(tenantId)) {
            try {
                TenantHelper.dynamicTenant(tenantId,() -> {
                    RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
                });
            } finally {
                RedisUtils.deleteObject(GlobalConstants.ONLINE_TOKEN_TENANT_ID_KEY + tokenValue);
            }
        }
    }
}
