package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;

import java.nio.charset.StandardCharsets;

/**
 * WebDav字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class WebDavStorageFieldConfig implements StorageFieldConfig {
    /** 服务器地址，注意“/”结尾，例如：http://192.168.1.105:8405/ */
    private FieldConfig<String> server;
    /** 用户名 */
    private FieldConfig<String> user;
    /** 密码 */
    private FieldConfig<String> password;
    /** 基础路径 */
    private FieldConfig<String> basePath;
    /** 存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾 */
    private FieldConfig<String> storagePath;
    /** 访问域名 */
    private FieldConfig<String> domain;

    public WebDavStorageFieldConfig() {
        this.server = FieldConfig.<String>builder()
            .useInput()
            .name("服务器地址")
            .help("服务器地址，注意“/”结尾，例如：http://192.168.1.105:8405/")
            .required(true)
            .build();
        this.user = FieldConfig.<String>builder()
            .useInput()
            .name("用户名")
            .required(true)
            .build();
        this.password = FieldConfig.<String>builder()
            .useInput()
            .name("密码")
            .inputComponent().type("password").end()
            .required(false)
            .build();
        this.basePath = FieldConfig.<String>builder()
            .useInput()
            .name("基础路径")
            .required(false)
            .build();
        this.storagePath = FieldConfig.<String>builder()
            .useInput()
            .value("/")
            .name("存储路径")
            .help("存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .useInput()
            .name("访问域名")
            .required(false)
            .build();
    }
}
