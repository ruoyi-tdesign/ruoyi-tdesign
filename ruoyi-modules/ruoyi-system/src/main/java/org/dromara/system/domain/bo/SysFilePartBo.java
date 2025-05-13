package org.dromara.system.domain.bo;

import org.dromara.system.domain.SysFilePart;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.hibernate.validator.constraints.Length;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.util.Date;
import java.io.Serial;
import java.io.Serializable;

/**
 * 文件分片信息，仅在手动分片上传时使用业务对象 sys_file_part
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
@Data
@AutoMapper(target = SysFilePart.class, reverseConvertGenerate = false)
public class SysFilePartBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分片id
     */
    @NotNull(message = "分片id不能为空", groups = {EditGroup.class})
    private Long filePartId;
    /**
     * 存储配置id
     */
    private Long storageConfigId;
    /**
     * 上传ID，仅在手动分片上传时使用
     */
    @Length(max = 128, message = "上传ID，仅在手动分片上传时使用不能大于{max}个字符", groups = {AddGroup.class, EditGroup.class})
    private String uploadId;
    /**
     * 分片 ETag
     */
    @Length(max = 255, message = "分片 ETag不能大于{max}个字符", groups = {AddGroup.class, EditGroup.class})
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
}
