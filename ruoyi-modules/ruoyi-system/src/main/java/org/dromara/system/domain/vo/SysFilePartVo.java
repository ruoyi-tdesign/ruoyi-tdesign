package org.dromara.system.domain.vo;

import org.dromara.system.domain.SysFilePart;
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
 * 文件分片信息，仅在手动分片上传时使用视图对象 sys_file_part
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysFilePart.class)
public class SysFilePartVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分片id
     */
    @ExcelProperty(value = "分片id")
    private Long filePartId;

    /**
     * 存储配置id
     */
    @ExcelProperty(value = "存储配置id")
    private Long storageConfigId;

    /**
     * 上传ID，仅在手动分片上传时使用
     */
    @ExcelProperty(value = "上传ID，仅在手动分片上传时使用")
    private String uploadId;

    /**
     * 分片 ETag
     */
    @ExcelProperty(value = "分片 ETag")
    private String eTag;

    /**
     * 分片号。每一个上传的分片都有一个分片号，一般情况下取值范围是1~10000
     */
    @ExcelProperty(value = "分片号。每一个上传的分片都有一个分片号，一般情况下取值范围是1~10000")
    private Integer partNumber;

    /**
     * 文件大小，单位字节
     */
    @ExcelProperty(value = "文件大小，单位字节")
    private Long partSize;

    /**
     * 哈希信息
     */
    @ExcelProperty(value = "哈希信息")
    private String hashInfo;

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
