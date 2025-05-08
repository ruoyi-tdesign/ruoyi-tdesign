package org.dromara.common.core.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 字段选项
 *
 * @author hexm
 * @date 2025/5/3
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldOption<T> {
    /** 标签 */
    private String label;
    /** 值 */
    private T value;
}
