package org.dromara.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.SysOssConfig;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 对象存储配置视图对象 sys_oss_config
 *
 * @author Lion Li
 * @author 孤舟烟雨
 * @date 2021-08-13
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysOssConfig.class)
public class SysOssConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主建")
    private Long ossConfigId;

    /**
     * 配置key
     */
    @ExcelProperty(value = "配置key")
    private String configKey;

    /**
     * accessKey
     */
    @ExcelProperty(value = "accessKey")
    private String accessKey;

    /**
     * 秘钥
     */
    @ExcelProperty(value = "秘钥")
    private String secretKey;

    /**
     * 桶名称
     */
    @ExcelProperty(value = "桶名称")
    private String bucketName;

    /**
     * 前缀
     */
    @ExcelProperty(value = "前缀")
    private String prefix;

    /**
     * 访问站点
     */
    @ExcelProperty(value = "访问站点")
    private String endpoint;

    /**
     * 自定义域名
     */
    @ExcelProperty(value = "自定义域名")
    private String domain;

    /**
     * 是否https（Y=是,N=否）
     */
    @ExcelProperty(value = "是否https")
    private String isHttps;

    /**
     * 域
     */
    @ExcelProperty(value = "域")
    private String region;

    /**
     * 桶权限类型(0=private 1=public 2=custom)
     */
    @ExcelProperty(value = "桶权限类型(0=private 1=public 2=custom)")
    private String accessPolicy;

    /**
     * 是否默认（1=是,0=否）
     */
    @ExcelProperty(value = "是否默认")
    private String status;

    /**
     * 扩展字段
     */
    @ExcelProperty(value = "扩展字段")
    private String ext1;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

}
