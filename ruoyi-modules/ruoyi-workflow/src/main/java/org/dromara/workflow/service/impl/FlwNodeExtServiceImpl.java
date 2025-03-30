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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 权限页code
     */
    private static final String PERMISSION_TAB = "wf_button_tab";

    /**
     * 权限页名称
     */
    private static final String PERMISSION_TAB_NAME = "权限";

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

    /**
     * 存储不同 dictType 对应的配置信息
     */
    private static final Map<String, Map<String, Object>> CHILD_NODE_MAP = new HashMap<>();

    static {
        CHILD_NODE_MAP.put("wf_button_permission", Map.of("type", 4, "must", false, "multiple", true));
    }

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
        nodeExtList.add(buildNodeExt(PERMISSION_TAB, PERMISSION_TAB_NAME, TYPE_NEW_TAB, DICT_TYPES));
        return nodeExtList;
    }

    /**
     * 构建一个 NodeExt 对象
     *
     * @param code      编码，此json中唯一
     * @param name      名称，如果type为新页签时，作为页签名称
     * @param type      节点类型，1：基础设置，2：新页签
     * @param dictTypes 字典类型逗号分隔
     * @return 返回构建好的 NodeExt 对象
     */
    private NodeExt buildNodeExt(String code, String name, int type, String dictTypes) {
        NodeExt nodeExt = new NodeExt();
        nodeExt.setCode(code);
        nodeExt.setType(type);
        nodeExt.setName(name);
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
        Map<String, Object> map = CHILD_NODE_MAP.get(dictType);
        // 1：输入框 2：输入框 3：下拉框 4：选择框
        childNode.setType(Convert.toInt(map.get("type"), 1));
        // 是否必填
        childNode.setMust(Convert.toBool(map.get("must"), false));
        // 是否多选
        childNode.setMultiple(Convert.toBool(map.get("multiple"), true));
        // 字典，下拉框和复选框时用到
        childNode.setDict(dictService.getDictDataDto(dictType)
            .stream().map(x ->
                new NodeExt.DictItem(x.getDictLabel(), x.getDictValue(), Convert.toBool(x.getIsDefault(), false))
            ).toList());
        return childNode;
    }

}
