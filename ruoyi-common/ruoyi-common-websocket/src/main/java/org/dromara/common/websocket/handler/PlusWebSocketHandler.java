package org.dromara.common.websocket.handler;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.model.BaseUser;
import org.dromara.common.websocket.dto.WebSocketMessageDto;
import org.dromara.common.websocket.holder.WebSocketSessionHolder;
import org.dromara.common.websocket.utils.WebSocketUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.List;

import static org.dromara.common.websocket.constant.WebSocketConstants.LOGIN_USER_KEY;

/**
 * WebSocketHandler 实现类
 *
 * @author zendwang
 */
@Slf4j
public class PlusWebSocketHandler extends AbstractWebSocketHandler {

    private final ICustomWebSocketHandler customWebSocketHandler;

    public PlusWebSocketHandler() {
        this(null);
    }

    public PlusWebSocketHandler(ICustomWebSocketHandler customWebSocketHandler) {
        this.customWebSocketHandler = customWebSocketHandler;
    }

    /**
     * 连接成功后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        BaseUser user = (BaseUser) session.getAttributes().get(LOGIN_USER_KEY);
        if (ObjectUtil.isNull(user)) {
            session.close(CloseStatus.BAD_DATA);
            log.info("[connect] invalid token received. sessionId: {}", session.getId());
            return;
        }
        WebSocketSessionHolder.addSession(user.getLoginType(), user.getUserId(), session);
        log.info("[connect] sessionId: {},userId:{},loginType:{},deviceType:{}", session.getId(), user.getUserId(), user.getLoginType(), user.getDeviceType());
        if (customWebSocketHandler != null) {
            customWebSocketHandler.afterConnectionEstablished(session, user);
        }
    }

    /**
     * 处理接收到的文本消息
     *
     * @param session WebSocket会话
     * @param message 接收到的文本消息
     * @throws Exception 处理消息过程中可能抛出的异常
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 从WebSocket会话中获取登录用户信息
        BaseUser user = (BaseUser) session.getAttributes().get(LOGIN_USER_KEY);
        // 创建WebSocket消息DTO对象
        WebSocketMessageDto webSocketMessageDto = new WebSocketMessageDto();
        webSocketMessageDto.setLoginType(user.getLoginType());
        webSocketMessageDto.setSessionKeys(List.of(user.getUserId()));
        webSocketMessageDto.setMessage(message.getPayload());
        WebSocketUtils.publishMessage(webSocketMessageDto);

        if (customWebSocketHandler != null) {
            customWebSocketHandler.handleTextMessage(session, message, user);
        }
    }

    /**
     * 处理接收到的二进制消息
     *
     * @param session WebSocket会话
     * @param message 接收到的二进制消息
     * @throws Exception 处理消息过程中可能抛出的异常
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);

        if (customWebSocketHandler != null) {
            BaseUser user = (BaseUser) session.getAttributes().get(LOGIN_USER_KEY);
            customWebSocketHandler.handleBinaryMessage(session, message, user);
        }
    }

    /**
     * 处理接收到的Pong消息（心跳监测）
     *
     * @param session WebSocket会话
     * @param message 接收到的Pong消息
     * @throws Exception 处理消息过程中可能抛出的异常
     */
    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) {
        WebSocketUtils.sendPongMessage(session);
    }

    /**
     * 处理WebSocket传输错误
     *
     * @param session   WebSocket会话
     * @param exception 发生的异常
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[transport error] sessionId: {} , exception:{}", session.getId(), exception.getMessage());

        if (customWebSocketHandler != null) {
            customWebSocketHandler.handleTransportError(session, exception);
        }
    }

    /**
     * 在WebSocket连接关闭后执行清理操作
     *
     * @param session WebSocket会话
     * @param status  关闭状态信息
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        BaseUser user = (BaseUser) session.getAttributes().get(LOGIN_USER_KEY);
        if (ObjectUtil.isNull(user)) {
            log.info("[disconnect] invalid token received. sessionId: {}", session.getId());
            return;
        } else {
            WebSocketSessionHolder.removeSession(user.getLoginType(), user.getUserId(), session);
            log.info("[disconnect] sessionId: {},userId:{},loginType:{},deviceType:{}",
                session.getId(), user.getUserId(), user.getLoginType(), user.getDeviceType());
        }

        if (customWebSocketHandler != null) {
            customWebSocketHandler.afterConnectionClosed(session, status, user);
        }
    }

    /**
     * 指示处理程序是否支持接收部分消息
     *
     * @return 如果支持接收部分消息，则返回true；否则返回false
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
