package org.dromara.system.config;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 字段配置
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@Builder
public class FieldConfig<T> {
    /** 字段默认值 */
    private T value;
    /** 字段名称 */
    private String name;
    /** 组件 */
    private String component;
    /** 帮助信息 */
    private String help;
    /** 是否必填 */
    private boolean required;
    /** 占用栅格数 */
    private Integer span;
    /** 最小值 */
    private T min;
    /** 最大值 */
    private T max;
    /** 可见性依赖字段。例如a=true，则b设置visible为a */
    private String visible;
    /** 其他校验规则 */
    private List<FormRule> rules;
    /** 组件类型 'number' | 'submit' | 'url' | 'text' | 'search' | 'password' | 'hidden' | 'tel' */
    private String type;
}
