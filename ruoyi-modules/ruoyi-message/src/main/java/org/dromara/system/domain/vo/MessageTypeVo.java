package org.dromara.system.domain.vo;

import lombok.Data;

import java.util.Map;

/**
 * 消息类型
 *
 * @author hexm
 * @date 2025/3/10
 */
@Data
public class MessageTypeVo {
    private String messageType;
    private String description;
    private Map<String, String> supplierTypeMap;
}
