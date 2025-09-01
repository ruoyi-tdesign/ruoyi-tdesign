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
 * AmazonS3字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class AmazonS3V2StorageFieldConfig implements StorageFieldConfig {
    /** Access Key */
    private FieldConfig<String> accessKey;
    /** Access Key Secret */
    private FieldConfig<String> secretKey;
    /** 存储区域 */
    private FieldConfig<String> region;
    /** 访问站点 */
    private FieldConfig<String> endPoint;
    /** 存储桶名称 */
    private FieldConfig<String> bucketName;
    /** 访问域名 */
    private FieldConfig<String> domain;
    /** 基础路径 */
    private FieldConfig<String> basePath;
    /** 默认的 ACL，详情 {@link Constant.AwsS3ACL} */
    private FieldConfig<String> defaultAcl;
    /** 自动分片上传阈值，达到此大小则使用分片上传，默认 128MB */
    private FieldConfig<Integer> multipartThreshold;
    /** 自动分片上传时每个分片大小，默认 32MB */
    private FieldConfig<Integer> multipartPartSize;

    public AmazonS3V2StorageFieldConfig() {
        this.accessKey = FieldConfig.<String>builder()
            .useInput()
            .label("accessKey")
            .help("若要使用第三方兼容的存储平台，建议使用 Amazon S3 （1.x）的方式，兼容性会好些")
            .required(true)
            .build();
        this.secretKey = FieldConfig.<String>builder()
            .label("secretKey")
            .required(true)
            .inputComponent().type("password").end()
            .build();
        this.region = FieldConfig.<String>builder()
            .useInput()
            .label("存储区域(region)")
            .help("存储区域。例如：ap-east-1")
            .required(true)
            .build();
        this.endPoint = FieldConfig.<String>builder()
            .useInput()
            .label("访问站点(endPoint)")
            .help("访问站点。例如：https://s3.ap-east-1.amazonaws.com/")
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
            .help("访问域名，注意“/”结尾，例如：https://abcd.s3.ap-east-1.amazonaws.com/")
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
                new FieldOptionGroup<>("AwsS3 ACL", List.of(
                    new FieldOption<>("authenticated-read", "authenticated-read"),
                    new FieldOption<>("log-delivery-write", "log-delivery-write"),
                    new FieldOption<>("bucket-owner-read", "bucket-owner-read"),
                    new FieldOption<>("bucket-owner-full-control", "bucket-owner-full-control"),
                    new FieldOption<>("aws-exec-read", "aws-exec-read")
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
        FileStorageProperties.AmazonS3V2Config amazonS3V2Config = JsonUtils.parseObject(json, FileStorageProperties.AmazonS3V2Config.class);
        amazonS3V2Config.setPlatform(platform);
        if (properties.getAmazonS3V2() == null) {
            properties.setAmazonS3V2(new ArrayList<>());
        }
        List<FileStorageProperties.AmazonS3V2Config> list = (List<FileStorageProperties.AmazonS3V2Config>) properties.getAmazonS3V2();
        list.add(amazonS3V2Config);
        return properties;
    }
}
