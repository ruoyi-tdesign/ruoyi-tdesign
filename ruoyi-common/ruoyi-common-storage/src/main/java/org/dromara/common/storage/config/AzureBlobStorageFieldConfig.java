package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.common.core.ui.FieldOption;
import org.dromara.x.file.storage.core.constant.Constant;

import java.util.List;

/**
 * AzureBlob字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class AzureBlobStorageFieldConfig implements StorageFieldConfig {

    /** 访问站点 */
    private FieldConfig<String> endPoint;
    /** 容器名称，类似于 s3 的 bucketName，AzureBlob控制台-数据存储-容器 */
    private FieldConfig<String> containerName;
    /** 访问域名 */
    private FieldConfig<String> domain;
    /** 基础路径 */
    private FieldConfig<String> basePath;
    /** 默认的 ACL，详情 {@link Constant.AliyunOssACL} */
    private FieldConfig<String> defaultAcl;
    /** 连接字符串，AzureBlob控制台-安全性和网络-访问秘钥-连接字符串 */
    private FieldConfig<String> connectionString;
    /** 自动分片上传阈值，超过此大小则使用分片上传，默认值256M */
    private FieldConfig<Long> multipartThreshold;
    /** 自动分片上传时每个分片大小，默认 4MB*/
    private FieldConfig<Long> multipartPartSize;
    /**
     * 最大上传并行度
     * 分片后 同时进行上传的 数量
     * 数量太大会占用大量缓冲区
     * 默认 8
     */
    private FieldConfig<Integer> maxConcurrency;
    /**
     * 预签名 URL 时，传入的 HTTP method 与 Azure Blob Storage 中的 SAS 权限映射表，
     * 目前默认支持 GET （获取），PUT（上传），DELETE（删除），
     * 其它可以自行扩展，例如你想自定义一个 ALL 的 method，赋予所有权限，可以写为 .put("ALL", "racwdxytlmei")
     * {@link com.azure.storage.blob.sas.BlobSasPermission}
     */
    private FieldConfig<List<String>> methodToPermissionMap;

    public AzureBlobStorageFieldConfig() {
        this.endPoint = FieldConfig.<String>builder()
            .component("input")
            .name("访问站点")
            .required(true)
            .build();
        this.containerName = FieldConfig.<String>builder()
            .component("input")
            .name("容器名称")
            .help("容器名称，类似于 s3 的 bucketName，AzureBlob控制台-数据存储-容器")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .component("input")
            .value("")
            .name("访问域名")
            .required(false)
            .build();
        this.basePath = FieldConfig.<String>builder()
            .component("input")
            .name("基础路径")
            .required(false)
            .build();
        this.defaultAcl = FieldConfig.<String>builder()
            .component("select")
            .name("默认的 ACL")
            .help("文件的访问控制列表，一般情况下只有对象存储支持该功能")
            .options(List.of(
                new FieldOption<>("私有", "private"),
                new FieldOption<>("公共读", "public-read"),
                new FieldOption<>("公共读写", "public-read-write"),
                new FieldOption<>("default", "default")
            ))
            .build();
        this.connectionString = FieldConfig.<String>builder()
            .component("input")
            .name("连接字符串")
            .help("连接字符串，AzureBlob控制台-安全性和网络-访问秘钥-连接字符串")
            .required(false)
            .build();
        this.multipartThreshold = FieldConfig.<Long>builder()
            .component("input-number")
            .value(256 * 1024 * 1024L)
            .name("分片阈值")
            .help("自动分片上传阈值，超过此大小则使用分片上传，默认值256M")
            .required(true)
            .build();
        this.multipartPartSize = FieldConfig.<Long>builder()
            .component("input-number")
            .value(4 * 1024 * 1024L)
            .name("分片大小")
            .help("自动分片上传时每个分片大小，默认 4MB")
            .required(true)
            .build();
        this.maxConcurrency = FieldConfig.<Integer>builder()
            .component("input-number")
            .value(8)
            .name("最大上传并行度")
            .help("最大上传并行度<br/>分片后 同时进行上传的 数量<br/>数量太大会占用大量缓冲区<br/>默认 8")
            .required(true)
            .build();
        this.methodToPermissionMap = FieldConfig.<List<String>>builder()
            .component("select")
            .name("权限映射表")
            .help("预签名 URL 时，传入的 HTTP method 与 Azure Blob Storage 中的 SAS 权限映射表<br/>目前默认支持 GET （获取），PUT（上传），DELETE（删除）")
            .options(List.of(
                new FieldOption<>("GET", "GET"),
                new FieldOption<>("PUT", "PUT"),
                new FieldOption<>("DELETE", "DELETE"),
                new FieldOption<>("ALL", "ALL")
            ))
            .build();
    }
}
