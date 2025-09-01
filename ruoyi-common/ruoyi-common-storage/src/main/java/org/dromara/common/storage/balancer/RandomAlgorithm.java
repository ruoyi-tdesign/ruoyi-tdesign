package org.dromara.common.storage.balancer;

import java.util.List;
import java.util.Random;


/**
 * 加权随机负载均衡算法
 * 根据服务器权重比例随机选择服务器，权重越高被选中的概率越大
 */
public class RandomAlgorithm implements LoadBalancingAlgorithm {
    private final Random random = new Random();

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
            // 如果总权重为0或负数，退化为普通随机选择
            return servers.get(random.nextInt(servers.size()));
        }

        // 生成一个0到totalWeight-1之间的随机数
        int randomValue = random.nextInt(totalWeight);
        int cumulativeWeight = 0;

        // 遍历服务器列表，累加权重，直到随机值落在当前服务器的权重区间内
        for (FileServer server : servers) {
            cumulativeWeight += server.getWeight();
            if (randomValue < cumulativeWeight) {
                return server;
            }
        }

        // 理论上不会执行到这里，为了编译通过返回第一个服务器
        return servers.get(0);
    }
}
