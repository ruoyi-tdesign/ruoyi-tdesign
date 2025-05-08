package org.dromara.system.config.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.system.config.SmsFieldConfigs;
import org.dromara.system.config.TemplateMode;

/**
 * 七牛云短信
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QiNiuSmsFieldConfigs extends SmsFieldConfigs {
    /** 请求地址 */
    private FieldConfig<String> baseUrl;
    /** 模板变量名称 */
    private FieldConfig<String> templateName;
    /** 单发链接 */
    private FieldConfig<String> singleMsgUrl;
    /** 群发链接 */
    private FieldConfig<String> massMsgUrl;
    /** 签名ID */
    private FieldConfig<String> signatureId;

    public QiNiuSmsFieldConfigs() {
        this.accessKeyId = FieldConfig.<String>builder()
            .useInput()
            .name("AccessKey")
            .required(true)
            .build();
        this.accessKeySecret = FieldConfig.<String>builder()
            .useInput()
            .name("SecretKey")
            .required(true)
            .inputComponent().type("password").end()
            .build();
        this.baseUrl = FieldConfig.<String>builder()
            .value("https://sms.qiniuapi.com")
            .useInput()
            .name("请求地址")
            .help("默认请求地址为 https://sms.qiniuapi.com")
            .required(true)
            .build();
        this.templateName = FieldConfig.<String>builder()
            .useInput()
            .name("模板变量名称")
            .required(false)
            .build();
        this.singleMsgUrl = FieldConfig.<String>builder()
            .value("/v1/message/single")
            .useInput()
            .name("单发链接")
            .help("默认单发链接为 /v1/message/single")
            .required(true)
            .build();
        this.massMsgUrl = FieldConfig.<String>builder()
            .value("/v1/message")
            .useInput()
            .name("群发链接")
            .help("默认群发链接为 /v1/message")
            .required(true)
            .build();
        this.signatureId = FieldConfig.<String>builder()
            .useInput()
            .name("签名ID")
            .required(false)
            .build();
    }

    /** 获取模板模式 */
    @Override
    public TemplateMode getTemplateMode() {
        return new TemplateMode(true, false);
    }
}
