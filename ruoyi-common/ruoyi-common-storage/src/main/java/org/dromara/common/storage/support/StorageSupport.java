package org.dromara.common.storage.support;

import org.dromara.common.storage.enums.StorageEnum;
import org.dromara.x.file.storage.core.FileStorageProperties;

import java.util.Arrays;
import java.util.List;

/**
 * 存储支持
 *
 * @author hexm
 * @date 2025/5/2
 */
public class StorageSupport {

    /**
     * 获取所有存储平台
     */
    public static List<StorageEnum> getAllStoragePlatform() {
        return List.of(StorageEnum.values());
    }

    /**
     * 获取支持的存储平台
     */
    public static List<StorageEnum> getSupportPlatform() {
        return Arrays.stream(StorageEnum.values())
            .filter(storageEnum -> existsClass(storageEnum.getClientClassName()))
            .toList();
    }

    /**
     * 获取不支持的存储平台
     */
    public static List<StorageEnum> getNotSupportPlatform() {
        return Arrays.stream(StorageEnum.values())
            .filter(storageEnum -> !existsClass(storageEnum.getClientClassName()))
            .toList();
    }

    public static void main(String[] args) {
        System.out.println(getSupportPlatform());
        System.out.println(getNotSupportPlatform());
    }

    /**
     * 判断是否存在Class
     *
     * @param className 类名
     * @return 是否存在
     */
    private static boolean existsClass(String className) {
        try {
            ClassLoader loader = FileStorageProperties.class.getClassLoader();
            loader.loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
