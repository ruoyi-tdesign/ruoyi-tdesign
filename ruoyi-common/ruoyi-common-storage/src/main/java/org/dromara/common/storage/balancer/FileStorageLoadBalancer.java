package org.dromara.common.storage.balancer;

import lombok.Setter;
import org.dromara.common.storage.config.StorageConfigData;
import org.dromara.common.storage.config.StorageFieldConfig;
import org.dromara.common.storage.expcetion.StorageServiceException;
import org.dromara.x.file.storage.core.FileStorageProperties;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.FileStorageServiceBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件存储负载均衡器
 *
 * @author hexm
 * @date 2025/5/20
 */
public class FileStorageLoadBalancer {

    @Setter
    private LoadBalancingAlgorithm algorithm;

    private final List<FileServer> servers;

    public FileStorageLoadBalancer(LoadBalancingAlgorithm algorithm) {
        this.algorithm = algorithm;
        servers = new ArrayList<>();
    }

    public void addServer(FileServer server) {
        servers.add(server);

    }

    public void removeServer(FileServer server) {
        servers.remove(server);
    }

    public FileStorageService getService() {
        FileServer server = algorithm.selectServer(servers);
        StorageFieldConfig config = StorageConfigData.getStorageConfig(server.getPlatform());
        if (config == null) {
            throw new StorageServiceException("平台配置不存在！");
        }
        FileStorageProperties properties = new FileStorageProperties();
        properties.setDefaultPlatform(server.getId());
        config.addStorageProperties(properties, server.getId(), server.getProperties());
        return FileStorageServiceBuilder.create(properties).useDefault().build();
    }

}
