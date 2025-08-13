package org.dromara.system.utils;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SmUtil;
import io.github.linpeilie.utils.StrUtil;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;
import org.dromara.common.core.enums.FormatsType;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.system.domain.dto.FileResourceDto;
import org.dromara.system.domain.vo.SysFileUploadVo;
import org.dromara.system.domain.vo.SysFileVo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
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
    /**
     * 生成方式：SecretKey secretKey = KeyUtil.generateKey("SM4");
     * HexUtil.encodeHexStr(secretKey.getEncoded());
     */
    public static final String KEY = "fa1d80a3866c3220b940f94341a447de";

    private SysFileUtil() {
    }

    /**
     * 获取预览地址
     *
     * @param filename 文件名
     * @return
     */
    public static String getPreviewUrl(String filename) {
        // 这里使用yyyyMMdd格式的时间，可以存储大致的过期时间和命中缓存
        long l = Long.parseLong(DateUtils.parseDateToStr(FormatsType.YYYYMMDD, DateUtils.addDays(new Date(), 1)));
        String timestamp = BigInteger.valueOf(l).toString(Character.MAX_RADIX);
        String sign = SmUtil.sm4(HexUtil.decodeHex(KEY)).encryptHex(timestamp);
        return RESOURCE_FILE_PREVIEW + sign + "/" + filename;
    }

    /**
     * 获取下载地址
     *
     * @param filename 文件名
     * @return
     */
    public static String getDownloadUrl(String filename) {
        long l = Long.parseLong(DateUtils.parseDateToStr(FormatsType.YYYYMMDD, DateUtils.addDays(new Date(), 1)));
        String timestamp = BigInteger.valueOf(l).toString(Character.MAX_RADIX);
        String sign = SmUtil.sm4(HexUtil.decodeHex(KEY)).encryptHex(timestamp);
        return RESOURCE_FILE_DOWNLOAD + sign + "/" + filename;
    }

    /**
     * 验证密文及过期时间
     *
     * @param encryptStr 密文
     */
    public static void verifySign(String encryptStr) {
        if (StrUtil.isBlank(encryptStr)) {
            throw new ServiceException("签名不存在");
        }
        String l;
        try {
            l = SmUtil.sm4(HexUtil.decodeHex(KEY)).decryptStr(encryptStr);
        } catch (Exception e) {
            throw new ServiceException("签名验证失败");
        }
        BigInteger timestamp = new BigInteger(l, Character.MAX_RADIX);
        long currentTime = Long.parseLong(DateUtils.parseDateToStr(FormatsType.YYYYMMDD, new Date()));
        if (timestamp.compareTo(BigInteger.valueOf(currentTime)) < 0) {
            throw new ServiceException("签名已过期");
        }
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
     * @param dto 图片处理参数
     * @param is  输入流
     * @param os  输出流
     */
    public static void handleImage(FileResourceDto dto, InputStream is, OutputStream os) {
        try {
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
            builder.toOutputStream(os);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
