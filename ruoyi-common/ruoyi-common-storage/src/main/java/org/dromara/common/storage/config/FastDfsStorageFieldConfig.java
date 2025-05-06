package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.common.core.ui.FieldOption;

import java.util.List;

/**
 * FastDFS字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class FastDfsStorageFieldConfig implements StorageFieldConfig {
    /**
     * 运行模式，由于 FastDFS 比较特殊，不支持自定义文件名及路径，所以使用运行模式来解决这个问题。
     * 详情请查看：https://x-file-storage.xuyanwu.cn/2.2.0/#/%E5%AD%98%E5%82%A8%E5%B9%B3%E5%8F%B0?id=OCI_FastDFS
     */
    private FieldConfig<String> runMod;
    /** Tracker Server 地址（IP:PORT），多个用英文逗号隔开 */
    private FieldConfig<String> trackerServerAddr;
    /** Tracker HTTP端口，默认：80 */
    private FieldConfig<Integer> trackerHttpPort;
    /** Storage Server 地址:IP:PORT（当不使用 Tracker Server 时使用） */
    private FieldConfig<String> storageServerAddr;
    /** Store path，默认 0（当不使用 Tracker Server 时使用） */
    private FieldConfig<Integer> storageStorePath;
    /** 扩展信息，组名，可以为空 */
    private FieldConfig<String> groupName;
    /** 扩展信息，连接超时，单位：秒，默认：5s */
    private FieldConfig<Integer> connectTimeoutInSeconds;
    /** 扩展信息，套接字超时，单位：秒，默认：30s */
    private FieldConfig<Integer> networkTimeoutInSeconds;
    /** 扩展信息，字符编码，默认：UTF-8 */
    private FieldConfig<String> charset;
    /** 扩展信息，token 防盗链 默认：false */
    private FieldConfig<Boolean> httpAntiStealToken;
    /** 扩展信息，安全密钥，默认：FastDFS1234567890 */
    private FieldConfig<String> httpSecretKey;
    /** 扩展信息，是否启用连接池。默认：true */
    private FieldConfig<Boolean> connectionPoolEnabled;
    /** 扩展信息，#每一个IP:Port的最大连接数，0为没有限制，默认：100 */
    private FieldConfig<Integer> connectionPoolMaxCountPerEntry;
    /** 扩展信息，连接池最大空闲时间。单位：秒，默认：3600 */
    private FieldConfig<Integer> connectionPoolMaxIdleTime;
    /** 扩展信息，连接池最大等待时间。单位：毫秒，默认：1000 */
    private FieldConfig<Integer> connectionPoolMaxWaitTimeInMs;
    /** 访问域名 */
    private FieldConfig<String> domain;
    /**
     * 基础路径，强烈建议留空
     * 仅在上传成功时和获取文件时原样传到 FileInfo 及 RemoteFileInfo 中，可以用来保存到数据库中使用，
     * 实际上作用也不大，还会破坏 url 约定（url：实际上就是 domain + basePath + path + filename），
     * 约定详情见文档 https://x-file-storage.xuyanwu.cn/2.2.0/#/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98?id=%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6%E5%8F%8A-fileinfo-%E4%B8%AD%E5%90%84%E7%A7%8D%E8%B7%AF%E5%BE%84%EF%BC%88path%EF%BC%89%E7%9A%84%E5%8C%BA%E5%88%AB%EF%BC%9F
     * FastDFS 兼容性说明：https://x-file-storage.xuyanwu.cn/2.2.0/#/%E5%AD%98%E5%82%A8%E5%B9%B3%E5%8F%B0?id=OCI_FastDFS
     */
    private FieldConfig<String> basePath;
    /** 自动分片上传阈值，达到此大小则使用分片上传，默认 128MB */
    private FieldConfig<Integer> multipartThreshold;
    /** 自动分片上传时每个分片大小，默认 32MB */
    private FieldConfig<Integer> multipartPartSize;

    public FastDfsStorageFieldConfig() {
        this.runMod = FieldConfig.<String>builder()
            .component("select")
            .name("运行模式")
            .help("运行模式，由于 FastDFS 比较特殊，不支持自定义文件名及路径，所以使用运行模式来解决这个问题。<br/>" +
                "详情请查看：https://x-file-storage.xuyanwu.cn/2.2.0/#/%E5%AD%98%E5%82%A8%E5%B9%B3%E5%8F%B0?id=OCI_FastDFS")
            .required(true)
            .options(List.of(
                new FieldOption<>("覆盖模式", "COVER"),
                new FieldOption<>("URL模式", "URL")
            ))
            .build();
        this.trackerServerAddr = FieldConfig.<String>builder()
            .component("input")
            .name("Tracker Server 地址")
            .help("Tracker Server 地址（IP:PORT），多个用英文逗号隔开")
            .required(false)
            .build();
        this.trackerHttpPort = FieldConfig.<Integer>builder()
            .component("input-number")
            .value(80)
            .name("Tracker HTTP端口")
            .help("Tracker HTTP端口，默认：80")
            .required(false)
            .build();
        this.storageServerAddr = FieldConfig.<String>builder()
            .component("input")
            .name("Storage Server 地址")
            .help("Storage Server 地址:IP:PORT（当不使用 Tracker Server 时使用）")
            .required(false)
            .build();
        this.storageStorePath = FieldConfig.<Integer>builder()
            .component("input-number")
            .value(0)
            .name("Store path")
            .help("Store path，默认 0（当不使用 Tracker Server 时使用）")
            .required(false)
            .build();
        this.groupName = FieldConfig.<String>builder()
            .component("input")
            .name("组名")
            .help("扩展信息，组名，可以为空")
            .required(false)
            .build();
        this.connectTimeoutInSeconds = FieldConfig.<Integer>builder()
            .component("input-number")
            .value(5)
            .name("连接超时")
            .help("扩展信息，连接超时，单位：秒，默认：5s")
            .required(false)
            .build();
        this.networkTimeoutInSeconds = FieldConfig.<Integer>builder()
            .component("input-number")
            .value(30)
            .name("套接字超时")
            .help("扩展信息，套接字超时，单位：秒，默认：30s")
            .required(false)
            .build();
        this.charset = FieldConfig.<String>builder()
            .component("input")
            .value("UTF-8")
            .name("字符编码")
            .help("扩展信息，字符编码，默认：UTF-8")
            .required(false)
            .build();
        this.httpAntiStealToken = FieldConfig.<Boolean>builder()
            .component("switch")
            .value(false)
            .name("token 防盗链")
            .help("扩展信息，token 防盗链 默认：false")
            .required(false)
            .build();
        this.httpSecretKey = FieldConfig.<String>builder()
            .component("input")
            .value("FastDFS1234567890")
            .name("安全密钥")
            .help("扩展信息，安全密钥，默认：FastDFS1234567890")
            .required(false)
            .build();
        this.connectionPoolEnabled = FieldConfig.<Boolean>builder()
            .component("switch")
            .value(true)
            .name("是否启用连接池")
            .help("扩展信息，是否启用连接池 默认：true")
            .required(false)
            .build();
        this.connectionPoolMaxCountPerEntry = FieldConfig.<Integer>builder()
            .component("input-number")
            .value(100)
            .name("最大连接数")
            .help("扩展信息，#每一个IP:Port的最大连接数，0为没有限制，默认：100")
            .required(false)
            .build();
        this.connectionPoolMaxIdleTime = FieldConfig.<Integer>builder()
            .component("input-number")
            .value(3600)
            .name("最大空闲时间")
            .help("扩展信息，连接池最大空闲时间。单位：秒，默认：3600")
            .required(false)
            .build();
        this.connectionPoolMaxWaitTimeInMs = FieldConfig.<Integer>builder()
            .component("input-number")
            .value(1000)
            .name("最大等待时间")
            .help("扩展信息，连接池最大等待时间。单位：毫秒，默认：1000")
            .required(false)
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
            .help("基础路径，强烈建议留空<br/>" +
                "仅在上传成功时和获取文件时原样传到 FileInfo 及 RemoteFileInfo 中，可以用来保存到数据库中使用，<br/>" +
                "实际上作用也不大，还会破坏 url 约定（url：实际上就是 domain + basePath + path + filename），<br/>" +
                "约定详情见文档 https://x-file-storage.xuyanwu.cn/2.2.0/#/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98?id=%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6%E5%8F%8A-fileinfo-%E4%B8%AD%E5%90%84%E7%A7%8D%E8%B7%AF%E5%BE%84%EF%BC%88path%EF%BC%89%E7%9A%84%E5%8C%BA%E5%88%AB%EF%BC%9F<br/>" +
                "FastDFS 兼容性说明：https://x-file-storage.xuyanwu.cn/2.2.0/#/%E5%AD%98%E5%82%A8%E5%B9%B3%E5%8F%B0?id=OCI_FastDFS")
            .required(false)
            .build();
        this.multipartThreshold = FieldConfig.<Integer>builder()
            .component("input-number")
            .value(128 * 1024 * 1024)
            .name("分片阈值")
            .help("自动分片上传阈值，达到此大小则使用分片上传，默认 128MB")
            .required(true)
            .build();
        this.multipartPartSize = FieldConfig.<Integer>builder()
            .component("input-number")
            .value(32 * 1024 * 1024)
            .name("分片大小")
            .help("自动分片上传时每个分片大小，默认 32MB")
            .required(true)
            .build();
    }
}
