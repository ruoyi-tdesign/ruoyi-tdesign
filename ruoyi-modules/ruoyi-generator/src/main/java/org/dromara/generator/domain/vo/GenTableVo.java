package org.dromara.generator.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.generator.constant.GenConstants;
import org.dromara.generator.domain.GenTable;
import org.dromara.generator.domain.GenTableColumn;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 代码生成业务视图对象 gen_table
 *
 * @author yixiacoco
 * @date 2023-08-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = GenTable.class)
public class GenTableVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    private Long tableId;

    /**
     * 数据源名称
     */
    @ExcelProperty(value = "数据源名称")
    private String dataName;

    /**
     * 表名称
     */
    @ExcelProperty(value = "表名称")
    private String tableName;

    /**
     * 表描述
     */
    @ExcelProperty(value = "表描述")
    private String tableComment;

    /**
     * 实体类名称
     */
    @ExcelProperty(value = "实体类名称")
    private String className;

    /**
     * 使用的模板（crud单表操作 tree树表操作）
     */
    @ExcelProperty(value = "使用的模板")
    private String tplCategory;

    /**
     * 生成包路径
     */
    @ExcelProperty(value = "生成包路径")
    private String packageName;

    /**
     * 生成模块名
     */
    @ExcelProperty(value = "生成模块名")
    private String moduleName;

    /**
     * 生成业务名
     */
    @ExcelProperty(value = "生成业务名")
    private String businessName;

    /**
     * 生成功能名
     */
    @ExcelProperty(value = "生成功能名")
    private String functionName;

    /**
     * 生成功能作者
     */
    @ExcelProperty(value = "生成功能作者")
    private String functionAuthor;

    /**
     * 生成代码方式（0zip压缩包 1自定义路径）
     */
    @ExcelProperty(value = "生成代码方式")
    private String genType;

    /**
     * 生成路径（不填默认项目路径）
     */
    @ExcelProperty(value = "生成路径")
    private String genPath;

    /**
     * 其它生成选项
     */
    @ExcelProperty(value = "其它生成选项")
    private String options;

    /**
     * 更新者
     */
    @ExcelProperty(value = "更新者")
    private Long updateBy;

    /**
     * 创建者
     */
    @ExcelProperty(value = "创建者")
    private Long createBy;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 主键信息
     */
    private GenTableColumn pkColumn;

    /**
     * 表列信息
     */
    private List<GenTableColumn> columns;

    /**
     * 菜单id列表
     */
    private List<Long> menuIds;

    /**
     * 代码生成选项
     */
    private GenTableOptions tableOptions;


    public boolean isTree() {
        return isTree(this.tplCategory);
    }

    public static boolean isTree(String tplCategory) {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_TREE, tplCategory);
    }

    public boolean isCrud() {
        return isCrud(this.tplCategory);
    }

    public static boolean isCrud(String tplCategory) {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_CRUD, tplCategory);
    }

    public boolean isSuperColumn(String javaField) {
        return isSuperColumn(this.tplCategory, javaField);
    }

    public static boolean isSuperColumn(String tplCategory, String javaField) {
        return StringUtils.equalsAnyIgnoreCase(javaField, GenConstants.BASE_ENTITY);
    }
}
