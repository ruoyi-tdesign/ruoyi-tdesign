package org.dromara.system.controller.system;

import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.servlet.http.HttpServletResponse;
import org.dromara.system.domain.dto.FileResourceDto;
import org.dromara.system.service.ISysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 资源控制器
 *
 * @author hexm
 * @date 2025/7/6
 */
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
    @RequestMapping("/preview/{fileName}")
    public void preview(@PathVariable String fileName, @Validated FileResourceDto dto, HttpServletResponse response) {
        fileService.preview(fileName, dto, response);
    }

    /**
     * 文件下载
     *
     * @param fileName 文件名称
     */
    @SaIgnore
    @RequestMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, @Validated FileResourceDto dto, HttpServletResponse response) {
        fileService.download(fileName, dto, response);
    }
}
