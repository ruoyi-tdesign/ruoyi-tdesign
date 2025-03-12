package org.dromara.common.core.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.NodeParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dromara.common.core.utils.reflect.ReflectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 扩展 hutool TreeUtil 封装系统树构建
 *
 * @author Lion Li
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TreeBuildUtils extends TreeUtil {

    /**
     * 根据前端定制差异化字段
     */
    public static final TreeNodeConfig DEFAULT_CONFIG = TreeNodeConfig.DEFAULT_CONFIG.setNameKey("label");

    public static <T, K> List<Tree<K>> build(List<T> list, NodeParser<T, K> nodeParser) {
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        K k = ReflectUtils.invokeGetter(list.get(0), "parentId");
        return TreeUtil.build(list, k, DEFAULT_CONFIG, nodeParser);
    }

    /**
     * 对一个tree展开
     *
     * @param data        数据
     * @param getChildren 获取子节点的方法
     * @param <T>
     * @return
     */
    public static <T> List<T> unwrap(List<T> data, Function<T, List<T>> getChildren) {
        List<T> list = new ArrayList<>();
        for (T t : data) {
            list.add(t);
            List<T> children = getChildren.apply(t);
            if (children != null) {
                list.addAll(unwrap(children, getChildren));
            }
        }
        return list;
    }

    /**
     * 向下遍历节点，并在回调中返回父节点与当前节点
     *
     * @param data 数据
     * @param node arg1：当前节点 arg2：父节点
     * @param <T>
     */
    public static <T> void forEachDown(List<T> data, Function<T, List<T>> getChildren, BiConsumer<T, Optional<T>> node) {
        forEachDown(data, getChildren, null, node);
    }

    private static <T> void forEachDown(List<T> data, Function<T, List<T>> getChildren, T parentNode, BiConsumer<T, Optional<T>> node) {
        for (T t : data) {
            node.accept(t, Optional.ofNullable(parentNode));
            List<T> children = getChildren.apply(t);
            if (children != null && children.size() != 0) {
                forEachDown(children, getChildren, t, node);
            }
        }
    }

}
