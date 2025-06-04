package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.system.domain.SysFile;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件记录业务对象 sys_file
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
@Data
@AutoMapper(target = SysFile.class, reverseConvertGenerate = false)
public class SysFileBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件id
     */
    @NotNull(message = "文件id不能为空", groups = {EditGroup.class})
    private Long fileId;
    /**
     * 原始文件名
     */
    @Length(max = 256, message = "原始文件名不能大于{max}个字符", groups = {AddGroup.class, EditGroup.class})
    private String originalFilename;
    /**
     * 分类id
     */
    @NotNull(message = "分类id不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long fileCategoryId;
    /**
     * 是否锁定状态
     */
    @NotNull(message = "是否锁定状态不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer isLock;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 上传人
     */
    private Long createBy;
}
