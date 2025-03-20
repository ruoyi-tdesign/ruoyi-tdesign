package org.dromara.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.SysClient;
import org.dromara.system.domain.bo.SysClientBo;
import org.dromara.system.domain.query.SysClientQuery;
import org.dromara.system.domain.vo.SysClientVo;

import java.util.Collection;
import java.util.List;

/**
 * 系统授权Service接口
 *
 * @author Michelle.Chung
 * @date 2023-06-18
 */
public interface ISysClientService extends IService<SysClient> {

    /**
     * 查询系统授权
     *
     * @param id 主键
     * @return SysClientVo
     */
    SysClientVo queryById(Long id);

    /**
     * 查询客户端信息基于客户端id
     */
    SysClientVo queryByClientId(String clientId);

    /**
     * 查询系统授权列表
     *
     * @param query 查询对象
     * @return SysClientVo
     */
    TableDataInfo<SysClientVo> queryPageList(SysClientQuery query);

    /**
     * 查询系统授权列表
     *
     * @param query 查询对象
     * @return SysClientVo
     */
    List<SysClientVo> queryList(SysClientQuery query);

    /**
     * 新增系统授权
     *
     * @param bo 系统授权新增业务对象
     * @return Boolean
     */
    Boolean insertByBo(SysClientBo bo);

    /**
     * 修改系统授权
     *
     * @param bo 系统授权编辑业务对象
     * @return Boolean
     */
    Boolean updateByBo(SysClientBo bo);

    /**
     * 修改状态
     */
    int updateClientStatus(String clientId, String status);

    /**
     * 校验并批量删除系统授权信息
     *
     * @param ids 主键集合
     * @return Boolean
     */
    Boolean deleteWithValidByIds(Collection<Long> ids);

}
