package org.dromara.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.enums.CommonStatusEnum;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ip.AddressUtils;
import org.dromara.common.log.event.LogininforEvent;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.system.domain.SysLogininfor;
import org.dromara.system.domain.bo.SysLogininforBo;
import org.dromara.system.domain.query.SysLogininforQuery;
import org.dromara.system.domain.vo.SysClientVo;
import org.dromara.system.domain.vo.SysLogininforVo;
import org.dromara.system.mapper.SysLogininforMapper;
import org.dromara.system.service.ISysClientService;
import org.dromara.system.service.ISysLogininforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author Lion Li
 */
@Slf4j
@Service
public class SysLogininforServiceImpl extends ServiceImpl<SysLogininforMapper, SysLogininfor> implements ISysLogininforService {

    @Autowired
    private ISysClientService clientService;

    /**
     * 查询系统访问记录
     *
     * @param infoId 主键
     * @return SysLogininforVo
     */
    @Override
    public SysLogininforVo queryById(Long infoId) {
        return baseMapper.selectVoById(infoId);
    }

    /**
     * 记录登录信息
     *
     * @param logininforEvent 登录事件
     */
    @Async
    @EventListener
    public void recordLogininfor(LogininforEvent logininforEvent) {
        String ip = logininforEvent.getIp();
        String clientId = logininforEvent.getClientId();
        // 客户端信息
        SysClientVo client = null;
        if (StringUtils.isNotBlank(clientId)) {
            client = clientService.queryByClientId(clientId);
        }

        String address = AddressUtils.getRealAddressByIP(ip);
        StringBuilder s = new StringBuilder();
        s.append(getBlock(ip));
        s.append(address);
        s.append(getBlock(logininforEvent.getUsername()));
        s.append(getBlock(logininforEvent.getStatus()));
        s.append(getBlock(logininforEvent.getMessage()));
        // 打印信息到日志
        log.info(s.toString(), logininforEvent.getArgs());
        // 封装对象
        SysLogininforBo logininfor = new SysLogininforBo();
        logininfor.setTenantId(logininforEvent.getTenantId());
        logininfor.setUserId(logininforEvent.getUserId());
        logininfor.setUserName(logininforEvent.getUsername());
        logininfor.setIpaddr(ip);
        logininfor.setLoginLocation(address);
        logininfor.setBrowser(logininforEvent.getBrowser());
        logininfor.setOs(logininforEvent.getOs());
        logininfor.setMsg(logininforEvent.getMessage());
        if (ObjectUtil.isNotNull(client)) {
            logininfor.setClientKey(client.getClientKey());
            logininfor.setDeviceType(client.getDeviceType());
        }
        // 日志状态
        if (StringUtils.equalsAny(logininforEvent.getStatus(), Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            logininfor.setStatus(CommonStatusEnum.SUCCESS.getCode());
        } else if (Constants.LOGIN_FAIL.equals(logininforEvent.getStatus())) {
            logininfor.setStatus(CommonStatusEnum.FAIL.getCode());
        }
        // 插入数据
        insertLogininfor(logininfor);
    }

    private String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg + "]";
    }

    /**
     * 查询系统访问记录列表
     *
     * @param query 查询对象
     * @return SysLogininforVo
     */
    @Override
    public TableDataInfo<SysLogininforVo> queryPageList(SysLogininforQuery query) {
        return PageQuery.of(() -> baseMapper.queryList(query));
    }

    /**
     * 查询系统登录日志集合
     *
     * @param query 查询对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininforVo> queryList(SysLogininforQuery query) {
        return baseMapper.queryList(query);
    }

    /**
     * 新增系统登录日志
     *
     * @param bo 访问日志对象
     */
    @Override
    public void insertLogininfor(SysLogininforBo bo) {
        SysLogininfor logininfor = MapstructUtils.convert(bo, SysLogininfor.class);
        logininfor.setLoginTime(new Date());
        TenantHelper.ignore(s -> {
            baseMapper.insert(logininfor);
        });
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLogininforByIds(Long[] infoIds) {
        return baseMapper.deleteByIds(Arrays.asList(infoIds));
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor() {
        baseMapper.delete(new LambdaQueryWrapper<>());
    }
}
