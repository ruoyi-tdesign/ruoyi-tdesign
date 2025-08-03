package org.dromara.system.utils;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.resizers.configurations.AlphaInterpolation;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;
import net.coobird.thumbnailator.resizers.configurations.Rendering;
import org.dromara.system.domain.dto.FileResourceDto;
import org.dromara.system.domain.vo.SysFileUploadVo;
import org.dromara.system.domain.vo.SysFileVo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    /**
     * 处理图片
     *
     * @param dto      图片处理参数
     * @param is       输入流
     * @param os       输出流
     */
    public static void handleImage(FileResourceDto dto, InputStream is, OutputStream os) {
        if (dto.getW() == null && dto.getH() == null) {
            IoUtil.copy(is, os);
            return;
        }
        Thumbnails.Builder<? extends InputStream> builder = Thumbnails.of(is);
        if (dto.getW() != null && dto.getH() != null) {
            if (dto.getM() == null) {
                dto.setM("lfit");
            }
            // lfit：固定宽高，按长边缩放
            // mfit：固定宽高，按短边缩放
            // fill：固定宽高，居中裁剪
            // fixed：强制宽高
            switch (dto.getM()) {
                case "lfit" -> {
                    if (dto.getH() > dto.getW()) {
                        builder.height(dto.getH());
                    } else {
                        builder.width(dto.getW());
                    }
                }
                case "mfit" -> {
                    if (dto.getH() > dto.getW()) {
                        builder.width(dto.getW());
                    } else {
                        builder.height(dto.getH());
                    }
                }
                case "fill" -> {
                    builder.size(dto.getW(), dto.getH())
                        .crop(Positions.CENTER);
                }
                case "fixed" -> {
                    builder.forceSize(dto.getW(), dto.getH());
                }
            }
        } else if (dto.getW() != null) {
            builder.width(dto.getW());
        } else if (dto.getH() != null) {
            builder.height(dto.getH());
        }
        if (dto.getQ() != null) {
            builder.outputQuality(dto.getQ());
        } else {
            builder.outputQuality(1);
        }
        if (dto.getR() != null) {
            builder.rotate(dto.getR());
        }
        builder.antialiasing(Antialiasing.ON)
//            .useExifOrientation(false)
//            .rendering(Rendering.SPEED)
//            .alphaInterpolation(AlphaInterpolation.SPEED)
            .useOriginalFormat();
        try {
            builder.toOutputStream(os);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
