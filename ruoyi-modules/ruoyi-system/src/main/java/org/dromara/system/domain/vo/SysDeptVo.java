package org.dromara.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.system.domain.SysDept;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 部门视图对象 sys_dept
 *
 * @author Michelle.Chung
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysDept.class)
public class SysDeptVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 部门id
     */
    @ExcelProperty(value = "部门id")
    private Long deptId;

    /**
     * 父部门id
     */
    @ExcelProperty(value = "父部门id")
    private Long parentId;

    /**
     * 父部门名称
     */
    @ExcelProperty(value = "父部门名称")
    private String parentName;

    /**
     * 祖级列表
     */
    @ExcelProperty(value = "祖级列表")
    private String ancestors;

    /**
     * 部门名称
     */
    @ExcelProperty(value = "部门名称")
    private String deptName;

    /**
     * 部门类别编码
     */
    @ExcelProperty(value = "部门类别编码")
    private String deptCategory;

    /**
     * 显示顺序
     */
    @ExcelProperty(value = "显示顺序")
    private Integer orderNum;

    /**
     * 负责人
     */
    @ExcelProperty(value = "负责人")
    private Long leader;

    /**
     * 负责人名称
     */
    @ExcelProperty(value = "负责人名称")
    private String leaderName;

    /**
     * 联系电话
     */
    @ExcelProperty(value = "联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @ExcelProperty(value = "邮箱")
    private String email;

    /**
     * 部门状态（1正常 0停用）
     */
    @ExcelProperty(value = "部门状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

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
     * 子部门
     */
    private List<SysDept> children = new ArrayList<>();

}
