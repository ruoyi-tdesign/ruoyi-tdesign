package org.dromara.common.tenant.handle;

import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.redis.handler.KeyPrefixHandler;
import org.dromara.common.tenant.helper.TenantHelper;

/**
 * 多租户redis缓存key前缀处理
 *
 * @author Lion Li
 */
public class TenantKeyPrefixHandler extends KeyPrefixHandler {

    public static final String PREFIX = "tenant:";

    public TenantKeyPrefixHandler(String keyPrefix) {
        super(keyPrefix);
    }

    /**
     * 增加前缀
     */
    @Override
    public String map(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        if (StringUtils.startsWith(name, GlobalConstants.GLOBAL_REDIS_KEY)) {
            return super.map(name);
        }
        // 忽略租户缓存时 https://github.com/redisson/redisson/issues/5727
        if (TenantHelper.isIgnoreCache()) {
            return super.map(name);
        }
        String tenantId = TenantHelper.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            throw new ServiceException("未能识别到有效tenantId");
        }
        if (StringUtils.startsWith(name, PREFIX + tenantId)) {
            // 如果存在则直接返回
            return super.map(name);
        }
        return super.map(PREFIX + tenantId + ":" + name);
    }

    /**
     * 去除前缀
     */
    @Override
    public String unmap(String name) {
        String unmap = super.unmap(name);
        if (StringUtils.isBlank(unmap)) {
            return null;
        }
        if (StringUtils.startsWith(name, GlobalConstants.GLOBAL_REDIS_KEY) || TenantHelper.isIgnoreCache()) {
            return unmap;
        }
        // 报错问题 https://github.com/redisson/redisson/issues/5727
        String tenantId = TenantHelper.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            // 程序员应该处理特殊情况，例如getKeys方法不在主线程，所以会进入此处
            return unmap;
//            throw new ServiceException("未能识别到有效tenantId");
        }
        if (StringUtils.startsWith(unmap, PREFIX + tenantId)) {
            // 如果存在则删除
            return unmap.substring((PREFIX + tenantId + ":").length());
        }
        return unmap;
    }

}
