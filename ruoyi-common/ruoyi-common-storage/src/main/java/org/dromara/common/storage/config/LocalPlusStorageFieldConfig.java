package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.x.file.storage.core.FileStorageProperties;

import java.util.Collections;

/**
 * 本地存储升级版字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class LocalPlusStorageFieldConfig implements StorageFieldConfig {
    /** 基础路径 */
    private FieldConfig<String> basePath;
    /** 存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾 */
    private FieldConfig<String> storagePath;
    /** 访问域名 */
    private FieldConfig<String> domain;

    public LocalPlusStorageFieldConfig() {
        this.basePath = FieldConfig.<String>builder()
            .useInput()
            .label("基础路径")
            .required(false)
            .build();
        this.storagePath = FieldConfig.<String>builder()
            .useInput()
            .value("/")
            .label("存储路径")
            .help("存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .useInput()
            .label("访问域名")
            .required(false)
            .build();
    }


    @Override
    public FileStorageProperties buildStorageProperties(String json) {
        FileStorageProperties properties = new FileStorageProperties();
        FileStorageProperties.LocalPlusConfig localPlusConfig = JsonUtils.parseObject(json, FileStorageProperties.LocalPlusConfig.class);
        localPlusConfig.setPlatform(properties.getDefaultPlatform());
        properties.setLocalPlus(Collections.singletonList(localPlusConfig));
        return properties;
    }
}
