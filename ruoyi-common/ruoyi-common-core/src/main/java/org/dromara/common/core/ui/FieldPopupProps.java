package org.dromara.common.core.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.function.Function;

/**
 * 弹出层组件配置
 *
 * @author hexm
 * @date 2025/5/7
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldPopupProps {
    /** 制定挂载节点。数据类型为 String 时，会被当作选择器处理，进行节点查询 */
    private String attach;
    /** 浮层里面的内容 */
    private String content;
    /**
     * 延时显示或隐藏浮层，[延迟显示的时间，延迟隐藏的时间]，单位：毫秒。
     * 如果只有一个时间，则表示显示和隐藏的延迟时间相同。示例 '300' 或者 [200, 200]。默认为：[250, 150]。
     * TS 类型：number | Array<number>
     */
    private Object delay;
    /** 是否在关闭浮层时销毁浮层 */
    private Boolean destroyOnClose;
    /** 是否禁用组件 */
    private Boolean disabled;
    /** 浮层是否隐藏空内容，默认不隐藏 */
    private Boolean hideEmptyPopup;
    /**
     * 浮层类名，示例：'name1 name2 name3' 或 ['name1', 'name2'] 或 [{ 'name1': true }]。
     */
    private Object overlayClassName;
    /**
     * 浮层内容部分类名，示例：'name1 name2 name3' 或 ['name1', 'name2'] 或 [{ 'name1': true }]。
     */
    private Object overlayInnerClassName;
    /** 浮层内容部分样式 */
    private Object overlayInnerStyle;
    /** 浮层样式 */
    private Object overlayStyle;
    /**
     * 浮层出现位置。
     * 'top'|'left'|'right'|'bottom'|'top-left'|'top-right'|'bottom-left'|'bottom-right'|'left-top'|'left-bottom'|'right-top'|'right-bottom'
     */
    private String placement;
    /**
     * popper 初始化配置，<a href="https://popper.js.org/docs/">详情参考</a>
     */
    private Object popperOptions;
    /** 是否显示浮层箭头 */
    private Boolean showArrow;
    /** 触发浮层出现的方式。可选项：hover/click/focus/mousedown/context-menu */
    private String trigger;
    /** 触发元素。值类型为字符串表示元素选择器。 */
    private String triggerElement;
    /** 组件层级，Web 侧样式默认为 5500，移动端和小程序样式默认为 1500 */
    private Integer zIndex;

    public static class FieldPopupPropsBuilder<T> {
        private Function<FieldPopupProps, T> end;

        public FieldPopupPropsBuilder() {
        }

        public FieldPopupPropsBuilder(Function<FieldPopupProps, T> end) {
            this.end = end;
        }

        public T end() {
            return end.apply(this.build());
        }
    }
}
