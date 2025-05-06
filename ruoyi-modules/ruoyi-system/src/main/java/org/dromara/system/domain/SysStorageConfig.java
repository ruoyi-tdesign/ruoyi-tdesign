package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 存储配置对象 sys_storage_config
 *
 * @author yixiacoco
 * @date 2025-05-04
 */
@Data
@TableName("sys_storage_config")
public class SysStorageConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主建
     */
    @TableId(value = "storage_config_id")
    private Long storageConfigId;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 平台
     */
    private String platform;

    /**
     * 负载均衡权重
     */
    private Integer weight;

    /**
     * 启用状态
     */
    private Integer status;

    /**
     * 配置json
     */
    private String configJson;

    /**
     * 删除标志
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private String delFlag;

    /**
     * 创建部门
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createDept;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

}
