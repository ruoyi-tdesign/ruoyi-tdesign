package org.dromara.system.controller.system;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.dromara.common.core.constant.RegexConstants;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.file.MimeTypeUtils;
import org.dromara.common.encrypt.annotation.ApiEncrypt;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.helper.DataPermissionHelper;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.SysUser;
import org.dromara.system.domain.bo.SysOssBo;
import org.dromara.system.domain.bo.SysUserBo;
import org.dromara.system.domain.bo.SysUserPasswordBo;
import org.dromara.system.domain.bo.SysUserProfileBo;
import org.dromara.system.domain.query.SysLogininforQuery;
import org.dromara.system.domain.vo.AvatarVo;
import org.dromara.system.domain.vo.ProfileVo;
import org.dromara.system.domain.vo.SysLogininforVo;
import org.dromara.system.domain.vo.SysOssVo;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.service.ISysLogininforService;
import org.dromara.system.service.ISysOssService;
import org.dromara.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * 个人信息 业务处理
 *
 * @author Lion Li
 */
@Validated
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {

    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysOssService ossService;
    @Autowired
    private ISysLogininforService logininforService;

    /**
     * 个人信息
     */
    @GetMapping
    public R<ProfileVo> profile() {
        SysUserVo user = userService.selectUserById(LoginHelper.getUserId());
        ProfileVo profileVo = new ProfileVo();
        profileVo.setUser(user);
        profileVo.setRoleGroup(userService.selectUserRoleGroup(user.getUserId()));
        profileVo.setPostGroup(userService.selectUserPostGroup(user.getUserId()));
        return R.ok(profileVo);
    }

    /**
     * 修改用户信息
     */
    @RepeatSubmit
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/basic")
    public R<Void> updateProfile(@Validated @RequestBody SysUserProfileBo profile) {
        SysUserBo user = BeanUtil.toBean(profile, SysUserBo.class);
        user.setUserId(LoginHelper.getUserId());
        int rows = DataPermissionHelper.ignore(() -> userService.updateUserProfile(user));
        if (rows > 0) {
            return R.ok();
        }
        return R.fail("修改个人信息异常，请联系管理员");
    }

    /**
     * 修改手机号
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePhonenumber")
    public R<Void> updateNickname(@Pattern(regexp = RegexConstants.MOBILE, message = "手机号格式不正确")
                                  @RequestParam String phonenumber) {
        String username = LoginHelper.getUsername();
        SysUserBo bo = new SysUserBo();
        bo.setUserId(LoginHelper.getUserId());
        bo.setPhonenumber(phonenumber);
        TenantHelper.ignore(() -> {
            if (StringUtils.isNotEmpty(phonenumber) && !userService.checkPhoneUnique(bo)) {
                throw new ServiceException("修改用户'" + username + "'失败，手机号码已存在");
            }
        });
        userService.lambdaUpdate().set(SysUser::getPhonenumber, phonenumber)
            .eq(SysUser::getUserId, LoginHelper.getUserId()).update();
        return R.ok();
    }

    /**
     * 修改邮箱
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updateEmail")
    public R<Void> updateEmail(@RequestParam @Email(message = "邮箱格式不正确")
                               @Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符") String email) {
        String username = LoginHelper.getUsername();
        SysUserBo bo = new SysUserBo();
        bo.setUserId(LoginHelper.getUserId());
        bo.setEmail(email);
        TenantHelper.ignore(() -> {
            if (StringUtils.isNotEmpty(email) && !userService.checkEmailUnique(bo)) {
                throw new ServiceException("修改用户'" + username + "'失败，邮箱账号已存在");
            }
        });
        userService.lambdaUpdate().set(SysUser::getEmail, email)
            .eq(SysUser::getUserId, LoginHelper.getUserId()).update();
        return R.ok();
    }

    /**
     * 修改密码
     *
     * @param bo 新旧密码
     */
    @RepeatSubmit
    @ApiEncrypt
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public R<Void> updatePwd(@Validated @RequestBody SysUserPasswordBo bo) {
        SysUserVo user = userService.selectUserById(LoginHelper.getUserId());
        String password = user.getPassword();
        if (!BCrypt.checkpw(bo.getOldPassword(), password)) {
            return R.fail("修改密码失败，旧密码错误");
        }
        if (BCrypt.checkpw(bo.getNewPassword(), password)) {
            return R.fail("新密码不能与旧密码相同");
        }

        if (userService.resetUserPwd(user.getUserId(), BCrypt.hashpw(bo.getNewPassword())) > 0) {
            return R.ok();
        }
        return R.fail("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     *
     * @param avatarfile 用户头像
     */
    @RepeatSubmit
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<AvatarVo> avatar(@RequestPart("avatarfile") MultipartFile avatarfile) {
        if (!avatarfile.isEmpty()) {
            String extension = FileUtil.extName(avatarfile.getOriginalFilename());
            if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION)) {
                return R.fail("文件格式不正确，请上传" + Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION) + "格式");
            }
            SysOssBo bo = new SysOssBo();
            bo.setCreateBy(LoginHelper.getUserId());
            bo.setUserTypeEnum(UserType.SYS_USER);
            bo.setIsLock(1);
            bo.setOssCategoryId(0L);
            SysOssVo oss = ossService.upload(avatarfile, bo);
            String avatar = oss.getUrl();
            if (userService.updateUserAvatar(LoginHelper.getUserId(), oss.getOssId())) {
                AvatarVo avatarVo = new AvatarVo();
                avatarVo.setImgUrl(avatar);
                return R.ok(avatarVo);
            }
        }
        return R.fail("上传图片异常，请联系管理员");
    }

    /**
     * 删除用户头像
     */
    @Log(title = "用户头像", businessType = BusinessType.DELETE)
    @DeleteMapping("/avatar/remove")
    public R<Void> removeAvatar() {
        userService.lambdaUpdate()
            .set(SysUser::getAvatar, null)
            .eq(SysUser::getUserId, LoginHelper.getUserId())
            .update();
        return R.ok();
    }

    /**
     * 获取访问记录列表
     */
    @GetMapping("/loginLog/list")
    public TableDataInfo<SysLogininforVo> loginLogList(SysLogininforQuery query) {
        query.setUserId(LoginHelper.getUserId());
        return logininforService.queryPageList(query);
    }
}
