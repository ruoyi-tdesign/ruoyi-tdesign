package org.dromara.common.core.constant;

/**
 * 通用常量信息
 *
 * @author ruoyi
 */
public interface Constants {

    /**
     * 未删除标识
     */
    String DEL_FLAG = "0";

    /**
     * UTF-8 字符集
     */
    String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    String GBK = "GBK";

    /**
     * www主域
     */
    String WWW = "www.";

    /**
     * http请求
     */
    String HTTP = "http://";

    /**
     * https请求
     */
    String HTTPS = "https://";

    /**
     * 登录成功
     */
    String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    String LOGOUT = "Logout";

    /**
     * 注册
     */
    String REGISTER = "Register";

    /**
     * 登录失败
     */
    String LOGIN_FAIL = "Error";

    /**
     * 验证码有效期（分钟）
     */
    Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 顶级父级id
     */
    Long TOP_PARENT_ID = 0L;

    /**
     * 管理员角色名称
     */
    String ADMIN_ROLE = "admin";

}

