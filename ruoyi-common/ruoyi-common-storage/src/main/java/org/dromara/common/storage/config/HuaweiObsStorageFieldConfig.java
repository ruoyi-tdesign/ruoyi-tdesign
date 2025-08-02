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
 * 华为云对象存储
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class HuaweiObsStorageFieldConfig implements StorageFieldConfig {
    /** Access Key */
    private FieldConfig<String> accessKey;
    /** Access Key Secret */
    private FieldConfig<String> secretKey;
    /** 访问站点 */
    private FieldConfig<String> endPoint;
    /** 存储桶名称 */
    private FieldConfig<String> bucketName;
    /** 访问域名 */
    private FieldConfig<String> domain;
    /** 基础路径 */
    private FieldConfig<String> basePath;
    /** 默认的 ACL，详情 {@link Constant.HuaweiObsACL} */
    private FieldConfig<String> defaultAcl;
    /** 自动分片上传阈值，达到此大小则使用分片上传，默认 128MB */
    private FieldConfig<Integer> multipartThreshold;
    /** 自动分片上传时每个分片大小，默认 32MB */
    private FieldConfig<Integer> multipartPartSize;

    public HuaweiObsStorageFieldConfig() {
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
            .label("存储桶名称")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .useInput()
            .value("")
            .label("访问域名")
            .help("访问域名，注意“/”结尾，例如：http://abc.obs.com/")
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
                new FieldOptionGroup<>("华为云OBS ACL", List.of(
                    new FieldOption<>("public-read-delivered", "public-read-delivered"),
                    new FieldOption<>("public-read-delivered", "public-read-delivered"),
                    new FieldOption<>("public-read-write-delivered", "public-read-write-delivered"),
                    new FieldOption<>("authenticated-read", "authenticated-read"),
                    new FieldOption<>("bucket-owner-read", "bucket-owner-read"),
                    new FieldOption<>("bucket-owner-full-control", "bucket-owner-full-control"),
                    new FieldOption<>("log-delivery-write", "log-delivery-write")
                ))
            )).end()
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
        FileStorageProperties.HuaweiObsConfig huaweiObsConfig = JsonUtils.parseObject(json, FileStorageProperties.HuaweiObsConfig.class);
        huaweiObsConfig.setPlatform(platform);
        if (properties.getHuaweiObs() == null) {
            properties.setHuaweiObs(new ArrayList<>());
        }
        List<FileStorageProperties.HuaweiObsConfig> list = (List<FileStorageProperties.HuaweiObsConfig>) properties.getHuaweiObs();
        list.add(huaweiObsConfig);
        return properties;
    }
}
