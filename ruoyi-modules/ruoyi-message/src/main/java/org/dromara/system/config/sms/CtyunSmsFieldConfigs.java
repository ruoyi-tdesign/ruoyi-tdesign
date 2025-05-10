package org.dromara.system.config.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.system.config.SignatureSmsFieldConfigs;
import org.dromara.system.config.TemplateMode;

/**
 * 天翼云短信
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CtyunSmsFieldConfigs extends SignatureSmsFieldConfigs {
    /** 请求地址 */
    private FieldConfig<String> requestUrl;
    /** 接口名称 */
    private FieldConfig<String> action;

    public CtyunSmsFieldConfigs() {
        this.accessKeyId = FieldConfig.<String>builder()
            .useInput()
            .label("accessKey")
            .help("天翼云的accessKey")
            .required(true)
            .build();
        this.accessKeySecret = FieldConfig.<String>builder()
            .useInput()
            .label("accessKeySecret")
            .help("天翼云的accessKeySecret")
            .required(true)
            .inputComponent().type("password").end()
            .build();
        this.signature = FieldConfig.<String>builder()
            .useInput()
            .label("默认短信签名")
            .required(true)
            .build();
        this.requestUrl = FieldConfig.<String>builder()
            .value("https://sms-global.ctapi.ctyun.cn/sms/api/v1")
            .useInput()
            .label("请求地址")
            .help("请求地址默认为 https://sms-global.ctapi.ctyun.cn/sms/api/v1 如无特殊改变可以不用设置")
            .required(true)
            .build();
        this.action = FieldConfig.<String>builder()
            .value("SendSms")
            .useInput()
            .label("接口方法")
            .help("接口方法默认为 SendSms 如无特殊改变可以不用设置")
            .required(true)
            .build();
    }

    /** 获取模板模式 */
    @Override
    public TemplateMode getTemplateMode() {
        return new TemplateMode(true, false);
    }
}
