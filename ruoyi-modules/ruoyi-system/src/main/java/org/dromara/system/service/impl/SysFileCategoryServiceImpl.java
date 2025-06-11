package org.dromara.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.system.domain.SysFile;
import org.dromara.system.domain.SysFileCategory;
import org.dromara.system.domain.bo.SysFileCategoryBo;
import org.dromara.system.domain.query.SysFileCategoryQuery;
import org.dromara.system.domain.vo.SysFileCategoryVo;
import org.dromara.system.mapper.SysFileCategoryMapper;
import org.dromara.system.service.ISysFileCategoryService;
import org.dromara.system.service.ISysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 文件分类Service业务层处理
 *
 * @author yixiacoco
 * @date 2025-05-13
 */
@Service
public class SysFileCategoryServiceImpl extends ServiceImpl<SysFileCategoryMapper, SysFileCategory> implements ISysFileCategoryService {

    @Autowired
    private ISysFileService fileService;

    /**
     * 查询文件分类
     *
     * @param query
     * @return
     */
    @Override
    public SysFileCategoryVo query(SysFileCategoryQuery query) {
        return baseMapper.queryVoById(query);
    }

    /**
     * 查询文件分类列表
     *
     * @param query 查询对象
     * @return 文件分类列表
     */
    @Override
    public List<SysFileCategoryVo> queryList(SysFileCategoryQuery query) {
        return baseMapper.queryList(query);
    }

    /**
     * 新增文件分类
     *
     * @param bo 文件分类新增业务对象
     * @return 是否新增成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SysFileCategoryBo bo) {
        SysFileCategory category = MapstructUtils.convert(bo, SysFileCategory.class);
        // 设置路径、层级
        if (bo.getParentId().equals(0L)) {
            category.setCategoryPath(Constants.ROOT_PATH + category.getCategoryName());
            category.setLevel(0);
            category.setParentId(0L);
        } else {
            SysFileCategory parent = getById(bo.getParentId());
            if (parent == null) {
                throw new ServiceException("父分类不存在！");
            }
            // 检查父类是否是当前分类或子分类下
            if ((parent.getCategoryPath() + Constants.ROOT_PATH).startsWith(category.getCategoryPath() + Constants.ROOT_PATH)) {
                throw new ServiceException("父分类不能为当前分类或子分类下");
            }
            category.setParentId(parent.getFileCategoryId());
            category.setCategoryPath(parent.getCategoryPath() + Constants.ROOT_PATH + category.getCategoryName());
            category.setLevel(parent.getLevel() + 1);
        }
        checkRepeat(category);
        return save(category);
    }

    /**
     * 修改文件分类
     *
     * @param bo 文件分类编辑业务对象
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SysFileCategoryBo bo) {
        SysFileCategory category = getById(bo.getFileCategoryId());
        if (!category.getParentId().equals(bo.getParentId()) || !category.getCategoryName().equals(bo.getCategoryName())) {
            int level = category.getLevel();
            String path = category.getCategoryPath();
            category.setCategoryName(bo.getCategoryName());
            int levelDiff;
            if (bo.getParentId().equals(0L)) {
                levelDiff = category.getLevel();
                category.setCategoryPath(Constants.ROOT_PATH + category.getCategoryName());
                category.setLevel(0);
                category.setParentId(0L);
            } else {
                SysFileCategory parent = getById(bo.getParentId());
                if (parent == null) {
                    throw new ServiceException("父分类不存在！");
                }
                // 检查父类是否是当前分类或子分类下
                if ((parent.getCategoryPath() + Constants.ROOT_PATH).startsWith(category.getCategoryPath() + Constants.ROOT_PATH)) {
                    throw new ServiceException("父分类不能为当前分类或子分类下");
                }
                levelDiff = level - (parent.getLevel() + 1);
                category.setParentId(parent.getFileCategoryId());
                category.setCategoryPath(parent.getCategoryPath() + Constants.ROOT_PATH + category.getCategoryName());
                category.setLevel(parent.getLevel() + 1);
            }
            // 检查分类名称是否重复
            checkRepeat(category);
            // 获取子分类，更新子分类的路径
            List<SysFileCategory> children = lambdaQuery()
                .likeRight(SysFileCategory::getCategoryPath, path + Constants.ROOT_PATH)
                .select(SysFileCategory::getFileCategoryId, SysFileCategory::getCategoryPath, SysFileCategory::getLevel)
                .list();
            for (SysFileCategory child : children) {
                child.setLevel(child.getLevel() - levelDiff);
                child.setCategoryPath(child.getCategoryPath().replaceFirst(path, category.getCategoryPath()));
            }
            if (CollUtil.isNotEmpty(children)) {
                updateBatchById(children);
            }
        }
        return update(category, lambdaQuery()
            .eq(SysFileCategory::getFileCategoryId, category.getFileCategoryId())
            .eq(SysFileCategory::getLoginType, bo.getLoginType())
            .eq(SysFileCategory::getCreateBy, bo.getCreateBy())
            .getWrapper());
    }

    /**
     * 批量删除文件分类信息
     *
     * @param ids       待删除的主键集合
     * @param loginType
     * @param userId
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, String loginType, Long userId) {
        boolean exists = fileService.lambdaQuery().in(SysFile::getFileCategoryId, ids).exists();
        if (exists) {
            throw new ServiceException("无法删除非空分类");
        }
        exists = lambdaQuery().in(SysFileCategory::getParentId, ids).exists();
        if (exists) {
            throw new ServiceException("请先删除子分类");
        }
        return lambdaUpdate()
            .in(SysFileCategory::getFileCategoryId, ids)
            .eq(SysFileCategory::getLoginType, loginType)
            .eq(SysFileCategory::getCreateBy, userId)
            .remove();
    }

    /**
     * 检查是否包含相同路径的分类
     *
     * @param category 分类对象
     */
    private void checkRepeat(SysFileCategory category) {
        boolean exists = lambdaQuery()
            .ne(category.getFileCategoryId() != null, SysFileCategory::getFileCategoryId, category.getFileCategoryId())
            .eq(SysFileCategory::getCategoryPath, category.getCategoryPath())
            .eq(SysFileCategory::getLoginType, category.getLoginType())
            .eq(SysFileCategory::getCreateBy, category.getCreateBy())
            .exists();
        if (exists) {
            throw new ServiceException("此位置已包含同名分类");
        }
    }

    /**
     * 是否存在分类id
     *
     * @param ossCategoryId 分类id
     * @param loginType     用户类型
     * @param userId        用户id
     * @return
     */
    @Override
    public boolean hasId(Long ossCategoryId, String loginType, Long userId) {
        if (ossCategoryId == null) {
            return false;
        }
        return lambdaQuery()
            .eq(SysFileCategory::getFileCategoryId, ossCategoryId)
            .eq(SysFileCategory::getLoginType, loginType)
            .eq(SysFileCategory::getCreateBy, userId)
            .select(SysFileCategory::getFileCategoryId)
            .exists();
    }
}
