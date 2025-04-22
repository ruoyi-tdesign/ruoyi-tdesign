package org.dromara.common.websocket.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.dromara.common.satoken.config.MultipleSaTokenProperties;
import org.dromara.common.websocket.config.properties.MultipleWebSocketProperties;
import org.dromara.common.websocket.config.properties.WebSocketProperties;
import org.dromara.common.websocket.handler.ICustomWebSocketHandler;
import org.dromara.common.websocket.handler.PlusWebSocketHandler;
import org.dromara.common.websocket.interceptor.PlusWebSocketInterceptor;
import org.dromara.common.websocket.listener.WebSocketTopicListener;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * WebSocket 配置
 *
 * @author zendwang
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(value = "websocket.enabled", havingValue = "true")
@EnableConfigurationProperties(MultipleWebSocketProperties.class)
@EnableWebSocket
public class WebSocketConfig {

    @Bean
    public List<WebSocketConfigurer> webSocketConfigurer(HandshakeInterceptor handshakeInterceptor,
                                                         WebSocketHandler webSocketHandler,
                                                         MultipleWebSocketProperties multipleWebSocketProperties,
                                                         MultipleSaTokenProperties multipleSaTokenProperties) {
        Set<String> userTypes = multipleSaTokenProperties.getMultiple().keySet();
        return multipleWebSocketProperties.getMultiple().entrySet().stream()
            .filter(entry -> {
                String key = entry.getKey();
                if (!userTypes.contains(key)) {
                    String msg = "websocket: 登录用户类型(%s),不存在".formatted(key);
                    log.error(msg, new RuntimeException(msg));
                    return false;
                }
                WebSocketProperties properties = entry.getValue();
                if (CollUtil.isEmpty(properties.getPaths())) {
                    String msg = "websocket: 登录用户类型(%s),匹配路径不存在".formatted(key);
                    log.error(msg, new RuntimeException(msg));
                    return false;
                }
                return true;
            })
            .<WebSocketConfigurer>map(entry -> {
                WebSocketProperties properties = entry.getValue();
                // 如果允许跨域访问的地址为空，则设置为 "*"，表示允许所有来源的跨域请求
                if (StrUtil.isBlank(properties.getAllowedOrigins())) {
                    properties.setAllowedOrigins("*");
                }
                WebSocketHandler handler = webSocketHandler;
                if (properties.getHandler() != null) {
                    try {
                        ICustomWebSocketHandler customWebSocketHandler;
                        customWebSocketHandler = SpringUtils.getBeanOptional(properties.getHandler()).orElse(null);
                        if (customWebSocketHandler == null) {
                            customWebSocketHandler = properties.getHandler().getDeclaredConstructor().newInstance();
                        }
                        handler = new PlusWebSocketHandler(customWebSocketHandler);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
                // 返回一个WebSocketConfigurer对象，用于配置WebSocket
                WebSocketHandler finalHandler = handler;
                return registry -> registry
                    // 添加WebSocket处理程序和拦截器到指定路径，设置允许的跨域来源
                    .addHandler(finalHandler, properties.getPaths().toArray(String[]::new))
                    .addInterceptors(handshakeInterceptor)
                    .setAllowedOrigins(properties.getAllowedOrigins());
            }).collect(Collectors.toList());
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor(MultipleWebSocketProperties multipleWebSocketProperties) {
        return new PlusWebSocketInterceptor(multipleWebSocketProperties);
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new PlusWebSocketHandler();
    }

    @Bean
    public WebSocketTopicListener topicListener() {
        return new WebSocketTopicListener();
    }
}
