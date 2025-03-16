package org.dromara.generator.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.generator.domain.GenTable;
import org.dromara.generator.domain.query.GenTableQuery;
import org.dromara.generator.domain.vo.GenTableVo;

import java.util.List;

/**
 * 业务 数据层
 *
 * @author Lion Li
 */
@InterceptorIgnore(dataPermission = "true", tenantLine = "true")
public interface GenTableMapper extends BaseMapperPlus<GenTable, GenTableVo> {

    /**
     * 查询代码生成业务列表
     *
     * @param query 查询对象
     * @return {@link GenTableVo}
     */
    List<GenTableVo> queryList(GenTableQuery query);

    /**
     * 查询所有表信息
     *
     * @return 表信息集合
     */
    List<GenTableVo> selectGenTableAll();

    /**
     * 查询表ID业务信息
     *
     * @param tableId 业务ID
     * @return 业务信息
     */
    GenTableVo selectGenTableById(@Param("tableId") Long tableId);

    /**
     * 查询表名称业务信息
     *
     * @param tableName 表名称
     * @return 业务信息
     */
    GenTableVo selectGenTableByName(@Param("tableName") String tableName);

    /**
     * 查询指定数据源下的所有表名列表
     *
     * @param dataName 数据源名称，用于选择不同的数据源
     * @return 当前数据库中的表名列表
     *
     * @DS("") 使用默认数据源执行查询操作
     */
    @DS("")
    List<String> selectTableNameList(@Param("dataName") String dataName);
}
