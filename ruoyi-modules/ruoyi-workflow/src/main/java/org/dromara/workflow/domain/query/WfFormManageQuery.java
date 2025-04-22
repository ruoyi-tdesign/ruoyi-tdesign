package org.dromara.workflow.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BasePageQuery;

/**
 * 表单管理查询对象 wf_form_manage
 *
 * @author yixiacoco
 * @date 2024-11-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WfFormManageQuery extends BasePageQuery {

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 表单类型
     */
    private String formType;

}
