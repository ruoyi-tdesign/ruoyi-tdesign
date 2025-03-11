package org.dromara.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.enums.MessageTypeEnum;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.config.MessageData;
import org.dromara.system.config.MessageFieldConfig;
import org.dromara.system.domain.bo.SysMessageConfigBo;
import org.dromara.system.domain.query.SysMessageConfigQuery;
import org.dromara.system.domain.vo.MessageTypeVo;
import org.dromara.system.domain.vo.SysMessageConfigVo;
import org.dromara.system.service.ISysMessageConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 消息配置
 *
 * @author hexm
 * @date 2023-06-27
 */
@Validated
@RestController
@RequestMapping("/system/messageConfig")
public class SysMessageConfigController extends BaseController {

    @Autowired
    private ISysMessageConfigService sysMessageConfigService;

    /**
     * 查询消息配置列表
     */
    @SaCheckPermission("system:messageConfig:list")
    @GetMapping("/list")
    public TableDataInfo<SysMessageConfigVo> list(SysMessageConfigQuery query) {
        return sysMessageConfigService.queryPageList(query);
    }

    /**
     * 导出消息配置列表
     */
    @SaCheckPermission("system:messageConfig:export")
    @Log(title = "消息配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysMessageConfigQuery query, HttpServletResponse response) {
        List<SysMessageConfigVo> list = sysMessageConfigService.queryList(query);
        ExcelUtil.exportExcel(list, "消息配置", SysMessageConfigVo.class, response);
    }

    /**
     * 获取消息配置详细信息
     *
     * @param messageConfigId 主键
     */
    @SaCheckPermission(value = {"system:messageConfig:query", "system:messageConfig:edit"}, mode = SaMode.OR)
    @GetMapping("/{messageConfigId}")
    public R<SysMessageConfigVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long messageConfigId) {
        return R.ok(sysMessageConfigService.queryById(messageConfigId));
    }

    /**
     * 新增消息配置
     */
    @SaCheckPermission("system:messageConfig:add")
    @Log(title = "消息配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysMessageConfigBo bo) {
        return toAjax(sysMessageConfigService.insertByBo(bo));
    }

    /**
     * 修改消息配置
     */
    @SaCheckPermission("system:messageConfig:edit")
    @Log(title = "消息配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysMessageConfigBo bo) {
        return toAjax(sysMessageConfigService.updateByBo(bo));
    }

    /**
     * 删除消息配置
     *
     * @param messageConfigIds 主键串
     */
    @SaCheckPermission("system:messageConfig:remove")
    @Log(title = "消息配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{messageConfigIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] messageConfigIds) {
        return toAjax(sysMessageConfigService.deleteWithValidByIds(List.of(messageConfigIds)));
    }

    /**
     * 刷新消息配置缓存
     */
    @SaCheckPermission("system:messageConfig:remove")
    @Log(title = "消息配置", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public R<Void> refreshCache() {
        sysMessageConfigService.removeCache();
        return R.ok();
    }

    /**
     * 获取支持的消息类型
     */
    @SaCheckPermission("system:messageConfig:*")
    @GetMapping("/getSupplierType")
    public static R<List<MessageTypeVo>> getSupplierType() {
        List<MessageTypeVo> list = Arrays.stream(MessageTypeEnum.values()).map(messageType -> {
            MessageTypeVo vo = new MessageTypeVo();
            vo.setMessageType(messageType.name());
            vo.setDescription(messageType.getDescription());
            vo.setSupplierTypeMap(messageType.getSupplierTypeMap());
            return vo;
        }).toList();
        return R.ok(list);
    }

    /**
     * 获取消息配置缓存
     */
    @SaCheckPermission("system:messageConfig:*")
    @GetMapping("/getMessageFieldConfigs")
    public R<Map<String, MessageFieldConfig>> getMessageFieldConfigs() {
        return R.ok(MessageData.SUPPLIER_TYPE_MAP);
    }
}
