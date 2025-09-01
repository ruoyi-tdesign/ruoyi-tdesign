package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 上传对象信息
 *
 * @author hexm
 */
@Data
public class SysFileUploadVo implements Serializable {

    /**
     * 文件名
     */
    private String filename;

    /**
     * 主键
     */
    private String fileId;

    /**
     * 预览地址
     */
    private String previewUrl;

    /**
     * 下载地址
     */
    private String downloadUrl;

}
