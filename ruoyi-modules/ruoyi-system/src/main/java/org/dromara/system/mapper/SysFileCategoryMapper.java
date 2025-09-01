package org.dromara.system.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.SysFileCategory;
import org.dromara.system.domain.query.SysFileCategoryQuery;
import org.dromara.system.domain.vo.SysFileCategoryVo;
import org.dromara.system.domain.vo.SysOssCategoryVo;

import java.util.List;

/**
 * 文件分类Mapper接口
 *
 * @author yixiacoco
 * @date 2025-05-13
 */
public interface SysFileCategoryMapper extends BaseMapperPlus<SysFileCategory, SysFileCategoryVo> {

    /**
     * 查询文件分类列表
     *
     * @param query 查询对象
     * @return {@link SysFileCategoryVo}
     */
    List<SysFileCategoryVo> queryList(SysFileCategoryQuery query);

    /**
     * 查询文件分类
     *
     * @param query 查询对象
     * @return
     */
    SysFileCategoryVo queryVoById(SysFileCategoryQuery query);
}
