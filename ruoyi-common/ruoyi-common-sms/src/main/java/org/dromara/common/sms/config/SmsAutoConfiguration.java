package org.dromara.common.sms.config;

import org.dromara.common.sms.handler.SmsExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 短信配置类
 *
 * @author Feng
 */
@AutoConfiguration(after = {RedisAutoConfiguration.class})
public class SmsAutoConfiguration {

    /**
     * 异常处理器
     */
    @Bean
    public SmsExceptionHandler smsExceptionHandler() {
        return new SmsExceptionHandler();
    }

}
