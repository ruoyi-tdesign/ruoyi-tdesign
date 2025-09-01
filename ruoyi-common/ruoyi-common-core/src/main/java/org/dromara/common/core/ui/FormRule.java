package org.dromara.common.core.ui;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.intellij.lang.annotations.Language;

import java.util.List;

/**
 * 表单校验规则
 *
 * @author hexm
 * @date 2025/3/9
 */
@Getter
@Setter
@ToString
@Builder
public class FormRule {
    /** 内置校验方法，校验值类型是否为布尔类型 */
    @JsonAlias("boolean")
    private Boolean bool;
    /** 内置校验方法，校验值是否为日期格式 */
    private Boolean date;
    /** 内置校验方法，校验值是否为邮件格式 */
    private Boolean email;
    /** 内置校验方法，校验值是否属于枚举值中的值 */
    @JsonAlias("enum")
    private List<String> enums;
    /** 内置校验方法，校验值是否为身份证号码，组件校验正则为 /^(\\d{18,18}|\\d{15,15}|\\d{17,17}x)$/i */
    private Boolean idcard;
    /** 内置校验方法，校验值固定长度，如：len: 10 表示值的字符长度只能等于 10 ，中文表示 2 个字符，英文为 1 个字符 */
    private Integer len;
    /** 内置校验方法，校验值最大长度，如：max: 100 表示值最多不能超过 100 个字符，中文表示 2 个字符，英文为 1 个字符 */
    private Integer max;
    /** 校验未通过时呈现的错误信息，值为空则不显示 */
    private String message;
    /** 内置校验方法，校验值最小长度，如：min: 10 表示值最多不能少于 10 个字符，中文表示 2 个字符，英文为 1 个字符 */
    private Integer min;
    /** 内置校验方法，校验值是否为数字（1.2 、 1e5 都算数字） */
    private Boolean number;
    /** 内置校验方法，校验值是否符合正则表达式匹配结果 */
    @Language("RegExp")
    private String pattern;
    /** 内置校验方法，校验值是否已经填写。该值为 true，默认显示必填标记，可通过设置 requiredMark: false 隐藏必填标记 */
    private Boolean required;
    /** 内置校验方法，校验值是否为手机号码，校验正则为 /^1[3-9]\d{9}$/ */
    private Boolean telnumber;
    /** 校验触发方式 'blur' | 'change' | 'submit' | 'all' */
    private String trigger;
    /** 校验未通过时呈现的错误信息类型，有 告警信息提示 和 错误信息提示 等两种。可选项：error/warning */
    private String type;
    /** 内置校验方法，校验值是否为网络链接地址 */
    private Boolean url;
    /** 自定义校验规则，示例：{ validator: (val) => val.length > 0, message: '请输入内容'} */
    private String validator;
    /** 内置校验方法，校验值是否为空格 */
    private Boolean whitespace;
}
