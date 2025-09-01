package org.dromara.system.config.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.system.config.SignatureSmsFieldConfigs;
import org.dromara.system.config.TemplateMode;

/**
 * 合一短信
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UniSmsFieldConfigs extends SignatureSmsFieldConfigs {
    /** 是否为简易模式 */
    private FieldConfig<Boolean> isSimple;

    public UniSmsFieldConfigs() {
        this.accessKeyId = FieldConfig.<String>builder()
            .useInput()
            .label("accessKeyId")
            .help("访问键标识")
            .required(true)
            .build();
        this.accessKeySecret = FieldConfig.<String>builder()
            .useInput()
            .label("accessKeySecret")
            .help("访问键秘钥 简易模式不需要配置")
            .required(false)
            .inputComponent().type("password").end()
            .build();
        this.signature = FieldConfig.<String>builder()
            .useInput()
            .label("默认短信签名")
            .required(true)
            .build();
        this.isSimple = FieldConfig.<Boolean>builder()
            .value(true)
            .useSwitch()
            .label("简易模式")
            .help("是否为简易模式 默认为true")
            .required(true)
            .build();
    }

    /** 获取模板模式 */
    @Override
    public TemplateMode getTemplateMode() {
        return new TemplateMode(true, false);
    }
}
