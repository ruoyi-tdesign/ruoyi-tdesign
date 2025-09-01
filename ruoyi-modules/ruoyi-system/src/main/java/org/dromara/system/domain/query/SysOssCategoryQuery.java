package org.dromara.system.domain.query;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.QueryOneGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * OSS分类查询对象 sys_oss_category
 *
 * @author hexm
 * @date 2023-08-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysOssCategoryQuery extends BaseEntity {

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 上传人
     */
    private Long createBy;

    /**
     * 多个文件后缀
     */
    private String[] suffixes;

    /**
     * 文件最大字节长度
     */
    private Long maxSize;

    /**
     * 内容类型
     */
    private String[] contentTypes;

    /**
     * 分类id
     */
    @NotNull(message = "分类id不能为空", groups = QueryOneGroup.class)
    private Long ossCategoryId;

}
