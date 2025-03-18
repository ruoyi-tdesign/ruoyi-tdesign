package org.dromara.generator.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.Column;
import org.anyline.metadata.Table;
import org.anyline.proxy.CacheProxy;
import org.anyline.proxy.ServiceProxy;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.file.FileUtils;
import org.dromara.common.core.utils.funtion.BiOperator;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.generator.constant.GenConstants;
import org.dromara.generator.domain.GenTable;
import org.dromara.generator.domain.GenTableColumn;
import org.dromara.generator.domain.bo.GenTableBo;
import org.dromara.generator.domain.bo.GenUpdateTableNameBo;
import org.dromara.generator.domain.query.GenTableQuery;
import org.dromara.generator.domain.vo.GenTableOptions;
import org.dromara.generator.domain.vo.GenTableVo;
import org.dromara.generator.mapper.GenTableColumnMapper;
import org.dromara.generator.mapper.GenTableMapper;
import org.dromara.generator.service.IGenTableService;
import org.dromara.generator.util.GenUtils;
import org.dromara.generator.util.VelocityInitializer;
import org.dromara.generator.util.VelocityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 业务 服务层实现
 *
 * @author Lion Li
 */
@Slf4j
@Service
public class GenTableServiceImpl extends ServiceImpl<GenTableMapper, GenTable> implements IGenTableService {

    @Autowired
    private GenTableColumnMapper genTableColumnMapper;
    @Autowired
    private IdentifierGenerator identifierGenerator;

    private static final String[] TABLE_IGNORE = new String[]{"sj_", "act_", "flw_", "gen_"};

    /**
     * 查询业务字段列表
     *
     * @param tableId 业务字段编号
     * @return 业务字段集合
     */
    @Override
    public List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId) {
        return genTableColumnMapper.selectList(new LambdaQueryWrapper<GenTableColumn>()
            .eq(GenTableColumn::getTableId, tableId)
            .orderByAsc(GenTableColumn::getSort));
    }

    /**
     * 查询业务信息
     *
     * @param id 业务ID
     * @return 业务信息
     */
    @Override
    public GenTableVo selectGenTableById(Long id) {
        GenTableVo genTable = baseMapper.selectGenTableById(id);
        setTableFromOptions(genTable);
        return genTable;
    }

    /**
     * 查询代码生成业务列表
     *
     * @param query 查询对象
     * @return GenTableVo
     */
    @Override
    public TableDataInfo<GenTableVo> selectPageGenTableList(GenTableQuery query) {
        query.setTableName(StringUtils.lowerCase(query.getTableName()));
        query.setTableComment(StringUtils.lowerCase(query.getTableComment()));
        return PageQuery.of(() -> baseMapper.queryList(query));
    }

    /**
     * 查询据库列表
     *
     * @param query 业务信息
     * @return 数据库表集合
     */
    @DS("#query.dataName")
    @Override
    public List<GenTableVo> selectPageDbTableList(GenTableQuery query) {
        // 清理数据库元数据缓存
        CacheProxy.clear();
        // 获取查询条件
        String tableName = query.getTableName();
        String tableComment = query.getTableComment();
        LinkedHashMap<String, Table<?>> tablesMap = ServiceProxy.metadata().tables();
        if (CollUtil.isEmpty(tablesMap)) {
            return new ArrayList<>(0);
        }
        List<String> tableNames = baseMapper.selectTableNameList(query.getDataName());
        String[] tableArrays;
        if (CollUtil.isNotEmpty(tableNames)) {
            tableArrays = tableNames.toArray(new String[0]);
        } else {
            tableArrays = new String[0];
        }
        // 过滤并转换表格数据
        return tablesMap.values().stream()
            .filter(x -> !startWithAnyIgnoreCase(x.getName(), TABLE_IGNORE))
            .filter(x -> {
                if (CollUtil.isEmpty(tableNames)) {
                    return true;
                }
                return !StringUtils.equalsAnyIgnoreCase(x.getName(), tableArrays);
            })
            .filter(x -> {
                boolean nameMatches = true;
                boolean commentMatches = true;
                // 进行表名称的模糊查询
                if (StringUtils.isNotBlank(tableName)) {
                    nameMatches = StringUtils.containsIgnoreCase(x.getName(), tableName);
                }
                // 进行表描述的模糊查询
                if (StringUtils.isNotBlank(tableComment)) {
                    commentMatches = StringUtils.containsIgnoreCase(x.getComment(), tableComment);
                }
                // 同时匹配名称和描述
                return nameMatches && commentMatches;
            })
            .map(x -> {
                GenTableVo gen = new GenTableVo();
                gen.setTableName(x.getName());
                gen.setTableComment(x.getComment());
                gen.setCreateTime(x.getCreateTime());
                gen.setUpdateTime(x.getUpdateTime());
                return gen;
            }).toList();
    }

    public static boolean startWithAnyIgnoreCase(CharSequence cs, CharSequence... searchCharSequences) {
        // 判断是否是以指定字符串开头
        for (CharSequence searchCharSequence : searchCharSequences) {
            if (StringUtils.startsWithIgnoreCase(cs, searchCharSequence)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询据库列表
     *
     * @param tableNames 表名称组
     * @param dataName   数据源名称
     * @return 数据库表集合
     */
    @Override
    public List<GenTableVo> selectDbTableListByNames(String[] tableNames, String dataName) {
        DynamicDataSourceContextHolder.push(dataName);
        try {
            // 清理数据库元数据缓存
            CacheProxy.clear();
            Set<String> tableNameSet = new HashSet<>(List.of(tableNames));
            LinkedHashMap<String, Table<?>> tablesMap = ServiceProxy.metadata().tables();

            if (CollUtil.isEmpty(tablesMap)) {
                return new ArrayList<>();
            }

            List<Table<?>> tableList = tablesMap.values().stream()
                .filter(x -> !StringUtils.containsAnyIgnoreCase(x.getName(), TABLE_IGNORE))
                .filter(x -> tableNameSet.contains(x.getName())).toList();

            if (CollUtil.isEmpty(tableList)) {
                return new ArrayList<>();
            }
            return tableList.stream().map(x -> {
                GenTableVo gen = new GenTableVo();
                gen.setDataName(dataName);
                gen.setTableName(x.getName());
                gen.setTableComment(x.getComment());
                gen.setCreateTime(x.getCreateTime());
                gen.setUpdateTime(x.getUpdateTime());
                return gen;
            }).toList();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 查询所有表信息
     *
     * @return 表信息集合
     */
    @Override
    public List<GenTableVo> selectGenTableAll() {
        return baseMapper.selectGenTableAll();
    }

    /**
     * 修改业务
     *
     * @param tableBo 业务信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateGenTable(GenTableBo tableBo) {
        String options = JsonUtils.toJsonString(tableBo.getTableOptions());
        tableBo.setOptions(options);
        GenTable table = MapstructUtils.convert(tableBo, GenTable.class);
        int row = baseMapper.updateById(table);
        if (row > 0) {
            genTableColumnMapper.updateBatchById(tableBo.getColumns());
        }
    }

    /**
     * 修改表名
     */
    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void updateTableName(GenUpdateTableNameBo bo) {
        List<GenTableVo> tableVos = selectDbTableListByNames(new String[]{bo.getTableName()}, bo.getDataName());
        if (tableVos.isEmpty()) {
            throw new ServiceException("数据源【%s】不存在表【%s】".formatted(bo.getDataName(), bo.getTableName()));
        }
        GenTableVo tableVo = tableVos.get(0);
        GenTable table = MapstructUtils.convert(tableVo, GenTable.class);
        GenUtils.initTable(table, LoginHelper.getUserId());

        GenTable updateTable = getById(bo.getTableId());
        updateTable.setDataName(bo.getDataName());
        updateTable.setClassName(table.getClassName());
        updateTable.setBusinessName(table.getBusinessName());
        updateTable.setFunctionName(table.getFunctionName());
        updateTable.setTableName(bo.getTableName());
        updateTable.setTableComment(table.getTableComment());
        baseMapper.updateById(updateTable);
    }

    /**
     * 删除业务对象
     *
     * @param tableIds 需要删除的数据ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteGenTableByIds(Long[] tableIds) {
        List<Long> ids = Arrays.asList(tableIds);
        baseMapper.deleteByIds(ids);
        genTableColumnMapper.delete(new LambdaQueryWrapper<GenTableColumn>().in(GenTableColumn::getTableId, ids));
    }

    /**
     * 导入表结构
     *
     * @param tableList 导入表列表
     * @param dataName  数据源名称
     */
    @DSTransactional
    @Override
    public void importGenTable(List<GenTableVo> tableList, String dataName) {
        try {
            for (GenTableVo tableVo : tableList) {
                GenTable table = MapstructUtils.convert(tableVo, GenTable.class);
                String tableName = tableVo.getTableName();
                GenUtils.initTable(table, LoginHelper.getUserId());
                table.setDataName(dataName);
                int row = baseMapper.insert(table);
                tableVo.setTableId(table.getTableId());
                if (row > 0) {
                    // 保存列信息
                    List<GenTableColumn> genTableColumns = SpringUtils.getAopProxy(this).selectDbTableColumnsByName(tableName, dataName);
                    List<GenTableColumn> saveColumns = new ArrayList<>();
                    for (GenTableColumn column : genTableColumns) {
                        GenUtils.initColumnField(column, tableVo);
                        saveColumns.add(column);
                    }
                    if (CollUtil.isNotEmpty(saveColumns)) {
                        genTableColumnMapper.insertBatch(saveColumns);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException("导入失败：" + e.getMessage());
        }
    }

    /**
     * 根据表名称查询列信息
     *
     * @param tableName 表名称
     * @param dataName  数据源名称
     * @return 列信息
     */
    @DS("#dataName")
    @Override
    public List<GenTableColumn> selectDbTableColumnsByName(String tableName, String dataName) {
        DynamicDataSourceContextHolder.push(dataName);
        try {
            Table<?> table = ServiceProxy.metadata().table(tableName);
            if (ObjectUtil.isNull(table)) {
                return new ArrayList<>();
            }
            LinkedHashMap<String, Column> columns = table.getColumns();
            List<GenTableColumn> tableColumns = new ArrayList<>();
            columns.forEach((columnName, column) -> {
                GenTableColumn tableColumn = new GenTableColumn();
                tableColumn.setIsPk(column.isPrimaryKey() == 1 ? "1" : "0");
                tableColumn.setColumnName(column.getName());
                tableColumn.setColumnComment(column.getComment());
                tableColumn.setColumnType(column.getOriginType().toLowerCase());
                tableColumn.setSort(column.getPosition());
                tableColumn.setIsRequired(column.isNullable() == 0 || column.isPrimaryKey() == 1 ? "1" : "0");
                tableColumn.setIsIncrement(column.isAutoIncrement() == 1 ? "1" : "0");
                tableColumns.add(tableColumn);
            });
            return tableColumns;
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 预览代码
     *
     * @param tableId 表编号
     * @return 预览数据列表
     */
    @Override
    public Map<String, String> previewCode(Long tableId) {
        // 查询表信息
        GenTableVo tableVo = baseMapper.selectGenTableById(tableId);
        return previewCode(tableVo);
    }

    /**
     * 临时预览代码
     *
     * @param tableBo 业务信息
     */
    @Override
    public Map<String, String> tempPreviewCode(GenTableBo tableBo) {
        String options = JsonUtils.toJsonString(tableBo.getTableOptions());
        tableBo.setOptions(options);
        // 查询表信息
        GenTableVo tableVo = baseMapper.selectGenTableById(tableBo.getTableId());
        MapstructUtils.convert(tableBo, tableVo);
        tableVo.setColumns(tableBo.getColumns());
        return previewCode(tableVo);
    }

    private Map<String, String> previewCode(GenTableVo tableVo) {
        List<Long> menuIds = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            menuIds.add(identifierGenerator.nextId(null).longValue());
        }
        tableVo.setMenuIds(menuIds);
        // 设置主键列信息
        setPkColumn(tableVo);
        // 设置其他选项
        setTableFromOptions(tableVo);
        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(tableVo);

        Map<String, String> dataMap = new LinkedHashMap<>();
        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(tableVo);
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            dataMap.put(template, sw.toString());
        }
        return dataMap;
    }

    /**
     * 生成代码（下载方式）
     *
     * @param tableId 表名称
     * @return 数据
     */
    @Override
    public byte[] downloadCode(Long tableId) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        generatorCode(tableId, zip);
        IoUtil.close(zip);
        return outputStream.toByteArray();
    }

    /**
     * 生成代码（自定义路径）
     *
     * @param tableId 表名称
     */
    @Override
    public void generatorCode(Long tableId) {
        // 查询表信息
        GenTableVo table = baseMapper.selectGenTableById(tableId);
        // 设置主键列信息
        setPkColumn(table);
        // 设置其他选项
        setTableFromOptions(table);

        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table);
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            try {
                String path = getGenPath(table, template);
                FileUtils.writeUtf8String(sw.toString(), path);
            } catch (Exception e) {
                throw new ServiceException("渲染模板失败，表名：" + table.getTableName());
            }
        }
    }

    /**
     * 同步数据库
     *
     * @param tableId 表名称
     */
    @DSTransactional
    @Override
    public void synchDb(Long tableId) {
        GenTableVo tableVo = baseMapper.selectGenTableById(tableId);
        List<GenTableColumn> tableColumns = StreamUtils.filter(tableVo.getColumns(), column -> Objects.nonNull(column.getColumnId()));
        Map<String, GenTableColumn> tableColumnMap = tableColumns.stream()
            .collect(Collectors.toMap(GenTableColumn::getColumnName, Function.identity(), BiOperator::last));

        List<GenTableColumn> dbTableColumns = SpringUtils.getAopProxy(this).selectDbTableColumnsByName(tableVo.getTableName(), tableVo.getDataName());
        if (CollUtil.isEmpty(dbTableColumns)) {
            throw new ServiceException("同步数据失败，原表结构不存在");
        }
        List<String> dbTableColumnNames = dbTableColumns.stream().map(GenTableColumn::getColumnName).toList();

        // 本地中不存在的字段，待增加
        List<GenTableColumn> saveColumns = new ArrayList<>();
        dbTableColumns.forEach(column -> {
            if (tableColumnMap.containsKey(column.getColumnName())) {
                // 更新
                GenTableColumn prevColumn = tableColumnMap.get(column.getColumnName());
                prevColumn.setColumnType(column.getColumnType());
                prevColumn.setIsPk(column.getIsPk());
                prevColumn.setIsIncrement(column.getIsIncrement());
                prevColumn.setSort(column.getSort());
                prevColumn.setUpdateTime(DateUtils.getNowDate());
                saveColumns.add(prevColumn);
            } else {
                GenUtils.initColumnField(column, tableVo);
                saveColumns.add(column);
            }
        });
        if (CollUtil.isNotEmpty(saveColumns)) {
            genTableColumnMapper.insertOrUpdateBatch(saveColumns);
        }

        // 数据库中不存在的字段，待删除
        List<GenTableColumn> delColumns = tableColumns.stream().filter(column -> !dbTableColumnNames.contains(column.getColumnName())).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(delColumns)) {
            List<Long> ids = delColumns.stream().map(GenTableColumn::getColumnId).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(ids)) {
                genTableColumnMapper.deleteByIds(ids);
            }
        }

        // 更新表描述
        List<GenTableVo> genTables = selectDbTableListByNames(new String[]{tableVo.getTableName()}, tableVo.getDataName());
        if (!genTables.isEmpty()) {
            GenTableVo genTableVo = genTables.get(0);
            GenTable genTable = MapstructUtils.convert(genTableVo, GenTable.class);
            genTable.setFunctionName(genTableVo.getFunctionName());
            genTable.setTableComment(genTableVo.getTableComment());
            baseMapper.updateById(genTable);
        }
    }

    /**
     * 批量生成代码（下载方式）
     *
     * @param tableIds 表ID数组
     * @return 数据
     */
    @Override
    public byte[] downloadCode(String[] tableIds) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableId : tableIds) {
            generatorCode(Long.parseLong(tableId), zip);
        }
        IoUtil.close(zip);
        return outputStream.toByteArray();
    }

    /**
     * 查询表信息并生成代码
     */
    private void generatorCode(Long tableId, ZipOutputStream zip) {
        // 查询表信息
        GenTableVo table = baseMapper.selectGenTableById(tableId);
        List<Long> menuIds = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            menuIds.add(identifierGenerator.nextId(null).longValue());
        }
        table.setMenuIds(menuIds);
        // 设置主键列信息
        setPkColumn(table);
        // 设置其他信息
        setTableFromOptions(table);

        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table);
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            try {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(VelocityUtils.getFileName(template, table)));
                IoUtil.write(zip, StandardCharsets.UTF_8, false, sw.toString());
                IoUtil.close(sw);
                zip.flush();
                zip.closeEntry();
            } catch (IOException e) {
                log.error("渲染模板失败，表名：" + table.getTableName(), e);
            }
        }
    }

    /**
     * 修改保存参数校验
     *
     * @param tableBo 业务信息
     */
    @Override
    public void validateEdit(GenTableBo tableBo) {
        if (GenConstants.TPL_TREE.equals(tableBo.getTplCategory())) {
            GenTableOptions options = tableBo.getTableOptions();
            if (StringUtils.isBlank(options.getTreeCode())) {
                throw new ServiceException("树编码字段不能为空");
            } else if (StringUtils.isBlank(options.getTreeParentCode())) {
                throw new ServiceException("树父编码字段不能为空");
            } else if (StringUtils.isBlank(options.getTreeName())) {
                throw new ServiceException("树名称字段不能为空");
            }
        }
    }

    /**
     * 设置主键列信息
     *
     * @param table 业务表信息
     */
    public void setPkColumn(GenTableVo table) {
        for (GenTableColumn column : table.getColumns()) {
            if (column.isPk()) {
                table.setPkColumn(column);
                break;
            }
        }
        if (ObjectUtil.isNull(table.getPkColumn())) {
            table.setPkColumn(table.getColumns().get(0));
        }
    }

    /**
     * 设置代码生成其他选项值
     *
     * @param tableVo 设置后的生成对象
     */
    public void setTableFromOptions(GenTableVo tableVo) {
        GenTableOptions options = JsonUtils.parseObject(tableVo.getOptions(), GenTableOptions.class);
        if (options == null) {
            options = new GenTableOptions();
        }
        tableVo.setTableOptions(options);
    }

    /**
     * 获取代码生成地址
     *
     * @param table    业务表信息
     * @param template 模板文件路径
     * @return 生成地址
     */
    public static String getGenPath(GenTableVo table, String template) {
        String genPath = table.getGenPath();
        if (StringUtils.equals(genPath, "/")) {
            return System.getProperty("user.dir") + File.separator + "src" + File.separator + VelocityUtils.getFileName(template, table);
        }
        return genPath + File.separator + VelocityUtils.getFileName(template, table);
    }
}

