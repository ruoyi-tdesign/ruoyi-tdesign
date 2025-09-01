package org.dromara.system.controller.system;

import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.system.domain.dto.FileResourceDto;
import org.dromara.system.service.ISysFileService;
import org.dromara.system.utils.SysFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

/**
 * 资源控制器
 *
 * @author hexm
 * @date 2025/7/6
 */
@Slf4j
@Validated
@Controller
@RequestMapping("/resource/file")
public class FileResourceController {

    @Autowired
    private ISysFileService fileService;

    /**
     * 文件预览
     *
     * @param fileName 文件名称
     */
    @SaIgnore
    @RequestMapping("/preview/{encryptStr}/{fileName}")
    public void preview(@PathVariable String encryptStr,
                        @PathVariable String fileName,
                        @Validated FileResourceDto dto,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        // 协商缓存
        String ifNoneMatch = ServletUtils.getHeader(request, "If-None-Match");
        if (Objects.equals(ifNoneMatch, fileName)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }
        try {
            SysFileUtil.verifySign(encryptStr);
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        fileService.preview(fileName, dto, response);
    }

    /**
     * 文件下载
     *
     * @param fileName 文件名称
     */
    @SaIgnore
    @RequestMapping("/download/{encryptStr}/{fileName}")
    public void download(@PathVariable String encryptStr,
                         @PathVariable String fileName,
                         @Validated FileResourceDto dto,
                         HttpServletResponse response) {
        try {
            SysFileUtil.verifySign(encryptStr);
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        fileService.download(fileName, dto, response);
    }
}
