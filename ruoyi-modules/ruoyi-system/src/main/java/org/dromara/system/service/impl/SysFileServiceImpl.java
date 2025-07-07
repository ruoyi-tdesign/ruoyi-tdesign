package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.dromara.common.core.enums.YesNoEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.storage.balancer.DefaultFileServer;
import org.dromara.common.storage.balancer.FileServer;
import org.dromara.common.storage.balancer.FileStorageLoadBalancer;
import org.dromara.common.storage.balancer.RedisRoundRobinAlgorithm;
import org.dromara.common.storage.utils.FileStorageUtil;
import org.dromara.common.tenant.annotation.IgnoreTenant;
import org.dromara.system.domain.SysFile;
import org.dromara.system.domain.SysFileCategory;
import org.dromara.system.domain.SysStorageConfig;
import org.dromara.system.domain.bo.SysFileBo;
import org.dromara.system.domain.query.SysFileQuery;
import org.dromara.system.domain.vo.SysFileVo;
import org.dromara.system.mapper.SysFileMapper;
import org.dromara.system.service.ISysFileCategoryService;
import org.dromara.system.service.ISysFileService;
import org.dromara.system.service.ISysStorageConfigService;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 文件记录Service业务层处理
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService {

    @Autowired
    private ISysStorageConfigService storageConfigService;
    @Autowired
    private SysFileRecorder fileRecorder;
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
        return baseMapper.queryById(fileId);
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
            SysStorageConfig config = storageConfigService.getById(file.getStorageConfigId());
            if (config == null) {
                throw new ServiceException("文件【%s】存储配置不存在".formatted(file.getOriginalFilename()));
            }
            FileInfo fileInfo = SysFileRecorder.toFileInfo(file);
            FileStorageService service = getFileStorageService(config);
            service.delete(fileInfo);
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
        FileStorageService service = getFileStorageService();
        FileInfo upload = service.of(file).upload();
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
     * 下载文件
     *
     * @param fileId   文件ID
     * @param response 响应
     */
    @Override
    @IgnoreTenant
    @SneakyThrows(IOException.class)
    public void download(Long fileId, HttpServletResponse response) {
        SysFile file = getById(fileId);
        if (file == null) {
            throw new ServiceException("文件不存在");
        }
        SysStorageConfig config = storageConfigService.getById(file.getStorageConfigId());
        if (config == null) {
            throw new ServiceException("文件存储配置不存在");
        }
        FileInfo fileInfo = SysFileRecorder.toFileInfo(file);
        FileStorageService service = getFileStorageService(config);
        service.download(fileInfo).outputStream(response.getOutputStream());
    }

    private static FileStorageService getFileStorageService(SysStorageConfig config) {
        DefaultFileServer fileServer = new DefaultFileServer();
        fileServer.setId(config.getStorageConfigId().toString());
        fileServer.setPlatform(config.getPlatform());
        fileServer.setWeight(config.getWeight());
        fileServer.setProperties(config.getConfigJson());
        return FileStorageUtil.getFileStorageService(fileServer);
    }

    /**
     * 根据ID列表查询文件
     *
     * @param fileIds 文件ID列表
     * @return 文件列表
     */
    @Override
    public List<SysFileVo> listVoByIds(List<Long> fileIds) {
        List<SysFile> list = lambdaQuery().in(SysFile::getFileId, fileIds).list();
        return MapstructUtils.convert(list, SysFileVo.class);
    }

    /**
     * 获取文件存储服务
     */
    private FileStorageService getFileStorageService() {
        List<FileServer> servers = storageConfigService.getFileServerList();
        FileStorageLoadBalancer balancer = new FileStorageLoadBalancer(new RedisRoundRobinAlgorithm(), servers);
        balancer.setFileRecorder(fileRecorder);
        return balancer.getService();
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
        List<SysFile> ossList = lambdaQuery()
            .in(SysFile::getFileId, fileIds)
            .eq(SysFile::getUserType, loginType)
            .eq(SysFile::getCreateBy, userId)
            .select(SysFile::getFileId)
            .list();
        fileIds = StreamUtils.toList(ossList, SysFile::getFileId);
        List<SysFile> list = fileIds.stream().map(id -> {
            SysFile file = new SysFile();
            file.setFileId(id);
            file.setFileCategoryId(categoryId);
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
}
