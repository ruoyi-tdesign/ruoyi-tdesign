package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.system.domain.SysTenantApp;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 租户应用管理视图对象 sys_tenant_app
 *
 * @author yixiacoco
 * @date 2023-05-17
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysTenantApp.class)
public class SysTenantAppVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用id
     */
    @ExcelProperty(value = "应用id")
    private Long appid;

    /**
     * 应用类型
     */
    @ExcelProperty(value = "应用类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_app_type")
    private String appType;

    /**
     * 应用key
     */
    @ExcelProperty(value = "应用key")
    private String appKey;

    /**
     * 应用名称
     */
    @ExcelProperty(value = "应用名称")
    private String appName;

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
