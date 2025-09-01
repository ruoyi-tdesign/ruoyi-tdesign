package org.dromara.system.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.ui.FieldConfig;

/**
 * 签名短信基本配置对象
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SignatureSmsFieldConfigs extends SmsFieldConfigs {
    /** 短信签名 */
    protected FieldConfig<String> signature;
}
