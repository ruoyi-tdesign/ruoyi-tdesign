package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;

import java.nio.charset.StandardCharsets;

/**
 * Sftp字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class SftpStorageFieldConfig implements StorageFieldConfig {
    /** 主机 */
    private FieldConfig<String> host;
    /** 端口，默认22 */
    private FieldConfig<Integer> port;
    /** 用户名 */
    private FieldConfig<String> user;
    /** 密码 */
    private FieldConfig<String> password;
    /** 私钥路径 */
    private FieldConfig<String> privateKeyPath;
    /** 编码，默认UTF-8 */
    private FieldConfig<String> charset;
    /**
     * 连接超时时长，单位毫秒，默认10秒 {@link org.apache.commons.net.SocketClient#setConnectTimeout(int)}
     */
    private FieldConfig<Long> connectionTimeout;
    /** 基础路径 */
    private FieldConfig<String> basePath;
    /** 存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾 */
    private FieldConfig<String> storagePath;
    /** 访问域名 */
    private FieldConfig<String> domain;

    public SftpStorageFieldConfig() {
        this.host = FieldConfig.<String>builder()
            .useInput()
            .name("主机")
            .required(true)
            .build();
        this.port = FieldConfig.<Integer>builder()
            .useInputNumber()
            .value(22)
            .name("端口")
            .help("端口，默认22")
            .required(true)
            .build();
        this.user = FieldConfig.<String>builder()
            .useInput()
            .name("用户名")
            .required(true)
            .build();
        this.password = FieldConfig.<String>builder()
            .useInput()
            .name("密码")
            .inputComponent().type("password").end()
            .required(false)
            .build();
        this.privateKeyPath = FieldConfig.<String>builder()
            .useInput()
            .name("私钥路径")
            .required(false)
            .build();
        this.charset = FieldConfig.<String>builder()
            .useInput()
            .value(StandardCharsets.UTF_8.name())
            .name("编码")
            .help("编码，默认UTF-8")
            .required(true)
            .build();
        this.connectionTimeout = FieldConfig.<Long>builder()
            .useInputNumber()
            .value(10 * 1000L)
            .name("连接超时时长")
            .help("连接超时时长，单位毫秒，默认10秒")
            .required(true)
            .build();
        this.basePath = FieldConfig.<String>builder()
            .useInput()
            .name("基础路径")
            .required(false)
            .build();
        this.storagePath = FieldConfig.<String>builder()
            .useInput()
            .value("/")
            .name("存储路径")
            .help("存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .useInput()
            .name("访问域名")
            .required(false)
            .build();
    }
}
