package org.dromara.common.websocket.constant;

import org.dromara.common.core.constant.GlobalConstants;

/**
 * websocket的常量配置
 *
 * @author zendwang
 */
public interface WebSocketConstants {

    /**
     * websocketSession中的参数的key
     */
    String LOGIN_USER_KEY = "loginUser";

    /**
     * 订阅的频道
     */
    String WEB_SOCKET_TOPIC = GlobalConstants.GLOBAL_REDIS_KEY + "websocket";

    /**
     * 前端心跳检查的命令
     */
    String PING = "ping";

    /**
     * 服务端心跳恢复的字符串
     */
    String PONG = "pong";
}
