package org.dromara.system.configuration;

import io.github.linpeilie.annotations.MapperConfig;

/**
 * 解决MapStructPlus在对象属性是List复制时发生转换报错
 * example:
 * <pre>{@code
 * public class A {
 *     private List<A> children = new ArrayList<>();
 * }
 *
 * @AutoMapper(target = A.class)
 * public class B {
 *     // 这里会从A自动转为B类型
 *     private List<B> children = new ArrayList<>();
 * }
 * }</pre>
 */
@MapperConfig(adapterClassName = "MessageConvertMapperAdapter",
    adapterPackage = "io.github.linpeilie.adapter",
    mapAdapterClassName = "MessageMapConvertMapperAdapter")
public class MessageModuleMapStructPlusConfiguration {
}
