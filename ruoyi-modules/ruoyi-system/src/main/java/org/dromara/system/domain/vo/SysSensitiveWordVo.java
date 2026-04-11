package org.dromara.system.domain.vo;

import org.dromara.system.domain.SysSensitiveWord;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import java.util.Date;
import java.io.Serial;
import java.io.Serializable;

/**
 * 敏感词视图对象 sys_sensitive_word
 *
 * @author hexm
 * @date 2024-08-16
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysSensitiveWord.class)
public class SysSensitiveWordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 敏感词id
     */
    @ExcelProperty(value = "敏感词id")
    private Long wordId;

    /**
     * 敏感词
     */
    @ExcelProperty(value = "敏感词")
    private String word;

    /**
     * 敏感词类别
     */
    @ExcelProperty(value = "敏感词类别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sensitive_words_category")
    private String category;

    /**
     * 描述
     */
    @ExcelProperty(value = "描述")
    private String description;

    /**
     * 启用状态
     */
    @ExcelProperty(value = "启用状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private Integer status;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

}
