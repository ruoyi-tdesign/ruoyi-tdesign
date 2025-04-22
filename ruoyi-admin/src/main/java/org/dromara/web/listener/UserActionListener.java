package org.dromara.web.listener;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.domain.model.BaseUser;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.utils.MessageUtils;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.dromara.common.log.event.LogininforEvent;
import org.dromara.common.satoken.context.SaSecurityContext;
import org.dromara.common.satoken.online.OnlineUserCacheManager;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.satoken.utils.MultipleStpUtil;
import org.dromara.web.service.SysLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 用户行为 侦听器的实现
 *
 * @author Lion Li
 */
@Component
@Slf4j
public class UserActionListener implements SaTokenListener {

    @Autowired
    private SaTokenConfig tokenConfig;
    @Autowired
    private OnlineUserCacheManager onlineUserCacheManager;
    @Autowired
    private SysLoginService loginService;

    /**
     * 每次登录时触发
     */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
        BaseUser baseUser = SaSecurityContext.getContext();
        if (baseUser == null) {
            return;
        }
        if (Objects.equals(baseUser.getLoginType(), MultipleStpUtil.SYSTEM_TYPE)) {
            // 系统用户登录
            UserType userType = UserType.getUserType(loginId.toString());
            Long timeout = ObjectUtil.defaultIfNull(loginModel.getTimeout(), tokenConfig.getTimeout());
            if (timeout <= 0) {
                onlineUserCacheManager.setCache(userType, tokenValue, loginModel);
            } else {
                onlineUserCacheManager.setCache(userType, tokenValue, loginModel, timeout);
            }
            HttpServletRequest request = ServletUtils.getRequest();
            final UserAgent userAgent = UserAgentUtil.parse(request.getHeader(Header.USER_AGENT.getValue()));
            String ip = ServletUtils.getClientIP();
            // 获取客户端操作系统
            String os = userAgent.getOs().getName();
            // 获取客户端浏览器
            String browser = userAgent.getBrowser().getName();
            String clientId = request.getHeader(LoginHelper.CLIENT_KEY);

            // 记录登录日志
            LogininforEvent logininforEvent = new LogininforEvent();
            logininforEvent.setTenantId(baseUser.getTenantId());
            logininforEvent.setUserId(baseUser.getUserId());
            logininforEvent.setUsername(baseUser.getUsername());
            logininforEvent.setStatus(Constants.LOGIN_SUCCESS);
            logininforEvent.setMessage(MessageUtils.message("user.login.success"));
            logininforEvent.setIp(ip);
            logininforEvent.setOs(os);
            logininforEvent.setBrowser(browser);
            logininforEvent.setClientId(clientId);
            SpringUtils.context().publishEvent(logininforEvent);
            // 更新登录信息
            loginService.recordLoginInfo(baseUser.getUserId(), ip);
            log.info("user doLogin, userId:{}, token:{}", loginId, tokenValue);
        }
        // 用户登录处理逻辑
//        else if (Objects.equals(baseUser.getLoginType(), MultipleStpUtil.USER_TYPE)) {
//        }
    }

    /**
     * 每次注销时触发
     */
    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        onlineUserCacheManager.deleteCache(loginType, loginId, tokenValue);
        log.info("user doLogout, userId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次被踢下线时触发
     */
    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        onlineUserCacheManager.deleteCache(loginType, loginId, tokenValue);
        log.info("user doKickout, userId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次被顶下线时触发
     */
    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        onlineUserCacheManager.deleteCache(loginType, loginId, tokenValue);
        log.info("user doReplaced, userId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次被封禁时触发
     */
    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
    }

    /**
     * 每次被解封时触发
     */
    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {
    }

    /**
     * 每次打开二级认证时触发
     */
    @Override
    public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
    }

    /**
     * 每次创建Session时触发
     */
    @Override
    public void doCloseSafe(String loginType, String tokenValue, String service) {
    }

    /**
     * 每次创建Session时触发
     */
    @Override
    public void doCreateSession(String id) {
    }

    /**
     * 每次注销Session时触发
     */
    @Override
    public void doLogoutSession(String id) {
    }

    /**
     * 每次Token续期时触发
     */
    @Override
    public void doRenewTimeout(String tokenValue, Object loginId, long timeout) {
    }
}
