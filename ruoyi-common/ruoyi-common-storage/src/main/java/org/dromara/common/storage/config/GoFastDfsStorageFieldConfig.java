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
 * GoFastDFS存储字段配置
 *
 * @author hexm
 * @date 2025/8/2
 */
@Data
public class GoFastDfsStorageFieldConfig implements StorageFieldConfig {
    /** 服务地址 */
    private FieldConfig<String> server;
    /** 服务器组名 */
    private FieldConfig<String> group;
    /** 服务器场景 */
    private FieldConfig<String> scene;
    /** 超时时间 */
    private FieldConfig<Integer> timeout;
    /** 访问域名 */
    private FieldConfig<String> domain;
    /** 基础路径 */
    private FieldConfig<String> basePath;

    public GoFastDfsStorageFieldConfig() {
        this.server = FieldConfig.<String>builder()
            .useInput()
            .label("服务地址")
            .help("服务端地址 例如http://172.26.11.44:10081。<br/>兼容性说明：<br/>" +
                "1、使用前需要先配置 IP 白名单，打开 go-fastdfs 的配置文件，将 admin_ips 设置为你的 IP ，例如 [\"192.168.1.2\"]，然后重启 go-fastdfs<br/>" +
                "2、由于多次上传相同的文件，go-fastdfs 会自动合并这些文件，导致后上传的文件无法访问，所以需要关闭文件去重功能，打开 go-fastdfs 的配置文件，将 enable_distinct_file 设置为 false，然后重启 go-fastdfs<br/>" +
                "3、开启支持自定义路径功能，打开 go-fastdfs 的配置文件，将 enable_custom_path 设置为 true，然后重启 go-fastdfs")
            .required(true)
            .build();
        this.group = FieldConfig.<String>builder()
            .useInput()
            .label("服务器组名")
            .help("服务端组名。例如：group1")
            .required(true)
            .build();
        this.scene = FieldConfig.<String>builder()
            .useInput()
            .label("服务器场景")
            .help("上传场景。例如：scene")
            .required(true)
            .build();
        this.timeout = FieldConfig.<Integer>builder()
            .inputNumberComponent().min(1000L).end()
            .value(30000)
            .label("超时时间(毫秒)")
            .help("访问服务端超时时间")
            .required(true)
            .build();
        this.domain = FieldConfig.<String>builder()
            .useInput()
            .value("")
            .label("访问域名")
            .help("访问域名，注意“/”结尾，例如：https://file.abc.com/")
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
        FileStorageProperties.GoFastDfsConfig goFastDfsConfig = JsonUtils.parseObject(json, FileStorageProperties.GoFastDfsConfig.class);
        goFastDfsConfig.setPlatform(platform);
        if (properties.getGoFastdfs() == null) {
            properties.setGoFastdfs(new ArrayList<>());
        }
        List<FileStorageProperties.GoFastDfsConfig> list = (List<FileStorageProperties.GoFastDfsConfig>) properties.getGoFastdfs();
        list.add(goFastDfsConfig);
        return properties;
    }
}
