package org.dromara.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.SysFile;
import org.dromara.system.domain.bo.SysFileBo;
import org.dromara.system.domain.query.SysFileQuery;
import org.dromara.system.domain.vo.SysFileVo;
import org.dromara.system.mapper.SysFileMapper;
import org.dromara.system.service.ISysFilePartService;
import org.dromara.system.service.ISysFileService;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.hash.HashInfo;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 文件记录Service业务层处理
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService {

    @Autowired
    private ISysFilePartService filePartService;

    /**
     * 查询文件记录
     *
     * @param fileId 主键
     * @return SysFileVo
     */
    @Override
    public SysFileVo queryById(Long fileId) {
        return baseMapper.selectVoById(fileId);
    }

    /**
     * 分页查询文件记录列表
     *
     * @param query 查询对象
     * @return 文件记录分页列表
     */
    @Override
    public TableDataInfo<SysFileVo> queryPageList(SysFileQuery query) {
        return PageQuery.of(() -> baseMapper.queryList(query));
    }

    /**
     * 查询文件记录列表
     *
     * @param query 查询对象
     * @return 文件记录列表
     */
    @Override
    public List<SysFileVo> queryList(SysFileQuery query) {
        return baseMapper.queryList(query);
    }

    /**
     * 修改文件记录
     *
     * @param bo 文件记录编辑业务对象
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SysFileBo bo) {
        SysFile update = MapstructUtils.convert(bo, SysFile.class);
        return updateById(update);
    }

    /**
     * 批量删除文件记录信息
     *
     * @param ids 待删除的主键集合
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        return removeByIds(ids);
    }

    /**
     * 保存文件信息到数据库
     */
    @SneakyThrows
    @Override
    public boolean save(FileInfo info) {
        SysFile detail = toSysFile(info);
        boolean b = save(detail);
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
        lambdaUpdate()
            .eq(detail.getUrl() != null, SysFile::getUrl, detail.getUrl())
            .eq(detail.getFileId() != null, SysFile::getFileId, detail.getFileId())
            .update(detail);
    }

    /**
     * 根据 url 查询文件信息
     */
    @Override
    public FileInfo getByUrl(String url) {
        return toFileInfo(lambdaQuery().eq(SysFile::getUrl, url).one());
    }

    /**
     * 根据 url 删除文件信息
     */
    @Override
    public boolean delete(String url) {
        lambdaUpdate().eq(SysFile::getUrl, url).remove();
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
    public SysFile toSysFile(FileInfo info) {
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
        return detail;
    }

    /**
     * 将 SysFile 转为 FileInfo
     */
    public FileInfo toFileInfo(SysFile detail) {
        FileInfo info = BeanUtil.copyProperties(
            detail, FileInfo.class, "metadata", "userMetadata", "thMetadata", "thUserMetadata", "attr", "hashInfo");

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
    public Map<String, String> jsonToMetadata(String json) {
        return JsonUtils.parseObject(json, new TypeReference<>() {
        });
    }

    /**
     * 将 json 字符串转换成哈希信息对象
     */
    public HashInfo jsonToHashInfo(String json) {
        return JsonUtils.parseObject(json, HashInfo.class);
    }
}
