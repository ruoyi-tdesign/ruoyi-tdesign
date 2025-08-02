package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.x.file.storage.core.FileStorageProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoGridFs存储字段配置
 *
 * @author hexm
 * @date 2025/8/2
 */
@Data
public class MongoGridFsStorageFieldConfig implements StorageFieldConfig {
    /** 链接字符串 */
    private FieldConfig<String> connectionString;
    /** 数据库名称 */
    private FieldConfig<String> database;
    /** 存储桶名称 */
    private FieldConfig<String> bucketName;
    /** 访问域名 */
    private FieldConfig<String> domain;
    /** 基础路径 */
    private FieldConfig<String> basePath;

    public MongoGridFsStorageFieldConfig() {
        this.connectionString = FieldConfig.<String>builder()
            .useInput()
            .label("链接字符串")
            .help("例如：mongodb://admin:123456@192.168.1.111:27017<br/>兼容性说明：<br/>" +
                "1、Mongo GridFS 不支持目录层级功能，这里在把文件保存到 Mongo GridFS 时，将整个文件路径作为文件名来进行模拟，好处是可以与其它存储平台保持一致，缺点是在用 Navicat 管理文件时，不能直接预览或下载文件，原因是文件名中包含 / 目录分隔符，在 Windows 等系统下不支持文件名中含有此类字符，不过可以进行重命名为正常的文件名后进行下载<br/>" +
                "2、因为进行了模拟目录层级，在文件数量过多的情况下，使用列举文件功能时性能可能会比较差，原因是需要把相同前缀的文件全部获取到，用于模拟目录层级<br/>" +
                "3、Mongo GridFS 是支持相同文件名的，但是为了与其它存储平台保持一致，会覆盖同名文件")
            .required(true)
            .build();
        this.database = FieldConfig.<String>builder()
            .useInput()
            .label("数据库名称")
            .required(true)
            .build();
        this.bucketName = FieldConfig.<String>builder()
            .useInput()
            .label("存储桶名称")
            .help("GridFS 存储桶名称")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .useInput()
            .value("")
            .label("访问域名")
            .help("访问域名，注意“/”结尾，主要用于配合你自己写的访问接口进行访问")
            .required(false)
            .build();
        this.basePath = FieldConfig.<String>builder()
            .useInput()
            .label("基础路径")
            .required(false)
            .build();
    }

    @Override
    public FileStorageProperties addStorageProperties(FileStorageProperties properties, String platform, String json) {
        FileStorageProperties.MongoGridFsConfig mongoGridFsConfig = JsonUtils.parseObject(json, FileStorageProperties.MongoGridFsConfig.class);
        mongoGridFsConfig.setPlatform(platform);
        if (properties.getMongoGridFs() == null) {
            properties.setMongoGridFs(new ArrayList<>());
        }
        List<FileStorageProperties.MongoGridFsConfig> list = (List<FileStorageProperties.MongoGridFsConfig>) properties.getMongoGridFs();
        list.add(mongoGridFsConfig);
        return properties;
    }
}
