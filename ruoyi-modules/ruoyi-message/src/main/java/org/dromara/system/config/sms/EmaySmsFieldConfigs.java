package org.dromara.system.config.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.system.config.SmsFieldConfigs;
import org.dromara.system.config.TemplateMode;

/**
 * 亿美软通短信
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EmaySmsFieldConfigs extends SmsFieldConfigs {
    /** APP接入地址 */
    private FieldConfig<String> requestUrl;

    public EmaySmsFieldConfigs() {
        this.accessKeyId = FieldConfig.<String>builder()
            .component("input")
            .name("appId")
            .help("访问键标识")
            .required(true)
            .build();
        this.accessKeySecret = FieldConfig.<String>builder()
            .component("input")
            .name("secretKey")
            .help("访问键秘钥")
            .required(true)
            .type("password")
            .build();
        this.requestUrl = FieldConfig.<String>builder()
            .component("input")
            .name("请求地址")
            .help("短信发送请求地址")
            .required(true)
            .build();
    }

    /** 获取模板模式 */
    @Override
    public TemplateMode getTemplateMode() {
        return new TemplateMode(true, false);
    }
}
