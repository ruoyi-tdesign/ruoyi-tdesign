package org.dromara.generator.util;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RegExUtils;
import org.dromara.common.core.enums.YesNoEnum;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.generator.config.GenConfig;
import org.dromara.generator.constant.GenConstants;
import org.dromara.generator.domain.GenTable;
import org.dromara.generator.domain.GenTableColumn;
import org.dromara.generator.domain.vo.GenTableVo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 代码生成器 工具类
 *
 * @author ruoyi
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenUtils {

    public static final Pattern ARRAY_PATTERN = Pattern.compile("(\\S+)\\[]");
    public static final Pattern LIST_PATTERN = Pattern.compile("List<(\\S+)>");
    private static final Set<String> KEY_WORD = new HashSet<>();

    static {
        /* mysql关键字 */
        KEY_WORD.addAll(ListUtil.toList("select", "insert", "into", "form", "group", "by", "as", "order", "and", "or", "desc",
            "asc", "join", "on", "left", "right", "inner", "table", "sort", "name", "show", "table", "language",
            "lock", "update", "lock", "option", "where", "ssl", "limit", "status", "exit", "drop", "div", "as",
            "not", "null", "is", "add", "all", "union", "alter", "blob", "txt", "clob", "before", "between",
            "bigint", "binary", "int", "case", "do", "then", "when", "char", "varchar", "check", "column", "if",
            "else", "elseif", "dec", "exit", "explain", "exists", "drop", "for", "delete", "user", "database",
            "force", "goto", "key", "regexp", "set", "unlock", "true", "false", "use", "tinyint", "sql", "xor",
            "values", "using", "tinyblob", "smallint", "double", "enum", "decimal", "date", "datetime", "timestamp",
            "year", "float", "time", "long", "geometry", "json", "integer", "tables", "change", "character", "collate"));
    }

    /**
     * 初始化表信息
     */
    public static void initTable(GenTable genTable, Long operId) {
        genTable.setClassName(convertClassName(genTable.getTableName()));
        genTable.setPackageName(GenConfig.getPackageName());
        genTable.setModuleName(getModuleName(GenConfig.getPackageName()));
        genTable.setBusinessName(getBusinessName(genTable.getTableName()));
        genTable.setFunctionName(replaceText(genTable.getTableComment()));
        genTable.setFunctionAuthor(GenConfig.getAuthor());
        genTable.setCreateBy(operId);
        genTable.setCreateTime(null);
        genTable.setUpdateTime(null);
    }

    /**
     * 初始化列属性字段
     */
    public static void initColumnField(GenTableColumn column, GenTableVo table) {
        String dataType = getDbType(column.getColumnType());
        String columnName = column.getColumnName();
        column.setTableId(table.getTableId());
        column.setCreateBy(table.getCreateBy());
        column.setCreateTime(null);
        column.setUpdateTime(null);
        // 设置java字段名
        column.setJavaField(StringUtils.toCamelCase(columnName));
        // 设置默认类型
        column.setJavaType(GenConstants.TYPE_STRING);
        column.setQueryType(GenConstants.QUERY_EQ);
        column.setIsSort(YesNoEnum.NO.getCodeNum().toString());
        column.setIsInsert(YesNoEnum.NO.getCodeNum().toString());
        column.setIsEdit(YesNoEnum.NO.getCodeNum().toString());
        column.setIsList(YesNoEnum.NO.getCodeNum().toString());
        column.setIsDetail(YesNoEnum.NO.getCodeNum().toString());

        if (arraysContains(GenConstants.COLUMN_TYPE_STR, dataType) || arraysContains(GenConstants.COLUMN_TYPE_TEXT, dataType)) {
            // 字符串长度超过500设置为文本域
            Integer columnLength = getColumnLength(column.getColumnType());
            String htmlType = columnLength >= 500 || arraysContains(GenConstants.COLUMN_TYPE_TEXT, dataType) ? GenConstants.HTML_TEXTAREA : GenConstants.HTML_INPUT;
            column.setHtmlType(htmlType);
        } else if (arraysContains(GenConstants.COLUMN_TYPE_DATE, dataType)) {
            column.setJavaType(GenConstants.TYPE_DATE);
            column.setHtmlType(GenConstants.HTML_DATETIME);
        } else if (arraysContains(GenConstants.COLUMN_TYPE_TIME, dataType)) {
            column.setJavaType(GenConstants.TYPE_TIME);
            column.setHtmlType(GenConstants.HTML_TIME);
        } else if (arraysContains(GenConstants.COLUMN_TYPE_NUMBER, dataType)) {
            column.setHtmlType(GenConstants.HTML_INPUT_NUMBER);

            // 如果是浮点型 统一用BigDecimal
            String[] str = StringUtils.split(StringUtils.substringBetween(column.getColumnType(), "(", ")"), StringUtils.SEPARATOR);
            if (str != null && str.length == 2 && Integer.parseInt(str[1]) > 0 || arraysContains(GenConstants.COLUMN_TYPE_DOUBLE, column.getColumnType())) {
                column.setJavaType(GenConstants.TYPE_BIGDECIMAL);
            }
            // 如果是整形
            else if (str != null && str.length == 1 && Integer.parseInt(str[0]) <= 11 || arraysContains(GenConstants.COLUMN_TYPE_INTEGER, column.getColumnType())) {
                column.setJavaType(GenConstants.TYPE_INTEGER);
            }
            // 长整形
            else {
                column.setJavaType(GenConstants.TYPE_LONG);
            }
        }

        // BO对象 默认插入勾选
        if (!arraysContains(GenConstants.COLUMN_NAME_NOT_ADD, columnName) && !column.isPk()) {
            column.setIsInsert(GenConstants.REQUIRE);
        }
        // BO对象 默认编辑勾选
        if (!arraysContains(GenConstants.COLUMN_NAME_NOT_EDIT, columnName) && !column.isPk()) {
            column.setIsEdit(GenConstants.REQUIRE);
        }
        // BO对象 默认是否必填勾选
        if (!arraysContains(GenConstants.COLUMN_NAME_NOT_EDIT, columnName) && column.getIsRequired() == null) {
            column.setIsRequired(GenConstants.REQUIRE);
        }
        // VO对象 默认返回勾选
        if (!arraysContains(GenConstants.COLUMN_NAME_NOT_LIST, columnName)) {
            column.setIsList(GenConstants.REQUIRE);
        }
        // VO对象 默认详情勾选
        if (!arraysContains(GenConstants.COLUMN_NAME_NOT_LIST, columnName)) {
            column.setIsDetail(GenConstants.REQUIRE);
        }
        // BO对象 默认查询勾选
        if (!arraysContains(GenConstants.COLUMN_NAME_NOT_QUERY, columnName) && !column.isPk()) {
            column.setIsQuery(GenConstants.REQUIRE);
        }

        // 查询字段类型
        if (StringUtils.endsWithIgnoreCase(columnName, "name")) {
            column.setQueryType(GenConstants.QUERY_LIKE);
        }
        // 状态字段设置单选框
        if (StringUtils.endsWithIgnoreCase(columnName, "status")) {
            column.setHtmlType(GenConstants.HTML_RADIO);
        }
        // 类型&性别字段设置下拉框
        else if (StringUtils.endsWithIgnoreCase(columnName, "type")
            || StringUtils.endsWithIgnoreCase(columnName, "sex")) {
            column.setHtmlType(GenConstants.HTML_SELECT);
        }
        // 图片字段设置图片上传控件
        else if (StringUtils.endsWithIgnoreCase(columnName, "image")) {
            column.setHtmlType(GenConstants.HTML_IMAGE_UPLOAD);
        }
        // 文件字段设置文件上传控件
        else if (StringUtils.endsWithIgnoreCase(columnName, "file")) {
            column.setHtmlType(GenConstants.HTML_FILE_UPLOAD);
        }
        // 内容字段设置富文本控件
        else if (StringUtils.endsWithIgnoreCase(columnName, "content")) {
            column.setHtmlType(GenConstants.HTML_EDITOR);
        }
    }

    /**
     * 校验数组是否包含指定值
     *
     * @param arr         数组
     * @param targetValue 值
     * @return 是否包含
     */
    public static boolean arraysContains(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    /**
     * 获取模块名
     *
     * @param packageName 包名
     * @return 模块名
     */
    public static String getModuleName(String packageName) {
        int lastIndex = packageName.lastIndexOf('.');
        int nameLength = packageName.length();
        return StringUtils.substring(packageName, lastIndex + 1, nameLength);
    }

    /**
     * 获取业务名
     *
     * @param tableName 表名
     * @return 业务名
     */
    public static String getBusinessName(String tableName) {
        int firstIndex = tableName.indexOf('_');
        int nameLength = tableName.length();
        String businessName = StringUtils.substring(tableName, firstIndex + 1, nameLength);
        businessName = StringUtils.toCamelCase(businessName);
        return businessName;
    }

    /**
     * 表名转换成Java类名
     *
     * @param tableName 表名称
     * @return 类名
     */
    public static String convertClassName(String tableName) {
        boolean autoRemovePre = GenConfig.getAutoRemovePre();
        String tablePrefix = GenConfig.getTablePrefix();
        if (autoRemovePre && StringUtils.isNotEmpty(tablePrefix)) {
            String[] searchList = StringUtils.split(tablePrefix, StringUtils.SEPARATOR);
            tableName = replaceFirst(tableName, searchList);
        }
        return StringUtils.convertToCamelCase(tableName);
    }

    /**
     * 批量替换前缀
     *
     * @param replacementm 替换值
     * @param searchList   替换列表
     */
    public static String replaceFirst(String replacementm, String[] searchList) {
        String text = replacementm;
        for (String searchString : searchList) {
            if (replacementm.startsWith(searchString)) {
                text = replacementm.replaceFirst(searchString, "");
                break;
            }
        }
        return text;
    }

    /**
     * 关键字替换
     *
     * @param text 需要被替换的名字
     * @return 替换后的名字
     */
    public static String replaceText(String text) {
        return RegExUtils.replaceAll(text, "(?:表|若依)", "");
    }

    /**
     * 获取数据库类型字段
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static String getDbType(String columnType) {
        if (StringUtils.indexOf(columnType, '(') > 0) {
            return StringUtils.substringBefore(columnType, "(");
        }
        if (StrUtil.contains(columnType, "unsigned")) {
            return columnType.replace("unsigned", "").trim();
        } else {
            return columnType;
        }
    }

    /**
     * 获取字段长度
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static Integer getColumnLength(String columnType) {
        // 检查是否包含括号，不包含直接返回0
        if (StringUtils.indexOf(columnType, '(') <= 0 || StringUtils.indexOf(columnType, ')') <= 0) {
            return 0;
        }
        String length = StringUtils.substringBetween(columnType, "(", ")");
        if (NumberUtil.isInteger(length)) {
            return Integer.valueOf(length);
        }
        return 0;
    }

    /**
     * 判断是否是mysql关键字
     * 在vm模板中使用
     *
     * @param key
     * @return
     */
    public static boolean isMysqlKeyword(String key) {
        return KEY_WORD.contains(key);
    }

    /**
     * java类型转typescript类型
     * 在vm模板中使用
     *
     * @param javaType
     * @return
     */
    public static String javaTypeToTypescript(String javaType) {
        if (StringUtils.containsAnyIgnoreCase(javaType, "Integer", "int", "short", "byte", "long", "BigInteger", "BigDecimal")) {
            return "number";
        } else if (StringUtils.containsAnyIgnoreCase(javaType, "String", "CharSequence")) {
            return "string";
        } else if (StringUtils.containsAnyIgnoreCase(javaType, "boolean")) {
            return "boolean";
        } else if (ARRAY_PATTERN.matcher(javaType).matches()) {
            String type = ARRAY_PATTERN.matcher(javaType).group(0);
            return "Array<" + javaTypeToTypescript(type) + ">";
        } else if (LIST_PATTERN.matcher(javaType).matches()) {
            String type = LIST_PATTERN.matcher(javaType).group(0);
            return "Array<" + javaTypeToTypescript(type) + ">";
        } else {
            return "any";
        }
    }
}
