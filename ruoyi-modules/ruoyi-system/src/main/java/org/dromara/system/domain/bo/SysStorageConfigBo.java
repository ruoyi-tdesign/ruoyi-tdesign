package org.dromara.system.domain.bo;

import org.dromara.system.domain.SysStorageConfig;
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
 * 存储配置业务对象 sys_storage_config
 *
 * @author yixiacoco
 * @date 2025-05-04
 */
@Data
@AutoMapper(target = SysStorageConfig.class, reverseConvertGenerate = false)
public class SysStorageConfigBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主建
     */
    @NotNull(message = "主建不能为空", groups = {EditGroup.class})
    private Long storageConfigId;
    /**
     * 平台
     */
    @NotBlank(message = "平台不能为空", groups = {AddGroup.class, EditGroup.class})
    @Length(max = 50, message = "平台不能大于{max}个字符", groups = {AddGroup.class, EditGroup.class})
    private String platform;
    /**
     * 负载均衡权重
     */
    @NotNull(message = "负载均衡权重不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer weight;
    /**
     * 启用状态
     */
    @NotNull(message = "启用状态不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer status;
    /**
     * 配置json
     */
    @NotBlank(message = "配置json不能为空", groups = {AddGroup.class, EditGroup.class})
    @Length(max = 2000, message = "配置json不能大于{max}个字符", groups = {AddGroup.class, EditGroup.class})
    private String configJson;
    /**
     * 备注
     */
    @Length(max = 500, message = "备注不能大于{max}个字符", groups = {AddGroup.class, EditGroup.class})
    private String remark;
}
