package org.dromara.system.domain.vo;

import org.dromara.system.domain.SysStorageConfig;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import java.util.Date;
import java.io.Serial;
import java.io.Serializable;

/**
 * 存储配置视图对象 sys_storage_config
 *
 * @author yixiacoco
 * @date 2025-05-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysStorageConfig.class)
public class SysStorageConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主建
     */
    @ExcelProperty(value = "主建")
    private Long storageConfigId;

    /**
     * 平台
     */
    @ExcelProperty(value = "平台")
    private String platform;

    /**
     * 负载均衡权重
     */
    @ExcelProperty(value = "负载均衡权重")
    private Integer weight;

    /**
     * 启用状态
     */
    @ExcelProperty(value = "启用状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private Integer status;

    /**
     * 配置json
     */
    @ExcelProperty(value = "配置json")
    private String configJson;

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
