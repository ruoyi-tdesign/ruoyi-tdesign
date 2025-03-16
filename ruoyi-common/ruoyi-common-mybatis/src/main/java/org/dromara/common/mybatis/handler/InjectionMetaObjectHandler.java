package org.dromara.common.mybatis.handler;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.handlers.StrictFill;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.dromara.common.core.domain.model.BaseUser;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.satoken.context.SaSecurityContext;
import org.dromara.common.satoken.utils.LoginHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * MP注入处理器
 *
 * @author Lion Li
 * @date 2021/4/25
 */
@Slf4j
public class InjectionMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入填充方法，用于在插入数据时自动填充实体对象中的创建时间、更新时间、创建人、更新人等信息
     *
     * @param metaObject 元对象，用于获取原始对象并进行填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            TableInfo tableInfo = findTableInfo(metaObject);
            List<StrictFill<?, ?>> fieldFills = new ArrayList<>();
            fieldFills.add(StrictFill.of("createTime", Date::new, Date.class));
            fieldFills.add(StrictFill.of("updateTime", Date::new, Date.class));
            fieldFills.add(StrictFill.of("createBy", this::getLoginUsername, String.class));
            fieldFills.add(StrictFill.of("createBy", this::getLoginId, Long.class));
            fieldFills.add(StrictFill.of("updateBy", this::getLoginUsername, String.class));
            fieldFills.add(StrictFill.of("updateBy", this::getLoginId, Long.class));
            fieldFills.add(StrictFill.of("createDept", this::getDeptId, Long.class));
            fieldFills.add(StrictFill.of("version", () -> 0L, Long.class));
            fieldFills.add(StrictFill.of("version", () -> 0, Integer.class));

            // 填充删除
            TableFieldInfo deleteFieldInfo = tableInfo.getLogicDeleteFieldInfo();
            if (deleteFieldInfo != null) {
                String property = deleteFieldInfo.getProperty();
                String notDeleteValue = deleteFieldInfo.getLogicNotDeleteValue();
                fieldFills.add(StrictFill.of(property, () -> notDeleteValue, String.class));
                fieldFills.add(StrictFill.of(property, () -> NumberUtil.parseInt(notDeleteValue), Integer.class));
            }

            // 插入时，如果对象值不为空，则使用对象值，否则使用填充值
            fillStrategy(true, metaObject, tableInfo, fieldFills);
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    /**
     * 更新填充方法，用于在更新数据时自动填充实体对象中的更新时间和更新人信息
     *
     * @param metaObject 元对象，用于获取原始对象并进行填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            TableInfo tableInfo = findTableInfo(metaObject);
            List<StrictFill<?, ?>> fieldFills = new ArrayList<>();
            fieldFills.add(StrictFill.of("updateTime", Date::new, Date.class));
            fieldFills.add(StrictFill.of("updateBy", this::getLoginUsername, String.class));
            fieldFills.add(StrictFill.of("updateBy", this::getLoginId, Long.class));

            // 更新时，不管值是否为空，都覆盖
            fillStrategy(false, metaObject, tableInfo, fieldFills);
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    /**
     * 获取部门id
     *
     * @return
     */
    private Long getDeptId() {
        return LoginHelper.getDeptId();
    }

    /**
     * 获取当前登录用户名
     *
     * @return 当前登录用户的名称，如果用户未登录则返回 null
     */
    private String getLoginUsername() {
        return SaSecurityContext.getContextOptional().map(BaseUser::getUsername).orElse(null);
    }

    /**
     * 获取登录用户id
     *
     * @return
     */
    private Long getLoginId() {
        return SaSecurityContext.getContextOptional().map(BaseUser::getUserId).orElse(null);
    }

    /**
     * insertFill为true时，需要判断是否为null，当为null时才填充数据
     * insertFill为false时，无需判断是否为null，覆盖数据
     *
     * @param insertFill 是否插入时填充
     * @param metaObject 元对象
     * @param tableInfo  表信息对象
     * @param fieldFills 字段填充脚本
     */
    private void fillStrategy(boolean insertFill, MetaObject metaObject, TableInfo tableInfo, List<StrictFill<?, ?>> fieldFills) {
        if ((insertFill && tableInfo.isWithInsertFill()) || (!insertFill && tableInfo.isWithUpdateFill())) {
            for (StrictFill<?, ?> fieldFill : fieldFills) {
                for (TableFieldInfo tableFieldInfo : tableInfo.getFieldList()) {
                    if (tableFieldInfo.getProperty().equals(fieldFill.getFieldName()) && tableFieldInfo.getPropertyType().equals(fieldFill.getFieldType())) {
                        // 如果是更新操作，则不需要判断是否为null，反之插入时需要判断当值为null时才覆盖
                        if (tableInfo.isWithUpdateFill() || metaObject.getValue(fieldFill.getFieldName()) == null) {
                            Object obj = fieldFill.getFieldVal().get();
                            if (Objects.nonNull(obj)) {
                                metaObject.setValue(fieldFill.getFieldName(), obj);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

}
