package org.dromara.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;
import org.dromara.system.domain.SysNotice;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 通知公告视图对象 sys_notice
 *
 * @author Michelle.Chung
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysNotice.class)
public class SysNoticeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
    @ExcelProperty(value = "公告ID")
    private Long noticeId;

    /**
     * 公告标题
     */
    @ExcelProperty(value = "公告标题")
    private String noticeTitle;

    /**
     * 公告类型（1通知 2公告）
     */
    @ExcelProperty(value = "公告类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_notice_type")
    private String noticeType;

    /**
     * 公告内容
     */
    @ExcelProperty(value = "公告内容")
    private String noticeContent;

    /**
     * 公告状态（0正常 1关闭）
     */
    @ExcelProperty(value = "公告状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_notice_status")
    private String status;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建人名称
     */
    @Translation(type = TransConstant.USER_ID_TO_NAME, mapper = "createBy")
    private String createByName;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

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
