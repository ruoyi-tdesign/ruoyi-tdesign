package org.dromara.system.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.dromara.common.core.enums.MessageTypeEnum;
import org.dromara.common.core.ui.FieldConfig;

import java.util.Map;

/**
 * 消息配置
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@AllArgsConstructor
public class MessageFieldConfig {

    /** 消息配置 */
    private Map<String, FieldConfig<Object>> supplierConfig;

    /** 消息类型 */
    private MessageTypeEnum messageType;

    /** 消息模板支持类型 */
    private TemplateMode templateMode;
}
