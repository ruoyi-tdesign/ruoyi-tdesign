package org.dromara.common.core.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 字段配置
 *
 * @author hexm
 * @date 2025/3/9
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldConfig<T> {
    /** 字段默认值 */
    private T value;
    /** 标签 */
    private String label;
    /** 自定义字段名称，默认为对象的属性名称。可以使用.作为嵌套对象的属性，例如：items.item */
    private String name;
    /** 组件, 设置inputComponent、inputNumberComponent...等组件属性时，自动设置该值 */
    private String component;
    /** 帮助信息 */
    private String help;
    /** 是否必填 */
    private boolean required;
    /** 占用栅格数 */
    private Integer span;
    /** 可见性依赖字段。例如a=true，则b设置visible为a */
    private String visible;
    /** 其他校验规则 */
    private List<FormRule> rules;
    /** 输入框组件属性 */
    private FieldInputProps inputProps;
    /** 数字输入框组件属性 */
    private FieldInputNumberProps inputNumberProps;
    /** 开关组件属性 */
    private FieldSwitchProps switchProps;
    /** 文本域组件属性 */
    private FieldTextareaProps textareaProps;
    /** 选择器组件属性 */
    private FieldSelectProps selectProps;

    public static class FieldConfigBuilder<T> {
        /**
         * 使用输入框组件
         */
        public FieldConfigBuilder<T> useInput() {
            this.component = "input";
            if (this.inputProps == null) {
                this.inputProps = FieldInputProps.builder().build();
            }
            return this;
        }

        /**
         * 使用数字输入框组件
         */
        public FieldConfigBuilder<T> useInputNumber() {
            this.component = "input-number";
            if (this.inputNumberProps == null) {
                this.inputNumberProps = FieldInputNumberProps.builder().build();
            }
            return this;
        }

        /**
         * 使用开关组件
         */
        public FieldConfigBuilder<T> useSwitch() {
            this.component = "switch";
            if (this.switchProps == null) {
                this.switchProps = FieldSwitchProps.builder().build();
            }
            return this;
        }

        /**
         * 使用文本域组件
         */
        public FieldConfigBuilder<T> useTextarea() {
            this.component = "textarea";
            if (this.textareaProps == null) {
                this.textareaProps = FieldTextareaProps.builder().build();
            }
            return this;
        }

        /**
         * 使用选择器组件
         */
        public FieldConfigBuilder<T> useSelect() {
            this.component = "select";
            if (this.selectProps == null) {
                this.selectProps = FieldSelectProps.builder().build();
            }
            return this;
        }

        /**
         * 输入框组件属性
         */
        public FieldInputProps.FieldInputPropsBuilder<FieldConfigBuilder<T>> inputComponent() {
            return new FieldInputProps.FieldInputPropsBuilder<>((inputProps) -> {
                this.inputProps = inputProps;
                return useInput();
            });
        }

        /**
         * 数字输入框组件属性
         */
        public FieldInputNumberProps.FieldInputNumberPropsBuilder<FieldConfigBuilder<T>> inputNumberComponent() {
            return new FieldInputNumberProps.FieldInputNumberPropsBuilder<>((inputNumberProps) -> {
                this.inputNumberProps = inputNumberProps;
                return useInputNumber();
            });
        }

        /**
         * 开关组件属性
         */
        public FieldSwitchProps.FieldSwitchPropsBuilder<FieldConfigBuilder<T>> switchComponent() {
            return new FieldSwitchProps.FieldSwitchPropsBuilder<>((switchProps) -> {
                this.switchProps = switchProps;
                return useSwitch();
            });
        }

        /**
         * 文本域组件属性
         */
        public FieldTextareaProps.FieldTextareaPropsBuilder<FieldConfigBuilder<T>> textareaComponent() {
            return new FieldTextareaProps.FieldTextareaPropsBuilder<>((textareaProps) -> {
                this.textareaProps = textareaProps;
                return useTextarea();
            });
        }

        /**
         * 选择器组件属性
         */
        public FieldSelectProps.FieldSelectPropsBuilder<FieldConfigBuilder<T>> selectComponent() {
            return new FieldSelectProps.FieldSelectPropsBuilder<>((selectProps) -> {
                this.selectProps = selectProps;
                return useSelect();
            });
        }
    }
}
