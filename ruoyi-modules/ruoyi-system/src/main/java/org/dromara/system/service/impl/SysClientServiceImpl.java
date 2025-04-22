package org.dromara.system.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.SortQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.SysClient;
import org.dromara.system.domain.bo.SysClientBo;
import org.dromara.system.domain.query.SysClientQuery;
import org.dromara.system.domain.vo.SysClientVo;
import org.dromara.system.mapper.SysClientMapper;
import org.dromara.system.service.ISysClientService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 系统授权Service业务层处理
 *
 * @author Michelle.Chung
 * @date 2023-06-18
 */
@Slf4j
@Service
public class SysClientServiceImpl extends ServiceImpl<SysClientMapper, SysClient> implements ISysClientService {

    /**
     * 查询系统授权
     *
     * @param id 主键
     * @return SysClientVo
     */
    @Override
    public SysClientVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }


    /**
     * 查询客户端管理
     */
    @Cacheable(cacheNames = CacheNames.SYS_CLIENT, key = "#clientId")
    @Override
    public SysClientVo queryByClientId(String clientId) {
        return baseMapper.selectVoOne(new LambdaQueryWrapper<SysClient>().eq(SysClient::getClientId, clientId));
    }

    /**
     * 查询系统授权列表
     *
     * @param query 查询对象
     * @return SysClientVo
     */
    @Override
    public TableDataInfo<SysClientVo> queryPageList(SysClientQuery query) {
        return PageQuery.of(() -> baseMapper.queryList(query));
    }

    /**
     * 查询系统授权列表
     *
     * @param query 查询对象
     * @return SysClientVo
     */
    @Override
    public List<SysClientVo> queryList(SysClientQuery query) {
        return SortQuery.of(() -> baseMapper.queryList(query));
    }

    /**
     * 根据新增业务对象插入系统授权
     *
     * @param bo 系统授权新增业务对象
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SysClientBo bo) {
        checkRepeat(bo);
        SysClient add = MapstructUtils.convert(bo, SysClient.class);
        // 生成clientid
        String clientKey = bo.getClientKey();
        String clientSecret = bo.getClientSecret();
        add.setClientId(SecureUtil.md5(clientKey + clientSecret + System.currentTimeMillis()));
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 根据编辑业务对象修改系统授权
     *
     * @param bo 系统授权编辑业务对象
     * @return Boolean
     */
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, key = "#bo.clientId")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SysClientBo bo) {
        checkRepeat(bo);
        SysClient update = MapstructUtils.convert(bo, SysClient.class);
        update.setGrantType(String.join(",", bo.getGrantTypeList()));
        return updateById(update);
    }

    /**
     * 修改状态
     */
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, key = "#clientId")
    @Override
    public int updateClientStatus(String clientId, String status) {
        return baseMapper.update(null,
            new LambdaUpdateWrapper<SysClient>()
                .set(SysClient::getStatus, status)
                .eq(SysClient::getClientId, clientId));
    }

    /**
     * 校验并批量删除系统授权信息
     *
     * @param ids 主键集合
     * @return Boolean
     */
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, allEntries = true)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        return removeByIds(ids);
    }

    /**
     * 检查重复key
     *
     * @param bo 业务对象
     */
    private void checkRepeat(SysClientBo bo) {
        boolean exists = lambdaQuery().ne(bo.getId() != null, SysClient::getId, bo.getId())
            .eq(SysClient::getClientKey, bo.getClientKey())
            .exists();
        if (exists) {
            throw new ServiceException("已经存在相同的客户端key");
        }
    }
}
