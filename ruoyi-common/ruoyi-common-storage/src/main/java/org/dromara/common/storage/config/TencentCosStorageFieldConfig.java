package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.common.core.ui.FieldOption;
import org.dromara.x.file.storage.core.constant.Constant;

import java.util.List;

/**
 * 腾讯云 COS 字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class TencentCosStorageFieldConfig implements StorageFieldConfig {
    /** Access Key */
    private FieldConfig<String> secretId;
    /** Access Key Secret */
    private FieldConfig<String> secretKey;
    /** 存储区域 */
    private FieldConfig<String> region;
    /** 存储空间名 */
    private FieldConfig<String> bucketName;
    /** 访问域名 */
    private FieldConfig<String> domain;
    /** 基础路径 */
    private FieldConfig<String> basePath;
    /** 默认的 ACL，详情 {@link Constant.TencentCosACL} */
    private FieldConfig<String> defaultAcl;
    /** 自动分片上传阈值，达到此大小则使用分片上传，默认 128MB */
    private FieldConfig<Integer> multipartThreshold;
    /** 自动分片上传时每个分片大小，默认 32MB */
    private FieldConfig<Integer> multipartPartSize;

    public TencentCosStorageFieldConfig() {
        this.secretId = FieldConfig.<String>builder()
            .useInput()
            .name("secretId")
            .required(true)
            .build();
        this.secretKey = FieldConfig.<String>builder()
            .useInput()
            .name("secretKey")
            .required(true)
            .inputComponent().type("password").end()
            .build();
        this.region = FieldConfig.<String>builder()
            .useInput()
            .name("存储区域(region)")
            .required(true)
            .build();
        this.bucketName = FieldConfig.<String>builder()
            .useInput()
            .name("存储空间名")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .useInput()
            .value("")
            .name("访问域名")
            .required(false)
            .build();
        this.basePath = FieldConfig.<String>builder()
            .useInput()
            .name("基础路径")
            .required(false)
            .build();
        this.defaultAcl = FieldConfig.<String>builder()
            .name("默认的 ACL")
            .help("文件的访问控制列表，一般情况下只有对象存储支持该功能")
            .selectComponent().options(List.of(
                new FieldOption<>("私有", "private"),
                new FieldOption<>("公共读", "public-read"),
                new FieldOption<>("公共读写", "public-read-write"),
                new FieldOption<>("default", "default")
            ))
            .end()
            .build();
        this.multipartThreshold = FieldConfig.<Integer>builder()
            .useInputNumber()
            .value(128 * 1024 * 1024)
            .name("分片阈值")
            .help("自动分片上传阈值，达到此大小则使用分片上传，默认 128MB")
            .required(true)
            .build();
        this.multipartPartSize = FieldConfig.<Integer>builder()
            .useInputNumber()
            .value(32 * 1024 * 1024)
            .name("分片大小")
            .help("自动分片上传时每个分片大小，默认 32MB")
            .required(true)
            .build();
    }
}
