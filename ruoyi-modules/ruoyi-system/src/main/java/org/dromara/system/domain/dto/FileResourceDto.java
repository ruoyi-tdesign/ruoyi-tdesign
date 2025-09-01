package org.dromara.system.domain.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

/**
 * 文件资源DTO
 *
 * @author hexm
 * @date 2025/8/2
 */
@Getter
@Setter
public class FileResourceDto implements Serializable {
    /** 宽度 */
    @Min(value = 1, message = "宽度不能小于1")
    private Integer w;
    /** 高度 */
    @Min(value = 1, message = "高度不能小于1")
    private Integer h;
    /** 质量: 0.0 ~ 1.0 */
    @Range(min = 0, max = 1, message = "质量数值范围取值区间：0.0 ~ 1.0")
    private Double q;
    /**
     * 缩放方式
     * lfit：固定宽高，按长边缩放
     * mfit：固定宽高，按短边缩放
     * fill：固定宽高，居中裁剪
     * fixed：强制宽高
     */
    private String m;
    /** 旋转：0~360° */
    private Double r;
}
