package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 文件分片信息，仅在手动分片上传时使用对象 sys_file_part
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
@Data
@TableName("sys_file_part")
public class SysFilePart implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分片id
     */
    @TableId(value = "file_part_id")
    private Long filePartId;

    /**
     * 存储配置id
     */
    private Long storageConfigId;

    /**
     * 上传ID，仅在手动分片上传时使用
     */
    private String uploadId;

    /**
     * 分片 ETag
     */
    private String eTag;

    /**
     * 分片号。每一个上传的分片都有一个分片号，一般情况下取值范围是1~10000
     */
    private Integer partNumber;

    /**
     * 文件大小，单位字节
     */
    private Long partSize;

    /**
     * 哈希信息
     */
    private String hashInfo;

    /**
     * 创建部门
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createDept;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 上传人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
