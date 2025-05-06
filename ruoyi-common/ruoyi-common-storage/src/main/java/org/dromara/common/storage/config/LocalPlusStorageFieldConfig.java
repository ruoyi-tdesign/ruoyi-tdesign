package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;

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
            .component("input")
            .value("test")
            .name("基础路径")
            .required(false)
            .build();
        this.storagePath = FieldConfig.<String>builder()
            .component("input")
            .value("/")
            .name("存储路径")
            .help("存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .component("input")
            .name("访问域名")
            .required(false)
            .build();
    }
}
