package org.dromara.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.system.domain.SysMessageTemplate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 消息模板视图对象 sys_message_template
 *
 * @author hexm
 * @date 2023-06-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysMessageTemplate.class)
public class SysMessageTemplateVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息模板id
     */
    @ExcelProperty(value = "消息模板id")
    private Long messageTemplateId;

    /**
     * 模板名称
     */
    @ExcelProperty(value = "模板名称")
    private String templateName;

    /**
     * 消息配置id
     */
    @ExcelProperty(value = "消息配置id")
    private Long messageConfigId;

    /**
     * 消息key主键
     */
    @ExcelProperty(value = "消息key主键")
    private Long messageKeyId;

    /**
     * 消息key
     */
    @ExcelProperty(value = "消息key")
    private String messageKey;

    /**
     * 消息类型
     */
    @ExcelProperty(value = "消息类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_message_type")
    private String messageType;

    /**
     * 模板类型
     */
    @ExcelProperty(value = "模板类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_message_template_mode")
    private String templateMode;

    /**
     * 签名
     */
    @ExcelProperty(value = "签名")
    private String signature;

    /**
     * 标题
     */
    @ExcelProperty(value = "标题")
    private String title;

    /**
     * 模板id
     */
    @ExcelProperty(value = "模板id")
    private String templateId;

    /**
     * 内容
     */
    @ExcelProperty(value = "内容")
    private String content;

    /**
     * 输入变量
     */
    @ExcelProperty(value = "输入变量")
    private String varsJson;

    /**
     * 状态（1正常 0停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private Integer status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

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

    /**
     * 支持平台标识
     */
    @ExcelProperty(value = "支持平台标识")
    private String supplierType;

}
