package org.dromara.common.satoken.utils;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import org.dromara.common.core.domain.dto.UserOnlineDTO;
import org.dromara.common.core.domain.model.BaseUser;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.core.utils.ip.AddressUtils;
import org.dromara.common.satoken.context.SaSecurityContext;

/**
 * 在线用户登录工具
 *
 * @author hexm
 * @date 2023/07/14 17:30
 */
public class OnlineUserUtil {

    /**
     * 获取在线登录用户对象
     *
     * @param tokenValue token值
     * @return
     */
    public static UserOnlineDTO getOnlineDTO(String tokenValue) {
        UserAgent userAgent = UserAgentUtil.parse(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = ServletUtils.getClientIP();
        BaseUser baseUser = SaSecurityContext.getContext();
        UserOnlineDTO dto = new UserOnlineDTO();
        dto.setIpaddr(ip);
        dto.setTenantId(baseUser.getTenantId());
        dto.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        dto.setBrowser(userAgent.getBrowser().getName());
        dto.setOs(userAgent.getOs().getName());
        dto.setLoginTime(System.currentTimeMillis());
        dto.setTokenId(tokenValue);
        dto.setUserName(baseUser.getUsername());
        if (baseUser instanceof LoginUser user) {
            dto.setDeptName(user.getDeptName());
            dto.setClientKey(user.getClientKey());
        }
        dto.setDeviceType(baseUser.getDeviceType());
        return dto;
    }
}
