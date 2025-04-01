package org.dromara.common.sse.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.dromara.common.sse.core.SseEmitterManager;
import org.dromara.common.sse.dto.SseMessageDto;

/**
 * SSE工具类
 *
 * @author Lion Li
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SseMessageUtils {

    private final static Boolean SSE_ENABLE = SpringUtils.getProperty("sse.enabled", Boolean.class, true);
    private static SseEmitterManager MANAGER;

    static {
        if (isEnable() && MANAGER == null) {
            MANAGER = SpringUtils.getBean(SseEmitterManager.class);
        }
    }

    /**
     * 向指定的SSE会话发送消息
     *
     * @param loginType 要发送消息的用户的登录类型
     * @param userId    要发送消息的用户id
     * @param message   要发送的消息内容
     */
    public static void sendMessage(String loginType, Long userId, String message) {
        if (!isEnable()) {
            return;
        }
        MANAGER.sendMessage(loginType, userId, message);
    }

    /**
     * 本机全用户会话发送消息
     *
     * @param loginType 要发送消息的用户的登录类型
     * @param message   要发送的消息内容
     */
    public static void sendMessage(String loginType, String message) {
        if (!isEnable()) {
            return;
        }
        MANAGER.sendMessage(loginType, message);
    }

    /**
     * 发布SSE订阅消息
     *
     * @param sseMessageDto 要发布的SSE消息对象
     */
    public static void publishMessage(SseMessageDto sseMessageDto) {
        if (!isEnable()) {
            return;
        }
        MANAGER.publishMessage(sseMessageDto);
    }

    /**
     * 向所有的用户发布订阅的消息(群发)
     *
     * @param loginType 要发送消息的用户的登录类型
     * @param message   要发布的消息内容
     */
    public static void publishAll(String loginType, String message) {
        if (!isEnable()) {
            return;
        }
        MANAGER.publishAll(loginType, message);
    }

    /**
     * 是否开启
     */
    public static Boolean isEnable() {
        return SSE_ENABLE;
    }

}
