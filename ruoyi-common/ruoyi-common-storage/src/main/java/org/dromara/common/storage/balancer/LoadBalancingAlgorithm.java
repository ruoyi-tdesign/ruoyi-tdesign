package org.dromara.common.storage.balancer;

import java.util.List;

/**
 * 负载均衡算法接口，定义了选择服务器的策略
 */
public interface LoadBalancingAlgorithm {
    /**
     * 从服务器列表中选择一个服务器
     * 参数：servers - 可用服务器列表
     * 返回：选中的服务器，如果列表为空则返回null
     *
     * @param servers 可用服务器列表
     */
    FileServer selectServer(List<FileServer> servers);
}
