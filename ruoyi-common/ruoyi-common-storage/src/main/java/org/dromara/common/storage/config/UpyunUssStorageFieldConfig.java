package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;

/**
 * 又拍云 USS 字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class UpyunUssStorageFieldConfig implements StorageFieldConfig {
    /** 账号 */
    private FieldConfig<String> username;
    /** 密码 */
    private FieldConfig<String> password;
    /** 存储空间名 */
    private FieldConfig<String> bucketName;
    /** 访问域名 */
    private FieldConfig<String> domain;
    /** 基础路径 */
    private FieldConfig<String> basePath;
    /**
     * 手动分片上传时，每个分片大小，单位字节，最小 1MB，最大 50MB，必须是 1MB 的整数倍，默认 1MB。
     * 又拍云 USS 比较特殊，必须提前传入分片大小（最后一个分片可以小于此大小，但不能超过）
     * 你可以在初始化文件时使用 putMetadata("X-Upyun-Multi-Part-Size", "1048576") 方法传入分片大小
     */
    private FieldConfig<Integer> multipartUploadPartSize;

    public UpyunUssStorageFieldConfig() {
        this.username = FieldConfig.<String>builder()
            .useInput()
            .label("账号")
            .required(true)
            .build();
        this.password = FieldConfig.<String>builder()
            .useInput()
            .label("密码")
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
        this.multipartUploadPartSize = FieldConfig.<Integer>builder()
            .useInputNumber()
            .value(1024 * 1024)
            .label("分片大小")
            .help("手动分片上传时，每个分片大小，单位字节，最小 1MB，最大 50MB，必须是 1MB 的整数倍，默认 1MB。")
            .required(true)
            .build();
    }
}
