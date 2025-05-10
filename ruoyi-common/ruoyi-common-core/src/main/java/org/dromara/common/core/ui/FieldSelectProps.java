package org.dromara.common.core.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.function.Function;

/**
 * 选择器组件配置
 *
 * @author hexm
 * @date 2025/5/7
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldSelectProps {
    /** 宽度随内容自适应 */
    private Boolean autoWidth;
    /** 自动聚焦 */
    private Boolean autofocus;
    /** 无边框模式 */
    private Boolean borderless;
    /** 是否可以清空选项 */
    @Builder.Default
    private Boolean clearable = true;
    /** 是否允许用户创建新条目，需配合 filterable 使用 */
    private Boolean creatable;
    /** 是否禁用组件 */
    private Boolean disabled;
    /** 当下拉列表为空时显示的内容。 */
    private String empty;
    /** 是否可搜索，默认搜索规则不区分大小写，全文本任意位置匹配。 */
    @Builder.Default
    private Boolean filterable = true;
    /** 透传 Input 输入框组件的全部属性。 */
    private FieldInputProps inputProps;
    /** 输入框的值。非受控属性。 */
    private String defaultInputValue;
    /** 用来定义 value / label / disabled 在 options 中对应的字段别名。 */
    private Object keys;
    /** 左侧文本 */
    private String label;
    /** 远程加载时显示的文字 */
    private String loadingText;
    /** 用于控制多选数量，值为 0 则不限制 */
    private Integer max;
    /** 最小折叠数量，用于多选情况下折叠选中项，超出该数值的选中项折叠。值为 0 则表示不折叠 */
    private Integer minCollapsedNum;
    /** 是否允许多选 */
    private Boolean multiple;
    /** 数据化配置选项内容。 */
    private List<FieldOptionProps<Object>> options;
    /** 面板内的底部内容 */
    private String panelBottomContent;
    /** 面板内的顶部内容 */
    private String panelTopContent;
    /** 占位符 */
    private String placeholder;
    /** 透传给 popup 组件的全部属性 */
    private FieldPopupProps popupProps;
    /** 是否显示下拉框。非受控属性 */
    private Boolean defaultPopupVisible;
    /** 只读状态，值为真会隐藏输入框，且无法打开下拉框 */
    private Boolean readonly;
    /** 多选且可搜索时，是否在选中一个选项后保留当前的搜索关键词 */
    private Boolean reserveKeyword;
    /** 懒加载和虚拟滚动。为保证组件收益最大化，当数据量小于阈值 scroll.threshold 时，无论虚拟滚动的配置是否存在，组件内部都不会开启虚拟滚动，scroll.threshold 默认为 100。 */
    private FieldScroll scroll;
    /** 透传 SelectInput 筛选器输入框组件的全部属性。 */
    private FieldSelectInputProps selectInputProps;
    /** 是否显示右侧箭头，默认显示 */
    private Boolean showArrow;
    /** 组件尺寸。可选项：small/medium/large */
    private String size;
    /** 输入框状态。可选项：default/success/warning/error */
    private String status;
    /** 后置图标前的后置内容 */
    private String suffix;
    /** 透传 TagInput 标签输入框组件的全部属性 */
    private FieldTagInputProps tagInputProps;
    /** 透传 Tag 标签组件全部属性 */
    private FieldTagProps tagProps;
    /** 输入框下方提示文本，会根据不同的 status 呈现不同的样式 */
    private String tips;

    public static class FieldSelectPropsBuilder<T> {
        private Function<FieldSelectProps, T> end;

        public FieldSelectPropsBuilder() {
        }

        public FieldSelectPropsBuilder(Function<FieldSelectProps, T> end) {
            this.end = end;
        }

        /**
         * 构建结束，并返回构建函数传入的回调
         */
        public T end() {
            return end.apply(this.build());
        }

        /**
         * 输入框组件配置
         */
        public FieldInputProps.FieldInputPropsBuilder<FieldSelectPropsBuilder<T>> inputProps() {
            return new FieldInputProps.FieldInputPropsBuilder<>((inputProps) -> {
                this.inputProps = inputProps;
                return this;
            });
        }

        /**
         * 弹出框组件配置
         */
        public FieldPopupProps.FieldPopupPropsBuilder<FieldSelectPropsBuilder<T>> popupProps() {
            return new FieldPopupProps.FieldPopupPropsBuilder<>((popupProps) -> {
                this.popupProps = popupProps;
                return this;
            });
        }

        /**
         * 筛选器输入框组件配置
         */
        public FieldSelectInputProps.FieldSelectInputPropsBuilder<FieldSelectPropsBuilder<T>> selectInputProps() {
            return new FieldSelectInputProps.FieldSelectInputPropsBuilder<>((selectInputProps) -> {
                this.selectInputProps = selectInputProps;
                return this;
            });
        }

        /**
         * 标签输入框组件配置
         */
        public FieldTagInputProps.FieldTagInputPropsBuilder<FieldSelectPropsBuilder<T>> tagInputProps() {
            return new FieldTagInputProps.FieldTagInputPropsBuilder<>((tagInputProps) -> {
                this.tagInputProps = tagInputProps;
                return this;
            });
        }

        /**
         * 标签组件配置
         */
        public FieldTagProps.FieldTagPropsBuilder<FieldSelectPropsBuilder<T>> tagProps() {
            return new FieldTagProps.FieldTagPropsBuilder<>((tagProps) -> {
                this.tagProps = tagProps;
                return this;
            });
        }
    }
}
