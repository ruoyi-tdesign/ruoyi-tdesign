package org.dromara.system.config;

import lombok.Data;
import org.dromara.system.config.mail.MailFieldConfigs;
import org.dromara.system.config.sms.*;
import org.dromara.common.core.enums.MailMessageSupplierType;
import org.dromara.common.core.enums.SmsMessageSupplierType;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息数据
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
public class MessageData {

    public static final Map<String, MessageFieldConfig> SUPPLIER_TYPE_MAP;

    static {
        SUPPLIER_TYPE_MAP = new HashMap<>();
        SUPPLIER_TYPE_MAP.put(MailMessageSupplierType.MAIL.name(), new MailFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.ALIBABA.name(), new AlibabaSmsFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.TENCENT.name(), new TencentSmsFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.HUAWEI.name(), new HuaweiSmsFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.YUNPIAN.name(), new YunpianSmsFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.UNI_SMS.name(), new UniSmsFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.JD_CLOUD.name(), new JdCloudSmsFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.CLOOPEN.name(), new CloopenSmsFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.EMAY.name(), new EmaySmsFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.CTYUN.name(), new CtyunSmsFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.NETEASE.name(), new NeteaseSmsFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.ZHUTONG.name(), new ZhutongSmsFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.DING_ZHONG.name(), new DingZhongSmsFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.LIAN_LU.name(), new LianLuSmsFieldConfigs().getMessageConfig());
        SUPPLIER_TYPE_MAP.put(SmsMessageSupplierType.QI_NIU.name(), new QiNiuSmsFieldConfigs().getMessageConfig());
    }
}
