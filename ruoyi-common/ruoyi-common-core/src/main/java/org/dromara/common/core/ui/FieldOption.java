package org.dromara.common.core.ui;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 字段选项
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
@AllArgsConstructor
public class FieldOption<T> {
    /** 标签 */
    private String label;
    /** 值 */
    private T value;
}
