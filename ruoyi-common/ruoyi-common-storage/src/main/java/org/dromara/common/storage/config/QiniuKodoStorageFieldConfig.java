package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;

/**
 * 七牛云 Kodo 字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class QiniuKodoStorageFieldConfig implements StorageFieldConfig {
    /** Access Key */
    private FieldConfig<String> accessKey;
    /** Access Key Secret */
    private FieldConfig<String> secretKey;
    /** 存储空间名 */
    private FieldConfig<String> bucketName;
    /** 访问域名 */
    private FieldConfig<String> domain;
    /** 基础路径 */
    private FieldConfig<String> basePath;

    public QiniuKodoStorageFieldConfig() {
        this.accessKey = FieldConfig.<String>builder()
            .component("input")
            .name("accessKey")
            .required(true)
            .build();
        this.secretKey = FieldConfig.<String>builder()
            .component("input")
            .name("secretKey")
            .required(true)
            .type("password")
            .build();
        this.bucketName = FieldConfig.<String>builder()
            .component("input")
            .name("存储空间名")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .component("input")
            .value("")
            .name("访问域名")
            .required(false)
            .build();
        this.basePath = FieldConfig.<String>builder()
            .component("input")
            .name("基础路径")
            .required(false)
            .build();
    }
}
