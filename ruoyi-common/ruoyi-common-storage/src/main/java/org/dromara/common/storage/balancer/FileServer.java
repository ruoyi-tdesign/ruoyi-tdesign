package org.dromara.common.storage.balancer;

/**
 * 文件服务器接口，定义了服务器的基本操作和属性
 */
public interface FileServer {
    /** 获取服务器唯一标识 */
    String getId();

    /**
     * 获取平台标识
     *
     * @see org.dromara.common.storage.enums.StorageEnum#getCode
     */
    String getPlatform();

    /**
     * 获取服务器权重，用于负载均衡算法计算
     * 权重越高，表示服务器处理能力越强，应分配更多请求
     */
    Integer getWeight();

    /**
     * 获取服务器配置信息
     * 返回一个JSON字符串，包含服务器的配置信息
     */
    String getProperties();
}
