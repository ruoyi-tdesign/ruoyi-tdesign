package org.dromara.common.translation.core.impl;

import lombok.AllArgsConstructor;
import org.dromara.common.core.service.FileService;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.annotation.TranslationType;
import org.dromara.common.translation.constant.TransConstant;

/**
 * 文件存储翻译实现
 *
 * @author hexm
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.FILE_ID_TO_URL)
public class FileUrlTranslationImpl extends SimpleTranslationImpl {

    private final FileService fileService;

    @Override
    public Object translation(Object key, Translation translation) {
        if (key instanceof String ids) {
            return fileService.selectUrlByIds(ids);
        } else if (key instanceof Long id) {
            return fileService.selectUrlByIds(id.toString());
        }
        return null;
    }
}
