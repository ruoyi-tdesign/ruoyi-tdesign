package org.dromara.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.SysFileBo;
import org.dromara.system.domain.query.SysFileQuery;
import org.dromara.system.domain.vo.SysFileVo;
import org.dromara.system.service.ISysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件记录
 *
 * @author yixiacoco
 * @date 2025-05-12
 */
@Validated
@RestController
@RequestMapping("/system/file")
public class SysFileController extends BaseController {

    @Autowired
    private ISysFileService sysFileService;

    /**
     * 查询文件记录列表
     */
    @SaCheckPermission("system:file:list")
    @GetMapping("/list")
    public TableDataInfo<SysFileVo> list(SysFileQuery query) {
        return sysFileService.queryPageList(query);
    }

    /**
     * 导出文件记录列表
     */
    @SaCheckPermission("system:file:export")
    @Log(title = "文件记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysFileQuery query, HttpServletResponse response) {
        List<SysFileVo> list = sysFileService.queryList(query);
        ExcelUtil.exportExcel(list, "文件记录", SysFileVo.class, response);
    }

    /**
     * 获取文件记录详细信息
     *
     * @param fileId 主键
     */
    @SaCheckPermission(value = {"system:file:query", "system:file:edit"}, mode = SaMode.OR)
    @GetMapping("/{fileId}")
    public R<SysFileVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long fileId) {
        return R.ok(sysFileService.queryById(fileId));
    }

    /**
     * 修改文件记录
     */
    @SaCheckPermission("system:file:edit")
    @Log(title = "文件记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysFileBo bo) {
        return toAjax(sysFileService.updateByBo(bo));
    }

    /**
     * 删除文件记录
     *
     * @param fileIds 主键串
     */
    @SaCheckPermission("system:file:remove")
    @Log(title = "文件记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fileIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] fileIds) {
        return toAjax(sysFileService.deleteWithValidByIds(List.of(fileIds)));
    }
}
