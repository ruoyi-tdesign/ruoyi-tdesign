package org.dromara.system.config.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.system.config.SignatureSmsFieldConfigs;
import org.dromara.system.config.TemplateMode;

/**
 * 阿里云短信
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AlibabaSmsFieldConfigs extends SignatureSmsFieldConfigs {
    /** 请求地址 */
    private FieldConfig<String> requestUrl;
    /** 接口名称 */
    private FieldConfig<String> action;
    /** 地域信息默认为 cn-hangzhou */
    private FieldConfig<String> regionId;
    /** 接口版本号 */
    private FieldConfig<String> version;

    public AlibabaSmsFieldConfigs() {
        this.accessKeyId = FieldConfig.<String>builder()
            .useInput()
            .label("accessKey")
            .help("如何获取AccessKey，请查询: (https://help.aliyun.com/document_detail/116401.htm) 或<br /> (https://usercenter.console.aliyun.com/#/manage/ak)")
            .required(true)
            .build();
        this.accessKeySecret = FieldConfig.<String>builder()
            .useInput()
            .label("accessKeySecret")
            .help("阿里云的accessKeySecret")
            .required(true)
            .inputComponent().type("password").end()
            .build();
        this.signature = FieldConfig.<String>builder()
            .useInput()
            .label("默认短信签名")
            .help("签名信息可前往 https://dysms.console.aliyun.com/domestic/text/sign 的签名管理查看")
            .required(true)
            .build();
        this.action = FieldConfig.<String>builder()
            .value("SendSms")
            .useInput()
            .label("接口方法")
            .help("接口方法默认为 SendSms 如无特殊改变可以不用设置")
            .required(true)
            .build();
        this.regionId = FieldConfig.<String>builder()
            .value("cn-hangzhou")
            .useInput()
            .label("地域")
            .help("地域信息默认为 cn-hangzhou 如无特殊改变可以不用设置")
            .required(true)
            .build();
        this.requestUrl = FieldConfig.<String>builder()
            .value("dysmsapi.aliyuncs.com")
            .useInput()
            .label("请求地址")
            .help("请求地址默认为 dysmsapi.aliyuncs.com 如无特殊改变可以不用设置")
            .required(true)
            .build();
        this.version = FieldConfig.<String>builder()
            .value("2017-05-25")
            .useInput()
            .label("接口版本号")
            .help("接口版本号默认为 2017-05-25 如无特殊改变可以不用设置")
            .required(true)
            .build();
    }

    /** 获取模板模式 */
    @Override
    public TemplateMode getTemplateMode() {
        return new TemplateMode(true, false);
    }
}
