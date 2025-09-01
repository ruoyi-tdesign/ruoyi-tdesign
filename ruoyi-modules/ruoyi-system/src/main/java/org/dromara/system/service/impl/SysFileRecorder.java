package org.dromara.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.system.domain.SysFile;
import org.dromara.system.service.ISysFilePartService;
import org.dromara.system.service.ISysFileService;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.hash.HashInfo;
import org.dromara.x.file.storage.core.recorder.FileRecorder;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 文件记录器，用于记录文件信息到数据库
 *
 * @author hexm
 * @date 2025/6/1
 */
@Service
public class SysFileRecorder implements FileRecorder {

    @Autowired
    private ISysFileService fileService;
    @Autowired
    private ISysFilePartService filePartService;

    /**
     * 保存文件信息到数据库
     */
    @SneakyThrows
    @Override
    public boolean save(FileInfo info) {
        SysFile detail = toSysFile(info);
        boolean b = fileService.save(detail);
        if (b) {
            info.setId(detail.getFileId().toString());
        }
        return b;
    }

    /**
     * 更新文件记录，可以根据文件 ID 或 URL 来更新文件记录，
     * 主要用在手动分片上传文件-完成上传，作用是更新文件信息
     */
    @Override
    public void update(FileInfo info) {
        SysFile detail = toSysFile(info);
        fileService.lambdaUpdate()
            .eq(detail.getUrl() != null, SysFile::getUrl, detail.getUrl())
            .eq(detail.getFileId() != null, SysFile::getFileId, detail.getFileId())
            .update(detail);
    }

    /**
     * 根据 url 查询文件信息
     */
    @Override
    public FileInfo getByUrl(String url) {
        return toFileInfo(fileService.lambdaQuery().eq(SysFile::getUrl, url).one());
    }

    /**
     * 根据 url 删除文件信息
     */
    @Override
    public boolean delete(String url) {
        fileService.lambdaUpdate().eq(SysFile::getUrl, url).remove();
        return true;
    }

    /**
     * 保存文件分片信息
     *
     * @param filePartInfo 文件分片信息
     */
    @Override
    public void saveFilePart(FilePartInfo filePartInfo) {
        filePartService.saveFilePart(filePartInfo);
    }

    /**
     * 删除文件分片信息
     */
    @Override
    public void deleteFilePartByUploadId(String uploadId) {
        filePartService.deleteFilePartByUploadId(uploadId);
    }

    /**
     * 将 FileInfo 转为 SysFile
     */
    public static SysFile toSysFile(FileInfo info) {
        SysFile detail = BeanUtil.copyProperties(
            info, SysFile.class, "metadata", "userMetadata", "thMetadata", "thUserMetadata", "attr", "hashInfo");

        // 这里手动获 元数据 并转成 json 字符串，方便存储在数据库中
        detail.setMetadata(JsonUtils.toJsonString(info.getMetadata()));
        detail.setUserMetadata(JsonUtils.toJsonString(info.getUserMetadata()));
        detail.setThMetadata(JsonUtils.toJsonString(info.getThMetadata()));
        detail.setThUserMetadata(JsonUtils.toJsonString(info.getThUserMetadata()));
        // 这里手动获 取附加属性字典 并转成 json 字符串，方便存储在数据库中
        detail.setAttr(JsonUtils.toJsonString(info.getAttr()));
        // 这里手动获 哈希信息 并转成 json 字符串，方便存储在数据库中
        detail.setHashInfo(JsonUtils.toJsonString(info.getHashInfo()));
        detail.setStorageConfigId(Long.parseLong(info.getPlatform()));
        return detail;
    }

    /**
     * 将 SysFile 转为 FileInfo
     */
    public static FileInfo toFileInfo(SysFile detail) {
        FileInfo info = BeanUtil.copyProperties(
            detail, FileInfo.class, "metadata", "userMetadata", "thMetadata", "thUserMetadata", "attr", "hashInfo");

        info.setPlatform(detail.getStorageConfigId().toString());
        // 这里手动获取数据库中的 json 字符串 并转成 元数据，方便使用
        info.setMetadata(jsonToMetadata(detail.getMetadata()));
        info.setUserMetadata(jsonToMetadata(detail.getUserMetadata()));
        info.setThMetadata(jsonToMetadata(detail.getThMetadata()));
        info.setThUserMetadata(jsonToMetadata(detail.getThUserMetadata()));
        // 这里手动获取数据库中的 json 字符串 并转成 附加属性字典，方便使用
        info.setAttr(JsonUtils.parseMap(detail.getAttr()));
        // 这里手动获取数据库中的 json 字符串 并转成 哈希信息，方便使用
        info.setHashInfo(jsonToHashInfo(detail.getHashInfo()));
        return info;
    }

    /**
     * 将 json 字符串转换成元数据对象
     */
    public static Map<String, String> jsonToMetadata(String json) {
        return JsonUtils.parseObject(json, new TypeReference<>() {
        });
    }

    /**
     * 将 json 字符串转换成哈希信息对象
     */
    public static HashInfo jsonToHashInfo(String json) {
        return JsonUtils.parseObject(json, HashInfo.class);
    }
}
