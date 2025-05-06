package org.dromara.system.config.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.system.config.SignatureSmsFieldConfigs;
import org.dromara.system.config.TemplateMode;

/**
 * 腾讯云短信
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TencentSmsFieldConfigs extends SignatureSmsFieldConfigs {
    /** 短信sdkAppId */
    private FieldConfig<String> sdkAppId;
    /** 地域信息默认为 ap-guangzhou */
    private FieldConfig<String> territory;
    /** 请求超时时间 */
    private FieldConfig<Integer> connTimeout;
    /** 请求地址 */
    private FieldConfig<String> requestUrl;
    /** 接口名称 */
    private FieldConfig<String> action;
    /** 接口版本 */
    private FieldConfig<String> version;

    public TencentSmsFieldConfigs() {
        this.accessKeyId = FieldConfig.<String>builder()
            .component("input")
            .name("accessKey")
            .help("腾讯云的accessKey。SecretId、SecretKey 查询: https://console.cloud.tencent.com/cam/capi")
            .required(true)
            .build();
        this.accessKeySecret = FieldConfig.<String>builder()
            .component("input")
            .name("accessKeySecret")
            .help("腾讯云的accessKeySecret")
            .required(true)
            .type("password")
            .build();
        this.signature = FieldConfig.<String>builder()
            .component("input")
            .name("默认短信签名")
            .help("签名信息可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-sign) 或<br />[国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-sign) 的签名管理查看")
            .required(true)
            .build();
        this.sdkAppId = FieldConfig.<String>builder()
            .component("input")
            .name("短信应用ID")
            .help("短信sdkAppId。应用 ID 可前往 [短信控制台](https://console.cloud.tencent.com/smsv2/app-manage) 查看")
            .required(true)
            .build();
        this.territory = FieldConfig.<String>builder()
            .value("ap-guangzhou")
            .component("input")
            .name("地域")
            .help("地域信息默认为 ap-guangzhou 如无特殊改变可不用设置")
            .required(true)
            .build();
        this.connTimeout = FieldConfig.<Integer>builder()
            .value(60)
            .component("input-number")
            .name("请求超时时间")
            .help("请求超时时间 默认60秒")
            .required(true)
            .min(30)
            .max(180)
            .build();
        this.requestUrl = FieldConfig.<String>builder()
            .value("sms.tencentcloudapi.com")
            .component("input")
            .name("请求地址")
            .help("请求地址默认为 sms.tencentcloudapi.com 如无特殊改变可不用设置")
            .required(true)
            .build();
        this.action = FieldConfig.<String>builder()
            .value("SendSms")
            .component("input")
            .name("接口方法")
            .help("接口方法默认为 SendSms 如无特殊改变可以不用设置")
            .required(true)
            .build();
        this.version = FieldConfig.<String>builder()
            .value("2021-01-11")
            .component("input")
            .name("接口版本号")
            .help("接口版本默认为 2021-01-11 如无特殊改变可不用设置")
            .required(true)
            .build();
    }

    /** 获取模板模式 */
    @Override
    public TemplateMode getTemplateMode() {
        return new TemplateMode(true, false);
    }
}
