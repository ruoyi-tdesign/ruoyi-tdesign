-- ----------------------------
-- 消息配置表
-- ----------------------------
drop table if exists sys_message_config;
create table sys_message_config  (
  message_config_id bigint(20)      not null                    comment '消息设置id',
  title             varchar(255)    not null                    comment '标题',
  message_type      varchar(20)     not null                    comment '消息类型 SMS、MAIL',
  supplier_type     varchar(20)     not null                    comment '支持平台标识',
  config_json       text            null                        comment '配置json',
  status            tinyint(1)      not null                    comment '状态（1正常 0停用）',
  remark            varchar(500)    null        default null    comment '备注',
  create_dept       bigint(20)      null        default null    comment '创建部门',
  update_by         bigint(20)      null        default null    comment '更新者',
  create_by         bigint(20)      null        default null    comment '创建者',
  update_time       datetime        null        default null    comment '更新时间',
  create_time       datetime        null        default null    comment '创建时间',
  primary key (message_config_id) using btree,
  index idx_message_type(message_type, status) using btree
) engine = innodb comment = '消息配置表';

-- ----------------------------
-- 消息常量表
-- ----------------------------
drop table if exists sys_message_key;
create table sys_message_key  (
  message_key_id    bigint(20)      not null                    comment '消息key主键',
  name              varchar(255)    not null                    comment '消息名称',
  message_key       varchar(50)     not null                    comment '消息key',
  remark            varchar(500)    null        default null    comment '备注',
  create_dept       bigint(20)      null        default null    comment '创建部门',
  update_by         bigint(20)      null        default null    comment '更新者',
  create_by         bigint(20)      null        default null    comment '创建者',
  update_time       datetime        null        default null    comment '更新时间',
  create_time       datetime        null        default null    comment '创建时间',
  primary key (message_key_id) using btree
) engine = innodb comment = '消息常量表';

-- ----------------------------
-- 消息发送记录表
-- ----------------------------
drop table if exists sys_message_log;
create table sys_message_log  (
  message_log_id        bigint(20)      not null                    comment '消息发送记录id',
  message_template_id   bigint(20)      null        default null    comment '消息模板id',
  message_key           varchar(50)     not null                    comment '消息key',
  message_template_name varchar(255)    null        default null    comment '模板名称',
  message_type          varchar(20)     not null                    comment '消息类型 SMS、MAIL',
  template_mode         varchar(20)     null                        comment '模板类型 模板id模式、内容模式',
  account               varchar(255)    not null                    comment '发送账号',
  title                 varchar(255)    null        default null    comment '标题',
  template_id           varchar(100)    null        default null    comment '模板ID',
  content               text            null                        comment '发送内容',
  message_config_title  varchar(255)    null        default null    comment '消息配置标题',
  supplier_type         varchar(20)     null        default null    comment '平台标识',
  is_success            tinyint(1)      null        default null    comment '是否成功',
  error_code            varchar(255)    null        default null    comment '错误码',
  error_message         varchar(500)    null        default null    comment '错误消息',
  biz_id                varchar(255)    null        default null    comment '回执消息id',
  message               varchar(255)    null        default null    comment '返回消息',
  log_time              datetime        not null                    comment '记录时间',
  primary key (message_log_id) using btree,
  index idx_message_template_id(message_template_id) using btree
) engine = innodb comment = '消息发送记录表';

-- ----------------------------
-- 消息模板表
-- ----------------------------
drop table if exists sys_message_template;
create table sys_message_template  (
  message_template_id   bigint(20)      not null                    comment '消息模板id',
  template_name         varchar(255)    not null                    comment '模板名称',
  message_config_id     bigint(20)      not null                    comment '消息配置id',
  message_key_id        bigint          NOT NULL                    COMMENT '消息key主键',
  message_key           varchar(50)     not null                    comment '消息key',
  message_type          varchar(20)     not null                    comment '消息类型 SMS、MAIL',
  template_mode         varchar(20)     not null                    comment '模板类型 模板id模式、内容模式',
  title                 varchar(255)    null        default null    comment '标题',
  signature             varchar(100)    null        default null    comment '签名',
  template_id           varchar(100)    null        default null    comment '模板id',
  content               text            null                        comment '内容',
  vars_json             text            null                        comment '输入变量',
  status                tinyint(1)      not null                    comment '状态（1正常 0停用）',
  remark                varchar(500)    null        default null    comment '备注',
  create_dept           bigint(20)      null        default null    comment '创建部门',
  update_by             bigint(20)      null        default null    comment '更新者',
  create_by             bigint(20)      null        default null    comment '创建者',
  update_time           datetime        null        default null    comment '更新时间',
  create_time           datetime        null        default null    comment '创建时间',
  primary key (message_template_id) using btree,
  index idx_message_key_id(message_key_id) using btree,
  index idx_message_key(message_key, message_type, status) using btree
) engine = innodb comment = '消息模板表';

-- 菜单
-- 消息管理
insert into sys_menu values('1801', '消息管理', '1', 11, 'messageManage', NULL, NULL, 0, 1, 'M', '1', '1', NULL, 'chat', 103, 1, sysdate(), 1, sysdate(), '');
-- 消息配置
insert into sys_menu values('1802', '消息配置',    '1801', 1, 'messageConfig', 'system/messageConfig/index', NULL, 0, 1, 'C', '1', '1', 'system:messageConfig:list', 'tools', 103, 1, sysdate(), 1, sysdate(), '消息配置菜单');
insert into sys_menu values('1803', '消息配置查询', '1802', 1, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageConfig:query', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1804', '消息配置新增', '1802', 2, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageConfig:add', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1805', '消息配置修改', '1802', 3, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageConfig:edit', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1806', '消息配置删除', '1802', 4, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageConfig:remove', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1807', '消息配置导出', '1802', 5, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageConfig:export', '#', 103, 1, sysdate(), NULL, NULL, '');
-- 消息常量
insert into sys_menu values('1810', '消息常量',     '1801', 2, 'messageKey', 'system/messageKey/index', NULL, 0, 1, 'C', '1', '1', 'system:messageKey:list', 'root-list', 103, 1, sysdate(), 1, sysdate(), '消息常量菜单');
insert into sys_menu values('1811', '消息常量查询', '1810', 1, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageKey:query', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1812', '消息常量新增', '1810', 2, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageKey:add', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1813', '消息常量修改', '1810', 3, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageKey:edit', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1814', '消息常量删除', '1810', 4, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageKey:remove', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1815', '消息常量导出', '1810', 5, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageKey:export', '#', 103, 1, sysdate(), NULL, NULL, '');
-- 消息模板
insert into sys_menu values('1820', '消息模板',    '1801', 3, 'messageTemplate', 'system/messageTemplate/index', NULL, 0, 1, 'C', '1', '1', 'system:messageTemplate:list', 'relativity', 103, 1, sysdate(), 1, sysdate(), '消息模板菜单');
insert into sys_menu values('1821', '消息模板查询', '1820', 1, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageTemplate:query', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1822', '消息模板新增', '1820', 2, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageTemplate:add', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1823', '消息模板修改', '1820', 3, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageTemplate:edit', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1824', '消息模板删除', '1820', 4, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageTemplate:remove', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1825', '消息模板导出', '1820', 5, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageTemplate:export', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1826', '发送测试消息', '1820', 6, '', NULL, NULL, 0, 1, 'F', '1', '1', 'system:messageTemplate:test', '#', 103, 1, sysdate(), 1, sysdate(), '');
-- 消息发送记录
insert into sys_menu values('1830', '消息发送记录',    '1801', 4, 'messageLog', 'system/messageLog/index', NULL, 0, 1, 'C', '1', '1', 'system:messageLog:list', 'history', 103, 1, sysdate(), 1, sysdate(), '消息发送记录菜单');
insert into sys_menu values('1831', '消息发送记录查询', '1830', 1, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageLog:query', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1832', '消息发送记录删除', '1830', 4, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageLog:remove', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1833', '消息发送记录导出', '1830', 5, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:messageLog:export', '#', 103, 1, sysdate(), NULL, NULL, '');

-- 字典类型
insert into sys_dict_type values(12, '000000', '消息类型', 'sys_message_type', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_type values(13, '000000', '消息支持平台', 'sys_message_supplier_type', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_type values(14, '000000', '消息模板类型', 'sys_message_template_mode', '1', 103, 1, sysdate(), 1, sysdate(), NULL);

-- 字典数据
insert into sys_dict_data values(34, '000000', 0, '短信', 'SMS', 'sys_message_type', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(35, '000000', 1, '邮箱', 'MAIL', 'sys_message_type', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(36, '000000', 1, '阿里云短信', 'ALIBABA', 'sys_message_supplier_type', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(37, '000000', 2, '华为云短信', 'HUAWEI', 'sys_message_supplier_type', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(38, '000000', 3, '腾讯云短信', 'TENCENT', 'sys_message_supplier_type', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(39, '000000', 4, '云片短信', 'YUNPIAN', 'sys_message_supplier_type', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(40, '000000', 5, '合一短信', 'UNI_SMS', 'sys_message_supplier_type', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(41, '000000', 6, '京东云短信', 'JD_CLOUD', 'sys_message_supplier_type', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(42, '000000', 7, '容联云短信', 'CLOOPEN', 'sys_message_supplier_type', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(43, '000000', 8, '亿美软通短信', 'EMAY', 'sys_message_supplier_type', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(44, '000000', 9, '天翼云短信', 'CTYUN', 'sys_message_supplier_type', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(45, '000000', 0, '邮箱', 'MAIL', 'sys_message_supplier_type', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(46, '000000', 10, '网易云短信', 'NETEASE', 'sys_message_supplier_type', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(47, '000000', 0, '模板ID', 'TEMPLATE_ID', 'sys_message_template_mode', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
insert into sys_dict_data values(48, '000000', 1, '模板内容', 'TEMPLATE_CONTENT', 'sys_message_template_mode', NULL, 'primary', 'N', '1', 103, 1, sysdate(), 1, sysdate(), NULL);
