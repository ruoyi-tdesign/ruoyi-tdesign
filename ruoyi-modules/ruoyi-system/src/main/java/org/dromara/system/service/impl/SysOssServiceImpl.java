package org.dromara.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.common.core.domain.dto.OssDTO;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.enums.YesNoEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.service.OssService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.file.FileUtils;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.oss.core.OssClient;
import org.dromara.common.oss.entity.UploadResult;
import org.dromara.common.oss.enumd.AccessPolicyType;
import org.dromara.common.oss.factory.OssFactory;
import org.dromara.common.redis.utils.CacheUtils;
import org.dromara.system.domain.SysOss;
import org.dromara.system.domain.SysOssCategory;
import org.dromara.system.domain.bo.SysOssBo;
import org.dromara.system.domain.query.SysOssQuery;
import org.dromara.system.domain.vo.SysOssVo;
import org.dromara.system.mapper.SysOssMapper;
import org.dromara.system.service.ISysOssCategoryService;
import org.dromara.system.service.ISysOssService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 文件上传 服务层实现
 *
 * @author Lion Li
 */
@Slf4j
@Service
public class SysOssServiceImpl extends ServiceImpl<SysOssMapper, SysOss> implements ISysOssService, OssService {

    @Autowired
    private ISysOssCategoryService categoryService;

    /**
     * 查询OSS对象存储
     *
     * @param ossId 主键
     * @return SysOssVo
     */
    @Override
    public SysOssVo queryById(Long ossId) {
        return baseMapper.queryById(ossId);
    }

    /**
     * 查询OSS对象存储列表
     *
     * @param query 查询对象
     * @return
     */
    @Override
    public TableDataInfo<SysOssVo> queryPageList(SysOssQuery query) {
        // 将后缀转为小写
        if (query.getSuffixes() != null) {
            String[] suffixes = query.getSuffixes();
            for (int i = 0; i < suffixes.length; i++) {
                String suffix = suffixes[i];
                suffixes[i] = suffix.toLowerCase();
            }
        }
        return PageQuery.of(() -> {
            List<SysOssVo> vos = baseMapper.queryList(query);
            for (SysOssVo vo : vos) {
                matchingUrl(vo);
            }
            return vos;
        });
    }

    /**
     * 根据一组 ossIds 获取对应的 SysOssVo 列表
     *
     * @param ossIds OSS对象ID串
     * @return 包含 SysOssVo 对象的列表
     */
    @Override
    public List<SysOssVo> listVoByIds(Collection<Long> ossIds) {
        List<SysOssVo> list = CacheUtils.takeCacheAndSet(CacheNames.SYS_OSS, ossIds, ids -> {
            List<SysOssVo> ossVos = baseMapper.selectVoList(lambdaQuery().in(SysOss::getOssId, ossIds).getWrapper());
            return StreamUtils.toIdentityMap(ossVos, SysOssVo::getOssId);
        });
        return StreamUtils.toList(list, this::matchingUrl);
    }

    /**
     * 查询OSS对象基于url串
     *
     * @param urls OSS对象url串
     * @return
     */
    @Override
    public List<SysOssVo> listVoByUrls(List<String> urls) {
        List<SysOssVo> ossVos = baseMapper.selectVoList(lambdaQuery().in(SysOss::getUrl, urls).getWrapper());
        for (SysOssVo ossVo : ossVos) {
            matchingUrl(ossVo);
        }
        return ossVos;
    }

    /**
     * 根据一组 ossIds 获取对应文件的 URL 列表
     *
     * @param ossIds ossIds 以逗号分隔的 ossId 字符串
     * @return 以逗号分隔的文件 URL 字符串
     */
    @Override
    public String selectUrlByIds(String ossIds) {
        List<Long> ids = StringUtils.splitTo(ossIds, Convert::toLong);
        List<SysOssVo> ossVos = listVoByIds(ids);
        return StreamUtils.join(ossVos, SysOssVo::getUrl);
    }

    @Override
    public List<OssDTO> selectByIds(String ossIds) {
        List<OssDTO> list = new ArrayList<>();
        SysOssServiceImpl ossService = SpringUtils.getAopProxy(this);
        for (Long id : StringUtils.splitTo(ossIds, Convert::toLong)) {
            SysOssVo vo = ossService.getById(id);
            if (ObjectUtil.isNotNull(vo)) {
                try {
                    vo.setUrl(this.matchingUrl(vo).getUrl());
                    list.add(BeanUtil.toBean(vo, OssDTO.class));
                } catch (Exception ignored) {
                    // 如果oss异常无法连接则将数据直接返回
                    list.add(BeanUtil.toBean(vo, OssDTO.class));
                }
            }
        }
        return list;
    }


    /**
     * 通过id获取oss对象
     *
     * @param ossId ossId
     * @return
     */
    @Cacheable(cacheNames = CacheNames.SYS_OSS, key = "#ossId")
    @Override
    public SysOssVo getById(Long ossId) {
        return baseMapper.selectVoById(ossId);
    }

    /**
     * 文件下载方法，支持一次性下载完整文件
     *
     * @param ossId OSS对象ID
     * @return
     */
    @Override
    public void download(Long ossId, HttpServletResponse response) throws IOException {
        SysOssVo sysOss = SpringUtils.getAopProxy(this).getById(ossId);
        if (ObjectUtil.isNull(sysOss)) {
            throw new ServiceException("文件数据不存在!");
        }
        FileUtils.setAttachmentResponseHeader(response, sysOss.getOriginalName());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");
        OssClient storage = OssFactory.instance(sysOss.getService());
        long contentLength = storage.download(sysOss.getFileName(), response.getOutputStream());
        response.setContentLengthLong(contentLength);
    }

    /**
     * 上传 MultipartFile 到对象存储服务，并保存文件信息到数据库
     *
     * @param file 要上传的 MultipartFile 对象
     * @param bo   业务对象
     * @return 上传成功后的 SysOssVo 对象，包含文件信息
     * @throws ServiceException 如果上传过程中发生异常，则抛出 ServiceException 异常
     */
    @Override
    public SysOssVo upload(MultipartFile file, SysOssBo bo) {
        String originalFilename = file.getOriginalFilename();
        String suffix = StringUtils.substring(originalFilename, originalFilename.lastIndexOf('.'), originalFilename.length());
        if (suffix != null) {
            suffix = suffix.toLowerCase();
        }
        OssClient storage = OssFactory.instance();
        UploadResult uploadResult;
        try {
            uploadResult = storage.uploadSuffix(file.getBytes(), suffix, file.getContentType());
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
        // 保存文件信息
        return buildResultEntity(originalFilename, suffix, file.getContentType(), storage.getConfigKey(), uploadResult, file.getSize(), bo);
    }

    /**
     * 上传文件到对象存储服务，并保存文件信息到数据库
     *
     * @param file 要上传的文件对象
     * @param bo   业务对象
     * @return 上传成功后的 SysOssVo 对象，包含文件信息
     */
    @Override
    public SysOssVo upload(File file, SysOssBo bo) {
        String originalFileName = file.getName();
        String suffix = StringUtils.substring(originalFileName, originalFileName.lastIndexOf("."), originalFileName.length());
        if (suffix != null) {
            suffix = suffix.toLowerCase();
        }
        OssClient storage = OssFactory.instance();
        UploadResult uploadResult = storage.uploadSuffix(file, suffix);
        String mimeType = FileUtil.getMimeType(file.getAbsolutePath());
        // 保存文件信息
        return buildResultEntity(originalFileName, suffix, mimeType, storage.getConfigKey(), uploadResult, file.length(), bo);
    }

    @NotNull
    private SysOssVo buildResultEntity(String originalFilename, String suffix, String contentType, String service, UploadResult uploadResult, Long size, SysOssBo bo) {
        SysOss oss = new SysOss();
        oss.setUrl(uploadResult.getUrl());
        oss.setFileSuffix(suffix);
        oss.setFileName(uploadResult.getFilename());
        oss.setOriginalName(originalFilename);
        oss.setService(service);
        oss.setSize(size);
        oss.setContentType(contentType);
        oss.setUserType(bo.getUserTypeEnum().getUserType());
        oss.setCreateBy(bo.getCreateBy());
        oss.setIsLock(bo.getIsLock());
        oss.setOssCategoryId(bo.getOssCategoryId());
        baseMapper.insert(oss);
        SysOssVo sysOssVo = MapstructUtils.convert(oss, SysOssVo.class);
        return this.matchingUrl(sysOssVo);
    }

    /**
     * 根据编辑业务对象修改OSS对象存储
     *
     * @param bo OSS对象存储编辑业务对象
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SysOssBo bo) {
        SysOss oss = new SysOss();
        checkCategory(bo.getOssCategoryId(), bo.getUserTypeEnum(), bo.getCreateBy());
        oss.setOssId(bo.getOssId());
        oss.setOriginalName(bo.getOriginalName());
        oss.setOssCategoryId(bo.getOssCategoryId());
        oss.setIsLock(bo.getIsLock());
        boolean update = update(oss, lambdaQuery()
            .eq(SysOss::getOssId, bo.getOssId())
            .eq(SysOss::getUserType, bo.getUserTypeEnum().getUserType())
            .eq(SysOss::getCreateBy, bo.getCreateBy())
            .getWrapper());
        if (update) {
            CacheUtils.evict(CacheNames.SYS_OSS, bo.getOssId());
        }
        return update;
    }

    /**
     * 检查分类是否存在
     *
     * @param ossCategoryId 分类id
     * @param userType      用户类型
     * @param userId        用户id
     */
    private void checkCategory(Long ossCategoryId, UserType userType, Long userId) {
        if (!ossCategoryId.equals(0L)) {
            boolean exists = categoryService.lambdaQuery()
                .eq(SysOssCategory::getOssCategoryId, ossCategoryId)
                .eq(SysOssCategory::getUserType, userType.getUserType())
                .eq(SysOssCategory::getCreateBy, userId)
                .exists();
            if (!exists) {
                throw new ServiceException("分类不存在");
            }
        }
    }

    /**
     * 删除OSS对象存储
     *
     * @param ids OSS对象ID串
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        boolean exists = lambdaQuery().in(SysOss::getOssId, ids).eq(SysOss::getIsLock, YesNoEnum.YES.getCodeNum()).exists();
        if (exists) {
            throw new ServiceException("加锁文件必须解锁后才能删除");
        }
        List<SysOss> list = baseMapper.selectBatchIds(ids);
        for (SysOss oss : list) {
            CacheUtils.evict(CacheNames.SYS_OSS, oss.getOssId());
        }
        boolean b = removeByIds(ids);
        if (b) {
            removeRealOss(list);
        }
        return b;
    }

    /**
     * 桶类型为 private 的URL 修改为临时URL时长为120s
     *
     * @param oss OSS对象
     * @return oss 匹配Url的OSS对象
     */
    private SysOssVo matchingUrl(SysOssVo oss) {
        try {
            OssClient storage = OssFactory.instance(oss.getService());
            // 仅修改桶类型为 private 的URL，临时URL时长为120s
            if (AccessPolicyType.PRIVATE == storage.getAccessPolicy()) {
                oss.setUrl(storage.getPrivateUrl(oss.getFileName(), 120));
            }
        } catch (Exception e) {
            log.error("获取oss地址失败", e);
        }
        return oss;
    }

    /**
     * 移动到分类
     *
     * @param categoryId 分类id
     * @param ossIds     主键id
     * @param userType   用户类型
     * @param userId     用户id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void move(Long categoryId, List<Long> ossIds, UserType userType, Long userId) {
        checkCategory(categoryId, userType, userId);
        // 安全过滤
        List<SysOss> ossList = lambdaQuery()
            .in(SysOss::getOssId, ossIds)
            .eq(SysOss::getUserType, userType.getUserType())
            .eq(SysOss::getCreateBy, userId)
            .select(SysOss::getOssId)
            .list();
        ossIds = StreamUtils.toList(ossList, SysOss::getOssId);
        List<SysOss> list = ossIds.stream().map(id -> {
            SysOss oss = new SysOss();
            oss.setOssId(id);
            oss.setOssCategoryId(categoryId);
            return oss;
        }).toList();
        for (SysOss oss : list) {
            CacheUtils.evict(CacheNames.SYS_OSS, oss.getOssId());
        }
        updateBatchById(list);
    }

    /**
     * 删除我的OSS对象存储
     *
     * @param ossIds   OSS对象ID串
     * @param userType 用户类型
     * @param userId   用户id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMyIds(List<Long> ossIds, UserType userType, Long userId) {
        boolean exists = lambdaQuery().in(SysOss::getOssId, ossIds).eq(SysOss::getIsLock, YesNoEnum.YES.getCodeNum()).exists();
        if (exists) {
            throw new ServiceException("加锁文件必须解锁后才能删除");
        }
        List<SysOss> ossList = lambdaQuery()
            .in(SysOss::getOssId, ossIds)
            .eq(SysOss::getUserType, userType.getUserType())
            .eq(SysOss::getCreateBy, userId)
            .list();
        boolean remove = lambdaUpdate()
            .in(SysOss::getOssId, ossIds)
            .eq(SysOss::getUserType, userType.getUserType())
            .eq(SysOss::getCreateBy, userId)
            .remove();
        if (remove) {
            for (SysOss oss : ossList) {
                CacheUtils.evict(CacheNames.SYS_OSS, oss.getOssId());
            }
            removeRealOss(ossList);
        }
        return remove;
    }

    /**
     * 真实删除oss文件
     *
     * @param list oss对象
     */
    private static void removeRealOss(List<SysOss> list) {
        for (SysOss sysOss : list) {
            OssClient storage = OssFactory.instance(sysOss.getService());
            storage.delete(sysOss.getUrl());
        }
    }
}
