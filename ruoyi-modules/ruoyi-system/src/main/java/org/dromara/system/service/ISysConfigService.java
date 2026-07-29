package org.dromara.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.SysConfig;
import org.dromara.system.domain.bo.SysConfigBo;
import org.dromara.system.domain.vo.SysConfigVo;

import java.util.List;
import java.util.Map;

/**
 * 参数配置 服务层
 *
 * @author Lion Li
 */
public interface ISysConfigService extends IService<SysConfig> {

    /**
     * 获取参数配置列表
     *
     * @param config 参数配置
     * @return 参数配置结果
     */
    TableDataInfo<SysConfigVo> selectPageConfigList(SysConfigBo config);

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    SysConfigVo selectConfigById(Long configId);

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    String selectConfigByKey(String configKey);

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    List<SysConfigVo> selectConfigList(SysConfigBo config);

    /**
     * 新增参数配置
     *
     * @param bo 参数配置信息
     * @return 结果
     */
    String insertConfig(SysConfigBo bo);

    /**
     * 修改参数配置
     *
     * @param bo 参数配置信息
     * @return 结果
     */
    String updateConfigs(SysConfigBo bo);

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    void deleteConfigByIds(List<Long> configIds);

    /**
     * 重置参数缓存数据
     */
    void resetConfigCache();

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数信息
     * @return 结果
     */
    boolean checkConfigKeyUnique(SysConfigBo config);

    /**
     * 查询多个参数
     *
     * @param keys
     * @return
     */
    Map<String, SysConfigVo> queryConfigs(List<String> keys);

    /**
     * 更新配置
     *
     * @param configs
     */
    void updateConfigs(List<SysConfigBo> configs);

    /**
     * 更新配置
     *
     * @param isGlobal   是否全局配置
     * @param configsMap 更新的配置
     */
    void updateMaps(Integer isGlobal, Map<String, String> configsMap);
}
