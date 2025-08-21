package org.dromara.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.url.UrlBuilder;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.dto.FileDTO;
import org.dromara.common.core.enums.StorageRequestMode;
import org.dromara.common.core.enums.YesNoEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.service.FileService;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.file.FileUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.storage.utils.FileStorageUtil;
import org.dromara.common.tenant.annotation.IgnoreTenant;
import org.dromara.system.domain.SysFile;
import org.dromara.system.domain.SysFileCategory;
import org.dromara.system.domain.SysStorageConfig;
import org.dromara.system.domain.bo.SysFileBo;
import org.dromara.system.domain.dto.FileResourceDto;
import org.dromara.system.domain.query.SysFileQuery;
import org.dromara.system.domain.vo.SysFileVo;
import org.dromara.system.mapper.SysFileMapper;
import org.dromara.system.service.ISysFileCategoryService;
import org.dromara.system.service.ISysFileService;
import org.dromara.system.service.ISysStorageConfigService;
import org.dromara.system.utils.SysFileUtil;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.constant.Constant;
import org.dromara.x.file.storage.core.presigned.GeneratePresignedUrlResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 文件记录Service业务层处理
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
@Slf4j
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService, FileService {

    @Autowired
    private ISysStorageConfigService storageConfigService;
    @Autowired
    private ISysFileCategoryService categoryService;

    /**
     * 查询文件记录
     *
     * @param fileId 主键
     * @return SysFileVo
     */
    @Override
    public SysFileVo queryById(Long fileId) {
        SysFileVo vo = baseMapper.queryById(fileId);
        if (vo != null) {
            SysFileUtil.packedPreviewAndDownloadUrl(vo);
        }
        return vo;
    }

    /**
     * 根据文件名查询文件
     *
     * @param fileName 文件名
     * @return 文件
     */
    @Override
    public SysFile getByFileName(String fileName) {
        return lambdaQuery().eq(SysFile::getFilename, fileName).one();
    }

    /**
     * 分页查询文件记录列表
     *
     * @param query 查询对象
     * @return 文件记录分页列表
     */
    @Override
    public TableDataInfo<SysFileVo> queryPageList(SysFileQuery query) {
        return PageQuery.of(() -> {
            List<SysFileVo> vos = baseMapper.queryList(query);
            SysFileUtil.packedPreviewAndDownloadUrl(vos);
            return vos;
        });
    }

    /**
     * 查询文件记录列表
     *
     * @param query 查询对象
     * @return 文件记录列表
     */
    @Override
    public List<SysFileVo> queryList(SysFileQuery query) {
        List<SysFileVo> vos = baseMapper.queryList(query);
        SysFileUtil.packedPreviewAndDownloadUrl(vos);
        return vos;
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
        checkCategory(bo.getFileCategoryId(), bo.getUserType(), bo.getCreateBy());
        SysFile oss = new SysFile();
        oss.setFileId(bo.getFileId());
        oss.setOriginalFilename(bo.getOriginalFilename());
        oss.setFileCategoryId(bo.getFileCategoryId());
        oss.setIsLock(bo.getIsLock());
        return update(oss, lambdaQuery()
            .eq(SysFile::getFileId, bo.getFileId())
            .eq(SysFile::getUserType, bo.getUserType())
            .eq(SysFile::getCreateBy, bo.getCreateBy())
            .getWrapper());
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
        boolean exists = lambdaQuery()
            .in(SysFile::getFileId, ids)
            .eq(SysFile::getIsLock, YesNoEnum.YES.getCodeNum())
            .exists();
        if (exists) {
            throw new ServiceException("加锁文件必须解锁后才能删除");
        }
        List<SysFile> list = baseMapper.selectByIds(ids);
        boolean b = removeByIds(ids);
        if (b) {
            realRemoveFile(list);
        }
        return b;
    }

    private void realRemoveFile(List<SysFile> list) {
        for (SysFile file : list) {
            FileStorageService service = storageConfigService.getFileStorageService(file.getStorageConfigId());
            if (service == null) {
                throw new ServiceException("文件存储配置不存在");
            }
            FileInfo fileInfo = SysFileRecorder.toFileInfo(file);
            service.delete(fileInfo);
            FileStorageUtil.serviceRecycle(service);
        }
    }

    /**
     * 上传文件
     *
     * @param bo
     * @param file 文件
     * @return 文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile upload(SysFileBo bo, MultipartFile file) {
        FileStorageService service = storageConfigService.getFileStorageService();
        // 生成日期路径
        String datePath = DateUtils.datePath() + StringUtils.SLASH;
        FileInfo upload = service.of(file).setPath(datePath).upload();
        FileStorageUtil.serviceRecycle(service);
        SysFile sysFile = new SysFile();
        BeanUtils.copyProperties(upload, sysFile);
        long id = Long.parseLong(upload.getId());
        sysFile.setFileId(id);
        sysFile.setCreateBy(bo.getCreateBy());
        sysFile.setUserType(bo.getUserType());
        sysFile.setIsLock(bo.getIsLock());
        sysFile.setFileCategoryId(bo.getFileCategoryId());
        updateById(sysFile);
        return getById(id);
    }

    /**
     * 预览文件
     *
     * @param fileName 文件ID
     * @param dto
     * @param response 响应
     */
    @Override
    @IgnoreTenant
    public void preview(String fileName, FileResourceDto dto, HttpServletResponse response) {
        handleFileAccess(fileName, dto, response, fileInfo -> {
            // 设置缓存控制头信息
            // 缓存有效期为1小时
            response.setHeader("Cache-Control", "private, max-age=3600");

            // 设置过期时间（GMT格式）
            ZonedDateTime expirationTime = ZonedDateTime.now().plusHours(1);
            String formattedExpiration = expirationTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
            response.setHeader("Expires", formattedExpiration);
            response.setHeader("ETag", fileInfo.getFilename());
            response.setContentType(fileInfo.getContentType());
        });
    }

    /**
     * 下载文件
     *
     * @param fileName 文件ID
     * @param dto
     * @param response 响应
     */
    @Override
    @IgnoreTenant
    public void download(String fileName, FileResourceDto dto, HttpServletResponse response) {
        handleFileAccess(fileName, dto, response, fileInfo -> {
            FileUtils.setAttachmentResponseHeader(response, fileInfo.getOriginalFilename());
            response.setContentType(fileInfo.getContentType());
        });
    }

    /**
     * 处理文件访问
     *
     * @param fileName
     * @param dto
     * @param response
     */
    private void handleFileAccess(String fileName, FileResourceDto dto, HttpServletResponse response, Consumer<FileInfo> process) {
        SysFile file = getByFileName(fileName);
        if (file == null) {
            throw new ServiceException("文件不存在");
        }
        SysStorageConfig config = storageConfigService.getCacheMap().get(file.getStorageConfigId().toString());
        if (config == null) {
            throw new ServiceException("文件存储配置不存在");
        }
        FileStorageService service = storageConfigService.getFileStorageService(config);
        FileInfo fileInfo = SysFileRecorder.toFileInfo(file);
        if (process != null) {
            process.accept(fileInfo);
        }
        StorageRequestMode requestMode = StorageRequestMode.valueOf(config.getRequestMode());
        if (requestMode == StorageRequestMode.direct) {
            UrlBuilder urlBuilder = UrlBuilder.of(fileInfo.getUrl(), StandardCharsets.UTF_8);
            Map<String, String> paramMap = ServletUtils.getParamMap(ServletUtils.getRequest());
            paramMap.forEach(urlBuilder::addQuery);
            try {
                response.sendRedirect(urlBuilder.build());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new ServiceException(e.getMessage());
            }
        } else if (requestMode == StorageRequestMode.direct_signature && service.isSupportPresignedUrl()) {
            Map<String, String> paramMap = ServletUtils.getParamMap(ServletUtils.getRequest());

            // 生成下载或访问用的 URL
            GeneratePresignedUrlResult presigned = service
                .generatePresignedUrl()
                .setPath(fileInfo.getPath()) // 文件路径
                .setFilename(fileInfo.getFilename()) // 文件名，也可以换成缩略图的文件名
                .setMethod(Constant.GeneratePresignedUrl.Method.GET) // 签名方法
                .setExpiration(DateUtil.offsetHour(new Date(), 1)) // 过期时间 1 小时
                .putResponseHeaders(Constant.Metadata.CONTENT_DISPOSITION, FileUtils.getContentDispositionValue(fileInfo.getOriginalFilename()))
                .putQueryParamsAll(paramMap)
                .generatePresignedUrl();

            String presignedUrl = presigned.getUrl();
            try {
                response.sendRedirect(presignedUrl);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new ServiceException(e.getMessage());
            }
        } else {
            response.setContentType(fileInfo.getContentType());
            service.download(fileInfo).inputStream((is) -> {
                try (is) {
                    SysFileUtil.handleImage(dto, is, response.getOutputStream());
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    throw new ServiceException(e.getMessage());
                }
            });
        }
        FileStorageUtil.serviceRecycle(service);
    }

    /**
     * 根据ID列表查询文件
     *
     * @param fileIds 文件ID列表
     * @return 文件列表
     */
    @Override
    public List<SysFileVo> listVoByIds(Collection<Long> fileIds) {
        List<SysFile> list = lambdaQuery().in(SysFile::getFileId, fileIds).list();
        List<SysFileVo> vos = MapstructUtils.convert(list, SysFileVo.class);
        SysFileUtil.packedPreviewAndDownloadUrl(vos);
        return vos;
    }

    /**
     * 删除我的文件存储
     *
     * @param fileIds   文件ID列表
     * @param loginType 登录类型
     * @param userId    用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMyIds(List<Long> fileIds, String loginType, Long userId) {
        boolean exists = lambdaQuery()
            .in(SysFile::getFileId, fileIds)
            .eq(SysFile::getIsLock, YesNoEnum.YES.getCodeNum())
            .exists();
        if (exists) {
            throw new ServiceException("加锁文件必须解锁后才能删除");
        }
        List<SysFile> fileList = lambdaQuery()
            .in(SysFile::getFileId, fileIds)
            .eq(SysFile::getUserType, loginType)
            .eq(SysFile::getCreateBy, userId)
            .list();
        boolean remove = lambdaUpdate()
            .in(SysFile::getFileId, fileIds)
            .eq(SysFile::getUserType, loginType)
            .eq(SysFile::getCreateBy, userId)
            .remove();
        if (remove) {
            realRemoveFile(fileList);
        }
        return remove;
    }

    /**
     * 移动到分类
     *
     * @param categoryId 分类ID
     * @param fileIds    文件ID列表
     * @param loginType  登录类型
     * @param userId     用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void move(Long categoryId, List<Long> fileIds, String loginType, Long userId) {
        checkCategory(categoryId, loginType, userId);
        // 安全过滤
        List<SysFile> fileList = lambdaQuery()
            .in(SysFile::getFileId, fileIds)
            .eq(SysFile::getUserType, loginType)
            .eq(SysFile::getCreateBy, userId)
            .select(SysFile::getFileId)
            .list();
        fileIds = StreamUtils.toList(fileList, SysFile::getFileId);
        List<SysFile> list = fileIds.stream().map(id -> {
            SysFile file = new SysFile();
            file.setFileId(id);
            file.setFileCategoryId(categoryId);
            return file;
        }).toList();
        updateBatchById(list);
    }

    /**
     * 锁定或解锁文件
     *
     * @param fileIds   文件ID列表
     * @param loginType 登录类型
     * @param userId    用户ID
     * @param lock      是否锁定
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void securityLockOps(List<Long> fileIds, String loginType, Long userId, boolean lock) {
        // 安全过滤
        List<SysFile> fileList = lambdaQuery()
            .in(SysFile::getFileId, fileIds)
            .eq(SysFile::getUserType, loginType)
            .eq(SysFile::getCreateBy, userId)
            .select(SysFile::getFileId)
            .list();
        fileIds = StreamUtils.toList(fileList, SysFile::getFileId);
        List<SysFile> list = fileIds.stream().map(id -> {
            SysFile file = new SysFile();
            file.setFileId(id);
            file.setIsLock(lock ? 1 : 0);
            return file;
        }).toList();
        updateBatchById(list);
    }

    /**
     * 锁定或解锁文件
     *
     * @param fileIds 文件ID列表
     * @param lock    是否锁定
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockOps(List<Long> fileIds, boolean lock) {
        List<SysFile> list = fileIds.stream().map(id -> {
            SysFile file = new SysFile();
            file.setFileId(id);
            file.setIsLock(lock ? 1 : 0);
            return file;
        }).toList();
        updateBatchById(list);
    }

    /**
     * 检查分类是否存在
     *
     * @param fileCategoryId 分类id
     * @param loginType      登录类型
     * @param userId         用户id
     */
    private void checkCategory(Long fileCategoryId, String loginType, Long userId) {
        if (!fileCategoryId.equals(0L)) {
            boolean exists = categoryService.lambdaQuery()
                .eq(SysFileCategory::getFileCategoryId, fileCategoryId)
                .eq(SysFileCategory::getLoginType, loginType)
                .eq(SysFileCategory::getCreateBy, userId)
                .exists();
            if (!exists) {
                throw new ServiceException("分类不存在");
            }
        }
    }

    /**
     * 通过fileId查询对应的url
     *
     * @param fileIds fileId串逗号分隔
     * @return url串逗号分隔
     */
    @Override
    public String selectUrlByIds(Collection<Long> fileIds) {
        List<SysFileVo> fileVos = listVoByIds(fileIds);
        SysFileUtil.packedPreviewAndDownloadUrl(fileVos);
        return StreamUtils.join(fileVos, SysFileVo::getPreviewUrl);
    }

    /**
     * 通过fileId查询列表
     *
     * @param fileIds fileId串逗号分隔
     * @return 列表
     */
    @Override
    public List<FileDTO> selectByIds(Collection<Long> fileIds) {
        List<FileDTO> list = new ArrayList<>();
        List<SysFileVo> fileVos = listVoByIds(fileIds);
        for (SysFileVo vo : fileVos) {
            SysFileUtil.packedPreviewAndDownloadUrl(vo);
            list.add(BeanUtil.toBean(vo, FileDTO.class));
        }
        return list;
    }
}
