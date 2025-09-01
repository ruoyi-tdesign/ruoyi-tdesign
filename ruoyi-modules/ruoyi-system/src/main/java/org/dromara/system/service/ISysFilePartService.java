package org.dromara.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.system.domain.SysFilePart;
import org.dromara.x.file.storage.core.upload.FilePartInfo;

/**
 * 文件分片信息，仅在手动分片上传时使用Service接口
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
public interface ISysFilePartService extends IService<SysFilePart> {

    /**
     * 保存文件分片信息
     *
     * @param info 文件分片信息
     */
    void saveFilePart(FilePartInfo info);

    /**
     * 删除文件分片信息
     */
    void deleteFilePartByUploadId(String uploadId);

    /**
     * 将 FilePartInfo 转成 FilePartDetail
     *
     * @param info 文件分片信息
     */
    SysFilePart toFilePart(FilePartInfo info);
}
