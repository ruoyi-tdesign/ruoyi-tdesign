package org.dromara.system.service;

import org.dromara.system.domain.SysFile;
import org.dromara.system.domain.bo.SysFileBo;
import org.dromara.system.domain.query.SysFileQuery;
import org.dromara.system.domain.vo.SysFileVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.x.file.storage.core.recorder.FileRecorder;

import java.util.Collection;
import java.util.List;

/**
 * 文件记录Service接口
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
public interface ISysFileService extends IService<SysFile>, FileRecorder {

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
}
