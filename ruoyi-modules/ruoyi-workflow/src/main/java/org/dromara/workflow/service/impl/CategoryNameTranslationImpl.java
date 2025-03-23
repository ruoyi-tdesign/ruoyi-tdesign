package org.dromara.workflow.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.annotation.TranslationType;
import org.dromara.common.translation.core.impl.SimpleTranslationImpl;
import org.dromara.workflow.common.ConditionalOnEnable;
import org.dromara.workflow.common.constant.FlowConstant;
import org.dromara.workflow.service.IFlwCategoryService;
import org.springframework.stereotype.Service;

/**
 * 流程分类名称翻译实现
 *
 * @author AprilWind
 */
@ConditionalOnEnable
@Slf4j
@RequiredArgsConstructor
@Service
@TranslationType(type = FlowConstant.CATEGORY_ID_TO_NAME)
public class CategoryNameTranslationImpl extends SimpleTranslationImpl {

    private final IFlwCategoryService flwCategoryService;

    @Override
    public String translation(Object key, Translation translation) {
        if (key instanceof String categoryId) {
            return flwCategoryService.selectCategoryNameById(categoryId);
        }
        return null;
    }
}
