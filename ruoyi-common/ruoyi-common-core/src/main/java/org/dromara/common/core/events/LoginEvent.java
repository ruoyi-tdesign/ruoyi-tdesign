package org.dromara.common.core.events;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录事件
 *
 * @author hexm
 * @date 2023/11/20 15:45
 */
@Data
public class LoginEvent implements Serializable {

    /**
     * 登录类型
     */
    private String loginType;

    /**
     * 用户id
     */
    private Long userId;
}
