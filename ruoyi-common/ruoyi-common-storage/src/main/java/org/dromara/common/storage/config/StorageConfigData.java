package org.dromara.common.storage.config;

import org.dromara.common.storage.enums.StorageEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储配置数据
 *
 * @author hexm
 * @date 2025/5/4
 */
public class StorageConfigData {

    public static final Map<String, StorageFieldConfig> STORAGE_CONFIG_MAP;

    static {
        STORAGE_CONFIG_MAP = new HashMap<>();
        STORAGE_CONFIG_MAP.put(StorageEnum.LOCAL_PLUS.getCode(), new LocalPlusStorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.HUAWEI_OBS.getCode(), new HuaweiObsStorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.ALIYUN_OSS.getCode(), new AliyunOssStorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.QINIU_KODO.getCode(), new QiniuKodoStorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.TENCENT_COS.getCode(), new TencentCosStorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.BAIDU_BOS.getCode(), new BaiduBosStorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.UPYUN_USS.getCode(), new UpyunUssStorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.MINIO.getCode(), new MinioStorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.AMAZON_S3.getCode(), new AmazonS3StorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.FTP.getCode(), new FtpStorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.SFTP.getCode(), new SftpStorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.WEB_DAV.getCode(), new WebDavStorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.GOOGLE_CLOUD_STORAGE.getCode(), new GoogleCloudStorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.FAST_DFS.getCode(), new FastDfsStorageFieldConfig());
        STORAGE_CONFIG_MAP.put(StorageEnum.AZURE_BLOB_STORAGE.getCode(), new AzureBlobStorageFieldConfig());
    }

}
