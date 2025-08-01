package org.dromara.system.utils;

import org.dromara.system.domain.vo.SysFileUploadVo;
import org.dromara.system.domain.vo.SysFileVo;

import java.util.List;

/**
 * 系统存储工具类
 *
 * @author hexm
 * @date 2025/8/1
 */
public class SysFileUtil {

    public static final String RESOURCE_FILE_PREVIEW = "/resource/file/preview/";
    public static final String RESOURCE_FILE_DOWNLOAD = "/resource/file/download/";

    private SysFileUtil() {
    }

    /**
     * 获取预览地址
     *
     * @param filename 文件名
     * @return
     */
    public static String getPreviewUrl(String filename) {
        return RESOURCE_FILE_PREVIEW + filename;
    }

    /**
     * 获取下载地址
     *
     * @param filename 文件名
     * @return
     */
    public static String getDownloadUrl(String filename) {
        return RESOURCE_FILE_DOWNLOAD + filename;
    }

    /**
     * 批量设置预览和下载地址
     *
     * @param vo 文件vo
     */
    public static void packedPreviewAndDownloadUrl(SysFileVo vo) {
        vo.setPreviewUrl(getPreviewUrl(vo.getFilename()));
        vo.setDownloadUrl(getDownloadUrl(vo.getFilename()));
    }

    /**
     * 批量设置预览和下载地址
     *
     * @param vos 文件列表
     */
    public static void packedPreviewAndDownloadUrl(List<SysFileVo> vos) {
        for (SysFileVo vo : vos) {
            packedPreviewAndDownloadUrl(vo);
        }
    }

    /**
     * 批量设置预览和下载地址
     *
     * @param vo 文件vo
     */
    public static void packedPreviewAndDownloadUrl(SysFileUploadVo vo) {
        vo.setPreviewUrl(getPreviewUrl(vo.getFilename()));
        vo.setDownloadUrl(getDownloadUrl(vo.getFilename()));
    }
}
