package org.dromara.generator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.generator.domain.GenTable;
import org.dromara.generator.domain.GenTableColumn;
import org.dromara.generator.domain.bo.GenTableBo;
import org.dromara.generator.domain.bo.GenUpdateTableNameBo;
import org.dromara.generator.domain.query.GenTableQuery;
import org.dromara.generator.domain.vo.GenTableVo;

import java.util.List;
import java.util.Map;

/**
 * 业务 服务层
 *
 * @author Lion Li
 */
public interface IGenTableService extends IService<GenTable> {

    /**
     * 查询业务字段列表
     *
     * @param tableId 业务字段编号
     * @return 业务字段集合
     */
    List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId);

    /**
     * 查询业务列表
     *
     * @param query 查询对象
     * @return 业务集合
     */
    TableDataInfo<GenTableVo> selectPageGenTableList(GenTableQuery query);

    /**
     * 查询据库列表
     *
     * @param query 查询对象
     * @return 数据库表集合
     */
    List<GenTableVo> selectPageDbTableList(GenTableQuery query);

    /**
     * 查询据库列表
     *
     * @param tableNames 表名称组
     * @param dataName   数据源名称
     * @return 数据库表集合
     */
    List<GenTableVo> selectDbTableListByNames(String[] tableNames, String dataName);

    /**
     * 查询所有表信息
     *
     * @return 表信息集合
     */
    List<GenTableVo> selectGenTableAll();

    /**
     * 查询业务信息
     *
     * @param id 业务ID
     * @return 业务信息
     */
    GenTableVo selectGenTableById(Long id);

    /**
     * 修改业务
     *
     * @param tableBo 业务信息
     */
    void updateGenTable(GenTableBo tableBo);

    /**
     * 修改表名
     */
    void updateTableName(GenUpdateTableNameBo updateTableNameBo);

    /**
     * 删除业务信息
     *
     * @param tableIds 需要删除的表数据ID
     */
    void deleteGenTableByIds(Long[] tableIds);

    /**
     * 导入表结构
     *
     * @param tableList 导入表列表
     * @param dataName  数据源名称
     */
    void importGenTable(List<GenTableVo> tableList, String dataName);

    /**
     * 根据表名称查询列信息
     *
     * @param tableName 表名称
     * @param dataName  数据源名称
     * @return 列信息
     */
    List<GenTableColumn> selectDbTableColumnsByName(String tableName, String dataName);

    /**
     * 预览代码
     *
     * @param tableId 表编号
     * @return 预览数据列表
     */
    Map<String, String> previewCode(Long tableId);

    /**
     * 临时预览代码
     *
     * @param tableBo 业务信息
     */
    Map<String, String> tempPreviewCode(GenTableBo tableBo);

    /**
     * 生成代码（下载方式）
     *
     * @param tableId 表名称
     * @return 数据
     */
    byte[] downloadCode(Long tableId);

    /**
     * 生成代码（自定义路径）
     *
     * @param tableId 表名称
     */
    void generatorCode(Long tableId);

    /**
     * 同步数据库
     *
     * @param tableId 表名称
     */
    void synchDb(Long tableId);

    /**
     * 批量生成代码（下载方式）
     *
     * @param tableIds 表ID数组
     * @return 数据
     */
    byte[] downloadCode(String[] tableIds);

    /**
     * 修改保存参数校验
     *
     * @param tableBo 业务信息
     */
    void validateEdit(GenTableBo tableBo);
}
