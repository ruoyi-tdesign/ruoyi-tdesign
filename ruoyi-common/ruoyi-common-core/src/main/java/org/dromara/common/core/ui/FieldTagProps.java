package org.dromara.common.core.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.function.Function;

/**
 * 标签组件配置
 *
 * @author hexm
 * @date 2025/5/7
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldTagProps {
    /** 标签是否可关闭 */
    private Boolean closable;
    /** 自定义标签颜色 */
    private String color;
    /** 组件子元素 */
    private String content;
    /** 标签禁用态，失效标签不能触发事件。默认风格（theme=default）才有禁用态 */
    private Boolean disabled;
    /** 标签最大宽度，宽度超出后会出现省略号。示例：'50px' / 80 */
    private String maxWidth;
    /** 标签类型，有三种：方形、圆角方形、标记型。可选项：square/round/mark */
    private String shape;
    /** 标签尺寸。可选项：small/medium/large */
    private String size;
    /** 组件风格，用于描述组件不同的应用场景。可选项：default/primary/warning/danger/success */
    private String theme;
    /** 标签标题，在标签hover时展示，默认为标签内容 */
    private String title;
    /** 标签风格变体。可选项：dark/light/outline/light-outline */
    private String variant;

    public static class FieldTagPropsBuilder<T> {
        private Function<FieldTagProps, T> end;

        public FieldTagPropsBuilder() {
        }

        public FieldTagPropsBuilder(Function<FieldTagProps, T> end) {
            this.end = end;
        }

        public T end() {
            return end.apply(this.build());
        }
    }
}
