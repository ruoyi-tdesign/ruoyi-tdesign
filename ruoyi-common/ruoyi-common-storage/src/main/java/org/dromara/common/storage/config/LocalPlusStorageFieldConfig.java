package org.dromara.common.storage.config;

import lombok.Data;
import org.dromara.common.core.ui.FieldConfig;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.x.file.storage.core.FileStorageProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地存储升级版字段配置
 *
 * @author hexm
 * @date 2025/5/3
 */
@Data
public class LocalPlusStorageFieldConfig implements StorageFieldConfig {
    /** 基础路径 */
    private FieldConfig<String> basePath;
    /** 存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾 */
    private FieldConfig<String> storagePath;
    /** 访问域名 */
    private FieldConfig<String> domain;

    public LocalPlusStorageFieldConfig() {
        this.basePath = FieldConfig.<String>builder()
            .useInput()
            .label("基础路径")
            .help("主要用于不同的环境或项目共用同一个存储平台的情况，这时就可以通过 basePath 来区分，例如这里的 dev/ 就表示开发环境，测试环境就可以配置成 test/ ，如果不需要可以留空")
            .required(false)
            .build();
        this.storagePath = FieldConfig.<String>builder()
            .useInput()
            .value("/")
            .label("存储路径")
            .help("存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾。<br />主要用于配合 Nginx 实现文件对外访问，文件上传后的实际存储地址为 存储路径 + 基础路径 + 上传路径 + 文件名，不会对外暴露，仅在本地升级版、FTP、SFTP 等需要自行搭建访问服务存储平台的配置文件中，对象存储等自带访问服务的存储平台没有此参数，例如 /www/wwwroot/file.abc.com/或者D:/Temp/ 就刚好可配合 Nginx 实现文件对外访问")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .useInput()
            .label("访问域名")
            .help("访问域名，如果不需要可以留空。最终url实际就是 访问域名 + 基础路径 + 上传路径 + 文件名。<br/>例如：“http://127.0.0.1:8030/file/”，“/”结尾，本地存储建议使用相对路径，方便后期更换域名")
            .required(false)
            .build();
    }


    @Override
    public FileStorageProperties addStorageProperties(FileStorageProperties properties, String platform, String json) {
        FileStorageProperties.LocalPlusConfig localPlusConfig = JsonUtils.parseObject(json, FileStorageProperties.LocalPlusConfig.class);
        localPlusConfig.setPlatform(platform);
        if (properties.getLocalPlus() == null) {
            properties.setLocalPlus(new ArrayList<>());
        }
        List<FileStorageProperties.LocalPlusConfig> list = (List<FileStorageProperties.LocalPlusConfig>) properties.getLocalPlus();
        list.add(localPlusConfig);
        return properties;
    }
}
