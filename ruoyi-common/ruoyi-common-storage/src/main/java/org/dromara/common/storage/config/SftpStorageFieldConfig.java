package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.x.file.storage.core.FileStorageProperties;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
            .label("主机")
            .help("主机，例如：192.168.1.105")
            .required(true)
            .build();
        this.port = FieldConfig.<Integer>builder()
            .useInputNumber()
            .value(22)
            .label("端口")
            .help("端口，默认22")
            .required(true)
            .build();
        this.user = FieldConfig.<String>builder()
            .useInput()
            .label("用户名")
            .help("用户名。例如：root")
            .required(true)
            .build();
        this.password = FieldConfig.<String>builder()
            .useInput()
            .label("密码")
            .help("密码或私钥路径")
            .inputComponent().type("password").end()
            .required(false)
            .build();
        this.privateKeyPath = FieldConfig.<String>builder()
            .useInput()
            .label("私钥路径")
            .help("私钥路径，兼容Spring的ClassPath路径、文件路径、HTTP路径等，例如：classpath:id_rsa_2048")
            .required(false)
            .build();
        this.charset = FieldConfig.<String>builder()
            .useInput()
            .value(StandardCharsets.UTF_8.name())
            .label("编码")
            .help("编码，默认UTF-8")
            .required(true)
            .build();
        this.connectionTimeout = FieldConfig.<Long>builder()
            .useInputNumber()
            .value(10 * 1000L)
            .label("连接超时时长")
            .help("连接超时时长，单位毫秒，默认10秒")
            .required(true)
            .build();
        this.basePath = FieldConfig.<String>builder()
            .useInput()
            .label("基础路径")
            .required(false)
            .build();
        this.storagePath = FieldConfig.<String>builder()
            .useInput()
            .value("/")
            .label("存储路径")
            .help("存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾。例如：/www/wwwroot/file.abc.com/")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .useInput()
            .label("访问域名")
            .help("访问域名，注意“/”结尾，例如：https://file.abc.com/")
            .required(false)
            .build();
    }

    @Override
    public FileStorageProperties addStorageProperties(FileStorageProperties properties, String platform, String json) {
        FileStorageProperties.SftpConfig sftpConfig = JsonUtils.parseObject(json, FileStorageProperties.SftpConfig.class);
        sftpConfig.setPlatform(platform);
        if (properties.getSftp() == null) {
            properties.setSftp(new ArrayList<>());
        }
        List<FileStorageProperties.SftpConfig> list = (List<FileStorageProperties.SftpConfig>) properties.getSftp();
        list.add(sftpConfig);
        return properties;
    }
}
