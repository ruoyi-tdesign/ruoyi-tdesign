package org.dromara.common.core.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 字段选项组
 *
 * @author hexm
 * @date 2025/5/10
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldOptionGroup<T> implements FieldOptionProps<T> {
    /** 是否显示分隔线，默认为true */
    private Boolean divider;
    /** 分组别名 */
    private String label;
    /** 分组名称 */
    private String group;
    /** 子选项 */
    private List<FieldOption<T>> children;

    public FieldOptionGroup() {
    }

    public FieldOptionGroup(String group, List<FieldOption<T>> children) {
        this.group = group;
        this.children = children;
    }
}
