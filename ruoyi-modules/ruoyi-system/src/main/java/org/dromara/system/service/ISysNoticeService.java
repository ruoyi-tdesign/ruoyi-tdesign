package org.dromara.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.SysNotice;
import org.dromara.system.domain.bo.SysNoticeBo;
import org.dromara.system.domain.query.SysNoticeQuery;
import org.dromara.system.domain.vo.SysNoticeVo;

import java.util.List;

/**
 * 公告 服务层
 *
 * @author Lion Li
 */
public interface ISysNoticeService extends IService<SysNotice> {


    /**
     * 分页查询通知公告列表
     *
     * @param query 查询对象
     * @return SysNoticeVo
     */
    TableDataInfo<SysNoticeVo> queryPageList(SysNoticeQuery query);

    /**
     * 查询通知公告列表
     *
     * @param query 查询对象
     * @return SysNoticeVo
     */
    List<SysNoticeVo> queryList(SysNoticeQuery query);

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    SysNoticeVo selectNoticeById(Long noticeId);

    /**
     * 查询公告列表
     *
     * @param query 公告信息
     * @return 公告集合
     */
    List<SysNoticeVo> selectNoticeList(SysNoticeQuery query);

    /**
     * 新增公告
     *
     * @param bo 公告信息
     * @return 结果
     */
    Boolean insertNotice(SysNoticeBo bo);

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    Boolean updateNotice(SysNoticeBo notice);

    /**
     * 删除公告信息
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    int deleteNoticeById(Long noticeId);

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    int deleteNoticeByIds(Long[] noticeIds);
}
