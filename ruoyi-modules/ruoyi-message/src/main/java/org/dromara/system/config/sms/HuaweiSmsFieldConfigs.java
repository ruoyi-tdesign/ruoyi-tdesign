package org.dromara.system.config.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.system.config.SignatureSmsFieldConfigs;
import org.dromara.system.config.TemplateMode;

/**
 * 华为云
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HuaweiSmsFieldConfigs extends SignatureSmsFieldConfigs {
    /** 国内短信签名通道号 */
    private FieldConfig<String> sender;
    /** 短信状态报告接收地 */
    private FieldConfig<String> statusCallBack;
    /** APP接入地址 */
    private FieldConfig<String> url;

    public HuaweiSmsFieldConfigs() {
        this.accessKeyId = FieldConfig.<String>builder()
            .component("input")
            .name("appkey")
            .help("华为短信应用appkey")
            .required(true)
            .build();
        this.accessKeySecret = FieldConfig.<String>builder()
            .component("input")
            .name("appSecret")
            .help("华为云的appSecret")
            .required(true)
            .type("password")
            .build();
        this.signature = FieldConfig.<String>builder()
            .component("input")
            .name("默认短信签名")
            .required(true)
            .build();
        this.sender = FieldConfig.<String>builder()
            .component("input")
            .name("短信签名通道号")
            .required(true)
            .build();
        this.statusCallBack = FieldConfig.<String>builder()
            .component("input")
            .name("短信状态回调")
            .help("华为云国内短信状态报告接收地址")
            .required(false)
            .build();
        this.url = FieldConfig.<String>builder()
            .value("https://smsapi.cn-xxxxx-4.myhuaweicloud.com:443")
            .component("input")
            .name("APP接入地址")
            .help("APP接入地址 建立短信应用后获取到的地址")
            .required(true)
            .build();
    }

    /** 获取模板模式 */
    @Override
    public TemplateMode getTemplateMode() {
        return new TemplateMode(true, false);
    }
}
