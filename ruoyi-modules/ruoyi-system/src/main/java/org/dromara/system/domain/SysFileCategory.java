package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 文件分类对象 sys_file_category
 *
 * @author yixiacoco
 * @date 2025-05-13
 */
@Data
@TableName("sys_file_category")
public class SysFileCategory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件分类id
     */
    @TableId(value = "file_category_id")
    private Long fileCategoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父级分类id
     */
    private Long parentId;

    /**
     * 分类路径
     */
    private String categoryPath;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 用户类型
     */
    private String loginType;

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
