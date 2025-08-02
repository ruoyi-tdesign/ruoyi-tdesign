package org.dromara.common.storage.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.common.core.ui.FieldOption;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.x.file.storage.core.FileStorageProperties;
import org.dromara.x.file.storage.core.constant.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    /** 默认的 ACL，详情 {@link Constant.AzureBlobStorageACL} */
    private FieldConfig<String> defaultAcl;
    /** 连接字符串，AzureBlob控制台-安全性和网络-访问秘钥-连接字符串 */
    private FieldConfig<String> connectionString;
    /** 自动分片上传阈值，超过此大小则使用分片上传，默认值256M */
    private FieldConfig<Long> multipartThreshold;
    /** 自动分片上传时每个分片大小，默认 4MB */
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
            .useInput()
            .label("访问站点")
            .help("终结点 AzureBlob控制台-设置-终结点-主终结点-Blob服务" +
                "<br/>兼容性说明：<br/>" +
                "Azure Blob Storage 主要要注意的地方就是 ACL （访问控制列表）功能，无法做到像 Amazon S3 那样针对单个文件设置公开访问， 即使将文件的 ACL 设置为 公共读PUBLIC_READ ，上传成功后的 url 也无法通过浏览器直接公开访问，但实际上已经设置成功了， 可以在 AzureBlob 控制台看到，现有有以下解决办法可以参考：<br/>" +
                "方式一：可以使用 预签名 URL 获取临时授权访问代替<br/>" +
                "方式二：将数据湖和容器同时开启公开访问，这样所有文件就都可以公开访问了（无法针对单个文件设置）<br/>" +
                "数据湖：AzureBlob控制台-设置-配置-允许Blob匿名访问-勾选已启用<br/>" +
                "容器：AzureBlob控制台-数据存储-容器-勾选对应容器-点击顶部匿名访问级别-选择第二个Blob（仅匿名读取访问blob）<br/>" +
                "方式三：将 domain 参数设置为自己的服务器地址，在服务器上编写对应接口，这样上传文件后的 url 就是后台地址了，当访问这个 url 时， 后台根据 url 解析出文件信息或从数据中查询出文件信息，校验是否有权限访问，如果有则再使用 预签名 URL 获取临时授权访问地址， 最后发起重定向到此地址即可。我觉得这可能是兼容性最好的方式了，只要编写这一个重定向接口，所有操作同其它存储平台一样")
            .required(true)
            .build();
        this.containerName = FieldConfig.<String>builder()
            .useInput()
            .label("容器名称")
            .help("容器名称，类似于 s3 的 bucketName，AzureBlob控制台-数据存储-容器")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .useInput()
            .value("")
            .label("访问域名")
            .help("访问域名，注意“/”结尾，与 end-point 保持一致")
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
                new FieldOption<>("私有", "private"),
                new FieldOption<>("公共读", "public-read"),
                new FieldOption<>("公共读写", "public-read-write")
            ))
            .end()
            .build();
        this.connectionString = FieldConfig.<String>builder()
            .useInput()
            .label("连接字符串")
            .help("连接字符串，AzureBlob控制台-安全性和网络-访问秘钥-连接字符串")
            .required(false)
            .build();
        this.multipartThreshold = FieldConfig.<Long>builder()
            .useInputNumber()
            .value(256 * 1024 * 1024L)
            .label("分片阈值")
            .help("自动分片上传阈值，超过此大小则使用分片上传，默认值256M")
            .required(true)
            .build();
        this.multipartPartSize = FieldConfig.<Long>builder()
            .useInputNumber()
            .value(4 * 1024 * 1024L)
            .label("分片大小")
            .help("自动分片上传时每个分片大小，默认 4MB")
            .required(true)
            .build();
        this.maxConcurrency = FieldConfig.<Integer>builder()
            .useInputNumber()
            .value(8)
            .label("最大上传并行度")
            .help("最大上传并行度，分片后同时进行上传的数量，数量太大会占用大量缓冲区。默认 8")
            .required(true)
            .build();
        this.methodToPermissionMap = FieldConfig.<List<String>>builder()
            .label("权限映射表")
            .help("预签名 URL 时，传入的 HTTP method 与 Azure Blob Storage 中的 SAS 权限映射表<br/>目前默认支持 GET （获取），PUT（上传），DELETE（删除）")
            .selectComponent().options(List.of(
                new FieldOption<>("GET", "GET,r"),
                new FieldOption<>("PUT", "PUT,w"),
                new FieldOption<>("DELETE", "DELETE,d"),
                new FieldOption<>("ALL", "ALL,racwdxytlmei")
            ))
            .multiple(true)
            .end()
            .build();
    }

    @Override
    public FileStorageProperties addStorageProperties(FileStorageProperties properties, String platform, String json) {
        Dict dict = JsonUtils.parseMap(json);
        FileStorageProperties.AzureBlobStorageConfig azureBlobStorageConfig =
            BeanUtil.copyProperties(dict, FileStorageProperties.AzureBlobStorageConfig.class, "methodToPermissionMap");

        // 处理methodToPermissionMap字段转换
        ArrayList<String> methodToPermissionList = dict.get("methodToPermissionMap", new ArrayList<>());
        Map<String, String> map = StreamUtils.toMap(methodToPermissionList,
            s -> s.split(",")[0], s -> s.split(",")[1]);
        azureBlobStorageConfig.setMethodToPermissionMap(map);

        azureBlobStorageConfig.setPlatform(platform);
        if (properties.getAzureBlob() == null) {
            properties.setAzureBlob(new ArrayList<>());
        }
        List<FileStorageProperties.AzureBlobStorageConfig> list = (List<FileStorageProperties.AzureBlobStorageConfig>) properties.getAzureBlob();
        list.add(azureBlobStorageConfig);
        return properties;
    }
}
