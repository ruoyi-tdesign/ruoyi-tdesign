package org.dromara.system.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.dromara.system.domain.SysStorageConfig;
import org.dromara.system.domain.bo.SysStorageConfigBo;
import org.dromara.system.domain.query.SysStorageConfigQuery;
import org.dromara.system.domain.vo.SysStorageConfigVo;
import org.dromara.system.mapper.SysStorageConfigMapper;
import org.dromara.system.service.ISysStorageConfigService;

import java.util.Collection;
import java.util.List;

/**
 * 存储配置Service业务层处理
 *
 * @author yixiacoco
 * @date 2025-05-04
 */
@Service
public class SysStorageConfigServiceImpl extends ServiceImpl<SysStorageConfigMapper, SysStorageConfig> implements ISysStorageConfigService {

    /**
     * 查询存储配置
     *
     * @param storageConfigId 主键
     * @return SysStorageConfigVo
     */
    @Override
    public SysStorageConfigVo queryById(Long storageConfigId) {
        return baseMapper.selectVoById(storageConfigId);
    }

    /**
     * 分页查询存储配置列表
     *
     * @param query 查询对象
     * @return 存储配置分页列表
     */
    @Override
    public TableDataInfo<SysStorageConfigVo> queryPageList(SysStorageConfigQuery query) {
        return PageQuery.of(() -> baseMapper.queryList(query));
    }

    /**
     * 查询存储配置列表
     *
     * @param query 查询对象
     * @return 存储配置列表
     */
    @Override
    public List<SysStorageConfigVo> queryList(SysStorageConfigQuery query) {
        return baseMapper.queryList(query);
    }

    /**
     * 新增存储配置
     *
     * @param bo 存储配置新增业务对象
     * @return 是否新增成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SysStorageConfigBo bo) {
        SysStorageConfig add = MapstructUtils.convert(bo, SysStorageConfig.class);
        return save(add);
    }

    /**
     * 修改存储配置
     *
     * @param bo 存储配置编辑业务对象
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SysStorageConfigBo bo) {
        SysStorageConfig update = MapstructUtils.convert(bo, SysStorageConfig.class);
        return updateById(update);
    }

    /**
     * 批量删除存储配置信息
     *
     * @param ids 待删除的主键集合
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        return removeByIds(ids);
    }
}
