package org.dromara.common.core.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.function.Function;

/**
 * 标签输入框组件配置
 *
 * @author hexm
 * @date 2025/5/7
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldTagInputProps {
    /** 宽度随内容自适应 */
    private Boolean autoWidth;
    /** 无边框模式 */
    private Boolean borderless;
    /** 是否可清空 */
    private Boolean clearable;
    /** 是否禁用标签输入框 */
    private Boolean disabled;
    /** 拖拽调整标签顺序 */
    private Boolean dragSort;
    /** 标签超出时的呈现方式，有两种：横向滚动显示 和 换行显示。可选项：scroll/break-line */
    private String excessTagsDisplayType;
    /** 透传 Input 输入框组件全部属性 */
    private FieldInputProps inputProps;
    /** 输入框的值。非受控属性 */
    private String defaultInputValue;
    /** 左侧文本 */
    private String label;
    /** 最大允许输入的标签数量 */
    private Integer max;
    /** 最小折叠数量，用于标签数量过多的情况下折叠选中项，超出该数值的选中项折叠。值为 0 则表示不折叠 */
    private Integer minCollapsedNum;
    /** 占位符 */
    private String placeholder;
    /** 只读状态，值为真会隐藏标签移除按钮和输入框 */
    private Boolean readonly;
    /** 组件尺寸。可选项：small/medium/large */
    private String size;
    /** 输入框状态。可选项：default/success/warning/error */
    private String status;
    /** 后置图标前的后置内容 */
    private String suffix;
    /** 自定义标签的内部内容，每一个标签的当前值。注意和 valueDisplay 区分，valueDisplay 是用来定义全部标签内容，而非某一个标签。 */
    private String tag;
    /** 透传 Tag 组件全部属性 */
    private FieldTagProps tagProps;
    /** 输入框下方提示文本，会根据不同的 status 呈现不同的样式 */
    private String tips;

    public static class FieldTagInputPropsBuilder<T> {
        private Function<FieldTagInputProps, T> end;

        public FieldTagInputPropsBuilder() {
        }

        public FieldTagInputPropsBuilder(Function<FieldTagInputProps, T> end) {
            this.end = end;
        }

        public T end() {
            return end.apply(this.build());
        }

        /**
         * 输入框组件配置
         */
        public FieldInputProps.FieldInputPropsBuilder<FieldTagInputPropsBuilder<T>> inputProps() {
            return new FieldInputProps.FieldInputPropsBuilder<>((inputProps) -> {
                this.inputProps = inputProps;
                return this;
            });
        }

        /**
         * 标签组件配置
         */
        public FieldTagProps.FieldTagPropsBuilder<FieldTagInputPropsBuilder<T>> tagProps() {
            return new FieldTagProps.FieldTagPropsBuilder<>((tagProps) -> {
                this.tagProps = tagProps;
                return this;
            });
        }
    }
}
