package org.dromara.system.mapper;

import org.dromara.system.domain.SysStorageConfig;
import org.dromara.system.domain.bo.SysStorageConfigBo;
import org.dromara.system.domain.query.SysStorageConfigQuery;
import org.dromara.system.domain.vo.SysStorageConfigVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 存储配置Mapper接口
 *
 * @author yixiacoco
 * @date 2025-05-04
 */
public interface SysStorageConfigMapper extends BaseMapperPlus<SysStorageConfig, SysStorageConfigVo> {

    /**
     * 查询存储配置列表
     *
     * @param query 查询对象
     * @return {@link SysStorageConfigVo}
     */
    List<SysStorageConfigVo> queryList(SysStorageConfigQuery query);
}
