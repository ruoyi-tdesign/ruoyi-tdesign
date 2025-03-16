package org.dromara.system.handle.handler;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.json.JSONUtil;
import org.dromara.common.core.enums.CommonStatusEnum;
import org.dromara.common.core.enums.MailMessageSupplierType;
import org.dromara.common.core.enums.MessageTypeEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.mail.utils.MailUtils;
import org.dromara.system.domain.SysMessageConfig;
import org.dromara.system.domain.SysMessageTemplate;
import org.dromara.system.handle.BaseMessageSendHandler;
import org.dromara.system.service.ISysMessageLogService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * 邮件发送消息处理类
 *
 * @author hexm
 * @date 2023/07/24 11:07
 */
@Component
public class MailMessageSendHandler extends BaseMessageSendHandler {

    /**
     * 判断是否是html内容的正则
     */
    private static final Pattern HTML = Pattern.compile(".*((<[a-zA-Z]+[^>]*>[^<>]*</[a-zA-Z]+>)+|(<[a-zA-Z]+[^>]*/>)+).*");

    public MailMessageSendHandler(ISysMessageLogService messageLogService) {
        super(messageLogService);
    }

    /**
     * 获取消息类型
     */
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.MAIL;
    }

    /**
     * 发送消息
     *
     * @param account  账号
     * @param message  消息变量
     * @param template 模板对象
     * @param config   消息配置
     */
    @Override
    public void send(Collection<String> account, Map<String, Object> message, SysMessageTemplate template, SysMessageConfig config) {
        LinkedHashMap<String, String> outputVars = getOutputVars(template, message);
        String content = getContent(template, outputVars);
        MailAccount mailAccount;
        if (Objects.equals(config.getSupplierType(), MailMessageSupplierType.MAIL.name())) {
            mailAccount = JSONUtil.toBean(config.getConfigJson(), MailAccount.class);
        } else {
            throw new ServiceException("不支持的邮箱消息类型");
        }
        Properties properties = getProperties(message);
        String title = helper.replacePlaceholders(template.getTitle(), properties);
        String messageId;
        if (isHtml(content)) {
            messageId = MailUtils.sendHtml(mailAccount, account, title, content);
        } else {
            messageId = MailUtils.sendText(mailAccount, account, title, content);
        }

        // 记录发送记录
        saveLog(account, template, config, content, log -> {
            log.setIsSuccess(CommonStatusEnum.SUCCESS.getCodeNum());
            log.setResponseBody(messageId);
            log.setTitle(title);
        });
    }

    /**
     * 是否是html
     *
     * @param content 内容
     */
    private static boolean isHtml(String content) {
        return HTML.matcher(content).matches();
    }
}
