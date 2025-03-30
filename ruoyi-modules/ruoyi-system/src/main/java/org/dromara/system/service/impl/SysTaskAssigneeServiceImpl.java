package org.dromara.system.service.impl;

import cn.hutool.core.convert.Convert;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.dto.TaskAssigneeDTO;
import org.dromara.common.core.domain.model.TaskAssigneeBody;
import org.dromara.common.core.service.TaskAssigneeService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.query.SysDeptQuery;
import org.dromara.system.domain.query.SysPostQuery;
import org.dromara.system.domain.query.SysRoleQuery;
import org.dromara.system.domain.query.SysUserQuery;
import org.dromara.system.domain.vo.SysDeptVo;
import org.dromara.system.domain.vo.SysPostVo;
import org.dromara.system.domain.vo.SysRoleVo;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.service.ISysDeptService;
import org.dromara.system.service.ISysPostService;
import org.dromara.system.service.ISysRoleService;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 工作流设计器获取任务执行人
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysTaskAssigneeServiceImpl implements TaskAssigneeService {

    // 上级Service注入下级Service 其他Service永远不可能注入当前类 避免循环注入
    private final ISysPostService postService;
    private final ISysDeptService deptService;
    private final ISysUserService userService;
    private final ISysRoleService roleService;

    /**
     * 查询角色并返回任务指派的列表，支持分页
     *
     * @param taskQuery 查询条件
     * @return 办理人
     */
    @Override
    public TaskAssigneeDTO selectRolesByTaskAssigneeList(TaskAssigneeBody taskQuery) {
        SysRoleQuery query = new SysRoleQuery();
        query.setPageSize(taskQuery.getPageSize());
        query.setPageNum(taskQuery.getPageNum());
        query.setRoleName(taskQuery.getHandlerCode());
        query.setRoleKey(taskQuery.getHandlerName());
        Map<String, Object> params = query.getParams();
        params.put("beginTime", taskQuery.getBeginTime());
        params.put("endTime", taskQuery.getEndTime());
        TableDataInfo<SysRoleVo> page = roleService.selectPageRoleList(query);
        // 使用封装的字段映射方法进行转换
        List<TaskAssigneeDTO.TaskHandler> handlers = TaskAssigneeDTO.convertToHandlerList(page.getRows(),
            SysRoleVo::getRoleId, SysRoleVo::getRoleKey, SysRoleVo::getRoleName, null, SysRoleVo::getCreateTime);
        return new TaskAssigneeDTO(page.getTotal(), handlers);
    }

    /**
     * 查询岗位并返回任务指派的列表，支持分页
     *
     * @param taskQuery 查询条件
     * @return 办理人
     */
    @Override
    public TaskAssigneeDTO selectPostsByTaskAssigneeList(TaskAssigneeBody taskQuery) {
        SysPostQuery query = new SysPostQuery();
        query.setPageSize(taskQuery.getPageSize());
        query.setPageNum(taskQuery.getPageNum());
        query.setPostCategory(taskQuery.getHandlerCode());
        query.setPostName(taskQuery.getHandlerName());
        Map<String, Object> params = query.getParams();
        params.put("beginTime", taskQuery.getBeginTime());
        params.put("endTime", taskQuery.getEndTime());
        query.setBelongDeptId(Convert.toLong(taskQuery.getGroupId()));
        TableDataInfo<SysPostVo> page = postService.selectPagePostList(query);
        // 使用封装的字段映射方法进行转换
        List<TaskAssigneeDTO.TaskHandler> handlers = TaskAssigneeDTO.convertToHandlerList(page.getRows(),
            SysPostVo::getPostId, SysPostVo::getPostCategory, SysPostVo::getPostName, SysPostVo::getDeptId, SysPostVo::getCreateTime);
        return new TaskAssigneeDTO(page.getTotal(), handlers);
    }

    /**
     * 查询部门并返回任务指派的列表，支持分页
     *
     * @param taskQuery 查询条件
     * @return 办理人
     */
    @Override
    public TaskAssigneeDTO selectDeptsByTaskAssigneeList(TaskAssigneeBody taskQuery) {
        SysDeptQuery query = new SysDeptQuery();
        query.setPageSize(taskQuery.getPageSize());
        query.setPageNum(taskQuery.getPageNum());
        query.setDeptCategory(taskQuery.getHandlerCode());
        query.setDeptName(taskQuery.getHandlerName());
        Map<String, Object> params = query.getParams();
        params.put("beginTime", taskQuery.getBeginTime());
        params.put("endTime", taskQuery.getEndTime());
        query.setBelongDeptId(Convert.toLong(taskQuery.getGroupId()));
        TableDataInfo<SysDeptVo> page = deptService.selectPageDeptList(query);
        // 使用封装的字段映射方法进行转换
        List<TaskAssigneeDTO.TaskHandler> handlers = TaskAssigneeDTO.convertToHandlerList(page.getRows(),
            SysDeptVo::getDeptId, SysDeptVo::getDeptCategory, SysDeptVo::getDeptName, SysDeptVo::getParentId, SysDeptVo::getCreateTime);
        return new TaskAssigneeDTO(page.getTotal(), handlers);
    }


    /**
     * 查询用户并返回任务指派的列表，支持分页
     *
     * @param taskQuery 查询条件
     * @return 办理人
     */
    @Override
    public TaskAssigneeDTO selectUsersByTaskAssigneeList(TaskAssigneeBody taskQuery) {
        SysUserQuery query = new SysUserQuery();
        query.setPageSize(taskQuery.getPageSize());
        query.setPageNum(taskQuery.getPageNum());
        query.setUserName(taskQuery.getHandlerCode());
        query.setNickName(taskQuery.getHandlerName());
        Map<String, Object> params = query.getParams();
        params.put("beginTime", taskQuery.getBeginTime());
        params.put("endTime", taskQuery.getEndTime());
        query.setDeptId(Convert.toLong(taskQuery.getGroupId()));
        TableDataInfo<SysUserVo> page = userService.selectPageUserList(query);
        // 使用封装的字段映射方法进行转换
        List<TaskAssigneeDTO.TaskHandler> handlers = TaskAssigneeDTO.convertToHandlerList(page.getRows(),
            SysUserVo::getUserId, SysUserVo::getUserName, SysUserVo::getNickName, SysUserVo::getDeptId, SysUserVo::getCreateTime);
        return new TaskAssigneeDTO(page.getTotal(), handlers);
    }

}
