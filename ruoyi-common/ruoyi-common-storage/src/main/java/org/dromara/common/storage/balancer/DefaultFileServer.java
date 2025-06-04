package org.dromara.common.storage.balancer;

import lombok.Data;

/**
 * @author hexm
 * @date 2025/5/27
 */
@Data
public class DefaultFileServer implements FileServer {
    private String id;
    private String platform;
    private Integer weight;
    private String properties;
}
