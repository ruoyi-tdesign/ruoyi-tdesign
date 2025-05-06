package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;

import java.nio.charset.StandardCharsets;

/**
 * Ftp字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class FtpStorageFieldConfig implements StorageFieldConfig {
    /** 主机 */
    private FieldConfig<String> host;
    /** 端口，默认21 */
    private FieldConfig<Integer> port;
    /** 用户名，默认 anonymous（匿名） */
    private FieldConfig<String> user;
    /** 密码，默认空 */
    private FieldConfig<String> password;
    /** 编码，默认UTF-8 */
    private FieldConfig<String> charset;
    /**
     * 连接超时时长，单位毫秒，默认10秒 {@link org.apache.commons.net.SocketClient#setConnectTimeout(int)}
     */
    private FieldConfig<Long> connectionTimeout;
    /**
     * Socket连接超时时长，单位毫秒，默认10秒 {@link org.apache.commons.net.SocketClient#setSoTimeout(int)}
     */
    private FieldConfig<Long> soTimeout;
    /**
     * 设置服务器语言，默认空，{@link org.apache.commons.net.ftp.FTPClientConfig#setServerLanguageCode(String)}
     */
    private FieldConfig<String> serverLanguageCode;
    /**
     * 服务器标识，默认空，{@link org.apache.commons.net.ftp.FTPClientConfig#FTPClientConfig(String)}
     * 例如：org.apache.commons.net.ftp.FTPClientConfig.SYST_NT
     */
    private FieldConfig<String> systemKey;
    /** 是否主动模式，默认被动模式 */
    private FieldConfig<Boolean> isActive;
    /** 基础路径 */
    private FieldConfig<String> basePath;
    /** 存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾 */
    private FieldConfig<String> storagePath;
    /** 访问域名 */
    private FieldConfig<String> domain;

    public FtpStorageFieldConfig() {
        this.host = FieldConfig.<String>builder()
            .component("input")
            .name("主机")
            .required(true)
            .build();
        this.port = FieldConfig.<Integer>builder()
            .component("input-number")
            .value(21)
            .name("端口")
            .help("端口，默认21")
            .required(true)
            .build();
        this.user = FieldConfig.<String>builder()
            .component("input")
            .value("anonymous")
            .name("用户名")
            .help("用户名，默认 anonymous（匿名）")
            .required(true)
            .build();
        this.password = FieldConfig.<String>builder()
            .component("input")
            .name("密码")
            .help("密码，默认空")
            .type("password")
            .required(false)
            .build();
        this.charset = FieldConfig.<String>builder()
            .component("input")
            .value(StandardCharsets.UTF_8.name())
            .name("编码")
            .help("编码，默认UTF-8")
            .required(true)
            .build();
        this.connectionTimeout = FieldConfig.<Long>builder()
            .component("input-number")
            .value(10 * 1000L)
            .name("连接超时时长")
            .help("连接超时时长，单位毫秒，默认10秒")
            .required(true)
            .build();
        this.soTimeout = FieldConfig.<Long>builder()
            .component("input-number")
            .value(10 * 1000L)
            .name("Socket连接超时时长")
            .help("Socket连接超时时长，单位毫秒，默认10秒")
            .required(true)
            .build();
        this.serverLanguageCode = FieldConfig.<String>builder()
            .component("input")
            .name("服务器语言")
            .help("设置服务器语言，默认空")
            .required(false)
            .build();
        this.systemKey = FieldConfig.<String>builder()
            .component("input")
            .name("服务器标识")
            .help("服务器标识，默认空。例如：org.apache.commons.net.ftp.FTPClientConfig.SYST_NT")
            .required(false)
            .build();
        this.isActive = FieldConfig.<Boolean>builder()
            .component("switch")
            .value(false)
            .name("是否主动模式")
            .help("是否主动模式，默认被动模式")
            .required(true)
            .build();
        this.basePath = FieldConfig.<String>builder()
            .component("input")
            .name("基础路径")
            .required(false)
            .build();
        this.storagePath = FieldConfig.<String>builder()
            .component("input")
            .value("/")
            .name("存储路径")
            .help("存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .component("input")
            .name("访问域名")
            .required(false)
            .build();
    }
}
