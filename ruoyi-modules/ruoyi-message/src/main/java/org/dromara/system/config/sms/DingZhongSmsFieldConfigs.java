package org.dromara.system.config.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.system.config.SmsFieldConfigs;
import org.dromara.system.config.TemplateMode;

/**
 * 鼎众短信
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DingZhongSmsFieldConfigs extends SmsFieldConfigs {
    /** 接口发送地址 */
    private FieldConfig<String> requestUrl;

    public DingZhongSmsFieldConfigs() {
        this.accessKeyId = FieldConfig.<String>builder()
            .useInput()
            .name("cdkey")
            .help("短信帐号")
            .required(true)
            .build();
        this.accessKeySecret = FieldConfig.<String>builder()
            .useInput()
            .name("password")
            .help("密码")
            .required(true)
            .inputComponent().type("password").end()
            .build();
        this.requestUrl = FieldConfig.<String>builder()
            .value("http://demoapi.321sms.com:8201")
            .useInput()
            .name("请求地址")
            .help("短信发送请求地址： （演示及测试域名：http://demoapi.321sms.com:8201；正式域名：http://api.321sms.com）")
            .required(true)
            .build();
    }

    /** 获取模板模式 */
    @Override
    public TemplateMode getTemplateMode() {
        return new TemplateMode(true, false);
    }
}
