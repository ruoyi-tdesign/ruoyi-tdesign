package org.dromara.system.config.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.enums.MessageTypeEnum;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.system.config.SupplierFieldConfigs;
import org.dromara.system.config.TemplateMode;

/**
 * 联麓短信
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LianLuSmsFieldConfigs extends SupplierFieldConfigs {
    /** 企业ID */
    private FieldConfig<String> mchId;
    /** appId */
    private FieldConfig<String> appId;
    /** appKey */
    private FieldConfig<String> appKey;
    /** 短信签名 */
    private FieldConfig<String> signature;

    public LianLuSmsFieldConfigs() {
        this.mchId = FieldConfig.<String>builder()
            .useInput()
            .name("企业ID")
            .help("请登录联麓客户端点击（通知/营销短信）进入概览页面获取")
            .required(true)
            .build();
        this.appId = FieldConfig.<String>builder()
            .useInput()
            .name("appId")
            .help("请登录联麓客户端点击（通知/营销短信）进入概览页面获取")
            .required(true)
            .build();
        this.appKey = FieldConfig.<String>builder()
            .useInput()
            .name("appKey")
            .help("请登录联麓客户端点击（通知/营销短信）进入概览页面获取")
            .required(true)
            .inputComponent().type("password").end()
            .build();
        this.signature = FieldConfig.<String>builder()
            .useInput()
            .name("默认短信签名")
            .help("短信签名以实名认证公司简称或品牌名称命名。请前往联麓客户端点击（通知/营销短信）-短信签名提交审核，审核通过即可使用，未提交审核签名无法使用。例如：【联麓信息】")
            .required(true)
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
