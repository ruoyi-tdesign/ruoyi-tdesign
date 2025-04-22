package org.dromara.system.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BasePageQuery;

import java.util.List;

/**
 * 岗位信息查询对象 sys_post
 *
 * @author hexm
 * @date 2023-04-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysPostQuery extends BasePageQuery {

    /**
     * 岗位编码
     */
    private String postCode;

    /**
     * 岗位类别编码
     */
    private String postCategory;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 状态（1正常 0停用）
     */
    private String status;

    /**
     * 部门id（单部门）
     */
    private Long deptId;

    /**
     * 归属部门id（部门树）
     */
    private Long belongDeptId;

    /**
     * 部门id
     */
    private List<Long> deptIds;

}
