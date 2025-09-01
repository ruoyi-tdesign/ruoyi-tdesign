package org.dromara.common.core.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.function.Function;

/**
 * 开关组件配置
 *
 * @author hexm
 * @date 2025/5/7
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldSwitchProps {
    /**
     * 用于自定义开关的值，[打开时的值，关闭时的值]。默认为 [true, false]。示例：[1, 0]、['open', 'close']。
     */
    private List<Object> customValue;
    /** 是否禁用组件 */
    private Boolean disabled;
    /** 开关内容，[开启时内容，关闭时内容]。示例：['开', '关'] */
    private List<String> label;
    /** 开关尺寸。可选项：small/medium/large */
    private String size;

    public static class FieldSwitchPropsBuilder<T> {
        private Function<FieldSwitchProps, T> end;

        public FieldSwitchPropsBuilder() {
        }

        public FieldSwitchPropsBuilder(Function<FieldSwitchProps, T> end) {
            this.end = end;
        }

        public T end() {
            return end.apply(this.build());
        }
    }
}
