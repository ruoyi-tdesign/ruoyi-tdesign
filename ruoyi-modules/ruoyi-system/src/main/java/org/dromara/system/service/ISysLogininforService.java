package org.dromara.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.SysLogininfor;
import org.dromara.system.domain.bo.SysLogininforBo;
import org.dromara.system.domain.query.SysLogininforQuery;
import org.dromara.system.domain.vo.SysLogininforVo;

import java.util.List;

/**
 * 系统访问日志情况信息 服务层
 *
 * @author Lion Li
 */
public interface ISysLogininforService extends IService<SysLogininfor> {

    /**
     * 查询系统访问记录
     *
     * @param infoId 主键
     * @return SysLogininforVo
     */
    SysLogininforVo queryById(Long infoId);

    /**
     * 分页查询登录日志列表
     *
     * @param query 查询对象
     * @return
     */
    TableDataInfo<SysLogininforVo> queryPageList(SysLogininforQuery query);

    /**
     * 查询系统登录日志集合
     *
     * @param query 查询对象
     * @return 登录记录集合
     */
    List<SysLogininforVo> queryList(SysLogininforQuery query);

    /**
     * 新增系统登录日志
     *
     * @param bo 访问日志对象
     */
    void insertLogininfor(SysLogininforBo bo);

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    int deleteLogininforByIds(Long[] infoIds);

    /**
     * 清空系统登录日志
     */
    void cleanLogininfor();
}
