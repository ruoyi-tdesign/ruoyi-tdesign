package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 上传对象信息
 *
 * @author Michelle.Chung
 */
@Data
public class SysFileUploadVo implements Serializable {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 主键
     */
    private String fileId;

}
