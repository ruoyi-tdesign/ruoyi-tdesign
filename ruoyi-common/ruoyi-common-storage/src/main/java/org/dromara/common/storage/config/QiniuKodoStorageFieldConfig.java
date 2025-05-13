package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.x.file.storage.core.FileStorageProperties;

import java.util.Collections;

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
    }

    @Override
    public FileStorageProperties buildStorageProperties(FileStorageProperties properties, String platform, String json) {
        FileStorageProperties.QiniuKodoConfig qiniuKodoConfig = JsonUtils.parseObject(json, FileStorageProperties.QiniuKodoConfig.class);
        qiniuKodoConfig.setPlatform(platform);
        properties.setQiniuKodo(Collections.singletonList(qiniuKodoConfig));
        return properties;
    }
}
