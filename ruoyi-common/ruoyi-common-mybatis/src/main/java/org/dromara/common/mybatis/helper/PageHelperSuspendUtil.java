package org.dromara.common.mybatis.helper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * PageHelper分页临时挂起工具，用于MyBatis拦截器内部执行子SQL
 */
public class PageHelperSuspendUtil {

    /**
     * 挂起：取出当前线程分页对象，清空分页，返回快照用于后续恢复
     * @return page快照，null代表当前无分页
     */
    public static Page<?> suspend() {
        Page<?> page = PageHelper.getLocalPage();
        if (page != null) {
            PageHelper.clearPage();
        }
        return page;
    }

    /**
     * 恢复分页上下文，把快照放回ThreadLocal
     * @param snapshot suspend拿到的page快照
     */
    public static void resume(Page<?> snapshot) {
        if (snapshot != null) {
            // 放回线程本地，后续业务SQL继续分页生效
            PageHelper.setLocalPage(snapshot);
        }
    }
}
