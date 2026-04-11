package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.system.domain.SysMessageConfig;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 消息配置视图对象 sys_message_config
 *
 * @author hexm
 * @date 2023-06-27
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysMessageConfig.class)
public class SysMessageConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息设置id
     */
    @ExcelProperty(value = "消息设置id")
    private Long messageConfigId;

    /**
     * 标题
     */
    @ExcelProperty(value = "标题")
    private String title;

    /**
     * 消息类型
     */
    @ExcelProperty(value = "消息类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_message_type")
    private String messageType;

    /**
     * 支持平台标识
     */
    @ExcelProperty(value = "支持平台标识", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_message_supplier_type")
    private String supplierType;

    /**
     * 配置json
     */
    @ExcelProperty(value = "配置json")
    private String configJson;

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

}
