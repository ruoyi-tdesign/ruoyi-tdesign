package org.dromara.common.core.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.core.domain.dto.RoleDTO;
import org.dromara.common.core.domain.dto.PostDTO;

import java.util.List;

/**
 * 登录用户身份权限
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LoginUser extends BaseUser {

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门类别编码
     */
    private String deptCategory;

    /**
     * 部门名
     */
    private String deptName;

    /**
     * 角色对象
     */
    private List<RoleDTO> roles;

    /**
     * 岗位对象
     */
    private List<PostDTO> posts;

    /**
     * 数据权限 当前角色ID
     */
    private Long roleId;

    /**
     * 客户端
     */
    private String clientKey;

    /**
     * 获取登录id
     */
    public String getLoginId() {
        if (userType == null) {
            throw new IllegalArgumentException("用户类型不能为空");
        }
        if (getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return userType + ":" + getUserId();
    }
}
