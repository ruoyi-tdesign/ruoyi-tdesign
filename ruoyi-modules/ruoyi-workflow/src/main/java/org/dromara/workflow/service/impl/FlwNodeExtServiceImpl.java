package org.dromara.workflow.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.dto.DictTypeDTO;
import org.dromara.common.core.service.DictService;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.warm.flow.ui.service.NodeExtService;
import org.dromara.warm.flow.ui.vo.NodeExt;
import org.dromara.workflow.common.ConditionalOnEnable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程设计器-节点扩展属性
 *
 * @author AprilWind
 */
@ConditionalOnEnable
@Slf4j
@RequiredArgsConstructor
@Service
public class FlwNodeExtServiceImpl implements NodeExtService {

    /**
     * 权限页
     */
    private static final String BUTTON_PERMISSION_TAB = "wf_button_tab";

    /**
     * 权限页页签名称
     */
    private static final String LIMITS_OF_AUTHORITY = "权限";

    /**
     * 字典类型逗号分隔
     */
    private static final String DICT_TYPES = "wf_button_permission";

    /**
     * 基础设置
     */
    private static final int TYPE_BASE_SETTING = 1;

    /**
     * 新页签
     */
    private static final int TYPE_NEW_TAB = 2;
    private final DictService dictService;

    /**
     * 获取节点扩展属性
     *
     * @return 结果
     */
    @Override
    public List<NodeExt> getNodeExt() {
        List<NodeExt> nodeExtList = new ArrayList<>();
        // 构建按钮权限页面
        nodeExtList.add(buildNodeExt(BUTTON_PERMISSION_TAB, TYPE_NEW_TAB, DICT_TYPES));
        return nodeExtList;
    }

    /**
     * 构建一个 NodeExt 对象
     *
     * @param code 编码，此json中唯一
     * @param type 节点类型，1：基础设置，2：新页签
     * @return 返回构建好的 NodeExt 对象
     */
    private NodeExt buildNodeExt(String code, int type, String dictTypes) {
        NodeExt nodeExt = new NodeExt();
        // 从系统参数配置里面获取信息构建新页面或者基础设置
        // 编码，此json中唯一
        nodeExt.setCode(code);
        // 1：基础设置 2：新页签
        nodeExt.setType(type);
        // 名称，如果type为新页签时，作为页签名称
        nodeExt.setName(LIMITS_OF_AUTHORITY);
        nodeExt.setChilds(StringUtils.splitList(dictTypes)
            .stream().map(this::buildChildNode).toList()
        );
        return nodeExt;
    }

    /**
     * 构建一个 ChildNode 对象
     *
     * @param dictType 字典类型
     * @return 返回构建好的 ChildNode 对象
     */
    private NodeExt.ChildNode buildChildNode(String dictType) {
        NodeExt.ChildNode childNode = new NodeExt.ChildNode();
        if (StringUtils.isBlank(dictType)) {
            return childNode;
        }
        DictTypeDTO dictTypeDTO = dictService.getDictTypeDto(dictType);
        if (ObjectUtil.isNull(dictTypeDTO)) {
            return childNode;
        }
        // 编码，此json中唯一
        childNode.setCode(dictType);
        // label名称
        childNode.setLabel(dictTypeDTO.getDictName());
        // 描述
        childNode.setDesc(dictTypeDTO.getRemark());
        // 1：输入框 2：输入框 3：下拉框 4：选择框
        childNode.setType(4);
        // 是否必填
        childNode.setMust(false);
        // 是否多选
        childNode.setMultiple(true);
        // 字典，下拉框和复选框时用到
        childNode.setDict(dictService.getDictDataDto(dictType)
            .stream().map(x ->
                new NodeExt.DictItem(x.getDictLabel(), x.getDictValue(), Convert.toBool(x.getIsDefault(), false))
            ).toList());
        return childNode;
    }

}
