package org.dromara.common.core.events;

import lombok.Data;

import java.io.Serializable;

/**
 * 新增通知事件
 *
 * @author hexm
 * @date 2023/11/20 16:00
 */
@Data
public class NoticeInsertEvent implements Serializable {

    /**
     * 通知类型
     */
    private String type;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知用户类型
     */
    private String loginType;
}
