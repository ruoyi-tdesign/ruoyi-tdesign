package org.dromara.system.service;

import org.dromara.system.domain.SysStorageConfig;
import org.dromara.system.domain.bo.SysStorageConfigBo;
import org.dromara.system.domain.query.SysStorageConfigQuery;
import org.dromara.system.domain.vo.SysStorageConfigVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

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
}
