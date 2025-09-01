package org.dromara.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.storage.balancer.FileServer;
import org.dromara.system.domain.SysStorageConfig;
import org.dromara.system.domain.bo.SysStorageConfigBo;
import org.dromara.system.domain.query.SysStorageConfigQuery;
import org.dromara.system.domain.vo.SysStorageConfigVo;
import org.dromara.x.file.storage.core.FileStorageService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 存储配置Service接口
 *
 * @author yixiacoco
 * @date 2025-05-04
 */
public interface ISysStorageConfigService extends IService<SysStorageConfig> {

    /**
     * 查询存储配置
     *
     * @param storageConfigId 主键
     * @return SysStorageConfigVo
     */
    SysStorageConfigVo queryById(Long storageConfigId);

    /**
     * 分页查询存储配置列表
     *
     * @param query 查询对象
     * @return 存储配置分页列表
     */
    TableDataInfo<SysStorageConfigVo> queryPageList(SysStorageConfigQuery query);

    /**
     * 查询存储配置列表
     *
     * @param query 查询对象
     * @return 存储配置列表
     */
    List<SysStorageConfigVo> queryList(SysStorageConfigQuery query);

    /**
     * 新增存储配置
     *
     * @param bo 存储配置新增业务对象
     * @return 是否新增成功
     */
    Boolean insertByBo(SysStorageConfigBo bo);

    /**
     * 修改存储配置
     *
     * @param bo 存储配置编辑业务对象
     * @return 是否修改成功
     */
    Boolean updateByBo(SysStorageConfigBo bo);

    /**
     * 批量删除存储配置信息
     *
     * @param ids 待删除的主键集合
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids);

    /**
     * 修改状态
     *
     * @param storageConfigId 主键
     * @param status          状态
     */
    boolean updateConfigStatus(Long storageConfigId, Integer status);

     /**
     * 获取缓存列表
     *
     * @return 缓存列表
     */
    Map<String, SysStorageConfig> getCacheMap();

    /**
     * 获取缓存列表
     *
     * @return 缓存列表
     */
    List<FileServer> getFileServerList();

    /**
     * 获取文件存储服务
     *
     * @return 文件存储服务
     */
    FileStorageService getFileStorageService();

    /**
     * 获取文件存储服务
     *
     * @param config 配置
     * @return 文件存储服务
     */
    FileStorageService getFileStorageService(SysStorageConfig config);

    /**
     * 获取文件存储服务
     *
     * @param id id
     * @return 文件存储服务
     */
    FileStorageService getFileStorageService(Long id);
}
