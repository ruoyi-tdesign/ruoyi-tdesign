package org.dromara.system.mapper;

import org.dromara.system.domain.SysFilePart;
import org.dromara.system.domain.bo.SysFilePartBo;
import org.dromara.system.domain.query.SysFilePartQuery;
import org.dromara.system.domain.vo.SysFilePartVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 文件分片信息，仅在手动分片上传时使用Mapper接口
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
public interface SysFilePartMapper extends BaseMapperPlus<SysFilePart, SysFilePartVo> {

    /**
     * 查询文件分片信息，仅在手动分片上传时使用列表
     *
     * @param query 查询对象
     * @return {@link SysFilePartVo}
     */
    List<SysFilePartVo> queryList(SysFilePartQuery query);
}
