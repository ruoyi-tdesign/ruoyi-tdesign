package org.dromara.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.SysOperLog;
import org.dromara.system.domain.bo.SysOperLogBo;
import org.dromara.system.domain.query.SysOperLogQuery;
import org.dromara.system.domain.vo.SysOperLogVo;

import java.util.List;

/**
 * 操作日志 服务层
 *
 * @author Lion Li
 */
public interface ISysOperLogService extends IService<SysOperLog> {

    /**
     * 查询操作日志记录
     *
     * @param operId 主键
     * @return SysOperLogVo
     */
    SysOperLogVo queryById(Long operId);

    /**
     * 分页查询操作日志列表
     *
     * @param query 查询对象
     * @return SysOperLogVo
     */
    TableDataInfo<SysOperLogVo> queryPageList(SysOperLogQuery query);

    /**
     * 查询操作日志记录列表
     *
     * @param query 查询对象
     * @return SysOperLogVo
     */
    List<SysOperLogVo> queryList(SysOperLogQuery query);

    /**
     * 新增操作日志
     *
     * @param bo 操作日志对象
     */
    void insertOperlog(SysOperLogBo bo);

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    int deleteOperLogByIds(Long[] operIds);

    /**
     * 清空操作日志
     */
    void cleanOperLog();
}
