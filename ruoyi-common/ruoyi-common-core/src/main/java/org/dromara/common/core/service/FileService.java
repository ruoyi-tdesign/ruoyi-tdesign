package org.dromara.common.core.service;

import cn.hutool.core.convert.Convert;
import org.dromara.common.core.domain.dto.FileDTO;
import org.dromara.common.core.utils.StringUtils;

import java.util.Collection;
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
    String selectUrlByIds(Collection<Long> fileIds);

    /**
     * 通过fileId查询列表
     *
     * @param fileIds fileId串逗号分隔
     * @return 列表
     */
    List<FileDTO> selectByIds(Collection<Long> fileIds);

    /**
     * 通过fileId查询对应的url
     *
     * @param fileIds fileId串逗号分隔
     * @return url串逗号分隔
     */
    default String selectUrlByIds(String fileIds) {
        List<Long> ids = StringUtils.splitTo(fileIds, Convert::toLong);
        return selectUrlByIds(ids);
    }

    /**
     * 通过fileId查询列表
     *
     * @param fileIds fileId串逗号分隔
     * @return 列表
     */
    default List<FileDTO> selectByIds(String fileIds) {
        List<Long> ids = StringUtils.splitTo(fileIds, Convert::toLong);
        return selectByIds(ids);
    }
}
