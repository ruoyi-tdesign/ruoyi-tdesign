package org.dromara.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.workflow.domain.WfCategory;
import org.dromara.workflow.domain.bo.WfCategoryBo;
import org.dromara.workflow.domain.query.WfCategoryQuery;
import org.dromara.workflow.domain.vo.WfCategoryVo;
import org.dromara.workflow.mapper.WfCategoryMapper;
import org.dromara.workflow.service.IWfCategoryService;
import org.dromara.workflow.utils.QueryUtils;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 流程分类Service业务层处理
 *
 * @author may
 * @date 2023-06-28
 */
@RequiredArgsConstructor
@Service
public class WfCategoryServiceImpl extends ServiceImpl<WfCategoryMapper, WfCategory> implements IWfCategoryService {
    @Autowired(required = false)
    private RepositoryService repositoryService;

    /**
     * 查询流程分类
     *
     * @param id 主键
     * @return WfCategoryVo
     */
    @Override
    public WfCategoryVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }


    /**
     * 查询流程分类列表
     *
     * @param query 查询对象
     * @return WfCategoryVo
     */
    @Override
    public List<WfCategoryVo> queryList(WfCategoryQuery query) {
        return baseMapper.queryList(query);
    }

    /**
     * 根据新增业务对象插入流程分类
     *
     * @param bo 流程分类新增业务对象
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(WfCategoryBo bo) {
        WfCategory add = MapstructUtils.convert(bo, WfCategory.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 根据编辑业务对象修改流程分类
     *
     * @param bo 流程分类编辑业务对象
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(WfCategoryBo bo) {
        WfCategory update = MapstructUtils.convert(bo, WfCategory.class);
        validEntityBeforeSave(update);
        WfCategoryVo wfCategoryVo = baseMapper.selectVoById(bo.getId());
        List<ProcessDefinition> processDefinitionList = QueryUtils.definitionQuery().processDefinitionCategory(wfCategoryVo.getCategoryCode()).list();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            repositoryService.setProcessDefinitionCategory(processDefinition.getId(), bo.getCategoryCode());
        }
        List<Deployment> deploymentList = QueryUtils.deploymentQuery().deploymentCategory(wfCategoryVo.getCategoryCode()).list();
        for (Deployment deployment : deploymentList) {
            repositoryService.setDeploymentCategory(deployment.getId(), bo.getCategoryCode());
        }
        List<Model> modelList = QueryUtils.modelQuery().modelCategory(wfCategoryVo.getCategoryCode()).list();
        for (Model model : modelList) {
            model.setCategory(bo.getCategoryCode());
            repositoryService.saveModel(model);
        }
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(WfCategory entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除流程分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 按照类别编码查询
     *
     * @param categoryCode 分类比吗
     */
    @Override
    public WfCategory queryByCategoryCode(String categoryCode) {
        return baseMapper.selectOne(new LambdaQueryWrapper<WfCategory>().eq(WfCategory::getCategoryCode, categoryCode));
    }
}
