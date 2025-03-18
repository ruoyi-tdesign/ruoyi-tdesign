package org.dromara.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpLogic;
import cn.hutool.core.bean.BeanUtil;
import org.dromara.common.core.constant.CacheConstants;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.domain.dto.UserOnlineDTO;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.satoken.utils.MultipleStpUtil;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.SysUserOnline;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 在线用户监控
 *
 * @author Lion Li
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController {

    /**
     * 获取在线用户监控列表
     *
     * @param ipaddr   IP地址
     * @param userName 用户名
     */
    @SaCheckPermission("monitor:online:list")
    @GetMapping("/list")
    public TableDataInfo<SysUserOnline> list(String ipaddr, String userName) {
        // 获取所有未过期的 token
        List<String> keys = MultipleStpUtil.SYSTEM.searchTokenValue("", 0, -1, false);
        List<UserOnlineDTO> userOnlineDTOList = new ArrayList<>();
        for (String key : keys) {
            String token = StringUtils.substringAfterLast(key, ":");
            // 如果已经过期则跳过
            if (MultipleStpUtil.SYSTEM.getTokenActiveTimeoutByToken(token) < -1) {
                continue;
            }
            // 开启租户 & 超级管理员 & 不使用动态租户状态下，查看所有租户在线情况
            if (TenantHelper.isEnable() && LoginHelper.isSuperAdmin() && !TenantHelper.isDynamic()) {
                String tenantId = RedisUtils.getObject(GlobalConstants.ONLINE_TOKEN_TENANT_ID_KEY + token);
                final List<UserOnlineDTO> finalUserOnlineDTOList = userOnlineDTOList;
                TenantHelper.dynamicTenant(tenantId, () -> {
                    finalUserOnlineDTOList.add(RedisUtils.getObject(CacheConstants.ONLINE_TOKEN_KEY + token));
                });
            } else {
                userOnlineDTOList.add(RedisUtils.getObject(CacheConstants.ONLINE_TOKEN_KEY + token));
            }
        }
        if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
            userOnlineDTOList = StreamUtils.filter(userOnlineDTOList, userOnline ->
                StringUtils.equals(ipaddr, userOnline.getIpaddr()) &&
                    StringUtils.equals(userName, userOnline.getUserName())
            );
        } else if (StringUtils.isNotEmpty(ipaddr)) {
            userOnlineDTOList = StreamUtils.filter(userOnlineDTOList, userOnline ->
                StringUtils.equals(ipaddr, userOnline.getIpaddr())
            );
        } else if (StringUtils.isNotEmpty(userName)) {
            userOnlineDTOList = StreamUtils.filter(userOnlineDTOList, userOnline ->
                StringUtils.equals(userName, userOnline.getUserName())
            );
        }
        Collections.reverse(userOnlineDTOList);
        userOnlineDTOList.removeAll(Collections.singleton(null));
        List<SysUserOnline> userOnlineList = BeanUtil.copyToList(userOnlineDTOList, SysUserOnline.class);
        return TableDataInfo.build(userOnlineList);
    }

    /**
     * 强退用户
     *
     * @param tokenId token值
     */
    @SaCheckPermission("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public R<Void> forceLogout(@PathVariable String tokenId) {
        try {
            MultipleStpUtil.SYSTEM.kickoutByTokenValue(tokenId);
        } catch (NotLoginException ignored) {
        }
        return R.ok();
    }

    /**
     * 获取当前用户登录在线设备
     */
    @GetMapping()
    public TableDataInfo<SysUserOnline> getInfo() {
        StpLogic logic = MultipleStpUtil.SYSTEM;
        // 获取指定账号 id 的 token 集合
        List<String> tokenIds = logic.getTokenValueListByLoginId(logic.getLoginIdAsString());
        List<UserOnlineDTO> userOnlineDTOList = tokenIds.stream()
            .filter(token -> logic.getTokenActiveTimeoutByToken(token) >= -1)
            .map(token -> {
                return (UserOnlineDTO) RedisUtils.getObject(CacheConstants.ONLINE_TOKEN_KEY + token);
            })
            .collect(Collectors.toList());
        //复制和处理 SysUserOnline 对象列表
        Collections.reverse(userOnlineDTOList);
        userOnlineDTOList.removeAll(Collections.singleton(null));
        List<SysUserOnline> userOnlineList = BeanUtil.copyToList(userOnlineDTOList, SysUserOnline.class);
        return TableDataInfo.build(userOnlineList);
    }

    /**
     * 强退当前在线设备
     *
     * @param tokenId token值
     */
    @Log(title = "在线设备", businessType = BusinessType.FORCE)
    @DeleteMapping("/myself/{tokenId}")
    public R<Void> remove(@PathVariable("tokenId") String tokenId) {
        StpLogic logic = MultipleStpUtil.SYSTEM;
        try {
            // 获取指定账号 id 的 token 集合
            List<String> keys = logic.getTokenValueListByLoginId(logic.getLoginIdAsString());
            keys.stream()
                .filter(key -> key.equals(tokenId))
                .findFirst()
                .ifPresent(key -> logic.kickoutByTokenValue(tokenId));
        } catch (NotLoginException ignored) {
        }
        return R.ok();
    }

}
