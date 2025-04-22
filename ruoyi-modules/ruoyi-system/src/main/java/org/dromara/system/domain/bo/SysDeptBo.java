package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.system.domain.SysDept;

/**
 * 部门业务对象 sys_dept
 *
 * @author Michelle.Chung
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysDept.class, reverseConvertGenerate = false)
public class SysDeptBo extends BaseEntity {

    /**
     * 部门id
     */
    @NotNull(message = "部门id不能为空", groups = {EditGroup.class})
    private Long deptId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空", groups = {AddGroup.class, EditGroup.class})
    @Size(min = 0, max = 30, message = "部门名称长度不能超过{max}个字符", groups = {AddGroup.class, EditGroup.class})
    private String deptName;

    /**
     * 上级部门id
     */
    private Long parentId;

    /**
     * 部门类别编码
     */
    @Size(min = 0, max = 100, message = "部门类别编码长度不能超过{max}个字符")
    private String deptCategory;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer orderNum;

    /**
     * 负责人
     */
    private Long leader;

    /**
     * 联系电话
     */
    @Size(min = 0, max = 11, message = "联系电话长度不能超过{max}个字符", groups = {AddGroup.class, EditGroup.class})
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确", groups = {AddGroup.class, EditGroup.class})
    @Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符", groups = {AddGroup.class, EditGroup.class})
    private String email;

    /**
     * 部门状态（1正常 0停用）
     */
    private String status;

}
