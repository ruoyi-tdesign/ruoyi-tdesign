package org.dromara.common.tenant.inner;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;

/**
 * 兼容分页后缀的多租户插件
 *
 * @author hexm
 * @date 2024/4/18
 */
public class CompatibleTenantLineInnerInterceptor extends TenantLineInnerInterceptor {

    private static final String SUFFIX = "_COUNT";

    public CompatibleTenantLineInnerInterceptor() {
    }

    public CompatibleTenantLineInnerInterceptor(TenantLineHandler tenantLineHandler) {
        super(tenantLineHandler);
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        String effectiveMsId = ms.getId();
        if (ms.getId().endsWith(SUFFIX)) {
            effectiveMsId = ms.getId().substring(0, ms.getId().length() - SUFFIX.length());
        }
        if (InterceptorIgnoreHelper.willIgnoreTenantLine(effectiveMsId)) {
            return;
        }
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        mpBs.sql(parserSingle(mpBs.sql(), null));
    }
}
