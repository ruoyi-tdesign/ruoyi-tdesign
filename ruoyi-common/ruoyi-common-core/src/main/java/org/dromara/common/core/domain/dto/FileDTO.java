package org.dromara.common.core.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件存储对象
 *
 * @author hexm
 */
@Data
@NoArgsConstructor
public class FileDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件存储主键
     */
    private Long fileId;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 原名
     */
    private String originalFilename;

    /**
     * 文件扩展名
     */
    private String ext;

    /**
     * 预览地址
     */
    private String previewUrl;

    /**
     * 下载地址
     */
    private String downloadUrl;

}
