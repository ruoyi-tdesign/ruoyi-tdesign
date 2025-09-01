package org.dromara.system.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import org.dromara.common.mybatis.core.domain.BasePageQuery;

/**
 * 文件分片信息，仅在手动分片上传时使用查询对象 sys_file_part
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysFilePartQuery extends BasePageQuery {

    /**
     * 存储配置id
     */
    private Long storageConfigId;

    /**
     * 上传ID，仅在手动分片上传时使用
     */
    private String uploadId;

    /**
     * 分片 ETag
     */
    private String eTag;

    /**
     * 分片号。每一个上传的分片都有一个分片号，一般情况下取值范围是1~10000
     */
    private Integer partNumber;

    /**
     * 文件大小，单位字节
     */
    private Long partSize;

    /**
     * 哈希信息
     */
    private String hashInfo;

}
