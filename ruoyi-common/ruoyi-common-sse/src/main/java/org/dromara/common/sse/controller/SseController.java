package org.dromara.common.sse.controller;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.stp.DynamicStpLogic;
import org.dromara.common.satoken.stp.MultipleStpLogic;
import org.dromara.common.satoken.utils.DynamicLoginHelper;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.satoken.utils.MultipleStpUtil;
import org.dromara.common.sse.core.SseEmitterManager;
import org.dromara.common.sse.dto.SseMessageDto;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * SSE 控制器
 *
 * @author Lion Li
 */
@RestController
@ConditionalOnProperty(value = "sse.enabled", havingValue = "true")
@RequiredArgsConstructor
public class SseController implements DisposableBean {

    private final SseEmitterManager sseEmitterManager;

    /**
     * 建立 SSE 连接
     */
    @GetMapping(value = "${sse.path}/{loginType}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@PathVariable String loginType) {
        StpLogic logic = DynamicStpLogic.getDynamicStpLogic(loginType);
        logic.checkLogin();
        String tokenValue = logic.getTokenValue();
        Long userId = DynamicLoginHelper.getUserId(logic);
        return sseEmitterManager.connect(loginType, userId, tokenValue);
    }

    /**
     * 关闭 SSE 连接
     */
    @GetMapping(value = "${sse.path}/{loginType}/close")
    public R<Void> close(@PathVariable String loginType) {
        StpLogic logic = DynamicStpLogic.getDynamicStpLogic(loginType);
        String tokenValue = logic.getTokenValue();
        Long userId = DynamicLoginHelper.getUserId(logic);
        sseEmitterManager.disconnect(loginType, userId, tokenValue);
        return R.ok();
    }

    /**
     * 向特定用户发送消息
     *
     * @param userId 目标用户的 ID
     * @param msg    要发送的消息内容
     */
    @GetMapping(value = "${sse.path}/{loginType}/send")
    public R<Void> send(@PathVariable String loginType, Long userId, String msg) {
        SseMessageDto dto = new SseMessageDto();
        dto.setLoginType(loginType);
        dto.setUserIds(List.of(userId));
        dto.setMessage(msg);
        sseEmitterManager.publishMessage(dto);
        return R.ok();
    }

    /**
     * 向所有用户发送消息
     *
     * @param msg 要发送的消息内容
     */
    @GetMapping(value = "${sse.path}/{loginType}/sendAll")
    public R<Void> send(@PathVariable String loginType, String msg) {
        sseEmitterManager.publishAll(loginType, msg);
        return R.ok();
    }

    /**
     * 清理资源。此方法目前不执行任何操作，但避免因未实现而导致错误
     */
    @Override
    public void destroy() throws Exception {
        // 销毁时不需要做什么 此方法避免无用操作报错
    }

}
