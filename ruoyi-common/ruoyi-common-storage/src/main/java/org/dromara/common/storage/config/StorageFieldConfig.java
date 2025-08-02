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
     * 增加存储属性
     *
     * @param properties 属性
     * @param platform   平台（一般是配置的id）
     * @param json       配置json
     * @return 存储属性
     */
    FileStorageProperties addStorageProperties(FileStorageProperties properties, String platform, String json);
}
