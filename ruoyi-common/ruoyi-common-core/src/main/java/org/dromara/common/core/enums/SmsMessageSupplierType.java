package org.dromara.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 消息支持类型
 *
 * @author hexm
 * @date 2023/06/29 16:56
 */
@Getter
@AllArgsConstructor
public enum SmsMessageSupplierType {
    /** 阿里云短信 */
    ALIBABA("阿里云短信"),
    /** 华为云短信 */
    HUAWEI("华为云短信"),
    /** 腾讯云短信 */
    TENCENT("腾讯云短信"),
    /** 云片短信 */
    YUNPIAN("云片短信"),
    /** 合一短信 */
    UNI_SMS("合一短信"),
    /** 京东云短信 */
    JD_CLOUD("京东云短信"),
    /** 容联云短信 */
    CLOOPEN("容联云短信"),
    /** 亿美软通短信 */
    EMAY("亿美软通短信"),
    /** 天翼云短信 */
    CTYUN("天翼云短信"),
    /** 网易云短信 */
    NETEASE("网易云短信"),
    /** 助通短信 */
    ZHUTONG("助通短信"),
    /** 鼎众短信 */
    DING_ZHONG("鼎众短信"),
    /** 联麓短信 */
    LIAN_LU("联麓短信"),
    /** 七牛云短信 */
    QI_NIU("七牛云短信");

    private final String description;

    public static Map<String, String> getMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (SmsMessageSupplierType value : values()) {
            map.put(value.name(), value.getDescription());
        }
        return map;
    }
}
