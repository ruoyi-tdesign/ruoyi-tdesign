package org.dromara.system.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.enums.MessageTypeEnum;

/**
 * 短信配置
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SmsFieldConfigs extends SupplierFieldConfigs {
    /** Access Key */
    protected FieldConfig<String> accessKeyId;
    /** Access Key Secret */
    protected FieldConfig<String> accessKeySecret;

    /** 获取消息类型 */
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.SMS;
    }
}
