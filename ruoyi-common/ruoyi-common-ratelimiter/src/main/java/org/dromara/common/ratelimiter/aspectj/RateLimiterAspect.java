package org.dromara.common.ratelimiter.aspectj;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MessageUtils;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.spring.SpringExpressionCreated;
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.ratelimiter.enums.LimitType;
import org.dromara.common.redis.utils.RedisUtils;
import org.redisson.api.RateType;

/**
 * 限流处理
 *
 * @author Lion Li
 */
@Slf4j
@Aspect
public class RateLimiterAspect {

    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) {
        int time = rateLimiter.time();
        int count = rateLimiter.count();
        int timeout = rateLimiter.timeout();
        try {
            String combineKey = getCombineKey(rateLimiter, point);
            RateType rateType = RateType.OVERALL;
            if (rateLimiter.limitType() == LimitType.CLUSTER) {
                rateType = RateType.PER_CLIENT;
            }
            long number = RedisUtils.rateLimiter(combineKey, rateType, count, time, timeout);
            if (number == -1) {
                String message = rateLimiter.message();
                if (StringUtils.startsWith(message, "{") && StringUtils.endsWith(message, "}")) {
                    message = MessageUtils.message(StringUtils.substring(message, 1, message.length() - 1));
                }
                throw new ServiceException(message);
            }
            log.info("限制令牌 => {}, 剩余令牌 => {}, 缓存key => '{}'", count, number, combineKey);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw e;
            } else {
                throw new RuntimeException("服务器限流异常，请稍候再试", e);
            }
        }
    }

    private String getCombineKey(RateLimiter rateLimiter, JoinPoint point) {
        // SpEL表达式
        // 解析返回给key
        String key = rateLimiter.key();
        String split = ":";
        if (StringUtils.isNotBlank(key)) {
            // 判断是否是spel格式
            try {
                if (StringUtils.containsAny(key, "#{") && StringUtils.containsAny(key, "}")) {
                    key = SpringExpressionCreated.createStandardTemplate(point).getValueString(rateLimiter.key()) + split;
                } else if (StringUtils.containsAny(key, "#")) {
                    key = SpringExpressionCreated.createStandard(point).getValueString(rateLimiter.key()) + split;
                }
            } catch (Exception e) {
                throw new ServiceException("限流key解析异常，请联系管理员!");
            }
        }
        StringBuilder stringBuffer = new StringBuilder(GlobalConstants.RATE_LIMIT_KEY);
        stringBuffer.append(ServletUtils.getRequest().getRequestURI()).append(split);
        if (rateLimiter.limitType() == LimitType.IP) {
            // 获取请求ip
            stringBuffer.append(ServletUtils.getClientIP()).append(split);
        } else if (rateLimiter.limitType() == LimitType.CLUSTER) {
            // 获取客户端实例id
            stringBuffer.append(RedisUtils.getClient().getId()).append(split);
        }
        return stringBuffer.append(key).toString();
    }
}
