package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.common.core.ui.FieldOption;
import org.dromara.x.file.storage.core.constant.Constant;

import java.util.List;

/**
 * GoogleCloudStorage字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class GoogleCloudStorageFieldConfig implements StorageFieldConfig {
    /** projectId */
    private FieldConfig<String> projectId;
    /** 证书路径，兼容Spring的ClassPath路径、文件路径、HTTP路径等 */
    private FieldConfig<String> credentialsPath;
    /** 存储空间名 */
    private FieldConfig<String> bucketName;
    /** 访问域名 */
    private FieldConfig<String> domain;
    /** 基础路径 */
    private FieldConfig<String> basePath;
    /** 默认的 ACL，详情 {@link Constant.GoogleCloudStorageACL} */
    private FieldConfig<String> defaultAcl;

    public GoogleCloudStorageFieldConfig() {
        this.projectId = FieldConfig.<String>builder()
            .useInput()
            .name("projectId")
            .required(true)
            .build();
        this.credentialsPath = FieldConfig.<String>builder()
            .useInput()
            .name("证书路径")
            .help("证书路径，兼容Spring的ClassPath路径、文件路径、HTTP路径等")
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
                new FieldOption<>("authenticated-read", "authenticated-read"),
                new FieldOption<>("all-authenticated-users", "all-authenticated-users"),
                new FieldOption<>("project-private", "project-private"),
                new FieldOption<>("bucket-owner-read", "bucket-owner-read"),
                new FieldOption<>("bucket-owner-full-control", "bucket-owner-full-control")
            ))
            .end()
            .build();
    }
}
