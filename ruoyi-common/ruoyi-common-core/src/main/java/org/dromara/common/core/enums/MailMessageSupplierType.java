package org.dromara.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 邮箱消息支持类型
 *
 * @author hexm
 * @date 2023/06/29 16:56
 */
@Getter
@AllArgsConstructor
public enum MailMessageSupplierType {
    /** 邮箱 */
    MAIL("邮箱");

    private final String description;

    public static Map<String, String> getMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (MailMessageSupplierType value : values()) {
            map.put(value.name(), value.getDescription());
        }
        return map;
    }
}
