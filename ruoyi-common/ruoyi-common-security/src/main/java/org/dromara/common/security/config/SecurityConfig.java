package org.dromara.common.security.config;

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.model.BaseUser;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.dromara.common.satoken.config.MultipleSaTokenConfig;
import org.dromara.common.satoken.config.MultipleSaTokenProperties;
import org.dromara.common.satoken.context.SaSecurityContext;
import org.dromara.common.satoken.stp.DynamicStpLogic;
import org.dromara.common.satoken.utils.MultipleLoginBaseHelper;
import org.dromara.common.security.config.properties.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

/**
 * 权限安全配置
 *
 * @author Lion Li
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final SecurityProperties securityProperties;
    private final MultipleSaTokenProperties multipleSaTokenProperties;

    /**
     * 注册sa-token的拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 使用注解的方式精细控制权限
        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(new SaInterceptor(handler -> {
                for (Map.Entry<String, MultipleSaTokenConfig> entry : multipleSaTokenProperties.getMultiple().entrySet()) {
                    String type = entry.getKey();
                    MultipleSaTokenConfig config = entry.getValue();
                    if (CollUtil.isNotEmpty(config.getMatch())) {
                        StpLogic logic = DynamicStpLogic.getDynamicStpLogic(type);
                        SaRouter
                            .match(config.getMatch())
                            .check(() -> {
                                if (logic.isLogin()) {
                                    BaseUser baseUser = MultipleLoginBaseHelper.getUser(logic);
                                    SaSecurityContext.setContext(baseUser);
                                }
                            });
                    }
                }
                if (SaSecurityContext.getContext() == null) {
                    throw NotLoginException.newInstance("unknown", NotLoginException.NOT_TOKEN, NotLoginException.NOT_TOKEN_MESSAGE, null)
                        .setCode(SaErrorCode.CODE_11011);
                }
            })).addPathPatterns("/**")
            // 排除不需要拦截的路径
            .excludePathPatterns(securityProperties.getExcludes());
    }

    /**
     * 对 actuator 健康检查接口 做账号密码鉴权
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        String username = SpringUtils.getProperty("spring.boot.admin.client.username");
        String password = SpringUtils.getProperty("spring.boot.admin.client.password");
        return new SaServletFilter()
            .addInclude("/actuator", "/actuator/**")
            .setAuth(obj -> {
                SaHttpBasicUtil.check(username + ":" + password);
            })
            .setError(e -> SaResult.error(e.getMessage()).setCode(HttpStatus.UNAUTHORIZED));
    }

}
