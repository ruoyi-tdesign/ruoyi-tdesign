package org.dromara.common.oss.constant;

import org.dromara.common.core.constant.GlobalConstants;

import java.util.Arrays;
import java.util.List;

/**
 * 对象存储常量
 *
 * @author Lion Li
 */
public interface OssConstant {

    /**
     * 默认配置KEY
     */
    String DEFAULT_CONFIG_KEY = GlobalConstants.GLOBAL_REDIS_KEY + "sys_oss:default_config";

    /**
     * OSS规则KEY
     */
    String OSS_RULE_KEY = GlobalConstants.GLOBAL_REDIS_KEY + "sys_oss_rule";

    /**
     * 系统数据ids
     */
    List<Long> SYSTEM_DATA_IDS = Arrays.asList(1L, 2L, 3L, 4L);

    /**
     * https 状态
     */
    String IS_HTTPS = "Y";

    /**
     * 启用s3路径访问风格
     */
    String PATH_STYLE_ACCESS_ENABLED = "1";
}
