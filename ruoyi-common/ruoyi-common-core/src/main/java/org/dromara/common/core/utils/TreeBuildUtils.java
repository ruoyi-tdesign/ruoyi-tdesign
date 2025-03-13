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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * 构建树形结构
     *
     * @param <T>        输入节点的类型
     * @param <K>        节点ID的类型
     * @param list       节点列表，其中包含了要构建树形结构的所有节点
     * @param nodeParser 解析器，用于将输入节点转换为树节点
     * @return 构建好的树形结构列表
     */
    public static <T, K> List<Tree<K>> build(List<T> list, NodeParser<T, K> nodeParser) {
        if (CollUtil.isEmpty(list)) {
            return CollUtil.newArrayList();
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

    /**
     * 获取节点列表中所有节点的叶子节点
     *
     * @param <K>   节点ID的类型
     * @param nodes 节点列表
     * @return 包含所有叶子节点的列表
     */
    public static <K> List<Tree<K>> getLeafNodes(List<Tree<K>> nodes) {
        if (CollUtil.isEmpty(nodes)) {
            return CollUtil.newArrayList();
        }
        return nodes.stream()
            .flatMap(TreeBuildUtils::extractLeafNodes)
            .collect(Collectors.toList());
    }

    /**
     * 获取指定节点下的所有叶子节点
     *
     * @param <K>  节点ID的类型
     * @param node 要查找叶子节点的根节点
     * @return 包含所有叶子节点的列表
     */
    private static <K> Stream<Tree<K>> extractLeafNodes(Tree<K> node) {
        if (!node.hasChild()) {
            return Stream.of(node);
        } else {
            // 递归调用，获取所有子节点的叶子节点
            return node.getChildren().stream()
                .flatMap(TreeBuildUtils::extractLeafNodes);
        }
    }

}
