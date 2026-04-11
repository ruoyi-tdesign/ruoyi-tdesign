package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.system.domain.SysOssRule;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * OSS处理规则视图对象 sys_oss_rule
 *
 * @author hexm
 * @date 2023-05-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysOssRule.class)
public class SysOssRuleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * oss规则id
     */
    @ExcelProperty(value = "oss规则id")
    private Long ossRuleId;

    /**
     * 规则名称（例如：80x80，则字段名称将输出字段名_80x80）
     */
    @ExcelProperty(value = "规则名称")
    private String ruleName;

    /**
     * 匹配域名
     */
    @ExcelProperty(value = "匹配域名")
    private String domain;

    /**
     * 媒体类型（规则对匹配的媒体类型生效）
     */
    @ExcelProperty(value = "媒体类型")
    private String mimeType;

    /**
     * 规则
     */
    @ExcelProperty(value = "规则")
    private String rule;

    /**
     * 是否覆盖默认字段值
     */
    @ExcelProperty(value = "是否覆盖默认字段值", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_yes_no")
    private String isOverwrite;

    /**
     * 是否默认（不指定规则时，默认输出的规则）
     */
    @ExcelProperty(value = "是否默认", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_yes_no")
    private String isDefault;

    /**
     * 启用状态
     */
    @ExcelProperty(value = "启用状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

    /**
     * 规则顺序
     */
    @ExcelProperty(value = "规则顺序")
    private Integer ruleSort;

    /**
     * 创建部门
     */
    @ExcelProperty(value = "创建部门")
    private Long createDept;

    /**
     * 创建者
     */
    @ExcelProperty(value = "创建者")
    private Long createBy;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    @ExcelProperty(value = "更新者")
    private Long updateBy;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

}
