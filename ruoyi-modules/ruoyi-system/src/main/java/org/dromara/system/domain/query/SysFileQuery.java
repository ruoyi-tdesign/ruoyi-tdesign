package org.dromara.system.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BasePageQuery;

/**
 * 文件记录查询对象 sys_file
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysFileQuery extends BasePageQuery {

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 原始文件名
     */
    private String originalFilename;

    /**
     * 基础存储路径
     */
    private String basePath;

    /**
     * 存储路径
     */
    private String path;

    /**
     * 文件扩展名
     */
    private String ext;

    /**
     * MIME类型
     */
    private String contentType;

    /**
     * 文件ACL
     */
    private String fileAcl;

    /**
     * 分类id
     */
    private Long fileCategoryId;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 是否锁定状态
     */
    private Integer isLock;

    /**
     * 上传人
     */
    private Long createBy;

    /**
     * 上传人
     */
    private String createByName;

    /**
     * 多个文件后缀
     */
    private String[] suffixes;


    /**
     * 文件最大字节长度
     */
    private Long maxSize;

    /**
     * 内容类型
     */
    private String[] contentTypes;
}
