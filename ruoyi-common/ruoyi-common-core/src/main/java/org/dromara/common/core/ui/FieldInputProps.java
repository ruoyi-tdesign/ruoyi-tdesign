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
public class FieldInputProps {
    /** 文本内容位置，居左/居中/居右。可选项：left/center/right */
    private String align;
    /** 超出 maxlength 或 maxcharacter 之后是否允许继续输入 */
    private Boolean allowInputOverMax;
    /** 宽度随内容自适应 */
    private Boolean autoWidth;
    /**
     * 是否开启自动填充功能，HTML5 原生属性
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/autocomplete">点击查看详情</a>
     */
    private String autocomplete;
    /** 自动聚焦 */
    @Builder.Default
    private Boolean autofocus = false;
    /** 是否开启无边框模式 */
    @Builder.Default
    private Boolean borderless = false;
    /** 是否可清空 */
    @Builder.Default
    private Boolean clearable = true;
    /** 是否禁用输入框 */
    private Boolean disabled;
    /** 左侧文本 */
    private String label;
    /** 用户最多可以输入的字符个数，一个中文汉字表示两个字符长度。maxcharacter 和 maxlength 二选一使用 */
    private Integer maxcharacter;
    /** 用户最多可以输入的字符个数，一个中文汉字表示两个字符长度。maxcharacter 和 maxlength 二选一使用 */
    private Integer maxlength;
    /** 名称 */
    private String name;
    /** 占位符 */
    private String placeholder;
    /** 只读状态 */
    private Boolean readonly;
    /** 输入框内容为空时，悬浮状态是否显示清空按钮，默认不显示 */
    @Builder.Default
    private Boolean showClearIconOnEmpty = false;
    /** 是否在输入框右侧显示字数统计 */
    @Builder.Default
    private Boolean showLimitNumber = false;
    /** 输入框尺寸。可选项：small/medium/large */
    private String size;
    /**
     * 是否开启拼写检查，HTML5 原生属性
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/spellcheck">点击查看详情</a>
     */
    private Boolean spellCheck;
    /** 输入框状态。可选项：default/success/warning/error */
    private String status;
    /** 输入框下方提示文本，会根据不同的 status 呈现不同的样式 */
    private String tips;
    /**
     * 输入框类型。type=number 仅支持最基础的数字输入功能，更多功能建议使用 InputNumber 组件。
     * 可选项：text/number/url/tel/password/search/submit/hidden
     */
    private String type;

    public static class FieldInputPropsBuilder<T> {
        private Function<FieldInputProps, T> end;

        public FieldInputPropsBuilder() {
        }

        public FieldInputPropsBuilder(Function<FieldInputProps, T> end) {
            this.end = end;
        }

        public T end() {
            return end.apply(this.build());
        }
    }
}
