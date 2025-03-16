package org.dromara.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.constant.TenantConstants;
import org.dromara.common.core.enums.NormalDisableEnum;
import org.dromara.common.core.enums.YesNoEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.service.TenantService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.CacheUtils;
import org.dromara.common.tenant.annotation.IgnoreTenant;
import org.dromara.common.tenant.core.TenantEntity;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.system.domain.*;
import org.dromara.system.domain.bo.SysTenantBo;
import org.dromara.system.domain.query.SysTenantQuery;
import org.dromara.system.domain.vo.SysTenantPackageVo;
import org.dromara.system.domain.vo.SysTenantVo;
import org.dromara.system.events.TenantNewEvent;
import org.dromara.system.mapper.*;
import org.dromara.system.service.ISysTenantPackageService;
import org.dromara.system.service.ISysTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 租户Service业务层处理
 *
 * @author Michelle.Chung
 */
@RequiredArgsConstructor
@Service
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements ISysTenantService, TenantService {

    private final SysUserMapper userMapper;
    private final SysDeptMapper deptMapper;
    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysRoleDeptMapper roleDeptMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysConfigMapper configMapper;
    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataMapper dictDataMapper;
    @Autowired
    private ISysTenantPackageService tenantPackageService;

    /**
     * 查询租户
     */
    @Override
    public SysTenantVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 基于租户ID查询租户
     */
    @Cacheable(cacheNames = CacheNames.SYS_TENANT, key = "#tenantId")
    @Override
    public SysTenantVo queryByTenantId(String tenantId) {
        return baseMapper.selectVoOne(new LambdaQueryWrapper<SysTenant>().eq(SysTenant::getTenantId, tenantId));
    }

    /**
     * 查询租户列表
     */
    @Override
    public TableDataInfo<SysTenantVo> queryPageList(SysTenantQuery query) {
        return PageQuery.of(() -> baseMapper.queryList(query));
    }

    /**
     * 查询租户列表
     */
    @Override
    public List<SysTenantVo> queryList(SysTenantQuery query) {
        return baseMapper.queryList(query);
    }

    /**
     * 新增租户
     */
    @Override
    @IgnoreTenant
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SysTenantBo bo) {
        // 检查用户是否存在
        boolean existsUser = userMapper.exists(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getUserName, bo.getUsername()));
        if (existsUser) {
            throw new ServiceException("新增用户名'" + bo.getUsername() + "'失败，用户名已被占用");
        }

        SysTenant add = MapstructUtils.convert(bo, SysTenant.class);

        // 获取所有租户编号
        List<String> tenantIds = baseMapper.selectObjs(
            new LambdaQueryWrapper<SysTenant>().select(SysTenant::getTenantId), x -> {
                return Convert.toStr(x);
            });
        String tenantId = generateTenantId(tenantIds);
        add.setTenantId(tenantId);
        boolean flag = baseMapper.insert(add) > 0;
        if (!flag) {
            throw new ServiceException("创建租户失败");
        }
        bo.setId(add.getId());

        // 根据套餐创建角色与角色菜单
        Long roleId = createTenantRole(tenantId, bo.getPackageId());

        // 创建部门: 公司名是部门名称
        SysDept dept = new SysDept();
        dept.setTenantId(tenantId);
        dept.setDeptName(bo.getCompanyName());
        dept.setParentId(Constants.TOP_PARENT_ID);
        dept.setAncestors(Constants.TOP_PARENT_ID.toString());
        deptMapper.insert(dept);
        Long deptId = dept.getDeptId();

        // 角色和部门关联表
        SysRoleDept roleDept = new SysRoleDept();
        roleDept.setRoleId(roleId);
        roleDept.setDeptId(deptId);
        roleDeptMapper.insert(roleDept);

        // 创建系统用户
        SysUser user = new SysUser();
        user.setTenantId(tenantId);
        user.setUserName(bo.getUsername());
        user.setNickName(bo.getUsername());
        user.setPassword(BCrypt.hashpw(bo.getPassword()));
        user.setDeptId(deptId);
        userMapper.insert(user);
        //新增系统用户后，默认当前用户为部门的负责人
        SysDept sd = new SysDept();
        sd.setLeader(user.getUserId());
        sd.setDeptId(deptId);
        deptMapper.updateById(sd);

        // 用户和角色关联表
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getUserId());
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);

        String defaultTenantId = TenantConstants.DEFAULT_TENANT_ID;
        // 字典不再支持多租户操作，转为通用表

        List<SysConfig> sysConfigList = configMapper.selectList(
            new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getTenantId, defaultTenantId)
                .eq(SysConfig::getIsGlobal, YesNoEnum.NO.getCodeNum())
        );
        for (SysConfig config : sysConfigList) {
            config.setConfigId(null);
            config.setTenantId(tenantId);
        }
        configMapper.insertBatch(sysConfigList);

        // 发布创建事件
        SpringUtils.context().publishEvent(new TenantNewEvent(add));
        return true;
    }

    /**
     * 生成租户id
     *
     * @param tenantIds 已有租户id列表
     * @return 租户id
     */
    private String generateTenantId(List<String> tenantIds) {
        // 随机生成6位
        String numbers = RandomUtil.randomNumbers(6);
        // 判断是否存在，如果存在则重新生成
        if (tenantIds.contains(numbers)) {
            return generateTenantId(tenantIds);
        }
        return numbers;
    }

    /**
     * 根据租户菜单创建租户角色
     *
     * @param tenantId  租户编号
     * @param packageId 租户套餐id
     * @return 角色id
     */
    private Long createTenantRole(String tenantId, Long packageId) {
        // 获取租户套餐
        SysTenantPackageVo tenantPackage = tenantPackageService.queryById(packageId);
        if (ObjectUtil.isNull(tenantPackage)) {
            throw new ServiceException("套餐不存在");
        }
        // 获取套餐菜单id
        List<Long> menuIds = tenantPackage.getMenuIds();

        // 创建角色
        SysRole role = new SysRole();
        role.setTenantId(tenantId);
        role.setRoleName(TenantConstants.TENANT_ADMIN_ROLE_NAME);
        role.setRoleKey(TenantConstants.TENANT_ADMIN_ROLE_KEY);
        role.setRoleSort(1);
        role.setStatus(NormalDisableEnum.NORMAL.getCode());
        roleMapper.insert(role);
        Long roleId = role.getRoleId();

        // 创建角色菜单
        List<SysRoleMenu> roleMenus = new ArrayList<>(menuIds.size());
        menuIds.forEach(menuId -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenus.add(roleMenu);
        });
        roleMenuMapper.insertBatch(roleMenus);

        return roleId;
    }

    /**
     * 修改租户
     */
    @CacheEvict(cacheNames = CacheNames.SYS_TENANT, key = "#bo.tenantId")
    @Override
    public Boolean updateByBo(SysTenantBo bo) {
        SysTenant olTenant = getById(bo.getId());
        SysTenant tenant = MapstructUtils.convert(bo, SysTenant.class);
        tenant.setTenantId(null);
        if (Objects.equals(bo.getTenantId(), TenantConstants.DEFAULT_TENANT_ID)) {
            tenant.setPackageId(null);
            tenant.setExpireTime(null);
            tenant.setAccountCount(null);
        }
        boolean b = updateById(tenant);
        if (b && !Objects.equals(olTenant.getPackageId(), bo.getPackageId()) && bo.getPackageId() != null) {
            // 同步菜单
            SpringUtils.getAopProxy(this).syncTenantPackage(olTenant.getTenantId(), bo.getPackageId());
        }
        return b;
    }

    /**
     * 修改租户状态
     *
     * @param bo 租户信息
     * @return 结果
     */
    @CacheEvict(cacheNames = CacheNames.SYS_TENANT, key = "#bo.tenantId")
    @Override
    public int updateTenantStatus(SysTenantBo bo) {
        SysTenant tenant = new SysTenant();
        tenant.setId(bo.getId());
        tenant.setStatus(bo.getStatus());
        return baseMapper.updateById(tenant);
    }

    /**
     * 校验租户是否允许操作
     *
     * @param tenantId 租户ID
     */
    @Override
    public void checkTenantAllowed(String tenantId) {
        if (ObjectUtil.isNotNull(tenantId) && TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            throw new ServiceException("不允许操作管理租户");
        }
    }

    /**
     * 批量删除租户
     */
    @CacheEvict(cacheNames = CacheNames.SYS_TENANT, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 校验企业名称是否唯一
     */
    @Override
    public boolean checkCompanyNameUnique(SysTenantBo bo) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysTenant>()
            .eq(SysTenant::getCompanyName, bo.getCompanyName())
            .ne(ObjectUtil.isNotNull(bo.getTenantId()), SysTenant::getTenantId, bo.getTenantId()));
        return !exist;
    }

    /**
     * 校验账号余额
     */
    @Override
    public boolean checkAccountBalance(String tenantId) {
        SysTenantVo tenant = SpringUtils.getAopProxy(this).queryByTenantId(tenantId);
        // 如果余额为-1代表不限制
        if (tenant.getAccountCount() == -1) {
            return true;
        }
        Long userNumber = userMapper.selectCount(new LambdaQueryWrapper<>());
        // 如果余额大于0代表还有可用名额
        return tenant.getAccountCount() - userNumber > 0;
    }

    /**
     * 校验有效期
     */
    @Override
    public boolean checkExpireTime(String tenantId) {
        SysTenantVo tenant = SpringUtils.getAopProxy(this).queryByTenantId(tenantId);
        // 如果未设置过期时间代表不限制
        if (ObjectUtil.isNull(tenant.getExpireTime())) {
            return true;
        }
        // 如果当前时间在过期时间之前则通过
        return new Date().before(tenant.getExpireTime());
    }

    /**
     * 同步租户套餐
     */
    @Override
    @IgnoreTenant
    @Transactional(rollbackFor = Exception.class)
    public Boolean syncTenantPackage(String tenantId, Long packageId) {
        SysTenantPackageVo tenantPackage = tenantPackageService.queryById(packageId);
        List<SysRole> roles = roleMapper.selectList(
            new LambdaQueryWrapper<SysRole>().eq(SysRole::getTenantId, tenantId));
        List<Long> roleIds = new ArrayList<>(roles.size() - 1);
        List<Long> menuIds = tenantPackage.getMenuIds();
        roles.forEach(item -> {
            if (TenantConstants.TENANT_ADMIN_ROLE_KEY.equals(item.getRoleKey())) {
                List<SysRoleMenu> roleMenus = new ArrayList<>(menuIds.size());
                menuIds.forEach(menuId -> {
                    SysRoleMenu roleMenu = new SysRoleMenu();
                    roleMenu.setRoleId(item.getRoleId());
                    roleMenu.setMenuId(menuId);
                    roleMenus.add(roleMenu);
                });
                roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, item.getRoleId()));
                roleMenuMapper.insertBatch(roleMenus);
            } else {
                roleIds.add(item.getRoleId());
            }
        });
        if (!roleIds.isEmpty()) {
            roleMenuMapper.delete(
                new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds).notIn(!menuIds.isEmpty(), SysRoleMenu::getMenuId, menuIds));
        }
        return true;
    }

    /**
     * 通过租户ID查询租户企业名称
     *
     * @param tenantId 租户ID
     * @return 租户名称
     */
    @Override
    public String selectTenantNameById(String tenantId) {
        Optional<SysTenant> tenantOptional = lambdaQuery().eq(SysTenant::getTenantId, tenantId).select(SysTenant::getCompanyName).oneOpt();
        return tenantOptional.map(SysTenant::getCompanyName).orElse(null);
    }

    /**
     * 同步租户字典
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void syncTenantDict() {
        // 查询超管 所有字典数据
        List<SysDictType> dictTypeList = new ArrayList<>();
        List<SysDictData> dictDataList = new ArrayList<>();
        TenantHelper.ignore(() -> {
            dictTypeList.addAll(dictTypeMapper.selectList());
            dictDataList.addAll(dictDataMapper.selectList());
        });
        Map<String, List<SysDictType>> typeMap = StreamUtils.groupByKey(dictTypeList, TenantEntity::getTenantId);
        Map<String, Map<String, List<SysDictData>>> typeDataMap = StreamUtils.groupBy2Key(
            dictDataList, TenantEntity::getTenantId, SysDictData::getDictType);
        // 管理租户字典数据
        List<SysDictType> defaultTypeMap = typeMap.get(TenantConstants.DEFAULT_TENANT_ID);
        Map<String, List<SysDictData>> defaultTypeDataMap = typeDataMap.get(TenantConstants.DEFAULT_TENANT_ID);

        // 获取所有租户编号
        List<String> tenantIds = baseMapper.selectObjs(
            new LambdaQueryWrapper<SysTenant>().select(SysTenant::getTenantId)
                .eq(SysTenant::getStatus, NormalDisableEnum.NORMAL.getCode()), x -> {return Convert.toStr(x);});
        List<SysDictType> saveTypeList = new ArrayList<>();
        List<SysDictData> saveDataList = new ArrayList<>();
        Set<String> set = new HashSet<>();
        for (String tenantId : tenantIds) {
            if (TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
                continue;
            }
            for (SysDictType dictType : defaultTypeMap) {
                List<String> typeList = StreamUtils.toList(typeMap.get(tenantId), SysDictType::getDictType);
                List<SysDictData> dataList = defaultTypeDataMap.get(dictType.getDictType());
                if (typeList.contains(dictType.getDictType())) {
                    List<SysDictData> dataListTenant = typeDataMap.get(tenantId).get(dictType.getDictType());
                    Map<String, SysDictData> map = StreamUtils.toIdentityMap(dataListTenant, SysDictData::getDictValue);
                    for (SysDictData dictData : dataList) {
                        if (!map.containsKey(dictData.getDictValue())) {
                            // 设置字典编码为 null
                            dictData.setDictCode(null);
                            dictData.setTenantId(tenantId);
                            dictData.setCreateTime(null);
                            dictData.setUpdateTime(null);
                            set.add(tenantId);
                            saveDataList.add(dictData);
                        }
                    }
                } else {
                    dictType.setDictId(null);
                    dictType.setTenantId(tenantId);
                    dictType.setCreateTime(null);
                    dictType.setUpdateTime(null);
                    set.add(tenantId);
                    saveTypeList.add(dictType);
                    if (CollUtil.isNotEmpty(dataList)) {
                        // 筛选出 dictType 对应的 data
                        for (SysDictData dictData : dataList) {
                            // 设置字典编码为 null
                            dictData.setDictCode(null);
                            dictData.setTenantId(tenantId);
                            dictData.setCreateTime(null);
                            dictData.setUpdateTime(null);
                            set.add(tenantId);
                        }
                        saveDataList.addAll(dataList);
                    }
                }
            }
        }
        TenantHelper.ignore(() -> {
            if (CollUtil.isNotEmpty(saveTypeList)) {
                dictTypeMapper.insertBatch(saveTypeList);
            }
            if (CollUtil.isNotEmpty(saveDataList)) {
                dictDataMapper.insertBatch(saveDataList);
            }
        });
        for (String tenantId : set) {
            TenantHelper.dynamicTenant(tenantId, () -> CacheUtils.clear(CacheNames.SYS_DICT));
        }
    }

}
