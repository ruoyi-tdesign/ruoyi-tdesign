package org.dromara.system.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import org.dromara.common.mybatis.core.domain.BasePageQuery;

/**
 * 存储配置查询对象 sys_storage_config
 *
 * @author yixiacoco
 * @date 2025-05-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysStorageConfigQuery extends BasePageQuery {

    /**
     * 平台
     */
    private String platform;

    /**
     * 启用状态
     */
    private Integer status;

}
