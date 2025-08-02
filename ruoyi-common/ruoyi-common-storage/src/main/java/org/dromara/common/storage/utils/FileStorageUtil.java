package org.dromara.common.storage.utils;

import org.dromara.common.storage.balancer.FileServer;
import org.dromara.common.storage.config.StorageConfigData;
import org.dromara.common.storage.config.StorageFieldConfig;
import org.dromara.common.storage.expcetion.StorageServiceException;
import org.dromara.common.storage.wrapper.MultipartFileWrapperAdapter;
import org.dromara.x.file.storage.core.FileStorageProperties;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.FileStorageServiceBuilder;
import org.dromara.x.file.storage.core.platform.FileStorage;

/**
 * 文件存储工具类
 *
 * @author hexm
 * @date 2025/6/1
 */
public class FileStorageUtil {

    /**
     * 根据文件服务器信息获取文件存储服务
     *
     * @param server 文件服务器信息
     * @return 文件存储服务
     */
    public static FileStorageService getFileStorageService(FileServer server) {
        StorageFieldConfig config = StorageConfigData.getStorageConfig(server.getPlatform());
        if (config == null) {
            throw new StorageServiceException("平台配置不存在！");
        }
        FileStorageProperties properties = new FileStorageProperties();
        properties.setDefaultPlatform(server.getId());
        config.addStorageProperties(properties, server.getId(), server.getProperties());
        return FileStorageServiceBuilder
            .create(properties)
            .useDefault()
            .addFileWrapperAdapter(new MultipartFileWrapperAdapter())
            .build();
    }

    /**
     * 释放文件存储服务
     *
     * @param service 文件存储服务
     */
    public static void serviceRecycle(FileStorageService service) {
        for (FileStorage storage : service.getFileStorageList()) {
            storage.close();
        }
    }
}
