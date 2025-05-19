package org.dromara.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.ObjectUtil;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.context.SaSecurityContext;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.SysFileBo;
import org.dromara.system.domain.bo.SysOssBo;
import org.dromara.system.domain.query.SysFileQuery;
import org.dromara.system.domain.vo.SysFileVo;
import org.dromara.system.domain.vo.SysOssUploadVo;
import org.dromara.system.domain.vo.SysOssVo;
import org.dromara.system.service.ISysFileCategoryService;
import org.dromara.system.service.ISysFileService;
import org.dromara.x.file.storage.core.FileStorageService;
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
//    @Autowired
//    private FileStorageService fileStorageService;

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
    @SaCheckPermission(value = {"system:file:list", "system:fileCategory:list", "system:fileCategory:query", "system:fileCategory:edit"}, mode = SaMode.OR)
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
    @SaCheckPermission("system:file:upload")
    @Log(title = "文件存储", businessType = BusinessType.INSERT)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<SysOssUploadVo> upload(@RequestPart("file") MultipartFile file, Long ossCategoryId) {
        if (ObjectUtil.isNull(file)) {
            return R.fail("上传文件不能为空");
        }
        String loginType = SaSecurityContext.getContext().getLoginType();
        Long userId = SaSecurityContext.getContext().getUserId();
        boolean exist = fileCategoryService.hasId(ossCategoryId, loginType, userId);

//        SysOssBo bo = new SysOssBo();
//        bo.setCreateBy(LoginHelper.getUserId());
//        bo.setUserTypeEnum(UserType.SYS_USER);
//        bo.setIsLock(0);
//
//        bo.setOssCategoryId(exist ? ossCategoryId : 0L);
//        SysOssVo oss = ossService.upload(file, bo);
//        SysOssUploadVo uploadVo = new SysOssUploadVo();
//        uploadVo.setUrl(oss.getUrl());
//        uploadVo.setFileName(oss.getOriginalName());
//        uploadVo.setOssId(oss.getOssId().toString());
//        return R.ok(uploadVo);
    }

    /**
     * 修改文件记录
     */
    @SaCheckPermission("system:file:edit")
    @Log(title = "文件记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysFileBo bo) {
        return toAjax(fileService.updateByBo(bo));
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
        return toAjax(fileService.deleteWithValidByIds(List.of(fileIds)));
    }
}
