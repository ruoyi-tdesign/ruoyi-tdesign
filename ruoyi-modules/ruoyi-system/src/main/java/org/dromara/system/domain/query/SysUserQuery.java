package org.dromara.system.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BasePageQuery;

import java.util.List;

/**
 * 用户信息查询对象 sys_user
 *
 * @author hexm
 * @date 2023-04-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserQuery extends BasePageQuery {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 帐号状态（1正常 0停用）
     */
    private String status;

    /**
     * 部门组
     */
    private Long[] deptIds;

    /**
     * 数据权限 当前角色ID
     */
    private Long roleId;

    /**
     * 用户id
     */
    private List<Long> userIds;

    /**
     * 排除不查询的用户(工作流用)
     */
    private String[] excludeUserIds;

}
