package org.dromara.common.storage.balancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 加权轮询负载均衡算法
 * 按顺序依次选择服务器，考虑服务器权重，权重越高被选中的次数越多
 */
public class RoundRobinAlgorithm implements LoadBalancingAlgorithm {
    /** 原子计数器，用于记录当前轮询位置 */
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public FileServer selectServer(List<FileServer> servers) {
        if (servers == null || servers.isEmpty()) {
            return null;
        }

        // 计算所有服务器的总权重
        int totalWeight = servers.stream()
            .mapToInt(FileServer::getWeight)
            .sum();

        if (totalWeight <= 0) {
            // 如果总权重为0或负数，退化为普通轮询
            int index = counter.getAndIncrement() % servers.size();
            return servers.get(index);
        }

        // 获取当前计数器值并自增
        int current = counter.getAndIncrement();
        // 计算当前位置对总权重取模的结果
        int mod = current % totalWeight;
        int cumulativeWeight = 0;

        // 遍历服务器列表，累加权重，直到mod值落在当前服务器的权重区间内
        for (FileServer server : servers) {
            cumulativeWeight += server.getWeight();
            if (mod < cumulativeWeight) {
                return server;
            }
        }

        // 理论上不会执行到这里，为了编译通过返回第一个服务器
        return servers.get(0);
    }
}
