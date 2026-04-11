package org.dromara.system.domain.template;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.system.domain.bo.SysSensitiveWordBo;

import java.io.Serial;
import java.io.Serializable;

/**
 * 敏感词导入对象
 *
 * @author hexm
 * @date 2024-08-16
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysSensitiveWordBo.class, reverseConvertGenerate = false)
public class SysSensitiveWordImportTemplate implements Serializable {

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
}
