package org.dromara.common.translation.core.impl;

import lombok.AllArgsConstructor;
import org.dromara.common.core.service.UserService;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.annotation.TranslationType;
import org.dromara.common.translation.constant.TransConstant;

/**
 * 用户名称翻译实现
 *
 * @author may
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.USER_ID_TO_NICKNAME)
public class NicknameTranslationImpl extends SimpleTranslationImpl {

    private final UserService userService;

    @Override
    public Object translation(Object key, Translation translation) {
        if (key instanceof Long id) {
            return userService.selectNicknameByIds(id.toString());
        } else if (key instanceof String ids) {
            return userService.selectNicknameByIds(ids);
        }
        return null;
    }
}
