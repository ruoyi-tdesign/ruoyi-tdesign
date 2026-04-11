package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.system.domain.SysMessageLog;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 消息发送记录视图对象 sys_message_log
 *
 * @author hexm
 * @date 2023-06-29
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysMessageLog.class)
public class SysMessageLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息发送记录id
     */
    @ExcelProperty(value = "消息发送记录id")
    private Long messageLogId;

    /**
     * 消息模板id
     */
    @ExcelProperty(value = "消息模板id")
    private Long messageTemplateId;

    /**
     * 消息key
     */
    @ExcelProperty(value = "消息key")
    private String messageKey;

    /**
     * 模板名称
     */
    @ExcelProperty(value = "模板名称")
    private String messageTemplateName;

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
     * 发送账号
     */
    @ExcelProperty(value = "发送账号")
    private String account;

    /**
     * 标题
     */
    @ExcelProperty(value = "标题")
    private String title;

    /**
     * 模板ID
     */
    @ExcelProperty(value = "模板ID")
    private String templateId;

    /**
     * 发送内容
     */
    @ExcelProperty(value = "发送内容")
    private String content;

    /**
     * 消息配置标题
     */
    @ExcelProperty(value = "消息配置标题")
    private String messageConfigTitle;

    /**
     * 平台标识
     */
    @ExcelProperty(value = "平台标识", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_message_supplier_type")
    private String supplierType;

    /**
     * 是否成功
     */
    @ExcelProperty(value = "是否成功", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_common_status")
    private Integer isSuccess;

    /**
     * 返回主体消息
     */
    @ExcelProperty(value = "返回主体消息")
    private String responseBody;

    /**
     * 记录时间
     */
    @ExcelProperty(value = "记录时间")
    private Date logTime;

    /**
     * 消耗时间
     */
    @ExcelProperty(value = "消耗时间")
    private Long costTime;

}
