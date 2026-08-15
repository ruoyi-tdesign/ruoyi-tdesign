package org.dromara.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.lock.annotation.Lock4j;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.domain.R;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.query.SysLogininforQuery;
import org.dromara.system.domain.vo.SysLogininforVo;
import org.dromara.system.service.ISysLogininforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统访问记录
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController {

    @Autowired
    private ISysLogininforService logininforService;

    /**
     * 获取系统访问记录列表
     */
    @SaCheckPermission("monitor:logininfor:list")
    @GetMapping("/list")
    public TableDataInfo<SysLogininforVo> list(SysLogininforQuery query) {
        return logininforService.queryPageList(query);
    }

    /**
     * 导出系统访问记录列表
     */
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission("monitor:logininfor:export")
    @PostMapping("/export")
    public void export(SysLogininforQuery query, HttpServletResponse response) {
        List<SysLogininforVo> list = logininforService.queryList(query);
        ExcelUtil.exportExcel(list, "登录日志", SysLogininforVo.class, response);
    }

    /**
     * 获取系统访问记录详细信息
     *
     * @param infoId 主键
     */
    @SaCheckPermission("monitor:logininfor:query")
    @GetMapping("/{infoId}")
    public R<SysLogininforVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long infoId) {
        return R.ok(logininforService.queryById(infoId));
    }

    /**
     * 批量删除登录日志
     *
     * @param infoIds 日志ids
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public R<Void> remove(@NotEmpty(message = "日志id不能为空") @PathVariable Long[] infoIds) {
        return toAjax(logininforService.deleteLogininforByIds(infoIds));
    }

    /**
     * 清理系统访问记录
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @Lock4j
    @DeleteMapping("/clean")
    public R<Void> clean() {
        logininforService.cleanLogininfor();
        return R.ok();
    }

    @SaCheckPermission("monitor:logininfor:unlock")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @RepeatSubmit()
    @GetMapping("/unlock/{userName}")
    public R<Void> unlock(@NotBlank(message = "用户名不能为空") @PathVariable("userName") String userName) {
        String loginName = GlobalConstants.PWD_ERR_CNT_KEY + userName;
        if (RedisUtils.hasKey(loginName)) {
            RedisUtils.deleteObject(loginName);
        }
        return R.ok();
    }

}
