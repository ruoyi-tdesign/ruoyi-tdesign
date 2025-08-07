package org.dromara.common.core.service;

import org.dromara.common.core.domain.dto.FileDTO;
import org.dromara.common.core.domain.dto.OssDTO;

import java.util.List;

/**
 * 通用 文件存储服务
 *
 * @author hexm
 */
public interface FileService {

    /**
     * 通过fileId查询对应的url
     *
     * @param fileIds fileId串逗号分隔
     * @return url串逗号分隔
     */
    String selectUrlByIds(String fileIds);

    /**
     * 通过fileId查询列表
     *
     * @param fileIds fileId串逗号分隔
     * @return 列表
     */
    List<FileDTO> selectByIds(String fileIds);
}
