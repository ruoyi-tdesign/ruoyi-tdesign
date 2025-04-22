package org.dromara.common.core.constant;

/**
 * 缓存的key 常量
 *
 * @author Lion Li
 */
public interface CacheConstants {

    /**
     * 在线用户 redis key
     */
    String ONLINE_TOKEN_KEY = "online_tokens:";

    /**
     * 参数管理 cache key
     */
    String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    String SYS_DICT_KEY = GlobalConstants.GLOBAL_REDIS_KEY + "sys_dict:";

    /**
     * 敏感词管理 cache key
     */
    String SYS_SENSITIVE_WORD = GlobalConstants.GLOBAL_REDIS_KEY + "sys_sensitive_word";

    /**
     * 字典类型管理 cache key
     */
    String SYS_ALL_DICT_TYPE_KEY = GlobalConstants.GLOBAL_REDIS_KEY + "sys_all_dict_type";

}
