package org.dromara.common.storage.balancer;

import lombok.Setter;
import org.dromara.common.storage.utils.FileStorageUtil;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.recorder.FileRecorder;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件存储负载均衡器
 *
 * @author hexm
 * @date 2025/5/20
 */
public class FileStorageLoadBalancer {

    /**
     * 负载均衡算法
     */
    @Setter
    private LoadBalancingAlgorithm algorithm;

    /**
     * 配置列表
     */
    private final List<FileServer> servers;

    /**
     * 持久化记录器
     */
    @Setter
    private FileRecorder fileRecorder;

    public FileStorageLoadBalancer(LoadBalancingAlgorithm algorithm) {
        this.algorithm = algorithm;
        servers = new ArrayList<>();
    }

    public FileStorageLoadBalancer(LoadBalancingAlgorithm algorithm, List<FileServer> servers) {
        this.servers = servers;
        this.algorithm = algorithm;
    }

    public void addServer(FileServer server) {
        servers.add(server);

    }

    public void removeServer(FileServer server) {
        servers.remove(server);
    }

    public FileStorageService getService() {
        FileServer server = algorithm.selectServer(servers);
        FileStorageService service = FileStorageUtil.getFileStorageService(server);
        if (fileRecorder != null) {
            service.setFileRecorder(fileRecorder);
        }
        return service;
    }

}
