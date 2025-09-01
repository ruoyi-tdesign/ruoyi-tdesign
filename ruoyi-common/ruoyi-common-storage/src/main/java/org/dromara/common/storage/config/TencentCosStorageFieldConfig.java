package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.common.core.ui.FieldOption;
import org.dromara.common.core.ui.FieldOptionGroup;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.x.file.storage.core.FileStorageProperties;
import org.dromara.x.file.storage.core.constant.Constant;

import java.util.ArrayList;
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
    /** 存储桶名称 */
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
            .label("secretId")
            .required(true)
            .build();
        this.secretKey = FieldConfig.<String>builder()
            .useInput()
            .label("secretKey")
            .required(true)
            .inputComponent().type("password").end()
            .build();
        this.region = FieldConfig.<String>builder()
            .useInput()
            .label("存储区域(region)")
            .help("存仓库所在地域")
            .required(true)
            .build();
        this.bucketName = FieldConfig.<String>builder()
            .useInput()
            .label("存储桶名称")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .useInput()
            .value("")
            .label("访问域名")
            .help("访问域名，注意“/”结尾，例如：https://abc.cos.ap-nanjing.myqcloud.com/")
            .required(false)
            .build();
        this.basePath = FieldConfig.<String>builder()
            .useInput()
            .label("基础路径")
            .required(false)
            .build();
        this.defaultAcl = FieldConfig.<String>builder()
            .label("默认的 ACL")
            .help("文件的访问控制列表，一般情况下只有对象存储支持该功能")
            .selectComponent().options(List.of(
                new FieldOptionGroup<>("默认ACL", List.of(
                    new FieldOption<>("私有", "private"),
                    new FieldOption<>("公共读", "public-read"),
                    new FieldOption<>("公共读写", "public-read-write")
                )),
                new FieldOptionGroup<>("腾讯云COS ACL", List.of(
                    new FieldOption<>("default", "default")
                ))
            ))
            .end()
            .build();
        this.multipartThreshold = FieldConfig.<Integer>builder()
            .useInputNumber()
            .value(128 * 1024 * 1024)
            .label("分片阈值")
            .help("自动分片上传阈值，达到此大小则使用分片上传，默认 128MB")
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

    @Override
    public FileStorageProperties addStorageProperties(FileStorageProperties properties, String platform, String json) {
        FileStorageProperties.TencentCosConfig tencentCosConfig = JsonUtils.parseObject(json, FileStorageProperties.TencentCosConfig.class);
        tencentCosConfig.setPlatform(platform);
        if (properties.getTencentCos() == null) {
            properties.setTencentCos(new ArrayList<>());
        }
        List<FileStorageProperties.TencentCosConfig> list = (List<FileStorageProperties.TencentCosConfig>) properties.getTencentCos();
        list.add(tencentCosConfig);
        return properties;
    }
}
