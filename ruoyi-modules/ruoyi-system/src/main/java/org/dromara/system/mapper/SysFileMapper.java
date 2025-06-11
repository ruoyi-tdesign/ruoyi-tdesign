package org.dromara.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.system.domain.SysFile;
import org.dromara.system.domain.bo.SysFileBo;
import org.dromara.system.domain.query.SysFileQuery;
import org.dromara.system.domain.vo.SysFileVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 文件记录Mapper接口
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
public interface SysFileMapper extends BaseMapperPlus<SysFile, SysFileVo> {

    /**
     * 查询文件记录列表
     *
     * @param query 查询对象
     * @return {@link SysFileVo}
     */
    List<SysFileVo> queryList(SysFileQuery query);

    /**
     * 根据ID查询文件记录
     *
     * @param fileId 文件记录ID
     * @return {@link SysFileVo}
     */
    SysFileVo queryById(@Param("fileId") Long fileId);
}
