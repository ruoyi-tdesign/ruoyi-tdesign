package org.dromara.workflow.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import org.dromara.common.mybatis.core.domain.BasePageQuery;

/**
 * 流程分类查询对象 flow_category
 *
 * @author yixiacoco
 * @date 2024-10-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WfCategoryQuery extends BasePageQuery {

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类编码
     */
    private String categoryCode;

}
