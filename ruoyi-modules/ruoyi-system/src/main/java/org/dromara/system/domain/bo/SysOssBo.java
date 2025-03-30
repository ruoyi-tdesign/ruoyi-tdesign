package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.system.domain.SysOss;
import org.hibernate.validator.constraints.Range;

/**
 * OSS对象存储业务对象 sys_oss
 *
 * @author yixiacoco
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysOss.class, reverseConvertGenerate = false)
public class SysOssBo extends BaseEntity {

    /**
     * 对象存储主键
     */
    @NotNull(message = "对象存储主键不能为空", groups = {EditGroup.class})
    private Long ossId;

    /**
     * 原名
     */
    @NotBlank(message = "原名不能为空", groups = {EditGroup.class})
    @Pattern(regexp = "^[^.][^\\\\/<>:?\"|*]*$", message = "文件名不能包含下列任何字符：\\/<>:?\"|*", groups = {AddGroup.class, EditGroup.class})
    private String originalName;

    /**
     * 分类id
     */
    @NotNull(message = "分类id不能为空", groups = {AddGroup.class, EditGroup.class})
    @Min(value = 0, message = "分类id不能小于0", groups = {AddGroup.class, EditGroup.class})
    private Long ossCategoryId = 0L;

    /**
     * 扩展字段
     */
    private String ext1;

    /**
     * 用户类型
     */
    private UserType userTypeEnum;

    /**
     * 是否锁定状态
     */
    @NotNull(message = "是否锁定状态不能为空", groups = {AddGroup.class, EditGroup.class})
    @Range(min = 0, max = 1, message = "锁定状态错误", groups = {AddGroup.class, EditGroup.class})
    private Integer isLock = 0;

    /**
     * 上传人
     */
    private Long createBy;

}
