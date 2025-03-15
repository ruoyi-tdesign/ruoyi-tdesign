package org.dromara.common.sse.core;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.sse.dto.SseMessageDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 管理 Server-Sent Events (SSE) 连接
 *
 * @author Lion Li
 */
@Slf4j
public class SseEmitterManager {

    /**
     * 订阅的频道
     */
    private final static String SSE_TOPIC = "global:sse";

    private final static Map<String, Map<Long, Map<String, SseEmitter>>> USER_TOKEN_EMITTERS = new ConcurrentHashMap<>();

    /**
     * 建立与指定用户的 SSE 连接
     *
     * @param loginType 登录类型，多用户体系
     * @param userId    用户的唯一标识符，用于区分不同用户的连接
     * @param token     用户的唯一令牌，用于识别具体的连接
     * @return 返回一个 SseEmitter 实例，客户端可以通过该实例接收 SSE 事件
     */
    public SseEmitter connect(String loginType, Long userId, String token) {
        // 从 USER_TOKEN_EMITTERS 中获取或创建当前用户的 SseEmitter 映射表（ConcurrentHashMap）
        // 每个用户可以有多个 SSE 连接，通过 token 进行区分
        Map<Long, Map<String, SseEmitter>> sseMap = USER_TOKEN_EMITTERS.computeIfAbsent(loginType, k -> new ConcurrentHashMap<>());
        Map<String, SseEmitter> emitters = sseMap.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());

        // 创建一个新的 SseEmitter 实例，超时时间设置为 0 表示无限制
        SseEmitter emitter = new SseEmitter(0L);

        emitters.put(token, emitter);

        // 当 emitter 完成、超时或发生错误时，从映射表中移除对应的 token
        emitter.onCompletion(() -> emitters.remove(token));
        emitter.onTimeout(() -> emitters.remove(token));
        emitter.onError((e) -> emitters.remove(token));

        try {
            // 向客户端发送一条连接成功的事件
            emitter.send(SseEmitter.event().comment("connected"));
        } catch (IOException e) {
            // 如果发送消息失败，则从映射表中移除 emitter
            emitters.remove(token);
        }
        return emitter;
    }

    /**
     * 断开指定用户的 SSE 连接
     *
     * @param userId 用户的唯一标识符，用于区分不同用户的连接
     * @param token  用户的唯一令牌，用于识别具体的连接
     */
    public void disconnect(String loginType, Long userId, String token) {
        Map<Long, Map<String, SseEmitter>> sseMap = USER_TOKEN_EMITTERS.get(loginType);
        if (sseMap == null) {
            return;
        }
        Map<String, SseEmitter> emitters = sseMap.get(userId);
        if (emitters != null) {
            try {
                emitters.get(token).send(SseEmitter.event().comment("disconnected"));
            } catch (Exception ignore) {
            }
            emitters.remove(token);
        }
    }

    /**
     * 订阅SSE消息主题，并提供一个消费者函数来处理接收到的消息
     *
     * @param consumer 处理SSE消息的消费者函数
     */
    public void subscribeMessage(Consumer<SseMessageDto> consumer) {
        RedisUtils.subscribe(SSE_TOPIC, SseMessageDto.class, consumer);
    }

    /**
     * 向指定的用户会话发送消息
     *
     * @param loginType 要发送消息的用户的登录类型
     * @param userId    要发送消息的用户id
     * @param message   要发送的消息内容
     */
    public void sendMessage(String loginType, Long userId, String message) {
        Map<Long, Map<String, SseEmitter>> sseMap = USER_TOKEN_EMITTERS.get(loginType);
        if (sseMap == null) {
            return;
        }
        Map<String, SseEmitter> emitters = sseMap.get(userId);
        if (emitters != null) {
            for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
                SseEmitter emitter = entry.getValue();
                try {
                    emitter.send(SseEmitter.event()
                        .name("message")
                        .data(message));
                } catch (Exception e) {
                    emitters.remove(entry.getKey());
                    emitter.completeWithError(e);
                }
            }
        }
    }

    /**
     * 本机全用户会话发送消息
     *
     * @param loginType 要发送消息的用户的登录类型
     * @param message   要发送的消息内容
     */
    public void sendMessage(String loginType, String message) {
        Map<Long, Map<String, SseEmitter>> sseMap = USER_TOKEN_EMITTERS.get(loginType);
        if (sseMap == null) {
            return;
        }
        for (Long userId : sseMap.keySet()) {
            sendMessage(loginType, userId, message);
        }
    }

    /**
     * 发布SSE订阅消息
     *
     * @param sseMessageDto 要发布的SSE消息对象
     */
    public void publishMessage(SseMessageDto sseMessageDto) {
        if (sseMessageDto == null) {
            return;
        }
        SseMessageDto broadcastMessage = new SseMessageDto();
        broadcastMessage.setMessage(sseMessageDto.getMessage());
        broadcastMessage.setLoginType(sseMessageDto.getLoginType());
        broadcastMessage.setUserIds(sseMessageDto.getUserIds());
        RedisUtils.publish(SSE_TOPIC, broadcastMessage, consumer -> {
            log.info("SSE发送主题订阅消息topic:{} loginType:{} session keys:{} message:{}",
                SSE_TOPIC, sseMessageDto.getLoginType(), sseMessageDto.getUserIds(), sseMessageDto.getMessage());
        });
    }

    /**
     * 向所有的用户发布订阅的消息(群发)
     *
     * @param loginType 要发送消息的用户的登录类型
     * @param message   要发布的消息内容
     */
    public void publishAll(String loginType, String message) {
        SseMessageDto broadcastMessage = new SseMessageDto();
        broadcastMessage.setLoginType(loginType);
        broadcastMessage.setMessage(message);
        RedisUtils.publish(SSE_TOPIC, broadcastMessage, consumer -> {
            log.info("SSE发送主题订阅消息topic:{} message:{}", SSE_TOPIC, message);
        });
    }
}
