package org.dromara.system.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * OSS对象存储查询对象 sys_oss
 *
 * @author hexm
 * @date 2023-04-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysOssQuery extends BaseEntity {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL地址
     */
    private String url;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 是否锁定状态
     */
    private Integer isLock;

    /**
     * 是否显示在列表
     */
    private Integer isList;

    /**
     * 上传人
     */
    private Long createBy;

    /**
     * 上传人
     */
    private String createByName;

    /**
     * 服务商
     */
    private String service;

}
