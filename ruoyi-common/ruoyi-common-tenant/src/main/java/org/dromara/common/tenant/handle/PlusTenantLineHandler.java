package org.dromara.common.tenant.handle;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.AllArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.common.tenant.properties.TenantProperties;

import java.util.Set;

/**
 * 自定义租户处理器
 *
 * @author Lion Li
 */
@AllArgsConstructor
public class PlusTenantLineHandler implements TenantLineHandler {

    private final TenantProperties tenantProperties;

    @Override
    public Expression getTenantId() {
        String tenantId = TenantHelper.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            throw new ServiceException("未能识别到有效tenantId");
        }
        // 返回固定租户
        return new StringValue(tenantId);
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // 不需要过滤租户的表
        Set<String> excludes = tenantProperties.getExcludes();
        if (excludes != null && !excludes.isEmpty()) {
            for (String exclude : excludes) {
                if (StringUtils.equalsAnyIgnoreCase(tableName, exclude)) {
                    return true;
                }
            }
        }
        // 非业务表
        Set<String> genTable = Set.of(
            "gen_table",
            "gen_table_column"
        );
        return genTable.contains(tableName.toLowerCase());
    }

}
