package org.dromara.common.satoken.context;

import cn.dev33.satoken.context.SaHolder;
import org.dromara.common.core.domain.model.BaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 授权上下文对象
 *
 * @author hexm
 * @date 2023/05/08 15:19
 */
public class SaSecurityContext {

    private static final String KEY = "SaToken-SecurityContext";

    /**
     * 设置上下文对象
     *
     * @param context 上下文对象
     */
    @SuppressWarnings("unchecked cast")
    public static <T extends BaseUser> void setContext(T context) {
        List<T> list = (List<T>) SaHolder.getStorage().get(KEY);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(context);
        SaHolder.getStorage().set(KEY, list);
    }

    /**
     * 获取当前上下文对象
     */
    @SuppressWarnings("unchecked cast")
    public static <T extends BaseUser> T getContext() {
        try {
            List<T> list = (List<T>) SaHolder.getStorage().get(KEY);
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取所有当前上下文对象
     */
    @SuppressWarnings("unchecked cast")
    public static <T extends BaseUser> List<T> getContextList() {
        try {
            List<T> list = (List<T>) SaHolder.getStorage().get(KEY);
            return Collections.unmodifiableList(list);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前下文对象
     *
     * @return
     */
    public static <T extends BaseUser> Optional<T> getContextOptional() {
        return Optional.ofNullable(getContext());
    }
}
