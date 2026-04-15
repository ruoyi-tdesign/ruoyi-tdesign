<template>
  <t-loading :loading="loading" size="small">
    <t-row :gutter="[0, 24]">
      <t-col :span="6">
        <t-form-item name="info.tplCategory">
          <template #label>生成模板</template>
          <t-select v-model="info.tplCategory">
            <t-option label="单表（增删改查）" value="crud" />
            <t-option label="树表（增删改查）" value="tree" />
          </t-select>
        </t-form-item>
      </t-col>

      <t-col :span="6">
        <t-form-item name="info.packageName">
          <template #label>
            生成包路径
            <t-tooltip content="生成在哪个java包下，例如 com.example.system" placement="top">
              <help-circle-filled-icon />
            </t-tooltip>
          </template>
          <t-input v-model="info.packageName" />
        </t-form-item>
      </t-col>

      <t-col :span="6">
        <t-form-item name="info.moduleName">
          <template #label>
            生成模块名
            <t-tooltip content="可理解为子系统名，例如 system" placement="top">
              <help-circle-filled-icon />
            </t-tooltip>
          </template>
          <t-input v-model="info.moduleName" />
        </t-form-item>
      </t-col>

      <t-col :span="6">
        <t-form-item name="info.businessName">
          <template #label>
            生成业务名
            <t-tooltip content="可理解为功能英文名，例如 user" placement="top">
              <help-circle-filled-icon />
            </t-tooltip>
          </template>
          <t-input v-model="info.businessName" />
        </t-form-item>
      </t-col>

      <t-col :span="6">
        <t-form-item name="info.functionName">
          <template #label>
            生成功能名
            <t-tooltip content="用作类描述，例如 用户" placement="top">
              <help-circle-filled-icon />
            </t-tooltip>
          </template>
          <t-input v-model="info.functionName" />
        </t-form-item>
      </t-col>

      <t-col :span="6">
        <t-form-item name="info.tableOptions.parentMenuId">
          <template #label>
            上级菜单
            <t-tooltip content="分配到指定菜单下，例如 系统管理" placement="top">
              <help-circle-filled-icon />
            </t-tooltip>
          </template>
          <t-tree-select
            v-model="info.tableOptions.parentMenuId"
            :data="menuOptions"
            :tree-props="{
              keys: { value: 'menuId', label: 'menuName', children: 'children' },
              checkStrictly: true,
            }"
            placeholder="请选择上级菜单"
          />
        </t-form-item>
      </t-col>

      <t-col :span="6">
        <t-form-item name="info.genType">
          <template #label>
            生成代码方式
            <t-tooltip content="默认为zip压缩包下载，也可以自定义生成路径" placement="top">
              <help-circle-filled-icon />
            </t-tooltip>
          </template>
          <t-radio-group v-model="info.genType">
            <t-radio value="0">zip压缩包</t-radio>
            <t-radio value="1">自定义路径</t-radio>
          </t-radio-group>
        </t-form-item>
      </t-col>
      <t-col :span="6">
        <t-form-item name="info.tableOptions.menuIcon" label="菜单图标">
          <icon-select v-model="info.tableOptions.menuIcon" />
        </t-form-item>
      </t-col>
      <t-col v-if="info.genType === '1'" :span="12">
        <t-form-item name="info.genPath">
          <template #label>
            自定义路径
            <t-tooltip content="填写磁盘绝对路径，若不填写，则生成到当前Web项目下" placement="top">
              <help-circle-filled-icon />
            </t-tooltip>
          </template>
          <t-input-adornment>
            <t-input v-model="info.genPath" />
            <template #append>
              <t-dropdown
                :max-column-width="300"
                :options="[{ content: '恢复默认的生成基础路径', value: 1 }]"
                @click="info.genPath = '/'"
              >
                <t-button theme="default" variant="outline"
                  >最近路径快速选择
                  <template #suffix>
                    <chevron-down-icon size="16" />
                  </template>
                </t-button>
              </t-dropdown>
            </template>
          </t-input-adornment>
        </t-form-item>
      </t-col>
      <t-col :span="12">
        <t-form-item name="info.genType">
          <template #label>
            选择生成对象
            <t-tooltip placement="top">
              <template #content>
                <p>Bo对象：保存和编辑时，会使用Bo对象替代原始对象</p>
                <p>Vo对象：列表查询、查看详情时，会使用Vo对象替代原始对象</p>
                <p>Query对象：查询条件传递时，会使用Query对象替代原始对象</p>
              </template>
              <help-circle-filled-icon />
            </t-tooltip>
          </template>
          <t-space break-line>
            <t-checkbox v-model="info.tableOptions.isUseQuery">使用Query对象</t-checkbox>
            <t-checkbox v-model="info.tableOptions.isUseBO">使用Bo对象</t-checkbox>
            <t-checkbox v-model="info.tableOptions.isUseVO">使用Vo对象</t-checkbox>
            <t-checkbox v-model="info.tableOptions.isUseController">使用Controller对象</t-checkbox>
            <t-checkbox v-model="info.tableOptions.isUseVue">生成Vue</t-checkbox>
            <t-checkbox v-model="info.tableOptions.isUseSql">生成Sql</t-checkbox>
          </t-space>
        </t-form-item>
      </t-col>
      <t-col :span="12">
        <t-form-item name="info.genType">
          <template #label>
            选择生成方法
            <t-tooltip placement="top">
              <template #content>
                <p>在vue和后台代码中生成的方法</p>
              </template>
              <help-circle-filled-icon />
            </t-tooltip>
          </template>
          <t-space break-line>
            <t-checkbox v-model="info.tableOptions.isUseAddMethod">新增</t-checkbox>
            <t-checkbox v-model="info.tableOptions.isUseEditMethod">编辑</t-checkbox>
            <t-checkbox v-model="info.tableOptions.isUseRemoveMethod">删除</t-checkbox>
            <t-checkbox v-model="info.tableOptions.isUseImportMethod">导入</t-checkbox>
            <t-checkbox v-model="info.tableOptions.isUseExportMethod">导出</t-checkbox>
            <t-checkbox v-model="info.tableOptions.isUseDetailMethod">详情</t-checkbox>
            <t-checkbox v-model="info.tableOptions.isUseQueryMethod">查询</t-checkbox>
          </t-space>
        </t-form-item>
      </t-col>
    </t-row>

    <template v-if="info.tplCategory === 'tree'">
      <t-divider align="left" dashed>其他信息</t-divider>
      <t-row :gutter="[5, 20]">
        <t-col :span="6">
          <t-form-item name="info.tableOptions.treeCode">
            <template #label>
              树编码字段
              <t-tooltip content="树的编码字段名， 如：dept_id" placement="top">
                <help-circle-filled-icon />
              </t-tooltip>
            </template>
            <t-select v-model="info.tableOptions.treeCode" placeholder="请选择编码字段">
              <t-option
                v-for="(column, index) in info.columns"
                :key="index"
                :label="`${column.columnName}：${column.columnComment}`"
                :value="column.columnName"
              ></t-option>
            </t-select>
          </t-form-item>
        </t-col>
        <t-col :span="6">
          <t-form-item name="info.tableOptions.treeParentCode">
            <template #label>
              树父编码字段
              <t-tooltip content="树的父编码字段名， 如：parent_Id" placement="top">
                <help-circle-filled-icon />
              </t-tooltip>
            </template>
            <t-select v-model="info.tableOptions.treeParentCode" placeholder="请选择父编码字段">
              <t-option
                v-for="(column, index) in info.columns"
                :key="index"
                :label="`${column.columnName}：${column.columnComment}`"
                :value="column.columnName"
              ></t-option>
            </t-select>
          </t-form-item>
        </t-col>
        <t-col :span="6">
          <t-form-item name="info.tableOptions.treeName">
            <template #label>
              树名称字段
              <t-tooltip content="树节点名称字段， 如：dept_name" placement="top">
                <help-circle-filled-icon />
              </t-tooltip>
            </template>
            <t-select v-model="info.tableOptions.treeName" placeholder="请选择树节点名称字段">
              <t-option
                v-for="(column, index) in info.columns"
                :key="index"
                :label="`${column.columnName}：${column.columnComment}`"
                :value="column.columnName"
              ></t-option>
            </t-select>
          </t-form-item>
        </t-col>
      </t-row>
    </template>
  </t-loading>
</template>
<script lang="ts" setup>
import { ChevronDownIcon, HelpCircleFilledIcon } from 'tdesign-icons-vue-next';
import type { PropType } from 'vue';
import { getCurrentInstance, ref, toRefs } from 'vue';

import { listMenu } from '@/api/system/menu';
import type { SysMenuVo } from '@/api/system/model/menuModel';
import type { GenTableVo } from '@/api/tool/model/genModel';

const props = defineProps({
  info: {
    type: Object as PropType<GenTableVo>,
    default: null,
  },
  loading: {
    type: Boolean,
    default: false,
  },
});
const menuOptions = ref<SysMenuVo[]>([]);
const { proxy } = getCurrentInstance();

const { info } = toRefs(props);

/** 查询菜单下拉树结构 */
function getMenuTreeselect() {
  listMenu().then((response) => {
    menuOptions.value = [{ menuId: 0, menuName: '主类目', children: proxy.handleTree(response.data, 'menuId') }];
  });
}

getMenuTreeselect();
</script>
