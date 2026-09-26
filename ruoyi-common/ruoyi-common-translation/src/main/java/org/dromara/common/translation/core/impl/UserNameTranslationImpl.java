package org.dromara.common.translation.core.impl;

import cn.hutool.core.convert.Convert;
import lombok.AllArgsConstructor;
import org.dromara.common.core.service.UserService;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.annotation.TranslationType;
import org.dromara.common.translation.constant.TransConstant;

/**
 * 用户名翻译实现
 *
 * @author Lion Li
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.USER_ID_TO_NAME)
public class UserNameTranslationImpl extends SimpleTranslationImpl {

    private final UserService userService;

    @Override
    public Object translation(Object key, Translation translation) {
        return userService.selectUserNameById(Convert.toLong(key));
    }
}
