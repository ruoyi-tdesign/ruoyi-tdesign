package org.dromara.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.SysOss;
import org.dromara.system.domain.bo.SysOssBo;
import org.dromara.system.domain.query.SysOssQuery;
import org.dromara.system.domain.vo.SysOssVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 文件上传 服务层
 *
 * @author Lion Li
 */
public interface ISysOssService extends IService<SysOss> {

    /**
     * 查询OSS对象存储列表
     *
     * @param ossId 主键
     * @return SysOssVo
     */
    SysOssVo queryById(Long ossId);

    /**
     * 查询OSS对象存储列表
     * @param query OSS对象存储分页查询对象
     */
    TableDataInfo<SysOssVo> queryPageList(SysOssQuery query);

    /**
     * 根据一组 ossIds 获取对应的 SysOssVo 列表
     *
     * @param ossIds ossIds 一组文件在数据库中的唯一标识集合
     * @return 包含 SysOssVo 对象的列表
     */
    List<SysOssVo> listVoByIds(Collection<Long> ossIds);

    /**
     * 根据 ossId 从缓存或数据库中获取 SysOssVo 对象
     *
     * @param ossId 文件在数据库中的唯一标识
     * @return SysOssVo 对象，包含文件信息
     */
    SysOssVo getById(Long ossId);

    /**
     * 上传 MultipartFile 到对象存储服务，并保存文件信息到数据库
     *
     * @param file 要上传的 MultipartFile 对象
     * @param bo   业务对象
     * @return 上传成功后的 SysOssVo 对象，包含文件信息
     */
    SysOssVo upload(MultipartFile file, SysOssBo bo);

    /**
     * 上传文件到对象存储服务，并保存文件信息到数据库
     *
     * @param file 要上传的文件对象
     * @param bo   业务对象
     * @return 上传成功后的 SysOssVo 对象，包含文件信息
     */
    SysOssVo upload(File file, SysOssBo bo);

    /**
     * 文件下载方法，支持一次性下载完整文件
     *
     * @param ossId OSS对象ID
     * @param response HttpServletResponse对象，用于设置响应头和向客户端发送文件内容
     */
    void download(Long ossId, HttpServletResponse response) throws IOException;

    /**
     * 修改OSS对象存储
     *
     * @param bo OSS对象存储编辑业务对象
     * @return Boolean
     */
    Boolean updateByBo(SysOssBo bo);

    /**
     * 删除OSS对象存储
     *
     * @param ids OSS对象ID串
     */
    Boolean deleteWithValidByIds(Collection<Long> ids);

    /**
     * 查询OSS对象基于url串
     *
     * @param urls OSS对象url串
     * @return
     */
    List<SysOssVo> listVoByUrls(List<String> urls);

    /**
     * 移动到分类
     *
     * @param categoryId 分类id
     * @param ossIds     主键id
     * @param userType   用户类型
     * @param userId     用户id
     * @return
     */
    void move(Long categoryId, List<Long> ossIds, UserType userType, Long userId);

    /**
     * 删除我的OSS对象存储
     *
     * @param ossIds   OSS对象ID串
     * @param userType 用户类型
     * @param userId   用户id
     */
    boolean deleteMyIds(List<Long> ossIds, UserType userType, Long userId);
}
