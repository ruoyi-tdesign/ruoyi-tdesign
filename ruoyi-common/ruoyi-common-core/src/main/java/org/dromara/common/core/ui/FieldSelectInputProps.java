package org.dromara.common.core.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.function.Function;

/**
 * 筛选器输入框组件配置
 *
 * @author hexm
 * @date 2025/5/7
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldSelectInputProps {
    /** 是否允许输入 */
    private Boolean allowInput;
    /** 宽度随内容自适应 */
    private Boolean autoWidth;
    /** 自动聚焦 */
    private Boolean autofocus;
    /** 无边框模式 */
    private Boolean borderless;
    /** 是否可清空 */
    private Boolean clearable;
    /** 是否禁用 */
    private Boolean disabled;
    /** 透传 Input 输入框组件全部属性。 */
    private FieldInputProps inputProps;
    /** 输入框的值。非受控属性。 */
    private String defaultInputValue;
    /**
     * 定义字段别名，示例：{ label: 'text', value: 'id', children: 'list' }。
     * TS 类型：SelectInputKeys interface SelectInputKeys { label?: string; value?: string; children?: string }。
     */
    private Object keys;
    /** 左侧文本 */
    private String label;
    /** 最小折叠数量，用于标签数量过多的情况下折叠选中项，超出该数值的选中项折叠。值为 0 则表示不折叠 */
    private Integer minCollapsedNum;
    /** 是否为多选模式，默认为单选 */
    private Boolean multiple;
    /** 下拉框内容，可完全自定义。 */
    private String panel;
    /** 占位符 */
    private String placeholder;
    /** 透传 Popup 浮层组件全部属性。 */
    private FieldPopupProps popupProps;
    /** 是否显示下拉框。非受控属性 */
    private Boolean defaultPopupVisible;
    /** 只读状态，值为真会隐藏输入框，且无法打开下拉框 */
    private Boolean readonly;
    /** 多选且可搜索时，是否在选中一个选项后保留当前的搜索关键词 */
    private Boolean reserveKeyword;
    /** 组件尺寸。可选项：small/medium/large。 */
    private String size;
    /** 输入框状态。可选项：default/success/warning/error */
    private String status;
    /** 后置图标前的后置内容。 */
    private String suffix;
    /** 多选场景下，自定义选中标签的内部内容。注意和 valueDisplay 区分，valueDisplay 是用来定义全部标签内容，而非某一个标签。 */
    private String tag;
    /** 透传 TagInput 组件全部属性。 */
    private FieldTagInputProps tagInputProps;
    /** 透传 Tag 标签组件全部属性。 */
    private FieldTagProps tagProps;
    /** 输入框下方提示文本，会根据不同的 status 呈现不同的样式。 */
    private String tips;

    public static class FieldSelectInputPropsBuilder<T> {
        private Function<FieldSelectInputProps, T> end;

        public FieldSelectInputPropsBuilder() {
        }

        public FieldSelectInputPropsBuilder(Function<FieldSelectInputProps, T> end) {
            this.end = end;
        }

        public T end() {
            return end.apply(this.build());
        }

        /**
         * 输入框组件配置
         */
        public FieldInputProps.FieldInputPropsBuilder<FieldSelectInputPropsBuilder<T>> inputProps() {
            return new FieldInputProps.FieldInputPropsBuilder<>((inputProps) -> {
                this.inputProps = inputProps;
                return this;
            });
        }

        /**
         * 弹出框组件配置
         */
        public FieldPopupProps.FieldPopupPropsBuilder<FieldSelectInputPropsBuilder<T>> popupProps() {
            return new FieldPopupProps.FieldPopupPropsBuilder<>((popupProps) -> {
                this.popupProps = popupProps;
                return this;
            });
        }

        /**
         * 标签输入框组件配置
         */
        public FieldTagInputProps.FieldTagInputPropsBuilder<FieldSelectInputPropsBuilder<T>> tagInputProps() {
            return new FieldTagInputProps.FieldTagInputPropsBuilder<>((tagInputProps) -> {
                this.tagInputProps = tagInputProps;
                return this;
            });
        }

        /**
         * 标签组件配置
         */
        public FieldTagProps.FieldTagPropsBuilder<FieldSelectInputPropsBuilder<T>> tagProps() {
            return new FieldTagProps.FieldTagPropsBuilder<>((tagProps) -> {
                this.tagProps = tagProps;
                return this;
            });
        }
    }
}
