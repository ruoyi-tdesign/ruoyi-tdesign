package org.dromara.common.core.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.function.Function;

/**
 * 多行文本框组件配置
 *
 * @author hexm
 * @date 2025/5/7
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldTextareaProps {
    /** 超出maxlength或maxcharacter之后是否还允许输入 */
    @Builder.Default
    private Boolean allowInputOverMax = false;
    /** 自动聚焦，拉起键盘 */
    @Builder.Default
    private Boolean autofocus = false;
    /**
     * 高度自动撑开。 autosize = true 表示组件高度自动撑开，同时，依旧允许手动拖高度。
     * 如果设置了 autosize.maxRows 或者 autosize.minRows 则不允许手动调整高度。
     * TS 类型：boolean | { minRows?: number; maxRows?: number }
     * Java类型: Boolean | Map
     */
    @Builder.Default
    private Object autosize = false;
    /** 是否禁用文本框 */
    private Boolean disabled;
    /** 用户最多可以输入的字符个数，一个中文汉字表示两个字符长度 */
    private Long maxcharacter;
    /** 用户最多可以输入的字符个数。 */
    private Long maxlength;
    /** 名称，HTML 元素原生属性 */
    private String name;
    /** 占位符 */
    private String placeholder;
    /** 只读状态 */
    private Boolean readonly;
    /** 文本框状态。可选项：default/success/warning/error */
    @Builder.Default
    private String status = "default";
    /** 输入框下方提示文本，会根据不同的 status 呈现不同的样式。 */
    private String tips;

    public static class FieldTextareaPropsBuilder<T> {
        private Function<FieldTextareaProps, T> end;

        public FieldTextareaPropsBuilder() {
        }

        public FieldTextareaPropsBuilder(Function<FieldTextareaProps, T> end) {
            this.end = end;
        }

        public T end() {
            return end.apply(this.build());
        }
    }
}
