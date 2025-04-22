package org.dromara.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.enums.YesNoEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.service.ConfigService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.CacheUtils;
import org.dromara.common.tenant.annotation.IgnoreTenant;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.system.domain.SysConfig;
import org.dromara.system.domain.bo.SysConfigBo;
import org.dromara.system.domain.vo.SysConfigVo;
import org.dromara.system.mapper.SysConfigMapper;
import org.dromara.system.service.ISysConfigService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 参数配置 服务层实现
 *
 * @author Lion Li
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService, ConfigService {

    @Override
    public TableDataInfo<SysConfigVo> selectPageConfigList(SysConfigBo config) {
        return PageQuery.of(() -> baseMapper.queryList(config));
    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    @DS("master")
    public SysConfigVo selectConfigById(Long configId) {
        return baseMapper.selectVoById(configId);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Cacheable(cacheNames = CacheNames.SYS_CONFIG, key = "#configKey")
    @Override
    public String selectConfigByKey(String configKey) {
        Optional<SysConfig> oneOpt = lambdaQuery()
            .eq(SysConfig::getConfigKey, configKey)
            .select(SysConfig::getConfigId, SysConfig::getConfigValue)
            .eq(SysConfig::getIsGlobal, YesNoEnum.NO.getCodeNum())
            .oneOpt();
        return oneOpt.map(SysConfig::getConfigValue).orElse(StringUtils.EMPTY);
    }

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfigVo> selectConfigList(SysConfigBo config) {
        return baseMapper.queryList(config);
    }

    /**
     * 新增参数配置
     *
     * @param bo 参数配置信息
     * @return 结果
     */
    @Override
    public String insertConfig(SysConfigBo bo) {
        SysConfig config = MapstructUtils.convert(bo, SysConfig.class);
        boolean isGlobal = Objects.equals(YesNoEnum.YES.getCodeNum(), bo.getIsGlobal());
        int row = TenantHelper.ignore(isGlobal, () ->
            baseMapper.insert(config)
        );
        if (row > 0) {
            CacheUtils.put(GlobalConstants.getGlobalKey(isGlobal, CacheNames.SYS_CONFIG), config.getConfigKey(), config.getConfigValue());
            return config.getConfigValue();
        }
        throw new ServiceException("操作失败");
    }

    /**
     * 修改参数配置
     *
     * @param bo 参数配置信息
     * @return 结果
     */
    @Override
    public String updateConfigs(SysConfigBo bo) {
        int row = 0;
        SysConfig config = MapstructUtils.convert(bo, SysConfig.class);
        boolean enabled = Objects.equals(YesNoEnum.YES.getCodeNum(), bo.getIsGlobal());
        if (config.getConfigId() != null) {
            // 更新
            row = TenantHelper.ignore(enabled, () -> {
                SysConfig temp = baseMapper.selectById(config.getConfigId());
                if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey())) {
                    boolean isGlobal = YesNoEnum.YES.getCodeNum().equals(temp.getIsGlobal());
                    CacheUtils.evict(GlobalConstants.getGlobalKey(isGlobal, CacheNames.SYS_CONFIG), temp.getConfigKey());
                }
                return baseMapper.updateById(config);
            });
        } else {
            // 新增
            row = TenantHelper.ignore(enabled,
                () -> baseMapper.update(config, lambdaQuery()
                    .eq(SysConfig::getConfigKey, config.getConfigKey())
                    .getWrapper()));
        }
        if (row > 0) {
            boolean isGlobal = YesNoEnum.YES.getCodeNum().equals(config.getIsGlobal());
            CacheUtils.evict(GlobalConstants.getGlobalKey(isGlobal, CacheNames.SYS_CONFIG), config.getConfigKey());
            return config.getConfigValue();
        }
        throw new ServiceException("操作失败");
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfigByIds(Long[] configIds) {
        for (Long configId : configIds) {
            SysConfig config = baseMapper.selectById(configId);
            if (StringUtils.equals(YesNoEnum.YES.getCodeStr(), config.getConfigType())) {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            boolean isGlobal = YesNoEnum.YES.getCodeNum().equals(config.getIsGlobal());
            CacheUtils.evict(GlobalConstants.getGlobalKey(isGlobal, CacheNames.SYS_CONFIG), config.getConfigKey());
        }
        baseMapper.deleteByIds(Arrays.asList(configIds));
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        CacheUtils.clear(CacheNames.SYS_CONFIG);
        CacheUtils.clear(GlobalConstants.getGlobalKey(CacheNames.SYS_CONFIG));
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 是否存在
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfigBo config) {
        boolean isGlobal = YesNoEnum.YES.getCodeNum().equals(config.getIsGlobal());
        return TenantHelper.ignore(isGlobal, () ->
            lambdaQuery()
                .ne(config.getConfigId() != null, SysConfig::getConfigId, config.getConfigId())
                .eq(SysConfig::getConfigKey, config.getConfigKey())
                .exists()
        );
    }

    /**
     * 查询多个参数
     *
     * @param keys
     * @return
     */
    @Override
    public Map<String, SysConfigVo> queryConfigs(List<String> keys) {
        List<SysConfig> list = lambdaQuery().in(SysConfig::getConfigKey, keys).list();
        return StreamUtils.toMap(list, SysConfig::getConfigKey, sysConfig -> MapstructUtils.convert(sysConfig, SysConfigVo.class));
    }

    /**
     * 更新配置
     *
     * @param configs
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfigs(List<SysConfigBo> configs) {
        // 全局配置
        List<SysConfigBo> globalConfigBos = StreamUtils.filter(configs, sysConfig -> Objects.equals(1, sysConfig.getIsGlobal()));
        // 租户配置
        List<SysConfigBo> tenantConfigBos = StreamUtils.filter(configs, sysConfig -> Objects.equals(0, sysConfig.getIsGlobal()));

        // 更新租户配置
        updateConfigs(tenantConfigBos, false);
        // 更新全局配置
        TenantHelper.ignore(() -> {
            updateConfigs(globalConfigBos, true);
        });
    }

    /**
     * 更新配置
     *
     * @param isGlobal   是否全局配置
     * @param configsMap 更新的配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMaps(Integer isGlobal, Map<String, String> configsMap) {
        if (CollUtil.isEmpty(configsMap)) {
            return;
        }
        boolean enabled = Objects.equals(YesNoEnum.YES.getCodeNum(), isGlobal);
        TenantHelper.ignore(enabled, () -> {
            Set<String> keySet = configsMap.keySet();
            List<SysConfig> list = lambdaQuery()
                .in(SysConfig::getConfigKey, keySet)
                .eq(SysConfig::getIsGlobal, isGlobal)
                .list();
            // 更新
            for (SysConfig config : list) {
                String value = configsMap.remove(config.getConfigKey());
                config.setConfigValue(value);
            }
            if (CollUtil.isNotEmpty(list)) {
                updateBatchById(list);
                updateCache(enabled, list);
            }
            // 新增
            List<SysConfig> configs = configsMap.entrySet().stream().map(entry -> {
                SysConfig config = new SysConfig();
                config.setConfigKey(entry.getKey());
                config.setConfigValue(entry.getValue());
                config.setConfigType(YesNoEnum.NO.getCodeStr());
                config.setIsGlobal(isGlobal);
                return config;
            }).toList();
            if (CollUtil.isNotEmpty(configs)) {
                saveBatch(configs);
                updateCache(enabled, configs);
            }
        });
    }

    /**
     * 更新配置
     *
     * @param configs  配置
     * @param isGlobal 是否是全局配置
     */
    private void updateConfigs(List<SysConfigBo> configs, boolean isGlobal) {
        if (CollUtil.isEmpty(configs)) {
            return;
        }
        List<SysConfig> list = lambdaQuery()
            .in(SysConfig::getConfigKey, StreamUtils.toSet(configs, SysConfigBo::getConfigKey))
            .eq(SysConfig::getIsGlobal, isGlobal ? YesNoEnum.YES.getCodeNum() : YesNoEnum.NO.getCodeNum())
            .list();
        Map<String, SysConfig> configMap = StreamUtils.toIdentityMap(list, SysConfig::getConfigKey);
        List<SysConfig> configList = new ArrayList<>();
        for (SysConfigBo configBo : configs) {
            SysConfig config = configMap.get(configBo.getConfigKey());
            if (config == null) {
                config = MapstructUtils.convert(configBo, SysConfig.class);
            } else {
                Long configId = config.getConfigId();
                MapstructUtils.convert(configBo, config);
                config.setConfigId(configId);
            }
            configList.add(config);
        }
        // 更新租户配置
        saveOrUpdateBatch(configList);
        // 更新缓存
        updateCache(isGlobal, configList);
    }

    /**
     * 更新缓存
     *
     * @param isGlobal   是否全局缓存
     * @param configList 缓存列表
     */
    private static void updateCache(boolean isGlobal, List<SysConfig> configList) {
        for (SysConfig sysConfig : configList) {
            String cacheNames = GlobalConstants.getGlobalKey(isGlobal, CacheNames.SYS_CONFIG);
            CacheUtils.put(cacheNames, sysConfig.getConfigKey(), sysConfig.getConfigValue());
        }
    }

    /**
     * 根据参数 key 获取参数值
     *
     * @param configKey 参数 key
     * @return 参数值
     */
    @Override
    @Cacheable(cacheNames = CacheNames.SYS_CONFIG, key = "#configKey")
    public String getConfigValue(String configKey) {
        return selectConfigByKey(configKey);
    }

    /**
     * 根据参数 key 获取全局参数值
     *
     * @param configKey 参数 key
     * @return 参数值
     */
    @Override
    @IgnoreTenant
    @Cacheable(cacheNames = GlobalConstants.GLOBAL_REDIS_KEY + CacheNames.SYS_CONFIG, key = "#configKey")
    public String getGlobalConfigValue(String configKey) {
        Optional<SysConfig> oneOpt = lambdaQuery()
            .eq(SysConfig::getConfigKey, configKey)
            .select(SysConfig::getConfigId, SysConfig::getConfigValue)
            .eq(SysConfig::getIsGlobal, YesNoEnum.YES.getCodeNum())
            .oneOpt();
        if (oneOpt.isPresent()) {
            return oneOpt.get().getConfigValue();
        }
        return StringUtils.EMPTY;
    }
}
