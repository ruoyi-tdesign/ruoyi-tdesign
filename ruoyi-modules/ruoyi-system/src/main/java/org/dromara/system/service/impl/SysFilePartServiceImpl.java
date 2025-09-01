package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.system.domain.SysFilePart;
import org.dromara.system.mapper.SysFilePartMapper;
import org.dromara.system.service.ISysFilePartService;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.springframework.stereotype.Service;

/**
 * 文件分片信息，仅在手动分片上传时使用Service业务层处理
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
@Service
public class SysFilePartServiceImpl extends ServiceImpl<SysFilePartMapper, SysFilePart> implements ISysFilePartService {

    /**
     * 保存文件分片信息
     *
     * @param info 文件分片信息
     */
    @Override
    public void saveFilePart(FilePartInfo info) {
        SysFilePart detail = toFilePart(info);
        if (save(detail)) {
            info.setId(detail.getFilePartId().toString());
        }
    }

    /**
     * 删除文件分片信息
     */
    @Override
    public void deleteFilePartByUploadId(String uploadId) {
        lambdaUpdate().eq(SysFilePart::getUploadId, uploadId).remove();
    }

    /**
     * 将 FilePartInfo 转成 FilePartDetail
     *
     * @param info 文件分片信息
     */
    @Override
    public SysFilePart toFilePart(FilePartInfo info) {
        SysFilePart detail = new SysFilePart();
        detail.setStorageConfigId(Long.parseLong(info.getPlatform()));
        detail.setUploadId(info.getUploadId());
        detail.setETag(info.getETag());
        detail.setPartNumber(info.getPartNumber());
        detail.setPartSize(info.getPartSize());
        detail.setHashInfo(JsonUtils.toJsonString(info.getHashInfo()));
        detail.setCreateTime(info.getCreateTime());
        return detail;
    }
}
