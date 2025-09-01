package org.dromara.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.system.domain.SysFileCategory;
import org.dromara.system.domain.bo.SysFileCategoryBo;
import org.dromara.system.domain.query.SysFileCategoryQuery;
import org.dromara.system.domain.vo.SysFileCategoryVo;

import java.util.Collection;
import java.util.List;

/**
 * 文件分类Service接口
 *
 * @author yixiacoco
 * @date 2025-05-13
 */
public interface ISysFileCategoryService extends IService<SysFileCategory> {

    /**
     * 查询文件分类
     *
     * @param query
     * @return
     */
    SysFileCategoryVo query(SysFileCategoryQuery query);

    /**
     * 查询文件分类列表
     *
     * @param query 查询对象
     * @return 文件分类列表
     */
    List<SysFileCategoryVo> queryList(SysFileCategoryQuery query);

    /**
     * 新增文件分类
     *
     * @param bo 文件分类新增业务对象
     * @return 是否新增成功
     */
    Boolean insertByBo(SysFileCategoryBo bo);

    /**
     * 修改文件分类
     *
     * @param bo 文件分类编辑业务对象
     * @return 是否修改成功
     */
    Boolean updateByBo(SysFileCategoryBo bo);

    /**
     * 批量删除文件分类信息
     *
     * @param ids       待删除的主键集合
     * @param loginType 登录类型
     * @param userId    用户id
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, String loginType, Long userId);

    /**
     * 是否存在分类id
     *
     * @param ossCategoryId 分类id
     * @param loginType     用户类型
     * @param userId        用户id
     * @return
     */
    boolean hasId(Long ossCategoryId, String loginType, Long userId);
}
