package org.dromara.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * 消息类型
 *
 * @author hexm
 * @date 2023/06/29 16:33
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    /**
     * 短信
     */
    SMS("短信", SmsMessageSupplierType.getMap()),
    /**
     * 邮箱
     */
    MAIL("邮箱", MailMessageSupplierType.getMap());

    private final String description;
    private final Map<String, String> supplierTypeMap;
}
