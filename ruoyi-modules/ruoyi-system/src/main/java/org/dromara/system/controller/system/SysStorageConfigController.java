package org.dromara.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.ui.FieldOption;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.storage.config.StorageConfigData;
import org.dromara.common.storage.config.StorageFieldConfig;
import org.dromara.common.storage.enums.StorageEnum;
import org.dromara.common.storage.support.StorageSupport;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.SysStorageConfigBo;
import org.dromara.system.domain.query.SysStorageConfigQuery;
import org.dromara.system.domain.vo.SysStorageConfigVo;
import org.dromara.system.service.ISysStorageConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 存储配置
 *
 * @author yixiacoco
 * @date 2025-05-04
 */
@Validated
@RestController
@RequestMapping("/system/storageConfig")
public class SysStorageConfigController extends BaseController {

    @Autowired
    private ISysStorageConfigService sysStorageConfigService;

    /**
     * 查询存储配置列表
     */
    @SaCheckPermission("system:storageConfig:list")
    @GetMapping("/list")
    public TableDataInfo<SysStorageConfigVo> list(SysStorageConfigQuery query) {
        return sysStorageConfigService.queryPageList(query);
    }

    /**
     * 导出存储配置列表
     */
    @SaCheckPermission("system:storageConfig:export")
    @Log(title = "存储配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysStorageConfigQuery query, HttpServletResponse response) {
        List<SysStorageConfigVo> list = sysStorageConfigService.queryList(query);
        ExcelUtil.exportExcel(list, "存储配置", SysStorageConfigVo.class, response);
    }

    /**
     * 获取存储配置详细信息
     *
     * @param storageConfigId 主键
     */
    @SaCheckPermission(value = {"system:storageConfig:query", "system:storageConfig:edit"}, mode = SaMode.OR)
    @GetMapping("/{storageConfigId}")
    public R<SysStorageConfigVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long storageConfigId) {
        return R.ok(sysStorageConfigService.queryById(storageConfigId));
    }

    /**
     * 新增存储配置
     */
    @SaCheckPermission("system:storageConfig:add")
    @Log(title = "存储配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysStorageConfigBo bo) {
        return toAjax(sysStorageConfigService.insertByBo(bo));
    }

    /**
     * 修改存储配置
     */
    @SaCheckPermission("system:storageConfig:edit")
    @Log(title = "存储配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysStorageConfigBo bo) {
        return toAjax(sysStorageConfigService.updateByBo(bo));
    }

    /**
     * 删除存储配置
     *
     * @param storageConfigIds 主键串
     */
    @SaCheckPermission("system:storageConfig:remove")
    @Log(title = "存储配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{storageConfigIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] storageConfigIds) {
        return toAjax(sysStorageConfigService.deleteWithValidByIds(List.of(storageConfigIds)));
    }

    /**
     * 获取所有存储平台
     */
    @SaCheckPermission(value = "system:storageConfig:*")
    @GetMapping("/getAllStoragePlatform")
    public R<List<FieldOption<String>>> getAllStoragePlatform() {
        List<StorageEnum> allStoragePlatform = StorageSupport.getAllStoragePlatform();
        List<FieldOption<String>> list = allStoragePlatform.stream()
            .map(platform -> new FieldOption<>(platform.getDesc(), platform.getCode()))
            .toList();
        return R.ok(list);
    }

    /**
     * 获取支持的存储平台
     */
    @SaCheckPermission(value = "system:storageConfig:*")
    @GetMapping("/getSupportPlatform")
    public R<List<FieldOption<String>>> getSupportPlatform() {
        List<StorageEnum> allStoragePlatform = StorageSupport.getSupportPlatform();
        List<FieldOption<String>> list = allStoragePlatform.stream()
            .map(platform -> new FieldOption<>(platform.getDesc(), platform.getCode()))
            .toList();
        return R.ok(list);
    }

    /**
     * 获取存储平台配置
     */
    @SaCheckPermission(value = "system:storageConfig:*")
    @GetMapping("/getStoragePlatformConfigs")
    public R<Map<String, StorageFieldConfig>> getStoragePlatformConfigs() {
        return R.ok(StorageConfigData.STORAGE_CONFIG_MAP);
    }
}
