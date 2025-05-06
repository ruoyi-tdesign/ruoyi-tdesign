package org.dromara.common.storage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存储类型枚举
 *
 * @author hexm
 * @date 2025/5/2
 */
@Getter
@AllArgsConstructor
public enum StorageEnum {
    /** 本地存储升级版 */
    LOCAL_PLUS("local_plus", "本地存储", "cn.hutool.core.io.FileUtil"),
    /** 华为云 OBS */
    HUAWEI_OBS("huawei_obs", "华为云 OBS", "com.obs.services.ObsClient"),
    /** 阿里云 OSS */
    ALIYUN_OSS("aliyun_oss", "阿里云 OSS", "com.aliyun.oss.OSS"),
    /** 七牛云 Kodo */
    QINIU_KODO("qiniu_kodo", "七牛云 Kodo", "com.qiniu.http.Client"),
    /** 腾讯云 COS */
    TENCENT_COS("tencent_cos", "腾讯云 COS", "com.qcloud.cos.COSClient"),
    /** 百度云 BOS */
    BAIDU_BOS("baidu_bos", "百度云 BOS", "com.baidubce.services.bos.BosClient"),
    /** 又拍云 USS */
    UPYUN_USS("upyun_uss", "又拍云 USS", "com.upyun.RestManager"),
    /** MinIO */
    MINIO("minio", "MinIO", "io.minio.MinioClient"),
    /** Amazon S3 */
    AMAZON_S3("amazon_s3", "Amazon S3", "com.amazonaws.services.s3.AmazonS3"),
    /** FTP */
    FTP("ftp", "FTP", "cn.hutool.extra.ftp.Ftp"),
    /** SFTP */
    SFTP("sftp", "SFTP", "cn.hutool.extra.ssh.Sftp"),
    /** WebDAV */
    WEB_DAV("web_dav", "WebDAV", "com.github.sardine.Sardine"),
    /** GoogleCloud Storage */
    GOOGLE_CLOUD_STORAGE("google_cloud_storage", "GoogleCloud Storage", "com.google.cloud.storage.Storage"),
    /** FastDFS */
    FAST_DFS("fast_dfs", "FastDFS", "org.csource.fastdfs.StorageClient"),
    /** Azure Blob Storage */
    AZURE_BLOB_STORAGE("azure_blob_storage", "Azure Blob Storage", "com.azure.storage.blob.BlobServiceClient");

    /** 存储平台key */
    private final String code;
    /** 存储平台描述 */
    private final String desc;
    /** 客户端类名 */
    private final String clientClassName;
}
