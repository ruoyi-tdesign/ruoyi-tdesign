package org.dromara.common.core.utils.ip;

import cn.hutool.http.HtmlUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.NetUtils;
import org.dromara.common.core.utils.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取地址类
 *
 * @author Lion Li
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressUtils {

    /** 未知地址 */
    public static final String UNKNOWN_IP = "XX XX";

    /** 内网IP */
    public static final String LOCAL_ADDRESS = "内网IP";

    /** 未知地址 */
    public static final String UNKNOWN_ADDRESS = "未知";

    /**
     * 判断一个 IPv6 字符串是否为内网地址
     *
     * @param ipv6AddressString 要检查的 IPv6 地址字符串
     * @return 如果是内网地址，返回 true；否则返回 false
     * @author Peerless-hero
     */
    public static boolean isIPv6InnerIP(String ipv6AddressString) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipv6AddressString);
            byte[] addressBytes = inetAddress.getAddress();
            // 检查是否为 ULA 前缀 (fc00::/7 或 fd00::/8)
            // ULA 地址的前缀是 'fc' 或 'fd' (十六进制)
            // 在 byte 数组中，第一个字节的高 8 位决定了前缀
            // 获取第一个字节 (索引 0)
            int firstByte = addressBytes[0] & 0xFF; // 使用 & 0xFF 将 byte 转换为无符号 int
            // 检查是否以 0xFC 或 0xFD 开头（对应 fc00::/7 和 fd00::/8 前缀）
            return firstByte == 0xFC || firstByte == 0xFD;
        } catch (UnknownHostException e) {
            // 地址字符串无效
            return false;
        }
    }

    public static String getRealAddressByIP(String ip) {
        // 处理空串并过滤HTML标签
        ip = HtmlUtil.cleanHtmlTag(StringUtils.blankToDefault(ip, ""));
        // 判断是否为IPv4
        if (NetUtils.isIPv4(ip)) {
            return resolverIPv4Region(ip);
        }
        // 判断是否为IPv6
        if (NetUtils.isIPv6(ip)) {
            return resolverIPv6Region(ip);
        }
        // 如果不是IPv4或IPv6，则返回未知IP
        return UNKNOWN_IP;
    }

    /**
     * 根据IPv4地址查询IP归属行政区域
     * @param ip ipv4地址
     * @return 归属行政区域
     */
    private static String resolverIPv4Region(String ip){
        // 内网不查询
        if (NetUtils.isInnerIP(ip)) {
            return LOCAL_ADDRESS;
        }
        return RegionUtils.getCityInfo(ip);
    }

    /**
     * 根据IPv6地址查询IP归属行政区域
     * @param ip ipv6地址
     * @return 归属行政区域
     */
    private static String resolverIPv6Region(String ip){
        // 内网不查询
        if (NetUtils.isInnerIPv6(ip)) {
            return LOCAL_ADDRESS;
        }
        log.warn("ip2region不支持IPV6地址解析：{}", ip);
        // 不支持IPv6，不再进行没有必要的IP地址信息的解析，直接返回
        // 如有需要，可自行实现IPv6地址信息解析逻辑，并在这里返回
        return UNKNOWN_ADDRESS;
    }
}
