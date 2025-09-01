package org.dromara.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import jakarta.validation.constraints.NotEmpty;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.core.validate.QueryOneGroup;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.satoken.context.SaSecurityContext;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.SysFileCategoryBo;
import org.dromara.system.domain.query.SysFileCategoryQuery;
import org.dromara.system.domain.vo.SysFileCategoryVo;
import org.dromara.system.service.ISysFileCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件分类
 *
 * @author yixiacoco
 * @date 2025-05-13
 */
@Validated
@RestController
@RequestMapping("/system/fileCategory")
public class SysFileCategoryController extends BaseController {

    @Autowired
    private ISysFileCategoryService sysFileCategoryService;

    /**
     * 查询文件分类列表
     */
    @SaCheckPermission("system:fileCategory:list")
    @GetMapping("/list")
    public R<List<SysFileCategoryVo>> list(SysFileCategoryQuery query) {
        query.setLoginType(SaSecurityContext.getContext().getLoginType());
        query.setCreateBy(SaSecurityContext.getContext().getUserId());
        List<SysFileCategoryVo> list = sysFileCategoryService.queryList(query);
        return R.ok(list);
    }

    /**
     * 获取文件分类详细信息
     *
     * @param query 查询对象
     */
    @SaCheckPermission(value = {"system:fileCategory:query", "system:fileCategory:edit"}, mode = SaMode.OR)
    @GetMapping("/query")
    public R<SysFileCategoryVo> getInfo(@Validated(QueryOneGroup.class) SysFileCategoryQuery query) {
        query.setLoginType(SaSecurityContext.getContext().getLoginType());
        query.setCreateBy(SaSecurityContext.getContext().getUserId());
        return R.ok(sysFileCategoryService.query(query));
    }

    /**
     * 新增文件分类
     */
    @SaCheckPermission("system:fileCategory:add")
    @Log(title = "文件分类", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysFileCategoryBo bo) {
        bo.setLoginType(SaSecurityContext.getContext().getLoginType());
        bo.setCreateBy(SaSecurityContext.getContext().getUserId());
        return toAjax(sysFileCategoryService.insertByBo(bo));
    }

    /**
     * 修改文件分类
     */
    @SaCheckPermission("system:fileCategory:edit")
    @Log(title = "文件分类", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysFileCategoryBo bo) {
        bo.setLoginType(SaSecurityContext.getContext().getLoginType());
        bo.setCreateBy(SaSecurityContext.getContext().getUserId());
        return toAjax(sysFileCategoryService.updateByBo(bo));
    }

    /**
     * 删除文件分类
     *
     * @param fileCategoryIds 主键串
     */
    @SaCheckPermission("system:fileCategory:remove")
    @Log(title = "文件分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fileCategoryIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] fileCategoryIds) {
        String loginType = SaSecurityContext.getContext().getLoginType();
        Long userId = SaSecurityContext.getContext().getUserId();
        return toAjax(sysFileCategoryService.deleteWithValidByIds(List.of(fileCategoryIds), loginType, userId));
    }
}
