package org.dromara.system.config.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.system.config.FieldConfig;
import org.dromara.system.config.SignatureSmsFieldConfigs;
import org.dromara.system.config.TemplateMode;

/**
 * 网易云短信
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NeteaseSmsFieldConfigs extends SignatureSmsFieldConfigs {
    /** 模板短信请求地址 */
    private FieldConfig<String> templateUrl;
    /** 验证码短信请求地址 */
    private FieldConfig<String> codeUrl;
    /** 验证码验证请求地址 */
    private FieldConfig<String> verifyUrl;
    /** 是否需要支持短信上行。true:需要，false:不需要 说明：如果开通了短信上行抄送功能，该参数需要设置为true，其它情况设置无效 */
    private FieldConfig<Boolean> needUp;

    public NeteaseSmsFieldConfigs() {
        this.accessKeyId = FieldConfig.<String>builder()
            .component("input")
            .name("accessKey")
            .help("访问键标识")
            .required(true)
            .build();
        this.accessKeySecret = FieldConfig.<String>builder()
            .component("input")
            .name("accessKeySecret")
            .help("访问键秘钥")
            .required(true)
            .type("password")
            .build();
        this.signature = FieldConfig.<String>builder()
            .component("input")
            .name("默认短信签名")
            .required(true)
            .build();
        this.templateUrl = FieldConfig.<String>builder()
            .value("https://api.netease.im/sms/sendtemplate.action")
            .component("input")
            .name("模板短信请求地址")
            .required(true)
            .build();
        this.codeUrl = FieldConfig.<String>builder()
            .value("https://api.netease.im/sms/sendcode.action")
            .component("input")
            .name("验证码短信请求地址")
            .required(true)
            .build();
        this.verifyUrl = FieldConfig.<String>builder()
            .value("https://api.netease.im/sms/verifycode.action")
            .component("input")
            .name("验证码验证请求地址")
            .required(true)
            .build();
        this.needUp = FieldConfig.<Boolean>builder()
            .value(false)
            .component("switch")
            .name("支持短信上行")
            .help("是否需要支持短信上行。true:需要，false:不需要 说明：如果开通了短信上行抄送功能，该参数需要设置为true，其它情况设置无效\n")
            .required(true)
            .build();
    }

    /** 获取模板模式 */
    @Override
    public TemplateMode getTemplateMode() {
        return new TemplateMode(true, false);
    }
}
