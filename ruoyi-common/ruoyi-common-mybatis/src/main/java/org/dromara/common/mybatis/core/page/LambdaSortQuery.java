package org.dromara.common.mybatis.core.page;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.util.HashMap;
import java.util.Map;

/**
 * 支持lambda表达式的排序查询实体类
 * <p>
 * example:
 * <pre>{@code
 *   LambdaSortQuery.of(SysUser::getUserId).execute(() -> userService.queryList(query));
 * }</pre>
 *
 * @author YiXiacoco
 */
@Accessors(chain = true)
public class LambdaSortQuery<T> extends SortQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 清除排序条件
     */
    @Override
    public LambdaSortQuery<T> clear() {
        super.clear();
        return this;
    }

    /**
     * 使用请求参数设置排序
     */
    @Override
    public LambdaSortQuery<T> defaultOrder() {
        super.defaultOrder();
        return this;
    }

    /**
     * 添加排序字段
     *
     * @param func lambda
     */
    public LambdaSortQuery<T> addOrderByColumn(Func1<T, ?> func) {
        String fieldName = getColumn(func);
        if (StrUtil.isNotBlank(fieldName)) {
            addOrderByColumn(fieldName);
        }
        return this;
    }

    /**
     * 添加排序字段
     *
     * @param func  lambda
     * @param isAsc 是否升序
     */
    public LambdaSortQuery<T> addOrderByColumn(Func1<T, ?> func, boolean isAsc) {
        String fieldName = getColumn(func);
        if (StrUtil.isNotBlank(fieldName)) {
            addOrderByColumn(fieldName, isAsc);
        }
        return this;
    }

    /**
     * 使用默认的请求参数排序创建对象
     */
    public static LambdaSortQuery<?> of() {
        LambdaSortQuery<?> sortQuery = new LambdaSortQuery<>();
        return sortQuery.defaultOrder();
    }

    /**
     * 使用升序排序创建对象
     */
    public static LambdaSortQuery<?> of(String orderByColumn) {
        LambdaSortQuery<?> sortQuery = new LambdaSortQuery<>();
        sortQuery.addOrderByColumn(orderByColumn);
        return sortQuery;
    }

    /**
     * 使用排序创建对象
     */
    public static LambdaSortQuery<?> of(String orderByColumn, boolean isAsc) {
        LambdaSortQuery<?> sortQuery = new LambdaSortQuery<>();
        sortQuery.addOrderByColumn(orderByColumn, isAsc);
        return sortQuery;
    }

    /**
     * 使用升序排序创建对象
     */
    public static <T> LambdaSortQuery<T> of(Func1<T, ?> func) {
        LambdaSortQuery<T> sortQuery = new LambdaSortQuery<>();
        return sortQuery.addOrderByColumn(func);
    }

    /**
     * 使用排序创建对象
     */
    public static <T> LambdaSortQuery<T> of(Func1<T, ?> func, boolean isAsc) {
        LambdaSortQuery<T> sortQuery = new LambdaSortQuery<>();
        return sortQuery.addOrderByColumn(func, isAsc);
    }

    private static final Map<Func1<?, ?>, String> cache = new HashMap<>();

    /**
     * 获取lambda字段信息
     *
     * @param func lambda
     * @return 字段使用下划线方式返回
     */
    private static <T> String getColumn(Func1<T, ?> func) {
        String fieldName = cache.get(func);
        if (fieldName != null) {
            return fieldName;
        }
        // IDEA 调试模式下 lambda 表达式是一个代理
        if (MethodHandleProxies.isWrapperInstance(func)) {
            MethodHandle dmh = MethodHandleProxies.wrapperInstanceTarget(func);
            Executable executable = MethodHandles.reflectAs(Executable.class, dmh);
            fieldName = BeanUtil.getFieldName(executable.getName());
        } else {
            fieldName = LambdaUtil.getFieldName(func);
        }
        fieldName = StrUtil.toUnderlineCase(fieldName);
        cache.put(func, fieldName);
        return fieldName;
    }
}
