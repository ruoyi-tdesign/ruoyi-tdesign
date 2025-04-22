package org.dromara.web.service;


import org.dromara.common.core.domain.model.LoginBody;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.dromara.system.domain.vo.SysClientVo;
import org.dromara.web.domain.vo.LoginVo;

/**
 * 授权策略
 *
 * @author Michelle.Chung
 */
public interface IAuthStrategy<L extends LoginBody> {

    String BASE_NAME = "AuthStrategy";

    /**
     * 登录
     *
     * @param client    授权管理视图对象
     * @param loginBody 登录对象
     * @return 登录验证信息
     */
    static <L extends LoginBody> LoginVo login(SysClientVo client, L loginBody) {
        // 授权类型和客户端id
        String grantType = loginBody.getGrantType();
        String beanName = grantType + BASE_NAME;
        if (!SpringUtils.containsBean(beanName)) {
            throw new ServiceException("授权类型不正确!");
        }
        IAuthStrategy<L> instance = SpringUtils.getBean(beanName);
        return instance.login(loginBody, client);
    }

    /**
     * 登录
     *
     * @param loginBody 登录对象
     * @param client    授权管理视图对象
     * @return 登录验证信息
     */
    LoginVo login(L loginBody, SysClientVo client);

}
