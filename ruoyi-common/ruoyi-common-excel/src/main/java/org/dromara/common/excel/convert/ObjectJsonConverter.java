package org.dromara.common.excel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import org.dromara.common.json.utils.JsonUtils;

import java.util.Optional;

/**
 * Object and String Converter
 *
 * @author hexm
 * @date 2024/02/23 15:51
 */
public class ObjectJsonConverter implements Converter<Object> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                    GlobalConfiguration globalConfiguration) {
        return JsonUtils.parseObject(cellData.getStringValue(), contentProperty.getField().getType());
    }

    @Override
    public WriteCellData<?> convertToExcelData(Object value, ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(Optional.ofNullable(JsonUtils.toJsonString(value)).orElse(""));
    }
}
