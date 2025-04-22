package org.dromara.system.controller.system;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.SysOssBo;
import org.dromara.system.domain.query.SysOssQuery;
import org.dromara.system.domain.vo.SysOssUploadVo;
import org.dromara.system.domain.vo.SysOssVo;
import org.dromara.system.service.ISysOssCategoryService;
import org.dromara.system.service.ISysOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文件上传 控制层
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/resource/oss")
public class SysOssController extends BaseController {

    @Autowired
    private ISysOssService ossService;
    @Autowired
    private ISysOssCategoryService ossCategoryService;

    /**
     * 查询OSS对象存储列表
     */
    @SaCheckPermission("system:oss:list")
    @GetMapping("/list")
    public TableDataInfo<SysOssVo> list(SysOssQuery query) {
        return ossService.queryPageList(query);
    }

    /**
     * 查询我的OSS对象存储列表
     */
    @SaCheckPermission(value = {"system:oss:list", "system:ossCategory:list", "system:ossCategory:query", "system:ossCategory:edit"}, mode = SaMode.OR)
    @GetMapping("/my/list")
    public TableDataInfo<SysOssVo> myList(SysOssQuery query) {
        query.setCreateBy(LoginHelper.getUserId());
        query.setUserType(UserType.SYS_USER.getUserType());
        return ossService.queryPageList(query);
    }

    /**
     * 获取OSS对象存储详细信息
     *
     * @param ossId 主键
     */
    @SaCheckPermission(value = {"system:oss:query", "system:oss:edit"}, mode = SaMode.OR)
    @GetMapping("/{ossId}")
    public R<SysOssVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long ossId) {
        return R.ok(ossService.queryById(ossId));
    }

    /**
     * 查询OSS对象基于id串
     *
     * @param ossIds OSS对象ID串
     */
    @SaCheckPermission("system:oss:query")
    @GetMapping("/listByIds/{ossIds}")
    public R<List<SysOssVo>> listByIds(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ossIds) {
        List<SysOssVo> list = ossService.listVoByIds(List.of(ossIds));
        return R.ok(list);
    }

    /**
     * 查询OSS对象基于url串
     *
     * @param urls OSS对象url串
     */
    @SaCheckPermission("system:oss:list")
    @GetMapping("/listByUrls")
    public R<List<SysOssVo>> listByUrls(@NotBlank(message = "url不能为空") String urls) {
        String[] urlArray = URLDecoder.decode(String.valueOf(urls), StandardCharsets.UTF_8).split(",");
        List<SysOssVo> list = ossService.listVoByUrls(List.of(urlArray));
        return R.ok(list);
    }

    /**
     * 上传OSS对象存储
     *
     * @param file 文件
     */
    @SaCheckPermission("system:oss:upload")
    @Log(title = "OSS对象存储", businessType = BusinessType.INSERT)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<SysOssUploadVo> upload(@RequestPart("file") MultipartFile file, Long ossCategoryId) {
        if (ObjectUtil.isNull(file)) {
            return R.fail("上传文件不能为空");
        }
        SysOssBo bo = new SysOssBo();
        bo.setCreateBy(LoginHelper.getUserId());
        bo.setUserTypeEnum(UserType.SYS_USER);
        bo.setIsLock(0);
        boolean exist = ossCategoryService.hasId(ossCategoryId, UserType.SYS_USER, LoginHelper.getUserId());
        bo.setOssCategoryId(exist ? ossCategoryId : 0L);
        SysOssVo oss = ossService.upload(file, bo);
        SysOssUploadVo uploadVo = new SysOssUploadVo();
        uploadVo.setUrl(oss.getUrl());
        uploadVo.setFileName(oss.getOriginalName());
        uploadVo.setOssId(oss.getOssId().toString());
        return R.ok(uploadVo);
    }

    /**
     * 下载OSS对象
     *
     * @param ossId OSS对象ID
     */
    @SaCheckPermission("system:oss:download")
    @GetMapping("/download/{ossId}")
    public void download(@NotNull(message = "主键不能为空") @PathVariable Long ossId, HttpServletResponse response) throws IOException {
        ossService.download(ossId, response);
    }

    /**
     * 修改OSS对象存储
     */
    @SaCheckPermission("system:oss:edit")
    @Log(title = "OSS对象存储", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysOssBo bo) {
        bo.setUserTypeEnum(UserType.SYS_USER);
        bo.setCreateBy(LoginHelper.getUserId());
        return toAjax(ossService.updateByBo(bo));
    }

    /**
     * 删除OSS对象存储
     *
     * @param ossIds OSS对象ID串
     */
    @SaCheckPermission("system:oss:remove")
    @Log(title = "OSS对象存储", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ossIds) {
        return toAjax(ossService.deleteWithValidByIds(List.of(ossIds)));
    }

    /**
     * 删除我的OSS对象存储
     *
     * @param ossIds OSS对象ID串
     */
    @SaCheckPermission("system:oss:remove")
    @Log(title = "OSS对象存储", businessType = BusinessType.DELETE)
    @DeleteMapping("/my/{ossIds}")
    public R<Void> removeMyIds(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ossIds) {
        return toAjax(ossService.deleteMyIds(List.of(ossIds), UserType.SYS_USER, LoginHelper.getUserId()));
    }

    /**
     * 移动到分类
     *
     * @param categoryId 分类id
     * @param ossIds     主键id
     * @return
     */
    @SaCheckPermission("system:oss:edit")
    @PostMapping("/{categoryId}/move")
    public R<Void> move(@NotNull(message = "分类id不能为空") @PathVariable Long categoryId,
                        @RequestBody @NotEmpty(message = "主键不能为空") List<Long> ossIds) {
        ossService.move(categoryId, ossIds, UserType.SYS_USER, LoginHelper.getUserId());
        return R.ok();
    }

}
