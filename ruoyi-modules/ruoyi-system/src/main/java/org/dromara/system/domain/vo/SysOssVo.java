package org.dromara.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.translation.annotation.OssRule;
import org.dromara.system.domain.SysOss;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * OSS对象存储视图对象 sys_oss
 *
 * @author Lion Li
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysOss.class)
public class SysOssVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 对象存储主键
     */
    @ExcelProperty(value = "对象存储主键")
    private Long ossId;

    /**
     * 文件名
     */
    @ExcelProperty(value = "文件名")
    private String fileName;

    /**
     * 原名
     */
    @ExcelProperty(value = "原名")
    private String originalName;

    /**
     * 文件后缀名
     */
    @ExcelProperty(value = "文件后缀名")
    private String fileSuffix;

    /**
     * URL地址
     */
    @OssRule("80x80")
    @ExcelProperty(value = "URL地址")
    private String url;

    /**
     * 字节长度
     */
    @ExcelProperty(value = "字节长度")
    private Long size;

    /**
     * 内容类型
     */
    @ExcelProperty(value = "内容类型")
    private String contentType;

    /**
     * 分类id
     */
    @ExcelProperty(value = "分类id")
    private Long ossCategoryId;

    /**
     * 分类路径
     */
    @ExcelProperty(value = "分类路径")
    private String categoryPath;

    /**
     * 用户类型
     */
    @ExcelProperty(value = "用户类型")
    private String userType;

    /**
     * 是否锁定状态
     */
    @ExcelProperty(value = "是否锁定状态")
    private Integer isLock;

    /**
     * 创建部门
     */
    @ExcelProperty(value = "创建部门")
    private Long createDept;

    /**
     * 扩展字段
     */
    private String ext1;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 上传人
     */
    @ExcelProperty(value = "上传人")
    private Long createBy;

    /**
     * 上传人名称
     */
    @ExcelProperty(value = "上传人名称")
    private String createByName;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 更新人
     */
    @ExcelProperty(value = "更新人")
    private Long updateBy;

    /**
     * 服务商
     */
    @ExcelProperty(value = "服务商")
    private String service;

}
