package org.dromara.common.core.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dromara.common.core.utils.funtion.BiOperator;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * stream 流工具类
 *
 * @author Lion Li
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamUtils {

    /**
     * 将collection过滤
     *
     * @param collection 需要转化的集合
     * @param function   过滤方法
     * @return 过滤后的list
     */
    public static <E> List<E> filter(Collection<E> collection, Predicate<E> function) {
        if (CollUtil.isEmpty(collection)) {
            return CollUtil.newArrayList();
        }
        // 注意此处不要使用 .toList() 新语法 因为返回的是不可变List 会导致序列化问题
        return collection.stream().filter(function).collect(Collectors.toList());
    }

    /**
     * 找到流中满足条件的第一个元素
     *
     * @param collection 需要查询的集合
     * @param function   过滤方法
     * @return 找到符合条件的第一个元素，没有则返回null
     */
    public static <E> E findFirst(Collection<E> collection, Predicate<E> function) {
        if (CollUtil.isEmpty(collection)) {
            return null;
        }
        return collection.stream().filter(function).findFirst().orElse(null);
    }

    /**
     * 找到流中任意一个满足条件的元素
     *
     * @param collection 需要查询的集合
     * @param function   过滤方法
     * @return 找到符合条件的任意一个元素，没有则返回null
     */
    public static <E> Optional<E> findAny(Collection<E> collection, Predicate<E> function) {
        if (CollUtil.isEmpty(collection)) {
            return Optional.empty();
        }
        return collection.stream().filter(function).findAny();
    }

    /**
     * 将collection拼接
     *
     * @param collection 需要转化的集合
     * @param function   拼接方法
     * @return 拼接后的list
     */
    public static <E> String join(Collection<E> collection, Function<E, String> function) {
        return join(collection, function, StringUtils.SEPARATOR);
    }

    /**
     * 将collection拼接
     *
     * @param collection 需要转化的集合
     * @param function   拼接方法
     * @param delimiter  拼接符
     * @return 拼接后的list
     */
    public static <E> String join(Collection<E> collection, Function<E, String> function, CharSequence delimiter) {
        if (CollUtil.isEmpty(collection)) {
            return StringUtils.EMPTY;
        }
        return collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.joining(delimiter));
    }

    /**
     * 将collection排序
     *
     * @param collection 需要转化的集合
     * @param comparing  排序方法
     * @return 排序后的list
     */
    public static <E> List<E> sorted(Collection<E> collection, Comparator<E> comparing) {
        if (CollUtil.isEmpty(collection)) {
            return CollUtil.newArrayList();
        }
        // 注意此处不要使用 .toList() 新语法 因为返回的是不可变List 会导致序列化问题
        return collection.stream().filter(Objects::nonNull).sorted(comparing).collect(Collectors.toList());
    }

    /**
     * 将collection转化为类型不变的map<br>
     * <B>{@code Collection<V>  ---->  Map<K,V>}</B>
     *
     * @param collection 需要转化的集合
     * @param key        V类型转化为K类型的lambda方法
     * @param <V>        collection中的泛型
     * @param <K>        map中的key类型
     * @return 转化后的map
     */
    public static <V, K> Map<K, V> toIdentityMap(Collection<V> collection, Function<V, K> key) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection.stream().filter(Objects::nonNull).collect(Collectors.toMap(key, Function.identity(), BiOperator::first, LinkedHashMap::new));
    }

    /**
     * 将Collection转化为map(value类型与collection的泛型不同)<br>
     * <B>{@code Collection<E> -----> Map<K,V>  }</B>
     *
     * @param collection 需要转化的集合
     * @param key        E类型转化为K类型的lambda方法
     * @param value      E类型转化为V类型的lambda方法
     * @param <E>        collection中的泛型
     * @param <K>        map中的key类型
     * @param <V>        map中的value类型
     * @return 转化后的map
     */
    public static <E, K, V> Map<K, V> toMap(Collection<E> collection, Function<E, K> key, Function<E, V> value) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection.stream().filter(Objects::nonNull).collect(Collectors.toMap(key, value, BiOperator::first, LinkedHashMap::new));
    }

    /**
     * 将collection按照规则(比如有相同的班级id)分类成map<br>
     * <B>{@code Collection<E> -------> Map<K,List<E>> } </B>
     *
     * @param collection 需要分类的集合
     * @param key        分类的规则
     * @param <E>        collection中的泛型
     * @param <K>        map中的key类型
     * @return 分类后的map
     */
    public static <E, K> Map<K, List<E>> groupByKey(Collection<E> collection, Function<E, K> key) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection
            .stream().filter(Objects::nonNull)
            .collect(Collectors.groupingBy(key, LinkedHashMap::new, Collectors.toList()));
    }

    /**
     * 将collection按照两个规则(比如有相同的年级id,班级id)分类成双层map<br>
     * <B>{@code Collection<E>  --->  Map<T,Map<U,List<E>>> } </B>
     *
     * @param collection 需要分类的集合
     * @param key1       第一个分类的规则
     * @param key2       第二个分类的规则
     * @param <E>        集合元素类型
     * @param <K>        第一个map中的key类型
     * @param <U>        第二个map中的key类型
     * @return 分类后的map
     */
    public static <E, K, U> Map<K, Map<U, List<E>>> groupBy2Key(Collection<E> collection, Function<E, K> key1, Function<E, U> key2) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap();
        }
        return collection
            .stream().filter(Objects::nonNull)
            .collect(Collectors.groupingBy(key1, LinkedHashMap::new, Collectors.groupingBy(key2, LinkedHashMap::new, Collectors.toList())));
    }

    /**
     * 将collection按照两个规则(比如有相同的年级id,班级id)分类成双层map<br>
     * <B>{@code Collection<E>  --->  Map<T,Map<U,E>> } </B>
     *
     * @param collection 需要分类的集合
     * @param key1       第一个分类的规则
     * @param key2       第二个分类的规则
     * @param <T>        第一个map中的key类型
     * @param <U>        第二个map中的key类型
     * @param <E>        collection中的泛型
     * @return 分类后的map
     */
    public static <E, T, U> Map<T, Map<U, E>> group2Map(Collection<E> collection, Function<E, T> key1, Function<E, U> key2) {
        if (CollUtil.isEmpty(collection) || key1 == null || key2 == null) {
            return MapUtil.newHashMap();
        }
        return collection
            .stream().filter(Objects::nonNull)
            .collect(Collectors.groupingBy(
                key1,
                LinkedHashMap::new,
                Collectors.toMap(key2, Function.identity(), BiOperator::first, LinkedHashMap::new))
            );
    }

    /**
     * 将collection按照两个规则(比如有相同的年级id,班级id)分类成双层map<br>
     * <B>{@code Collection<E>  --->  Map<T,Map<U,List<E>>> } </B>
     *
     * @param collection 需要分类的集合
     * @param groupKey   分组
     * @param key        Map中的key
     * @param <T>        第一个map中的key类型
     * @param <U>        第二个map中的key类型
     * @param <E>        collection中的泛型
     * @param <V>        Map中的value
     * @return 分类后的map
     */
    public static <E, T, U, V> Map<T, Map<U, List<V>>> group2MapList(Collection<E> collection, Function<E, T> groupKey, Function<E, U> key, Function<E, V> value) {
        if (CollUtil.isEmpty(collection) || groupKey == null || key == null || value == null) {
            return MapUtil.newHashMap();
        }
        return collection
            .stream().filter(Objects::nonNull)
            .collect(Collectors.groupingBy(
                    groupKey,
                    LinkedHashMap::new,
                    Collectors.groupingBy(key, Collectors.mapping(value, Collectors.toList()))
                )
            );
    }

    /**
     * 将collection按照两个规则(比如有相同的年级id,班级id)分类成双层map<br>
     * <B>{@code Collection<E>  --->  Map<T,Map<U,Set<E>>> } </B>
     *
     * @param collection 需要分类的集合
     * @param groupKey   分组
     * @param key        Map中的key
     * @param <T>        第一个map中的key类型
     * @param <U>        第二个map中的key类型
     * @param <E>        collection中的泛型
     * @param <V>        Map中的value
     * @return 分类后的map
     */
    public static <E, T, U, V> Map<T, Map<U, Set<V>>> group2MapSet(Collection<E> collection, Function<E, T> groupKey, Function<E, U> key, Function<E, V> value) {
        if (CollUtil.isEmpty(collection) || groupKey == null || key == null || value == null) {
            return MapUtil.newHashMap();
        }
        return collection
            .stream().filter(Objects::nonNull)
            .collect(Collectors.groupingBy(
                    groupKey,
                    LinkedHashMap::new,
                    Collectors.groupingBy(key, Collectors.mapping(value, Collectors.toCollection(LinkedHashSet::new)))
                )
            );
    }

    /**
     * 将collection转化为List集合，但是两者的泛型不同<br>
     * <B>{@code Collection<E>  ------>  List<T> } </B>
     *
     * @param collection 需要转化的集合
     * @param function   collection中的泛型转化为list泛型的lambda表达式
     * @param <E>        collection中的泛型
     * @param <T>        List中的泛型
     * @return 转化后的list
     */
    public static <E, T> List<T> toList(Collection<E> collection, Function<E, T> function) {
        if (CollUtil.isEmpty(collection)) {
            return CollUtil.newArrayList();
        }
        // 注意此处不要使用 .toList() 新语法 因为返回的是不可变List 会导致序列化问题
        return collection
            .stream()
            .map(function)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * 将collection转化为扁平的List集合，但是两者的泛型不同<br>
     * <B>{@code Collection<E>  ------>  List<T> } </B>
     *
     * @param collection 需要转化的集合
     * @param function   collection中的泛型转化为list泛型的lambda表达式
     * @param <E>        collection中的泛型
     * @param <T>        List中的泛型
     * @return 转化后的list
     */
    public static <E, T> List<T> toFlatList(Collection<E> collection, Function<E, Collection<T>> function) {
        if (CollUtil.isEmpty(collection)) {
            return CollUtil.newArrayList();
        }
        // 注意此处不要使用 .toList() 新语法 因为返回的是不可变List 会导致序列化问题
        return collection
            .stream()
            .filter(Objects::nonNull)
            .flatMap(e -> function.apply(e).stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * 将collection转化为Set集合，但是两者的泛型不同<br>
     * <B>{@code Collection<E>  ------>  Set<T> } </B>
     *
     * @param collection 需要转化的集合
     * @param function   collection中的泛型转化为set泛型的lambda表达式
     * @param <E>        collection中的泛型
     * @param <T>        Set中的泛型
     * @return 转化后的Set
     */
    public static <E, T> Set<T> toSet(Collection<E> collection, Function<E, T> function) {
        if (CollUtil.isEmpty(collection) || function == null) {
            return CollUtil.newHashSet();
        }
        return collection
            .stream()
            .map(function)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * 将collection转化为扁平的Set集合，但是两者的泛型不同<br>
     * <B>{@code Collection<E>  ------>  Set<T> } </B>
     *
     * @param collection 需要转化的集合
     * @param function   collection中的泛型转化为set泛型的lambda表达式
     * @param <E>        collection中的泛型
     * @param <T>        Set中的泛型
     * @return 转化后的Set
     */
    public static <E, T> Set<T> toFlatSet(Collection<E> collection, Function<E, Collection<T>> function) {
        if (CollUtil.isEmpty(collection) || function == null) {
            return CollUtil.newHashSet();
        }
        return collection
            .stream()
            .flatMap(e -> function.apply(e).stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }


    /**
     * 合并两个相同key类型的map
     *
     * @param map1  第一个需要合并的 map
     * @param map2  第二个需要合并的 map
     * @param merge 合并的lambda，将key  value1 value2合并成最终的类型,注意value可能为空的情况
     * @param <K>   map中的key类型
     * @param <X>   第一个 map的value类型
     * @param <Y>   第二个 map的value类型
     * @param <V>   最终map的value类型
     * @return 合并后的map
     */
    public static <K, X, Y, V> Map<K, V> merge(Map<K, X> map1, Map<K, Y> map2, BiFunction<X, Y, V> merge) {
        if (MapUtil.isEmpty(map1) && MapUtil.isEmpty(map2)) {
            return MapUtil.newHashMap();
        } else if (MapUtil.isEmpty(map1)) {
            map1 = MapUtil.newHashMap();
        } else if (MapUtil.isEmpty(map2)) {
            map2 = MapUtil.newHashMap();
        }
        Set<K> key = new HashSet<>();
        key.addAll(map1.keySet());
        key.addAll(map2.keySet());
        Map<K, V> map = new HashMap<>();
        for (K t : key) {
            X x = map1.get(t);
            Y y = map2.get(t);
            V z = merge.apply(x, y);
            if (z != null) {
                map.put(t, z);
            }
        }
        return map;
    }

    /**
     * 过滤不存在的值
     *
     * @param data          需要过滤的数据
     * @param compareValues 比较的数据
     * @return
     */
    public static <T> List<T> filterNotExist(Collection<T> data, Collection<T> compareValues) {
        return filterNotExist(data, compareValues, Function.identity());
    }

    /**
     * 过滤不存在的值
     *
     * @param data          需要过滤的数据
     * @param compareValues 比较的数据
     * @param convert       转换
     * @param <T>           过滤数据的类型
     * @param <K>           输入数据的类型
     * @return
     */
    public static <T, K> List<T> filterNotExist(Collection<T> data, Collection<K> compareValues, Function<K, T> convert) {
        return filterNotExist(data, Function.identity(), compareValues, convert);
    }

    /**
     * 过滤不存在的值
     *
     * @param data          需要过滤的数据
     * @param compareValues 比较的数据
     * @param convert       转换
     * @param <T>           过滤数据的类型
     * @param <K>           比较数据的类型
     * @return
     */
    public static <T, K> List<T> filterNotExist(Collection<T> data, Function<T, K> convert, Collection<K> compareValues) {
        return filterNotExist(data, convert, compareValues, Function.identity());
    }

    /**
     * 过滤不存在的值
     *
     * @param data                 需要过滤的数据
     * @param compareValues        比较的数据
     * @param dataConvert          需要过滤的数据的转换
     * @param compareValuesConvert 比较数据的转换
     * @param <T>                  过滤数据的类型
     * @param <K>                  比较数据的类型
     * @param <R>                  输出结果的类型
     * @return
     */
    public static <T, K, R> List<T> filterNotExist(Collection<T> data, Function<T, R> dataConvert, Collection<K> compareValues, Function<K, R> compareValuesConvert) {
        Set<R> set = compareValues.stream()
            .map(compareValuesConvert)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        return data.stream().filter(t -> !set.contains(dataConvert.apply(t))).collect(Collectors.toList());
    }

    /**
     * 过滤存在的值
     *
     * @param data          需要过滤的数据
     * @param compareValues 比较的数据
     * @return
     */
    public static <T> List<T> filterExist(Collection<T> data, Collection<T> compareValues) {
        return filterExist(data, compareValues, Function.identity());
    }

    /**
     * 过滤存在的值
     *
     * @param data          需要过滤的数据
     * @param compareValues 比较的数据
     * @param convert       转换
     * @param <T>           过滤数据的类型
     * @param <K>           输入数据的类型
     * @return
     */
    public static <T, K> List<T> filterExist(Collection<T> data, Collection<K> compareValues, Function<K, T> convert) {
        return filterExist(data, Function.identity(), compareValues, convert);
    }

    /**
     * 过滤存在的值
     *
     * @param data          需要过滤的数据
     * @param compareValues 比较的数据
     * @param convert       转换
     * @param <T>           过滤数据的类型
     * @param <K>           比较数据的类型
     * @return
     */
    public static <T, K> List<T> filterExist(Collection<T> data, Function<T, K> convert, Collection<K> compareValues) {
        return filterExist(data, convert, compareValues, Function.identity());
    }

    /**
     * 过滤存在的值
     *
     * @param data                 需要过滤的数据
     * @param compareValues        比较的数据
     * @param dataConvert          需要过滤的数据的转换
     * @param compareValuesConvert 比较数据的转换
     * @param <T>                  过滤数据的类型
     * @param <K>                  比较数据的类型
     * @param <R>                  输出结果的类型
     * @return
     */
    public static <T, K, R> List<T> filterExist(Collection<T> data, Function<T, R> dataConvert, Collection<K> compareValues, Function<K, R> compareValuesConvert) {
        Set<R> set = compareValues.stream()
            .map(compareValuesConvert)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        return data.stream().filter(t -> set.contains(dataConvert.apply(t))).collect(Collectors.toList());
    }

    /**
     * 字符串拆分
     *
     * @param str     字符串
     * @param convert 转换流
     * @param <T>
     * @return
     */
    public static <T> List<T> split(String str, Function<Stream<String>, Stream<T>> convert) {
        if (str != null) {
            Stream<String> stream = Arrays.stream(str.split(","));
            return convert.apply(stream).collect(Collectors.toList());
        }
        return new ArrayList<>(0);
    }

    /**
     * 合并多个集合
     *
     * @param collections 集合
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> List<T> merge(Collection<T>... collections) {
        return Arrays.stream(collections).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * 合并多个集合
     *
     * @param collections 集合
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> Set<T> mergeToSet(Collection<T>... collections) {
        return Arrays.stream(collections).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * 比较两个集合之间的差异
     *
     * @param data1    数据源1
     * @param data2    数据源2
     * @param consumer 形参1：只有数据源1有的数据 形参2：只有数据源2有的数据
     * @param <T>
     */
    public static <T> void diff(Collection<T> data1, Collection<T> data2, BiConsumer<List<T>, List<T>> consumer) {
        consumer.accept(filterNotExist(data1, data2), filterNotExist(data2, data1));
    }

    /**
     * 从方法中获取key的数据， 如果没有这个数据，则汇总后，从回调中获取
     *
     * @param keys       关键字，一般是一个id
     * @param take       获取方法
     * @param notPresent 没有值时的获取方法
     * @param <K>
     * @param <T>
     * @return
     */
    public static <K, T> List<T> take(Collection<K> keys, Function<K, T> take, Function<Set<K>, Map<K, T>> notPresent) {
        return take(keys, take, null, notPresent);
    }

    /**
     * 从方法中获取key的数据， 如果没有这个数据，则汇总后，从回调中获取
     *
     * @param keys       关键字，一般是一个id
     * @param take       获取方法
     * @param set        设置方法
     * @param notPresent 没有值时的获取方法
     * @param <K>
     * @param <T>
     * @return
     */
    public static <K, T> List<T> take(Collection<K> keys, Function<K, T> take, BiConsumer<K, T> set, Function<Set<K>, Map<K, T>> notPresent) {
        Map<K, T> temp = new LinkedHashMap<>();
        Set<K> kSet = keys.stream().distinct().filter(k -> {
            T t = take.apply(k);
            if (t != null) {
                temp.put(k, t);
                return false;
            }
            return true;
        }).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!kSet.isEmpty()) {
            Map<K, T> ts = notPresent.apply(kSet);
            if (ts != null && !ts.isEmpty()) {
                temp.putAll(ts);
            }
        }
        // 按照顺序，重新获取一遍
        return keys.stream()
            .map(k -> {
                T t = temp.get(k);
                if (t != null && set != null) {
                    set.accept(k, t);
                }
                return t;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * 获取第一个符合条件的数据
     *
     * @param collection 集合
     * @param filter     过滤条件
     * @return
     */
    public static <T> Optional<T> findFirstOptional(Collection<T> collection, Function<T, Boolean> filter) {
        return collection.stream().filter(filter::apply).findFirst();
    }

    /**
     * 合并集合内相同元素
     *
     * @param collection      集合
     * @param compareFunction 比较是否相同
     * @param accumulator     合并操作
     */
    public static <T> List<T> reduce(Collection<T> collection, BiFunction<T, T, Boolean> compareFunction, BinaryOperator<T> accumulator) {
        if (collection == null) {
            return null;
        }
        List<T> copy = new ArrayList<>(collection);
        List<Integer> indexList = new ArrayList<>(collection.size());
        for (int i = 0; i < copy.size(); i++) {
            int start = i;
            for (int j = i + 1; j < copy.size(); j++) {
                if (compareFunction.apply(copy.get(i), copy.get(j))) {
                    if (j - 1 > 1) {
                        // 此处不使用下标交换是为了保持原元素的顺序
                        copy.add(i + 1, copy.remove(j));
                    }
                    i++;
                }
            }
            indexList.add(i + 1 - start);
        }
        List<T> result = new ArrayList<>();
        for (int i = 0; i < copy.size(); i++) {
            for (Integer integer : indexList) {
                Optional<T> optional = copy.subList(i, i + integer).stream().reduce(accumulator);
                optional.ifPresent(result::add);
                i += integer;
            }
        }
        return result;
    }
}
