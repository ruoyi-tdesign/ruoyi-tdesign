package org.dromara.system.listener;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.core.ExcelListener;
import org.dromara.common.excel.core.ExcelResult;
import org.dromara.system.domain.bo.SysSensitiveWordBo;
import org.dromara.system.domain.template.SysSensitiveWordImportTemplate;
import org.dromara.system.service.ISysSensitiveWordService;

import java.util.List;

/**
 * 敏感词自定义导入
 *
 * @author hexm
 * @date 2024-08-16
 */
public class SysSensitiveWordImportListener extends AnalysisEventListener<SysSensitiveWordImportTemplate> implements ExcelListener<SysSensitiveWordImportTemplate> {

    private final ISysSensitiveWordService sysSensitiveWordService;

    private final boolean isUpdateSupport;

    private int successNum = 0;
    private int failureNum = 0;
    private final StringBuilder successMsg = new StringBuilder();
    private final StringBuilder failureMsg = new StringBuilder();

    public SysSensitiveWordImportListener(ISysSensitiveWordService sysSensitiveWordService, boolean isUpdateSupport) {
        this.sysSensitiveWordService = sysSensitiveWordService;
        this.isUpdateSupport = isUpdateSupport;
    }

    @Override
    public void invoke(SysSensitiveWordImportTemplate importTemplate, AnalysisContext context) {
        try {
            SysSensitiveWordBo bo = MapstructUtils.convert(importTemplate, SysSensitiveWordBo.class);
            if (!isUpdateSupport) {
                ValidatorUtils.validate(bo, AddGroup.class);
                sysSensitiveWordService.insertByBo(bo, false);
                successNum++;
                successMsg.append("<br/>第").append(successNum).append("行 导入成功");
            } else {
                ValidatorUtils.validate(bo, EditGroup.class);
                sysSensitiveWordService.updateByBo(bo, false);
                successNum++;
                successMsg.append("<br/>第").append(successNum).append("行 更新成功");
            }
        } catch (Exception e) {
            failureNum++;
            failureMsg.append("<br/>第").append(failureNum).append("行 导入失败：").append(e.getMessage());
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (successNum > 0) {
            sysSensitiveWordService.refreshCache();
        }
    }

    @Override
    public ExcelResult<SysSensitiveWordImportTemplate> getExcelResult() {
        return new ExcelResult<>() {

            @Override
            public String getAnalysis() {
                if (successNum > 0 && failureNum > 0) {
                    failureMsg.insert(0, "部分导入失败！共 " + successNum + " 条导入成功，" + failureNum + " 条导入失败，错误如下：");
                    throw new ServiceException(failureMsg.toString());
                } else if (failureNum > 0) {
                    failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
                    throw new ServiceException(failureMsg.toString());
                } else {
                    successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
                }
                return successMsg.toString();
            }

            @Override
            public List<SysSensitiveWordImportTemplate> getList() {
                return null;
            }

            @Override
            public List<String> getErrorList() {
                return null;
            }
        };
    }
}
