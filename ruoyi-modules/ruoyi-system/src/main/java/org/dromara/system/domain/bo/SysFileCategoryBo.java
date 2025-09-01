package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.system.domain.SysFileCategory;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件分类业务对象 sys_file_category
 *
 * @author yixiacoco
 * @date 2025-05-13
 */
@Data
@AutoMapper(target = SysFileCategory.class, reverseConvertGenerate = false)
public class SysFileCategoryBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件分类id
     */
    @NotNull(message = "文件分类id不能为空", groups = {EditGroup.class})
    private Long fileCategoryId;
    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空", groups = {AddGroup.class, EditGroup.class})
    @Pattern(regexp = "^[^/%_*]*$", message = "分类名不能包含下列任何字符：/%_*")
    @Length(max = 255, message = "分类名称不能大于{max}个字符", groups = {AddGroup.class, EditGroup.class})
    private String categoryName;
    /**
     * 父级分类id
     */
    @NotNull(message = "父级分类id不能为空", groups = {AddGroup.class, EditGroup.class})
    @Min(value = 0, message = "父级分类id不能小于0")
    private Long parentId;
    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer orderNum;
    /**
     * 用户类型
     */
    private String loginType;
    /**
     * 上传人
     */
    private Long createBy;
}
