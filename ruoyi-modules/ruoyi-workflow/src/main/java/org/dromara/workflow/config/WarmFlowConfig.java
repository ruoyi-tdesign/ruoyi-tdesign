package org.dromara.workflow.config;


import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.dromara.warm.flow.core.config.WarmFlow;
import org.dromara.warm.flow.core.utils.IdUtils;
import org.dromara.warm.plugin.modes.sb.config.BeanConfig;
import org.dromara.workflow.common.ConditionalOnEnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * warmFlow配置
 *
 * @author may
 */
@ConditionalOnEnable
@Configuration
public class WarmFlowConfig extends BeanConfig {

    @Autowired
    private IdentifierGenerator identifierGenerator;

    @Override
    public void after(WarmFlow flowConfig) {
        // 设置Mybatis-Plus默认主键生成器
        IdUtils.setInstanceNative(() -> identifierGenerator.nextId(null).longValue());
    }

}

