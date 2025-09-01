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
public class FieldOption<T> implements FieldOptionProps<T> {
    /** 当前选项是否为全选，全选可以在顶部，也可以在底部。点击当前选项会选中禁用态除外的全部选项，即使是分组选择器也会选中全部选项 */
    private Boolean checkAll;
    /** 是否禁用该选项 */
    private Boolean disabled;
    /** 选项标题，在选项过长时hover选项展示 */
    private String title;
    /** 标签 */
    private String label;
    /** 值 */
    private T value;

    public FieldOption() {
    }

    public FieldOption(String label, T value) {
        this.label = label;
        this.value = value;
    }
}
