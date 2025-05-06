package org.dromara.common.core.ui;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Data;
import org.intellij.lang.annotations.Language;

import java.util.List;

/**
 * tdesign 表单校验规则
 *
 * @author hexm
 * @date 2025/3/9
 */
@Data
@Builder
public class FormRule {
    @JsonAlias("boolean")
    private Boolean bool;
    private Boolean date;
    private Boolean email;
    @JsonAlias("enum")
    private List<String> enums;
    private Boolean idcard;
    private Integer len;
    private Integer max;
    private String message;
    private Integer min;
    private Boolean number;
    @Language("RegExp")
    private String pattern;
    private Boolean required;
    private Boolean telnumber;
    /** 'blur' | 'change' | 'submit' | 'all' */
    private String trigger;
    /** 'error' | 'warning' */
    private String type;
    private Boolean url;
    private String validator;
    private Boolean whitespace;
}
