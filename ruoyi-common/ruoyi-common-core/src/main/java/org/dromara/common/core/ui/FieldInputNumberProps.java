package org.dromara.common.core.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.function.Function;

/**
 * 输入框组件配置
 *
 * @author hexm
 * @date 2025/5/7
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldInputNumberProps {
    /** 文本内容位置，居左/居中/居右。可选项：left/center/right */
    private String align;
    /** 是否允许输入超过 max min 范围外的数字。为保障用户体验，仅在失去焦点时进行数字范围矫正。默认允许超出，数字超出范围时，输入框变红提醒 */
    @Builder.Default
    private Boolean allowInputOverLimit = false;
    /** 宽度随内容自适应 */
    @Builder.Default
    private Boolean autoWidth = true;
    /** 小数位数。 */
    @Builder.Default
    private Integer decimalPlaces = 0;
    /** 禁用组件 */
    private Boolean disabled;
    /** 透传 Input 输入框组件全部属性 */
    private FieldInputProps inputProps;
    /** 左侧文本 */
    private String label;
    /** 是否作为大数使用。JS 支持的最大数字位数是 16 位，超过 16 位的数字需作为字符串大数处理。此时，数据类型必须保持为字符串，否则会丢失数据 */
    private Boolean largeNumber;
    /** 最大值。如果是大数，请传入字符串 */
    private Long max;
    /** 最小值。如果是大数，请传入字符串 */
    private Long min;
    /** 占位符 */
    private String placeholder;
    /** 只读状态 */
    private Boolean readonly;
    /** 组件尺寸 */
    private String size;
    /** 文本框状态 */
    private String status;
    /** 数值改变步数，可以是小数。如果是大数，请保证数据类型为字符串 */
    private Double step;
    /** 后置内容 */
    private String suffix;
    /** 按钮布局。可选项：column/row/normal */
    private String theme;
    /** 输入框下方提示文本，会根据不同的 status 呈现不同的样式 */
    private String tips;

    public static class FieldInputNumberPropsBuilder<T> {
        private Function<FieldInputNumberProps, T> end;

        public FieldInputNumberPropsBuilder() {
        }

        public FieldInputNumberPropsBuilder(Function<FieldInputNumberProps, T> end) {
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
        public FieldInputProps.FieldInputPropsBuilder<FieldInputNumberPropsBuilder<T>> inputProps() {
            return new FieldInputProps.FieldInputPropsBuilder<>((inputProps) -> {
                this.inputProps = inputProps;
                return this;
            });
        }
    }
}
