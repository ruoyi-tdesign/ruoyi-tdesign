package org.dromara.common.websocket.interceptor;

import cn.dev33.satoken.stp.StpLogic;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.model.BaseUser;
import org.dromara.common.satoken.stp.DynamicStpLogic;
import org.dromara.common.satoken.utils.MultipleLoginBaseHelper;
import org.dromara.common.websocket.config.properties.MultipleWebSocketProperties;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static org.dromara.common.websocket.constant.WebSocketConstants.LOGIN_USER_KEY;

/**
 * WebSocket握手请求的拦截器
 *
 * @author zendwang
 */
@Slf4j
public class PlusWebSocketInterceptor implements HandshakeInterceptor {

    private final MultipleWebSocketProperties multipleWebSocketProperties;

    public PlusWebSocketInterceptor(MultipleWebSocketProperties multipleWebSocketProperties) {
        this.multipleWebSocketProperties = multipleWebSocketProperties;
    }

    /**
     * WebSocket握手之前执行的前置处理方法
     *
     * @param request    WebSocket握手请求
     * @param response   WebSocket握手响应
     * @param wsHandler  WebSocket处理程序
     * @param attributes 与WebSocket会话关联的属性
     * @return 如果允许握手继续进行，则返回true；否则返回false
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // 检查是否登录 是否有token
        String loginType = multipleWebSocketProperties.getMatchLoginType(request.getURI().getPath());
        if (loginType != null) {
            StpLogic logic = DynamicStpLogic.getDynamicStpLogic(loginType);
            BaseUser user = MultipleLoginBaseHelper.getUser(logic);
            attributes.put(LOGIN_USER_KEY, user);
            return true;
        }
        return false;
    }

    /**
     * WebSocket握手成功后执行的后置处理方法
     *
     * @param request   WebSocket握手请求
     * @param response  WebSocket握手响应
     * @param wsHandler WebSocket处理程序
     * @param exception 握手过程中可能出现的异常
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // 在这个方法中可以执行一些握手成功后的后续处理逻辑，比如记录日志或者其他操作
    }

}
