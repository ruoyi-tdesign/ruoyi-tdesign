package org.dromara.system.domain.bo;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.common.core.xss.Xss;

import java.io.Serializable;

/**
 * 个人信息业务处理
 *
 * @author Michelle.Chung
 */
@Data
@NoArgsConstructor
public class SysUserProfileBo implements Serializable {

    /**
     * 用户昵称
     */
    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过{max}个字符")
    private String nickName;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private String sex;

}
