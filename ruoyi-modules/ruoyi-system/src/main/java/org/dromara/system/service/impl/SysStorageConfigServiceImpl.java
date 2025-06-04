package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.common.core.enums.NormalDisableEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.storage.balancer.DefaultFileServer;
import org.dromara.common.storage.balancer.FileServer;
import org.dromara.common.storage.config.StorageConfigData;
import org.dromara.common.storage.config.StorageFieldConfig;
import org.dromara.system.domain.SysStorageConfig;
import org.dromara.system.domain.bo.SysStorageConfigBo;
import org.dromara.system.domain.query.SysStorageConfigQuery;
import org.dromara.system.domain.vo.SysStorageConfigVo;
import org.dromara.system.mapper.SysStorageConfigMapper;
import org.dromara.system.service.ISysStorageConfigService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @CacheEvict(cacheNames = CacheNames.SYS_STORAGE_CONFIG)
    public Boolean insertByBo(SysStorageConfigBo bo) {
        validateConfig(bo);
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
    @CacheEvict(cacheNames = CacheNames.SYS_STORAGE_CONFIG)
    public Boolean updateByBo(SysStorageConfigBo bo) {
        validateConfig(bo);
        SysStorageConfig update = MapstructUtils.convert(bo, SysStorageConfig.class);
        return updateById(update);
    }

    private static void validateConfig(SysStorageConfigBo bo) {
        StorageFieldConfig storageConfig = StorageConfigData.getStorageConfig(bo.getPlatform());
        if (storageConfig == null) {
            throw new ServiceException("平台配置不存在！");
        }
    }

    /**
     * 批量删除存储配置信息
     *
     * @param ids 待删除的主键集合
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = CacheNames.SYS_STORAGE_CONFIG)
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        return removeByIds(ids);
    }

    /**
     * 修改状态
     *
     * @param storageConfigId 主键
     * @param status          状态
     */
    @Override
    @CacheEvict(cacheNames = CacheNames.SYS_STORAGE_CONFIG)
    public boolean updateConfigStatus(Long storageConfigId, Integer status) {
        return lambdaUpdate()
            .set(SysStorageConfig::getStatus, status)
            .eq(SysStorageConfig::getStorageConfigId, storageConfigId)
            .update();
    }

    /**
     * 获取缓存列表
     *
     * @return 缓存列表
     */
    @Override
    @Cacheable(cacheNames = CacheNames.SYS_STORAGE_CONFIG)
    public Map<Long, SysStorageConfig> getCacheMap() {
        List<SysStorageConfig> list = lambdaQuery()
            .eq(SysStorageConfig::getStatus, NormalDisableEnum.NORMAL.getCode())
            .list();
        return StreamUtils.toIdentityMap(list, SysStorageConfig::getStorageConfigId);
    }

    /**
     * 获取缓存列表
     *
     * @return 缓存列表
     */
    @Override
    public List<FileServer> getFileServerList() {
        ISysStorageConfigService service = SpringUtils.getBean(ISysStorageConfigService.class);
        Map<Long, SysStorageConfig> cacheMap = service.getCacheMap();
        return cacheMap.values().stream().map(item -> {
            DefaultFileServer fileServer = new DefaultFileServer();
            fileServer.setId(item.getStorageConfigId().toString());
            fileServer.setPlatform(item.getPlatform());
            fileServer.setWeight(item.getWeight());
            fileServer.setProperties(item.getConfigJson());
            return fileServer;
        }).collect(Collectors.toList());
    }
}
