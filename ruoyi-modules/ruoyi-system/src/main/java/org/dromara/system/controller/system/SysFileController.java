package org.dromara.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.ObjectUtil;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.context.SaSecurityContext;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.SysFile;
import org.dromara.system.domain.bo.SysFileBo;
import org.dromara.system.domain.query.SysFileQuery;
import org.dromara.system.domain.vo.SysFileUploadVo;
import org.dromara.system.domain.vo.SysFileVo;
import org.dromara.system.service.ISysFileCategoryService;
import org.dromara.system.service.ISysFileService;
import org.dromara.system.utils.SysFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private ISysFileService fileService;
    @Autowired
    private ISysFileCategoryService fileCategoryService;

    /**
     * 查询文件记录列表
     */
    @SaCheckPermission("system:file:list")
    @GetMapping("/list")
    public TableDataInfo<SysFileVo> list(SysFileQuery query) {
        return fileService.queryPageList(query);
    }

    /**
     * 查询我的文件列表
     */
    @GetMapping("/my/list")
    public TableDataInfo<SysFileVo> myList(SysFileQuery query) {
        query.setCreateBy(SaSecurityContext.getContext().getUserId());
        query.setUserType(SaSecurityContext.getContext().getLoginType());
        return fileService.queryPageList(query);
    }

    /**
     * 获取文件记录详细信息
     *
     * @param fileId 主键
     */
    @SaCheckPermission(value = {"system:file:query", "system:file:edit"}, mode = SaMode.OR)
    @GetMapping("/{fileId}")
    public R<SysFileVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long fileId) {
        return R.ok(fileService.queryById(fileId));
    }

    /**
     * 查询文件基于id串
     *
     * @param fileIds 文件ID串
     */
    @SaCheckPermission("system:file:query")
    @GetMapping("/listByIds/{fileIds}")
    public R<List<SysFileVo>> listByIds(@NotEmpty(message = "主键不能为空") @PathVariable Long[] fileIds) {
        List<SysFileVo> list = fileService.listVoByIds(List.of(fileIds));
        return R.ok(list);
    }

    /**
     * 上传文件
     *
     * @param file 文件
     */
    @Log(title = "文件存储", businessType = BusinessType.INSERT)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<SysFileUploadVo> upload(@RequestPart("file") MultipartFile file, Long fileCategoryId) {
        if (ObjectUtil.isNull(file)) {
            return R.fail("上传文件不能为空");
        }
        String loginType = SaSecurityContext.getContext().getLoginType();
        Long userId = SaSecurityContext.getContext().getUserId();
        boolean exist = fileCategoryService.hasId(fileCategoryId, loginType, userId);

        SysFileBo bo = new SysFileBo();
        bo.setCreateBy(userId);
        bo.setUserType(loginType);
        bo.setIsLock(0);
        bo.setFileCategoryId(exist ? fileCategoryId : 0L);
        SysFile upload = fileService.upload(bo, file);

        SysFileUploadVo uploadVo = new SysFileUploadVo();
        uploadVo.setFileId(upload.getFileId().toString());
        uploadVo.setFilename(upload.getFilename());
        SysFileUtil.packedPreviewAndDownloadUrl(uploadVo);
        return R.ok(uploadVo);
    }

    /**
     * 修改文件记录
     */
    @RepeatSubmit()
    @PutMapping("/my")
    @Log(title = "文件存储", businessType = BusinessType.UPDATE)
    public R<Void> myEdit(@Validated(EditGroup.class) @RequestBody SysFileBo bo) {
        String loginType = SaSecurityContext.getContext().getLoginType();
        Long userId = SaSecurityContext.getContext().getUserId();
        bo.setUserType(loginType);
        bo.setCreateBy(userId);
        return toAjax(fileService.updateByBo(bo));
    }

    /**
     * 删除文件记录
     *
     * @param fileIds 主键串
     */
    @SaCheckPermission("system:file:remove")
    @DeleteMapping("/{fileIds}")
    @Log(title = "文件存储", businessType = BusinessType.DELETE)
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] fileIds) {
        return toAjax(fileService.deleteWithValidByIds(List.of(fileIds)));
    }

    /**
     * 删除我的文件存储
     *
     * @param fileIds 文件ID串
     */
    @Log(title = "文件存储", businessType = BusinessType.DELETE)
    @DeleteMapping("/my/{fileIds}")
    public R<Void> removeMyIds(@NotEmpty(message = "主键不能为空") @PathVariable Long[] fileIds) {
        String loginType = SaSecurityContext.getContext().getLoginType();
        Long userId = SaSecurityContext.getContext().getUserId();
        return toAjax(fileService.deleteMyIds(List.of(fileIds), loginType, userId));
    }

    /**
     * 移动到分类
     *
     * @param categoryId 分类id
     * @param fileIds    主键id
     * @return
     */
    @SaCheckPermission("system:file:edit")
    @PostMapping("/{categoryId}/move")
    @Log(title = "文件存储", businessType = BusinessType.UPDATE)
    public R<Void> move(@NotNull(message = "分类id不能为空") @PathVariable Long categoryId,
                        @RequestBody @NotEmpty(message = "主键不能为空") List<Long> fileIds) {
        String loginType = SaSecurityContext.getContext().getLoginType();
        Long userId = SaSecurityContext.getContext().getUserId();
        fileService.move(categoryId, fileIds, loginType, userId);
        return R.ok();
    }

    /**
     * 解锁文件
     *
     * @param fileIds 文件ID串
     */
    @SaCheckPermission("system:file:lock")
    @PostMapping("/unlock")
    @Log(title = "文件存储", businessType = BusinessType.UPDATE)
    public R<Void> unlock(@NotEmpty(message = "主键不能为空") @RequestBody List<Long> fileIds) {
        fileService.lockOps(fileIds, false);
        return R.ok();
    }

    /**
     * 锁定文件
     *
     * @param fileIds 文件ID串
     */
    @SaCheckPermission("system:file:lock")
    @PostMapping("/lock")
    @Log(title = "文件存储", businessType = BusinessType.UPDATE)
    public R<Void> lock(@NotEmpty(message = "主键不能为空") @RequestBody List<Long> fileIds) {
        fileService.lockOps(fileIds, true);
        return R.ok();
    }


    /**
     * 解锁我的文件
     *
     * @param fileIds 文件ID串
     */
    @PostMapping("/my/unlock")
    @Log(title = "文件存储", businessType = BusinessType.UPDATE)
    public R<Void> myUnlock(@NotEmpty(message = "主键不能为空") @RequestBody List<Long> fileIds) {
        String loginType = SaSecurityContext.getContext().getLoginType();
        Long userId = SaSecurityContext.getContext().getUserId();
        fileService.securityLockOps(fileIds, loginType, userId, false);
        return R.ok();
    }

    /**
     * 锁定我的文件
     *
     * @param fileIds 文件ID串
     */
    @PostMapping("/my/lock")
    @Log(title = "文件存储", businessType = BusinessType.UPDATE)
    public R<Void> myLock(@NotEmpty(message = "主键不能为空") @RequestBody List<Long> fileIds) {
        String loginType = SaSecurityContext.getContext().getLoginType();
        Long userId = SaSecurityContext.getContext().getUserId();
        fileService.securityLockOps(fileIds, loginType, userId, true);
        return R.ok();
    }
}
