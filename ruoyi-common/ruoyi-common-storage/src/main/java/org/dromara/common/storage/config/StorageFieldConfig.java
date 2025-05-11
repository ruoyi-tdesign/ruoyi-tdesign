package org.dromara.common.storage.config;

import org.dromara.x.file.storage.core.FileStorageProperties;

/**
 * 存储字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
public interface StorageFieldConfig {

    /**
     * 构建存储属性
     *
     * @param json 配置json
     * @return 存储属性
     */
    FileStorageProperties buildStorageProperties(String json);
}
