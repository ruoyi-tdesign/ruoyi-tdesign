package org.dromara.common.sse.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.sse.core.SseEmitterManager;
import org.dromara.common.sse.dto.SseMessageDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resource/sse")
public class SseController {

    private final SseEmitterManager sseEmitterManager;

    @GetMapping(value = "/connect/{loginType}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@PathVariable String loginType) {
        String tokenValue = StpUtil.getTokenValue();
        Long userId = LoginHelper.getUserId();
        return sseEmitterManager.connect(loginType, userId, tokenValue);
    }

    @SaIgnore
    @GetMapping(value = "/{loginType}/close")
    public R<Void> close(@PathVariable String loginType) {
        String tokenValue = StpUtil.getTokenValue();
        Long userId = LoginHelper.getUserId();
        sseEmitterManager.disconnect(loginType, userId, tokenValue);
        return R.ok();
    }

    @GetMapping(value = "/{loginType}/send")
    public R<Void> send(@PathVariable String loginType, Long userId, String msg) {
        SseMessageDto dto = new SseMessageDto();
        dto.setLoginType(loginType);
        dto.setUserIds(List.of(userId));
        dto.setMessage(msg);
        sseEmitterManager.publishMessage(dto);
        return R.ok();
    }

    @GetMapping(value = "/{loginType}/sendAll")
    public R<Void> send(@PathVariable String loginType, String msg) {
        sseEmitterManager.publishAll(loginType, msg);
        return R.ok();
    }

}
