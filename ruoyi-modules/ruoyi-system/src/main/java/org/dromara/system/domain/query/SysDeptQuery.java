package org.dromara.system.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BasePageQuery;

import java.util.List;

/**
 * 部门查询对象 sys_dept
 *
 * @author hexm
 * @date 2023-04-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDeptQuery extends BasePageQuery {

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 多部门id
     */
    private List<Long> deptIds;

    /**
     * 父部门id
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门类别编码
     */
    private String deptCategory;

    /**
     * 部门状态
     */
    private String status;

    /**
     * 归属部门id（部门树）
     */
    private Long belongDeptId;

}
