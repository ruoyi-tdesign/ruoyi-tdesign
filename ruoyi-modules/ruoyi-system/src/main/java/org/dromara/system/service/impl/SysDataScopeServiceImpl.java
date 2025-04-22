package org.dromara.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.mybatis.helper.DataBaseHelper;
import org.dromara.system.service.ISysDataScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据权限 实现
 * <p>
 * 注意: 此Service内不允许调用标注`数据权限`注解的方法
 * 例如: deptMapper.selectList 此 selectList 方法标注了`数据权限`注解 会出现循环解析的问题
 *
 * @author Lion Li
 */
@Service("sdss")
public class SysDataScopeServiceImpl implements ISysDataScopeService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取角色自定义权限
     *
     * @param roleId 角色Id
     * @return 部门Id组
     */
    @Cacheable(cacheNames = CacheNames.SYS_ROLE_CUSTOM, key = "#roleId", condition = "#roleId != null")
    @Override
    public String getRoleCustom(Long roleId) {
        if (ObjectUtil.isNull(roleId)) {
            return "-1";
        }
        // 此处使用jdbc防止与pagehelper插件冲突
        List<Long> deptIds = jdbcTemplate.queryForList("select dept_id from sys_role_dept srd where srd.role_id = ?", Long.class, roleId);
        if (CollUtil.isNotEmpty(deptIds)) {
            return deptIds.stream().map(Convert::toStr).collect(Collectors.joining(","));
        }
        return "-1";
    }

    /**
     * 获取部门及以下权限
     *
     * @param deptId 部门Id
     * @return 部门Id组
     */
    @Cacheable(cacheNames = CacheNames.SYS_DEPT_AND_CHILD, key = "#deptId", condition = "#deptId != null")
    @Override
    public String getDeptAndChild(Long deptId) {
        if (ObjectUtil.isNull(deptId)) {
            return "-1";
        }
        // 此处使用jdbc防止与pagehelper插件冲突
        List<Long> ids = jdbcTemplate.queryForList("select dept_id from sys_dept sd where " + DataBaseHelper.findInSet(deptId, "ancestors"), Long.class);
        ids.add(deptId);

        if (CollUtil.isNotEmpty(ids)) {
            return StreamUtils.join(ids, Convert::toStr);
        }
        return "-1";
    }

}
