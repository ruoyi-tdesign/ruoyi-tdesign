package org.dromara.workflow.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 按钮权限枚举
 *
 * @author AprilWind
 */
@Getter
@AllArgsConstructor
public enum ButtonPermissionEnum implements NodeExtEnum {

    /**
     * 是否弹窗选人
     */
    APPROVE("是否弹窗选人", "1", false),

    /**
     * 是否能委托
     */
    REJECT("是否能委托", "2", false);

    private final String label;
    private final String value;
    private final boolean selected;

}

