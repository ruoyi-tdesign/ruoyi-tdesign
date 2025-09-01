package org.dromara.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件存储请求模式
 *
 * @author hexm
 * @date 2025/8/6
 */
@Getter
@AllArgsConstructor
public enum StorageRequestMode {
    /** 代理转发请求 */
    proxy("代理转发请求"),
    /** 源地址重定向请求 */
    direct("源地址重定向请求"),
    /** 预签名重定向请求 */
    direct_signature("预签名重定向请求");

    private final String desc;
}
