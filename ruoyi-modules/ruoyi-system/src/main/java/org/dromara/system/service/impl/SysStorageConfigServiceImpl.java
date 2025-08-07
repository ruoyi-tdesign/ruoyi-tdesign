package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.common.core.constant.CacheConstants;
import org.dromara.common.core.enums.NormalDisableEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisLockUtil;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.storage.balancer.DefaultFileServer;
import org.dromara.common.storage.balancer.FileServer;
import org.dromara.common.storage.balancer.FileStorageLoadBalancer;
import org.dromara.common.storage.balancer.RedisRoundRobinAlgorithm;
import org.dromara.common.storage.config.StorageConfigData;
import org.dromara.common.storage.config.StorageFieldConfig;
import org.dromara.common.storage.utils.FileStorageUtil;
import org.dromara.system.domain.SysStorageConfig;
import org.dromara.system.domain.bo.SysStorageConfigBo;
import org.dromara.system.domain.query.SysStorageConfigQuery;
import org.dromara.system.domain.vo.SysStorageConfigVo;
import org.dromara.system.mapper.SysStorageConfigMapper;
import org.dromara.system.service.ISysStorageConfigService;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 存储配置Service业务层处理
 *
 * @author yixiacoco
 * @date 2025-05-04
 */
@Service
public class SysStorageConfigServiceImpl extends ServiceImpl<SysStorageConfigMapper, SysStorageConfig> implements ISysStorageConfigService {

    @Autowired
    private SysFileRecorder fileRecorder;

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
        validateConfig(bo);
        RedisUtils.deleteObject(CacheConstants.SYS_STORAGE_CONFIG);
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
        validateConfig(bo);
        RedisUtils.deleteObject(CacheConstants.SYS_STORAGE_CONFIG);
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
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        RedisUtils.deleteObject(CacheConstants.SYS_STORAGE_CONFIG);
        return removeByIds(ids);
    }

    /**
     * 修改状态
     *
     * @param storageConfigId 主键
     * @param status          状态
     */
    @Override
    public boolean updateConfigStatus(Long storageConfigId, Integer status) {
        RedisUtils.deleteObject(CacheConstants.SYS_STORAGE_CONFIG);
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
    public Map<String, SysStorageConfig> getCacheMap() {
        return RedisLockUtil.getOrSave(CacheConstants.SYS_STORAGE_CONFIG, () -> {
            List<SysStorageConfig> list = lambdaQuery().list();
            return StreamUtils.toIdentityMap(list, config -> config.getStorageConfigId().toString());
        });
    }

    /**
     * 获取缓存列表
     *
     * @return 缓存列表
     */
    @Override
    public List<FileServer> getFileServerList() {
        Map<String, SysStorageConfig> cacheMap = getCacheMap();
        return cacheMap.values().stream()
            .filter(config -> Objects.equals(config.getStatus(), NormalDisableEnum.NORMAL.getCodeNum()))
            .map(item -> {
                DefaultFileServer fileServer = new DefaultFileServer();
                fileServer.setId(item.getStorageConfigId().toString());
                fileServer.setPlatform(item.getPlatform());
                fileServer.setWeight(item.getWeight());
                fileServer.setProperties(item.getConfigJson());
                return fileServer;
            }).collect(Collectors.toList());
    }

    /**
     * 获取文件存储服务
     *
     * @return 文件存储服务
     */
    @Override
    public FileStorageService getFileStorageService() {
        List<FileServer> servers = getFileServerList();
        FileStorageLoadBalancer balancer = new FileStorageLoadBalancer(new RedisRoundRobinAlgorithm(), servers);
        balancer.setFileRecorder(fileRecorder);
        return balancer.getService();
    }

    /**
     * 获取文件存储服务
     *
     * @param config 配置
     * @return 文件存储服务
     */
    @Override
    public FileStorageService getFileStorageService(SysStorageConfig config) {
        if (config == null) {
            return null;
        }
        DefaultFileServer fileServer = new DefaultFileServer();
        fileServer.setId(config.getStorageConfigId().toString());
        fileServer.setPlatform(config.getPlatform());
        fileServer.setWeight(config.getWeight());
        fileServer.setProperties(config.getConfigJson());
        return FileStorageUtil.getFileStorageService(fileServer);
    }

    /**
     * 获取文件存储服务
     *
     * @param id id
     * @return 文件存储服务
     */
    @Override
    public FileStorageService getFileStorageService(Long id) {
        SysStorageConfig config = getCacheMap().get(id.toString());
        return getFileStorageService(config);
    }
}
