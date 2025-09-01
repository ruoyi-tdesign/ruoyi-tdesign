package org.dromara.generator.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.velocity.VelocityContext;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.helper.DataBaseHelper;
import org.dromara.generator.constant.GenConstants;
import org.dromara.generator.domain.GenTableColumn;
import org.dromara.generator.domain.vo.GenTableOptions;
import org.dromara.generator.domain.vo.GenTableVo;

import java.util.*;

/**
 * 模板处理工具类
 *
 * @author ruoyi
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VelocityUtils {

    /**
     * 项目空间路径
     */
    private static final String PROJECT_PATH = "main/java";

    /**
     * mybatis空间路径
     */
    private static final String MYBATIS_PATH = "main/resources/mapper";

    /**
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    public static VelocityContext prepareContext(GenTableVo genTable) {
        String moduleName = genTable.getModuleName();
        String businessName = genTable.getBusinessName();
        String packageName = genTable.getPackageName();
        String functionName = genTable.getFunctionName();
        GenTableOptions options = genTable.getTableOptions();
        Map<String, Object> optionsMap = BeanUtil.beanToMap(options);

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tplCategory", genTable.getTplCategory());
        velocityContext.put("tableName", genTable.getTableName());
        velocityContext.put("functionName", StringUtils.isNotEmpty(functionName) ? functionName : "【请填写功能名称】");
        velocityContext.put("ClassName", genTable.getClassName());
        velocityContext.put("className", StringUtils.uncapitalize(genTable.getClassName()));
        velocityContext.put("moduleName", genTable.getModuleName());
        velocityContext.put("BusinessName", StringUtils.capitalize(genTable.getBusinessName()));
        velocityContext.put("businessName", genTable.getBusinessName());
        velocityContext.put("basePackage", getPackagePrefix(packageName));
        velocityContext.put("packageName", packageName);
        velocityContext.put("author", genTable.getFunctionAuthor());
        velocityContext.put("datetime", DateUtils.getDate());
        velocityContext.put("pkColumn", genTable.getPkColumn());
        velocityContext.put("importList", getImportList(genTable));
        velocityContext.put("permissionPrefix", getPermissionPrefix(moduleName, businessName));
        velocityContext.put("columns", genTable.getColumns());
        velocityContext.put("table", genTable);
        velocityContext.put("hasSorting", genTable.getColumns().stream().anyMatch(GenTableColumn::isSorting));
        velocityContext.put("dicts", getDicts(genTable));
        velocityContext.put("dbName", firstLetter(genTable.getTableName(), "_"));
        velocityContext.put("GenUtil", GenUtils.class);
        velocityContext.put("StringUtils", StringUtils.class);
        velocityContext.put("StrUtil", StrUtil.class);
        optionsMap.forEach(velocityContext::put);
        velocityContext.put("treeCode", StringUtils.toCamelCase(options.getTreeCode()));
        velocityContext.put("treeName", StringUtils.toCamelCase(options.getTreeName()));
        velocityContext.put("treeParentCode", StringUtils.toCamelCase(options.getTreeParentCode()));
        return velocityContext;
    }

    /**
     * 分割字符串，返回小写首字母
     * 例如： str=sys_user concat=_ =>su
     *
     * @param str
     * @param concat
     * @return
     */
    public static String firstLetter(String str, String concat) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        List<String> strs = StrUtil.split(str, concat);
        StringBuilder sb = new StringBuilder();
        for (String s : strs) {
            sb.append(s.charAt(0));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getTemplateList(GenTableVo table) {
        String tplCategory = table.getTplCategory();
        List<String> templates = new ArrayList<>();
        templates.add("vm/java/domain.java.vm");
        GenTableOptions options = table.getTableOptions();
        if (options.getIsUseQuery()) {
            templates.add("vm/java/query.java.vm");
        }
        if (options.getIsUseBO()) {
            templates.add("vm/java/bo.java.vm");
        }
        if (options.getIsUseVO()) {
            templates.add("vm/java/vo.java.vm");
        }
        if (options.getIsUseImportMethod()) {
            templates.add("vm/java/import.java.vm");
            templates.add("vm/java/listener.java.vm");
        }
        if (options.getIsUseController()) {
            templates.add("vm/java/controller.java.vm");
        }
        templates.add("vm/java/service.java.vm");
        templates.add("vm/java/serviceImpl.java.vm");
        templates.add("vm/java/mapper.java.vm");
        templates.add("vm/xml/mapper.xml.vm");
        if (options.getIsUseVue()) {
            templates.add("vm/ts/model.ts.vm");
            templates.add("vm/ts/api.ts.vm");
            if (GenConstants.TPL_CRUD.equals(tplCategory)) {
                templates.add("vm/vue/index.vue.vm");
            } else if (GenConstants.TPL_TREE.equals(tplCategory)) {
                templates.add("vm/vue/index-tree.vue.vm");
            }
        }
        if (options.getIsUseSql()) {
            if (DataBaseHelper.isOracle()) {
                templates.add("vm/sql/oracle/sql.vm");
            } else if (DataBaseHelper.isPostgerSql()) {
                templates.add("vm/sql/postgres/sql.vm");
            } else if (DataBaseHelper.isSqlServer()) {
                templates.add("vm/sql/sqlserver/sql.vm");
            } else {
                templates.add("vm/sql/sql.vm");
            }
        }
        return templates;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, GenTableVo genTable) {
        // 文件名称
        String fileName = "";
        // 包路径
        String packageName = genTable.getPackageName();
        // 模块名
        String moduleName = genTable.getModuleName();
        // 大写类名
        String className = genTable.getClassName();
        // 业务名称
        String businessName = genTable.getBusinessName();

        String javaPath = PROJECT_PATH + "/" + StringUtils.replace(packageName, ".", "/");
        String mybatisPath = MYBATIS_PATH + "/" + moduleName;
        String vuePath = "vue";

        if (template.contains("domain.java.vm")) {
            fileName = StringUtils.format("{}/domain/{}.java", javaPath, className);
        }
        if (template.contains("query.java.vm")) {
            fileName = StringUtils.format("{}/domain/query/{}Query.java", javaPath, className);
        }
        if (template.contains("vo.java.vm")) {
            fileName = StringUtils.format("{}/domain/vo/{}Vo.java", javaPath, className);
        }
        if (template.contains("bo.java.vm")) {
            fileName = StringUtils.format("{}/domain/bo/{}Bo.java", javaPath, className);
        }
        if (template.contains("import.java.vm")) {
            fileName = StringUtils.format("{}/domain/template/{}ImportTemplate.java", javaPath, className);
        }
        if (template.contains("listener.java.vm")) {
            fileName = StringUtils.format("{}/listener/{}ImportListener.java", javaPath, className);
        }
        if (template.contains("mapper.java.vm")) {
            fileName = StringUtils.format("{}/mapper/{}Mapper.java", javaPath, className);
        } else if (template.contains("service.java.vm")) {
            fileName = StringUtils.format("{}/service/I{}Service.java", javaPath, className);
        } else if (template.contains("serviceImpl.java.vm")) {
            fileName = StringUtils.format("{}/service/impl/{}ServiceImpl.java", javaPath, className);
        } else if (template.contains("controller.java.vm")) {
            fileName = StringUtils.format("{}/controller/{}Controller.java", javaPath, className);
        } else if (template.contains("mapper.xml.vm")) {
            fileName = StringUtils.format("{}/{}Mapper.xml", mybatisPath, className);
        } else if (template.contains("sql.vm")) {
            fileName = businessName + "Menu.sql";
        } else if (template.contains("api.ts.vm")) {
            fileName = StringUtils.format("{}/api/{}/{}.ts", vuePath, moduleName, businessName);
        } else if (template.contains("model.ts.vm")) {
            fileName = StringUtils.format("{}/api/{}/model/{}Model.ts", vuePath, moduleName, businessName);
        } else if (template.contains("index.vue.vm")) {
            fileName = StringUtils.format("{}/pages/{}/{}/index.vue", vuePath, moduleName, businessName);
        } else if (template.contains("index-tree.vue.vm")) {
            fileName = StringUtils.format("{}/pages/{}/{}/index.vue", vuePath, moduleName, businessName);
        }
        return fileName;
    }

    /**
     * 获取包前缀
     *
     * @param packageName 包名称
     * @return 包前缀名称
     */
    public static String getPackagePrefix(String packageName) {
        int lastIndex = packageName.lastIndexOf('.');
        return StringUtils.substring(packageName, 0, lastIndex);
    }

    /**
     * 根据列类型获取导入包
     *
     * @param genTable 业务表对象
     * @return 返回需要导入的包列表
     */
    public static HashSet<String> getImportList(GenTableVo genTable) {
        List<GenTableColumn> columns = genTable.getColumns();
        HashSet<String> importList = new HashSet<>();
        for (GenTableColumn column : columns) {
            if (!column.isSuperColumn() && GenConstants.TYPE_DATE.equals(column.getJavaType())) {
                importList.add("java.util.Date");
                importList.add("com.fasterxml.jackson.annotation.JsonFormat");
            } else if (!column.isSuperColumn() && GenConstants.TYPE_BIGDECIMAL.equals(column.getJavaType())) {
                importList.add("java.math.BigDecimal");
            } else if (!column.isSuperColumn() && GenConstants.TYPE_TIME.equals(column.getJavaType())) {
                importList.add("java.sql.Time");
            }
        }
        return importList;
    }

    /**
     * 根据列类型获取字典组
     *
     * @param genTable 业务表对象
     * @return 返回字典组
     */
    public static String getDicts(GenTableVo genTable) {
        List<GenTableColumn> columns = genTable.getColumns();
        Set<String> dicts = new HashSet<>();
        addDicts(dicts, columns);
        return StringUtils.join(dicts, ", ");
    }

    /**
     * 添加字典列表
     *
     * @param dicts   字典列表
     * @param columns 列集合
     */
    public static void addDicts(Set<String> dicts, List<GenTableColumn> columns) {
        for (GenTableColumn column : columns) {
            if (column.isSuperColumn() || StringUtils.isEmpty(column.getDictType())) {
                continue;
            }
            if (StringUtils.equalsAny(column.getHtmlType(), new String[]{GenConstants.HTML_SELECT, GenConstants.HTML_RADIO, GenConstants.HTML_CHECKBOX})
                || column.isDetail()) {
                dicts.add("'" + column.getDictType() + "'");
            }
        }
    }

    /**
     * 获取权限前缀
     *
     * @param moduleName   模块名称
     * @param businessName 业务名称
     * @return 返回权限前缀
     */
    public static String getPermissionPrefix(String moduleName, String businessName) {
        return StringUtils.format("{}:{}", moduleName, businessName);
    }
}
