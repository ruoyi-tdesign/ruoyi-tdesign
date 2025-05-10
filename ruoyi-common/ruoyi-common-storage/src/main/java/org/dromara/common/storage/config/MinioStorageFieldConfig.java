package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;

/**
 * MinIO字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class MinioStorageFieldConfig implements StorageFieldConfig {
    /** Access Key */
    private FieldConfig<String> accessKey;
    /** Access Key Secret */
    private FieldConfig<String> secretKey;
    /** 访问站点 */
    private FieldConfig<String> endPoint;
    /** 存储空间名 */
    private FieldConfig<String> bucketName;
    /** 访问域名 */
    private FieldConfig<String> domain;
    /** 基础路径 */
    private FieldConfig<String> basePath;
    /**
     * 自动分片上传阈值，达到此大小则使用分片上传，默认 128MB。
     * 在获取不到文件大小或达到这个阈值的情况下，会使用这里提供的分片大小，否则 MinIO 会自动分片大小
     */
    private FieldConfig<Integer> multipartThreshold;
    /** 自动分片上传时每个分片大小，默认 32MB */
    private FieldConfig<Integer> multipartPartSize;

    public MinioStorageFieldConfig() {
        this.accessKey = FieldConfig.<String>builder()
            .useInput()
            .label("accessKey")
            .required(true)
            .build();
        this.secretKey = FieldConfig.<String>builder()
            .useInput()
            .label("secretKey")
            .required(true)
            .inputComponent().type("password").end()
            .build();
        this.endPoint = FieldConfig.<String>builder()
            .useInput()
            .label("访问站点")
            .required(true)
            .build();
        this.bucketName = FieldConfig.<String>builder()
            .useInput()
            .label("存储空间名")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .useInput()
            .value("")
            .label("访问域名")
            .required(false)
            .build();
        this.basePath = FieldConfig.<String>builder()
            .useInput()
            .label("基础路径")
            .required(false)
            .build();
        this.multipartThreshold = FieldConfig.<Integer>builder()
            .useInputNumber()
            .value(128 * 1024 * 1024)
            .label("分片阈值")
            .help("自动分片上传阈值，达到此大小则使用分片上传，默认 128MB。<br/>" +
                "在获取不到文件大小或达到这个阈值的情况下，会使用这里提供的分片大小，否则 MinIO 会自动分片大小")
            .required(true)
            .build();
        this.multipartPartSize = FieldConfig.<Integer>builder()
            .useInputNumber()
            .value(32 * 1024 * 1024)
            .label("分片大小")
            .help("自动分片上传时每个分片大小，默认 32MB")
            .required(true)
            .build();
    }
}
