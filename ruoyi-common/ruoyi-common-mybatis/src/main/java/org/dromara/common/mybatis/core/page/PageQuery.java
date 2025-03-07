package org.dromara.common.mybatis.core.page;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.common.core.utils.ServletUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.function.Supplier;

/**
 * 分页查询实体类
 *
 * @author YiXiacoco、Lion Li
 */
@Data
@Accessors(chain = true)
public class PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 当前页数
     */
    private Integer pageNum;

    /**
     * 查询时是否统计总数
     */
    private boolean isSearchCount = true;

    /**
     * 排序对象
     */
    private SortQuery sortQuery;

    /**
     * 当前记录起始索引 默认值
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 每页显示记录数 默认值 10
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 使用请求参数设置分页页码
     *
     * @return
     */
    public PageQuery defaultPageNum() {
        pageNum = getRequestPageNum();
        return this;
    }

    /**
     * 使用请求参数设置分页数
     *
     * @return
     */
    public PageQuery defaultPageSize() {
        pageSize = getRequestPageSize();
        return this;
    }

    /**
     * 使用请求参数设置排序
     *
     * @return
     */
    public PageQuery defaultOrder() {
        sortQuery = SortQuery.of();
        return this;
    }

    /**
     * 查询时默认统计
     *
     * @return
     */
    public PageQuery defaultSearchCount() {
        isSearchCount = true;
        return this;
    }

    /**
     * 使用分页创建对象
     *
     * @return
     */
    public static PageQuery of() {
        PageQuery pageQuery = new PageQuery();
        return pageQuery
            .defaultPageNum()
            .defaultPageSize()
            .defaultSearchCount()
            .defaultOrder();
    }

    /**
     * 使用分页创建对象
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return
     */
    public static PageQuery of(Integer pageNum, Integer pageSize) {
        return of(pageNum, pageSize, true);
    }

    /**
     * 使用分页创建对象
     *
     * @param pageNum       页码
     * @param pageSize      每页数量
     * @param isSearchCount 是否查询统计
     * @return
     */
    public static PageQuery of(Integer pageNum, Integer pageSize, boolean isSearchCount) {
        PageQuery pageQuery = new PageQuery();
        return pageQuery
            .setPageNum(pageNum)
            .setPageSize(pageSize)
            .setSearchCount(isSearchCount)
            .defaultOrder();
    }

    /**
     * 使用分页创建对象
     *
     * @param pageNum       页码
     * @param pageSize      每页数量
     * @param isSearchCount 是否查询统计
     * @param sortQuery     排序
     * @return
     */
    public static PageQuery of(Integer pageNum, Integer pageSize, boolean isSearchCount, SortQuery sortQuery) {
        PageQuery pageQuery = new PageQuery();
        return pageQuery
            .setPageNum(pageNum)
            .setPageSize(pageSize)
            .setSearchCount(isSearchCount)
            .setSortQuery(sortQuery);
    }

    /**
     * 构建分页对象
     */
    public <T> Page<T> build() {
        Integer pageNum = ObjectUtil.defaultIfNull(getPageNum(), DEFAULT_PAGE_NUM);
        Integer pageSize = ObjectUtil.defaultIfNull(getPageSize(), DEFAULT_PAGE_SIZE);
        if (pageNum <= 0) {
            pageNum = DEFAULT_PAGE_NUM;
        }
        Page<T> page = new Page<>(pageNum, pageSize, isSearchCount);
        if (sortQuery != null) {
            List<OrderItem> orderItems = sortQuery.getOrderItems();
            if (CollUtil.isNotEmpty(orderItems)) {
                page.addOrder(orderItems);
            }
        }
        return page;
    }

    /**
     * 执行方法分页查询
     *
     * @param supplier 查询方法
     * @param <T>
     * @return TableDataInfo
     */
    public <T> T get(Supplier<T> supplier) {
        Page<T> page = build();
        if (CollUtil.isNotEmpty(page.orders())) {
            List<OrderItem> orderItems = page.orders();
            String orderBy = "";
            if (orderItems != null) {
                orderBy = SortQuery.getOrderBy(orderItems);
            }
            try (Closeable ignored = PageHelper.startPage(pageNum, pageSize, page.searchCount()).setOrderBy(orderBy)) {
                return supplier.get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (Closeable ignored = PageHelper.startPage(pageNum, pageSize, page.searchCount())) {
                return supplier.get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 执行方法分页查询
     *
     * @param supplier 查询方法
     * @param <T>
     * @return TableDataInfo
     */
    public <T> TableDataInfo<T> execute(Supplier<List<T>> supplier) {
        return wrap(get(supplier));
    }

    private <T> TableDataInfo<T> wrap(List<T> list) {
        PageInfo<T> pageInfo = new PageInfo<>(list);
        TableDataInfo<T> rspData = new TableDataInfo<>();
        rspData.setCode(HttpStatus.HTTP_OK);
        rspData.setMsg("查询成功");
        rspData.setRows(pageInfo.getList());
        rspData.setTotal(pageInfo.getTotal());
        rspData.setPageNum(pageInfo.getPageNum());
        rspData.setPageSize((long) pageInfo.getPageSize());
        return rspData;
    }

    /**
     * 获取请求中的分页页码
     *
     * @return
     */
    public static Integer getRequestPageNum() {
        return ServletUtils.getParameterToInt(PAGE_NUM, DEFAULT_PAGE_NUM);
    }

    /**
     * 获取请求中的分页数
     *
     * @return
     */
    public static Integer getRequestPageSize() {
        return ServletUtils.getParameterToInt(PAGE_SIZE, DEFAULT_PAGE_SIZE);
    }

    /**
     * mybatis分页插件的使用
     *
     * @param supplier 查询方法
     * @param <T>
     * @return TableDataInfo
     */
    public static <T> TableDataInfo<T> of(Supplier<List<T>> supplier) {
        return of().execute(supplier);
    }

    public Integer getFirstNum() {
        return (pageNum - 1) * pageSize;
    }

}
