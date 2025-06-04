package org.dromara.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.SysFile;
import org.dromara.system.domain.bo.SysFileBo;
import org.dromara.system.domain.query.SysFileQuery;
import org.dromara.system.domain.vo.SysFileVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

/**
 * 文件记录Service接口
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
public interface ISysFileService extends IService<SysFile> {

    /**
     * 查询文件记录
     *
     * @param fileId 主键
     * @return SysFileVo
     */
    SysFileVo queryById(Long fileId);

    /**
     * 分页查询文件记录列表
     *
     * @param query 查询对象
     * @return 文件记录分页列表
     */
    TableDataInfo<SysFileVo> queryPageList(SysFileQuery query);

    /**
     * 查询文件记录列表
     *
     * @param query 查询对象
     * @return 文件记录列表
     */
    List<SysFileVo> queryList(SysFileQuery query);

    /**
     * 修改文件记录
     *
     * @param bo 文件记录编辑业务对象
     * @return 是否修改成功
     */
    Boolean updateByBo(SysFileBo bo);

    /**
     * 批量删除文件记录信息
     *
     * @param ids 待删除的主键集合
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids);

    /**
     * 上传文件
     *
     * @param bo
     * @param file 文件
     * @return 文件
     */
    SysFile upload(SysFileBo bo, MultipartFile file);

    /**
     * 下载文件
     *
     * @param fileId   文件ID
     * @param response 响应
     */
    void download(Long fileId, HttpServletResponse response);

    /**
     * 根据ID列表查询文件
     *
     * @param fileIds 文件ID列表
     * @return 文件列表
     */
    List<SysFileVo> listVoByIds(List<Long> fileIds);

    /**
     * 删除我的文件存储
     *
     * @param fileIds   文件ID列表
     * @param loginType 登录类型
     * @param userId    用户ID
     */
    boolean deleteMyIds(List<Long> fileIds, String loginType, Long userId);

    /**
     * 移动到分类
     *
     * @param categoryId 分类ID
     * @param fileIds    文件ID列表
     * @param loginType  登录类型
     * @param userId     用户ID
     */
    void move(Long categoryId, List<Long> fileIds, String loginType, Long userId);
}
