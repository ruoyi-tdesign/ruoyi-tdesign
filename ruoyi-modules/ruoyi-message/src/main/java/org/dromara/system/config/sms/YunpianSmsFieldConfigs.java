package org.dromara.system.config.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.enums.MessageTypeEnum;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.system.config.SupplierFieldConfigs;
import org.dromara.system.config.TemplateMode;

/**
 * 云片短信
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class YunpianSmsFieldConfigs extends SupplierFieldConfigs {
    /** Access Key */
    private FieldConfig<String> accessKeyId;
    /** 短信签名 */
    private FieldConfig<String> signature;
    /** 短信发送后将向这个地址推送(运营商返回的)发送报告 */
    private FieldConfig<String> callbackUrl;

    public YunpianSmsFieldConfigs() {
        this.accessKeyId = FieldConfig.<String>builder()
            .useInput()
            .label("apikey")
            .help("账号唯一标识")
            .required(true)
            .inputComponent().type("password").end()
            .build();
        this.signature = FieldConfig.<String>builder()
            .useInput()
            .label("默认短信签名")
            .required(false)
            .build();
        this.callbackUrl = FieldConfig.<String>builder()
            .useInput()
            .label("回调地址")
            .help("云片官方回调地址，无需求可以不设置")
            .required(false)
            .build();
    }

    /** 获取消息类型 */
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.SMS;
    }

    /** 获取模板模式 */
    @Override
    public TemplateMode getTemplateMode() {
        return new TemplateMode(true, false);
    }
}
