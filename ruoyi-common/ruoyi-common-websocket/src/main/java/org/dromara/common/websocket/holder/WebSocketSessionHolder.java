package org.dromara.common.websocket.holder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocketSession 用于保存当前所有在线的会话信息
 *
 * @author zendwang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebSocketSessionHolder {

    private static final Map<String, Map<Long, Set<WebSocketSession>>> USER_SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 将WebSocket会话添加到用户会话Map中
     *
     * @param loginType  账号类型
     * @param sessionKey 会话键，用于检索会话
     * @param session    要添加的WebSocket会话
     */
    public static void addSession(String loginType, Long sessionKey, WebSocketSession session) {
        if (session == null) {
            return;
        }
        Map<Long, Set<WebSocketSession>> map = USER_SESSION_MAP.computeIfAbsent(loginType, s -> new ConcurrentHashMap<>());

        Set<WebSocketSession> sessions = map.computeIfAbsent(sessionKey, aLong -> new LinkedHashSet<>());
        sessions.add(session);
    }

    /**
     * 从用户会话Map中移除指定会话键对应的WebSocket会话
     *
     * @param loginType  账号类型
     * @param sessionKey 要移除的会话键
     * @param session    要移除的WebSocket会话
     */
    public static void removeSession(String loginType, Long sessionKey, WebSocketSession session) {
        Map<Long, Set<WebSocketSession>> map = USER_SESSION_MAP.get(loginType);
        if (map == null) {
            return;
        }
        Set<WebSocketSession> sessions = map.get(sessionKey);
        if (sessions == null) {
            return;
        }
        sessions.remove(session);
        if (session != null) {
            try {
                session.close(CloseStatus.BAD_DATA);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 从用户会话Map中移除指定会话键对应的WebSocket会话
     *
     * @param loginType  账号类型
     * @param sessionKey 要移除的会话键
     * @param sessionId  要移除的WebSocket会话Id
     */
    public static void removeSession(String loginType, Long sessionKey, String sessionId) {
        Map<Long, Set<WebSocketSession>> map = USER_SESSION_MAP.get(loginType);
        if (map == null) {
            return;
        }
        Set<WebSocketSession> sessions = map.get(sessionKey);
        if (sessions == null) {
            return;
        }
        sessions.removeIf(webSocketSession -> {
            if (webSocketSession.getId().equals(sessionId)) {
                try {
                    webSocketSession.close(CloseStatus.BAD_DATA);
                } catch (Exception ignored) {
                }
                return true;
            }
            return false;
        });
    }

    /**
     * 从用户会话Map中移除指定会话键对应的WebSocket会话
     *
     * @param loginType  账号类型
     * @param sessionKey 要移除的会话键
     */
    public static void removeSession(String loginType, Long sessionKey) {
        Map<Long, Set<WebSocketSession>> map = USER_SESSION_MAP.get(loginType);
        if (map != null) {
            Set<WebSocketSession> sessions = map.remove(sessionKey);
            for (WebSocketSession session : sessions) {
                try {
                    session.close(CloseStatus.BAD_DATA);
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 根据会话键从用户会话Map中获取WebSocket会话
     *
     * @param loginType  账号类型
     * @param sessionKey 要获取的会话键
     * @return 与给定会话键对应的WebSocket会话，如果不存在则返回null
     */
    public static Set<WebSocketSession> getSessions(String loginType, Long sessionKey) {
        Map<Long, Set<WebSocketSession>> map = USER_SESSION_MAP.get(loginType);
        if (map != null) {
            return map.get(sessionKey);
        }
        return null;
    }

    /**
     * 获取存储在用户会话Map中所有WebSocket会话的会话键集合
     *
     * @param loginType 账号类型
     * @return 所有WebSocket会话的会话键集合
     */
    public static Set<Long> getSessionsAll(String loginType) {
        return Optional.ofNullable(USER_SESSION_MAP.get(loginType)).map(Map::keySet).orElse(null);
    }

    /**
     * 检查给定的会话键是否存在于用户会话Map中
     *
     * @param loginType  账号类型
     * @param sessionKey 要检查的会话键
     * @return 如果存在对应的会话键，则返回true；否则返回false
     */
    public static Boolean existSession(String loginType, Long sessionKey) {
        return Optional.ofNullable(USER_SESSION_MAP.get(loginType)).map(map -> map.containsKey(sessionKey)).orElse(false);
    }
}
