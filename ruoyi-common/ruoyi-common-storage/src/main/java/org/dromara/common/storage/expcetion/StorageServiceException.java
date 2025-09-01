package org.dromara.common.storage.expcetion;

/**
 * 存储服务异常
 *
 * @author hexm
 * @date 2025/5/20
 */
public class StorageServiceException extends RuntimeException {
    public StorageServiceException(String message) {
        super(message);
    }
}
