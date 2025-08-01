package org.dromara.system.domain.query;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.dromara.common.core.validate.QueryOneGroup;
import org.dromara.common.mybatis.core.domain.BasePageQuery;

/**
 * 文件分类查询对象 sys_file_category
 *
 * @author yixiacoco
 * @date 2025-05-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysFileCategoryQuery extends BasePageQuery {

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父级分类id
     */
    private String parentId;

    /**
     * 用户类型
     */
    private String loginType;

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
    private Long fileCategoryId;

}
