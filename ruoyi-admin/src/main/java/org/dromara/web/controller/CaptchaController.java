package org.dromara.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.constant.MessageConstants;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.enums.MessageTypeEnum;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.reflect.ReflectUtils;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.ratelimiter.enums.LimitType;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.web.config.properties.CaptchaProperties;
import org.dromara.common.web.enums.CaptchaType;
import org.dromara.system.service.ISysMessageSendService;
import org.dromara.web.domain.vo.CaptchaVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证码操作处理
 *
 * @author Lion Li
 */
@SaIgnore
@Slf4j
@Validated
@RestController
public class CaptchaController {

    @Autowired
    private CaptchaProperties captchaProperties;
    @Autowired
    private ISysMessageSendService messageSendService;

    /**
     * 短信验证码
     *
     * @param phonenumber 用户手机号
     */
    @RateLimiter(key = "#phonenumber", time = 60, count = 1)
    @GetMapping("/resource/sms/code")
    public R<Void> smsCode(@NotBlank(message = "{user.phonenumber.not.blank}") String phonenumber) {
        String key = GlobalConstants.CAPTCHA_CODE_KEY + phonenumber;
        String code = RandomUtil.randomNumbers(4);
        Map<String, Object> map = new HashMap<>(1);
        map.put("code", code);
        // 发送验证码
        messageSendService.send(MessageConstants.LOGIN_CAPTCHA, MessageTypeEnum.SMS, phonenumber, map);
        RedisUtils.setObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
        return R.ok();
    }

    /**
     * 邮箱验证码
     *
     * @param email 邮箱
     */
    @GetMapping("/resource/email/code")
    public R<Void> emailCode(@NotBlank(message = "{user.email.not.blank}") String email) {
        String key = GlobalConstants.CAPTCHA_CODE_KEY + email;
        String code = RandomUtil.randomNumbers(4);
        Map<String, Object> map = new HashMap<>(1);
        map.put("code", code);
        map.put("time", Constants.CAPTCHA_EXPIRATION);
        // 发送验证码
        messageSendService.send(MessageConstants.LOGIN_CAPTCHA, MessageTypeEnum.MAIL, email, map);
        RedisUtils.setObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
        return R.ok();
    }

    /**
     * 生成验证码
     */
    @GetMapping("/auth/code")
    public R<CaptchaVo> getCode() {
        boolean captchaEnabled = captchaProperties.getEnable();
        if (!captchaEnabled) {
            CaptchaVo captchaVo = new CaptchaVo();
            captchaVo.setCaptchaEnabled(false);
            return R.ok(captchaVo);
        }
        return R.ok(SpringUtils.getAopProxy(this).getCodeImpl());
    }

    /**
     * 生成验证码
     * 独立方法避免验证码关闭之后仍然走限流
     */
    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    public CaptchaVo getCodeImpl() {
        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + uuid;
        // 生成验证码
        CaptchaType captchaType = captchaProperties.getType();
        boolean isMath = CaptchaType.MATH == captchaType;
        Integer length = isMath ? captchaProperties.getNumberLength() : captchaProperties.getCharLength();
        CodeGenerator codeGenerator = ReflectUtils.newInstance(captchaType.getClazz(), length);
        AbstractCaptcha captcha = SpringUtils.getBean(captchaProperties.getCategory().getClazz());
        captcha.setGenerator(codeGenerator);
        captcha.createCode();
        // 如果是数学验证码，使用SpEL表达式处理验证码结果
        String code = captcha.getCode();
        if (isMath) {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(StringUtils.remove(code, "="));
            code = exp.getValue(String.class);
        }
        RedisUtils.setObject(verifyKey, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
        CaptchaVo captchaVo = new CaptchaVo();
        captchaVo.setUuid(uuid);
        captchaVo.setImg(captcha.getImageBase64());
        return captchaVo;
    }

}
