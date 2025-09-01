package org.dromara.system.config;


import cn.hutool.core.util.ReflectUtil;
import org.dromara.common.core.enums.MessageTypeEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息供应商配置
 *
 * @author hexm
 * @date 2025/3/9
 */
public abstract class SupplierFieldConfigs {

    /** 获取消息类型 */
    public abstract MessageTypeEnum getMessageType();

    /** 获取模板模式 */
    public abstract TemplateMode getTemplateMode();

    @SuppressWarnings("unchecked cast")
    public MessageFieldConfig getMessageConfig() {
        Map<String, FieldConfig<Object>> supplierConfig = new LinkedHashMap<>();

        List<Field> fieldList = getFieldsDirectly(this.getClass());
        for (Field field : fieldList) {
            if (field.getType().isAssignableFrom(FieldConfig.class)) {
                supplierConfig.put(field.getName(), (FieldConfig<Object>) ReflectUtil.getFieldValue(this, field));
            }
        }
        return new MessageFieldConfig(supplierConfig, getMessageType(), getTemplateMode());
    }

    public static List<Field> getFieldsDirectly(Class<?> beanClass) throws SecurityException {
        List<Field> fieldList = new ArrayList<>();

        Class<?> searchType = beanClass;
        Field[] declaredFields;
        while (searchType != null) {
            declaredFields = searchType.getDeclaredFields();
            if (fieldList.isEmpty()) {
                fieldList.addAll(Arrays.asList(declaredFields));
            } else {
                // 父类字段在前
                fieldList.addAll(0, Arrays.asList(declaredFields));
            }
            searchType = searchType.getSuperclass();
        }
        return fieldList;
    }
}
