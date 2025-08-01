package org.dromara.system.domain.vo;

import org.dromara.system.domain.SysFile;
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
 * 文件记录视图对象 sys_file
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysFile.class)
public class SysFileVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件id
     */
    @ExcelProperty(value = "文件id")
    private Long fileId;

    /**
     * 文件访问地址
     */
    @ExcelProperty(value = "文件访问地址")
    private String url;

    /**
     * 预览地址
     */
    @ExcelProperty(value = "预览地址")
    private String previewUrl;

    /**
     * 下载地址
     */
    @ExcelProperty(value = "下载地址")
    private String downloadUrl;

    /**
     * 文件大小，单位字节
     */
    @ExcelProperty(value = "文件大小，单位字节")
    private Long size;

    /**
     * 文件名称
     */
    @ExcelProperty(value = "文件名称")
    private String filename;

    /**
     * 原始文件名
     */
    @ExcelProperty(value = "原始文件名")
    private String originalFilename;

    /**
     * 基础存储路径
     */
    @ExcelProperty(value = "基础存储路径")
    private String basePath;

    /**
     * 存储路径
     */
    @ExcelProperty(value = "存储路径")
    private String path;

    /**
     * 文件扩展名
     */
    @ExcelProperty(value = "文件扩展名")
    private String ext;

    /**
     * MIME类型
     */
    @ExcelProperty(value = "MIME类型")
    private String contentType;

    /**
     * 存储配置id
     */
    @ExcelProperty(value = "存储配置id")
    private Long storageConfigId;

    /**
     * 缩略图访问路径
     */
    @ExcelProperty(value = "缩略图访问路径")
    private String thUrl;

    /**
     * 缩略图名称
     */
    @ExcelProperty(value = "缩略图名称")
    private String thFilename;

    /**
     * 缩略图大小，单位字节
     */
    @ExcelProperty(value = "缩略图大小，单位字节")
    private Long thSize;

    /**
     * 缩略图MIME类型
     */
    @ExcelProperty(value = "缩略图MIME类型")
    private String thContentType;

    /**
     * 文件所属对象id
     */
    @ExcelProperty(value = "文件所属对象id")
    private String objectId;

    /**
     * 文件所属对象类型，例如用户头像，评价图片
     */
    @ExcelProperty(value = "文件所属对象类型，例如用户头像，评价图片")
    private String objectType;

    /**
     * 文件元数据
     */
    @ExcelProperty(value = "文件元数据")
    private String metadata;

    /**
     * 文件用户元数据
     */
    @ExcelProperty(value = "文件用户元数据")
    private String userMetadata;

    /**
     * 缩略图元数据
     */
    @ExcelProperty(value = "缩略图元数据")
    private String thMetadata;

    /**
     * 缩略图用户元数据
     */
    @ExcelProperty(value = "缩略图用户元数据")
    private String thUserMetadata;

    /**
     * 附加属性
     */
    @ExcelProperty(value = "附加属性")
    private String attr;

    /**
     * 文件ACL
     */
    @ExcelProperty(value = "文件ACL")
    private String fileAcl;

    /**
     * 缩略图文件ACL
     */
    @ExcelProperty(value = "缩略图文件ACL")
    private String thFileAcl;

    /**
     * 哈希信息
     */
    @ExcelProperty(value = "哈希信息")
    private String hashInfo;

    /**
     * 上传ID，仅在手动分片上传时使用
     */
    @ExcelProperty(value = "上传ID，仅在手动分片上传时使用")
    private String uploadId;

    /**
     * 上传状态，仅在手动分片上传时使用，1：初始化完成，2：上传完成
     */
    @ExcelProperty(value = "上传状态，仅在手动分片上传时使用，1：初始化完成，2：上传完成")
    private Integer uploadStatus;

    /**
     * 分类id
     */
    @ExcelProperty(value = "分类id")
    private Long fileCategoryId;

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
     * 上传人名称
     */
    @ExcelProperty(value = "上传人名称")
    private String createByName;
}
