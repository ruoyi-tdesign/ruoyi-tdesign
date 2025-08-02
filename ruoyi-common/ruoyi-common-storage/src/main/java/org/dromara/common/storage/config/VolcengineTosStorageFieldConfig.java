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
 * 火山引擎 TOS存储字段配置
 *
 * @author hexm
 * @date 2025/8/2
 */
@Data
public class VolcengineTosStorageFieldConfig implements StorageFieldConfig {
    /** Access Key */
    private FieldConfig<String> accessKey;
    /** Access Key Secret */
    private FieldConfig<String> secretKey;
    /** 访问站点 */
    private FieldConfig<String> endPoint;
    /** 地域 */
    private FieldConfig<String> region;
    /** 存储桶名称 */
    private FieldConfig<String> bucketName;
    /** 访问域名 */
    private FieldConfig<String> domain;
    /** 基础路径 */
    private FieldConfig<String> basePath;
    /** 默认的 ACL，详情 {@link Constant.VolcengineTosACL} */
    private FieldConfig<String> defaultAcl;
    /** 自动分片上传阈值，达到此大小则使用分片上传，默认 128MB */
    private FieldConfig<Integer> multipartThreshold;
    /** 自动分片上传时每个分片大小，默认 32MB */
    private FieldConfig<Integer> multipartPartSize;

    public VolcengineTosStorageFieldConfig() {
        this.accessKey = FieldConfig.<String>builder()
            .useInput()
            .label("accessKey")
            .required(true)
            .build();
        this.secretKey = FieldConfig.<String>builder()
            .inputComponent().type("password").end()
            .label("secretKey")
            .required(true)
            .build();
        this.endPoint = FieldConfig.<String>builder()
            .useInput()
            .label("访问站点")
            .help("终端节点")
            .required(true)
            .build();
        this.region = FieldConfig.<String>builder()
            .useInput()
            .label("地域")
            .help("地区")
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
            .help("访问域名，注意\"/\"结尾，例如：https://your-bucket.tos-cn-beijing.volces.com/")
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
            .selectComponent()
            .options(List.of(
                new FieldOptionGroup<>("默认ACL", List.of(
                    new FieldOption<>("私有", "private"),
                    new FieldOption<>("公共读", "public-read"),
                    new FieldOption<>("公共读写", "public-read-write")
                )),
                new FieldOptionGroup<>("火山引擎TOS ACL", List.of(
                    new FieldOption<>("authenticated-read", "authenticated-read"),
                    new FieldOption<>("bucket-owner-read", "bucket-owner-read"),
                    new FieldOption<>("bucket-owner-full-control", "bucket-owner-full-control"),
                    new FieldOption<>("log-delivery-write", "log-delivery-write"),
                    new FieldOption<>("bucket-owner-entrusted", "bucket-owner-entrusted"),
                    new FieldOption<>("unknown", "unknown")
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
        FileStorageProperties.VolcengineTosConfig volcengineTosConfig = JsonUtils.parseObject(json, FileStorageProperties.VolcengineTosConfig.class);
        volcengineTosConfig.setPlatform(platform);
        if (properties.getVolcengineTos() == null) {
            properties.setVolcengineTos(new ArrayList<>());
        }
        List<FileStorageProperties.VolcengineTosConfig> list = (List<FileStorageProperties.VolcengineTosConfig>) properties.getVolcengineTos();
        list.add(volcengineTosConfig);
        return properties;
    }
}
