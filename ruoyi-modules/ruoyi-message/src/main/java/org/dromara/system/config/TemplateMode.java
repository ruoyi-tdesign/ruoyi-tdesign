package org.dromara.system.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模板模式
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateMode {
    /** 支持模板id */
    private Boolean supportTemplateId;
    /** 支持模板内容 */
    private Boolean supportTemplateContent;
}
