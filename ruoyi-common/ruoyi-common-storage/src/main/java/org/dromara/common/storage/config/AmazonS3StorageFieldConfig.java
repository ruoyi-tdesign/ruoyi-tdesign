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
public class AmazonS3StorageFieldConfig implements StorageFieldConfig {
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

    public AmazonS3StorageFieldConfig() {
        this.accessKey = FieldConfig.<String>builder()
            .useInput()
            .label("accessKey")
            .help("若要使用第三方兼容的存储平台，使用这种方式（1.x）兼容性会好些，否则请使用 Amazon S3 V2 版本")
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
            .help("与 endPoint 参数至少填一个。例如：ap-east-1")
            .required(false)
            .build();
        this.endPoint = FieldConfig.<String>builder()
            .useInput()
            .label("访问站点(endPoint)")
            .help("与 region 参数至少填一个。例如：https://s3.ap-east-1.amazonaws.com/")
            .required(false)
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
            .help("例如：s3/")
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
        FileStorageProperties.AmazonS3Config amazonS3Config = JsonUtils.parseObject(json, FileStorageProperties.AmazonS3Config.class);
        amazonS3Config.setPlatform(platform);
        if (properties.getAmazonS3() == null) {
            properties.setAmazonS3(new ArrayList<>());
        }
        List<FileStorageProperties.AmazonS3Config> list = (List<FileStorageProperties.AmazonS3Config>) properties.getAmazonS3();
        list.add(amazonS3Config);
        return properties;
    }
}
