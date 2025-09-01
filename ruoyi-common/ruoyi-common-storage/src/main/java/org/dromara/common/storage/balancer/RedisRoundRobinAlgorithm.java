package org.dromara.common.storage.balancer;

import org.dromara.common.redis.utils.RedisUtils;

import java.util.List;

/**
 * 加权轮询负载均衡算法 - redis版本
 * 按顺序依次选择服务器，考虑服务器权重，权重越高被选中的次数越多
 */
public class RedisRoundRobinAlgorithm implements LoadBalancingAlgorithm {

    /** 计数器缓存key */
    private static final String CACHE_KEY = "round_robin_counter";

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
            int index = (int) RedisUtils.incrAtomicLong(CACHE_KEY) % servers.size();
            return servers.get(index);
        }

        // 获取当前计数器值并自增
        int current = (int) RedisUtils.incrAtomicLong(CACHE_KEY);
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
