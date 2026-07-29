-- 字典不再支持多租户操作，转为通用表。删除其他租户的字典与数据 (若想继续保持字典多租户，请忽略此提交更新)
DELETE FROM sys_dict_type WHERE tenant_id != '000000';
DELETE FROM sys_dict_data WHERE tenant_id != '000000';

-- 修改应用表名为租户应用管理表
ALTER TABLE sys_app RENAME TO sys_tenant_app;
ALTER TABLE sys_tenant_app COMMENT = '租户应用管理表';

UPDATE sys_menu SET menu_name = '租户应用管理', path = 'tenantApp', component = 'system/tenantApp/index', component_name = 'TenantApp', perms = 'system:tenantApp:list', remark = '租户应用管理菜单' WHERE menu_id = 1701;
UPDATE sys_menu SET menu_name = '租户应用管理查询', perms = 'system:tenantApp:query' WHERE menu_id = 1702;
UPDATE sys_menu SET menu_name = '租户应用管理新增', perms = 'system:tenantApp:add' WHERE menu_id = 1703;
UPDATE sys_menu SET menu_name = '租户应用管理修改', perms = 'system:tenantApp:edit' WHERE menu_id = 1704;
UPDATE sys_menu SET menu_name = '租户应用管理删除', perms = 'system:tenantApp:remove' WHERE menu_id = 1705;
UPDATE sys_menu SET menu_name = '租户应用管理导出', perms = 'system:tenantApp:export' WHERE menu_id = 1706;

-- ----------------------------
-- 敏感词表
-- ----------------------------
create table sys_sensitive_word  (
  word_id       bigint          not null            comment '敏感词id',
  word          varchar(255)    not null            comment '敏感词',
  category      varchar(20)     not null            comment '敏感词类别',
  description   varchar(500)    null default null   comment '描述',
  status        tinyint         not null            comment '启用状态 1启用 0禁用',
  create_dept   bigint          null default null   comment '创建部门',
  create_by     bigint          null default null   comment '创建者',
  update_by     bigint          null default null   comment '更新者',
  update_time   datetime        null default null   comment '更新时间',
  create_time   datetime        not null            comment '创建时间',
  primary key (word_id) using btree
) engine = innodb comment = '敏感词表';

-- 敏感词菜单
insert into sys_menu values ('124', '敏感词', '1', '13', 'sensitiveWord', 'system/sensitiveWord/index', 'SensitiveWord', '', 0, 1, 'C', '1', '1', 'system:sensitiveWord:list', 'book-open', null, null, 103, 1, sysdate(), 1, sysdate(), '敏感词菜单');
insert into sys_menu values ('1710', '敏感词查询', '124', 1, '#', '', NULL, '', 0, 1, 'F', '1', '1', 'system:sensitiveWord:query', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1711', '敏感词新增', '124', 2, '#', '', null, '', 0, 1, 'F', '1', '1', 'system:sensitiveWord:add', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1712', '敏感词修改', '124', 3, '#', '', null, '', 0, 1, 'F', '1', '1', 'system:sensitiveWord:edit', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1713', '敏感词删除', '124', 4, '#', '', null, '', 0, 1, 'F', '1', '1', 'system:sensitiveWord:remove', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1714', '敏感词导出', '124', 5, '#', '', null, '', 0, 1, 'F', '1', '1', 'system:sensitiveWord:export', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1715', '敏感词导入', '124', 6, '#', '', null, '', 0, 1, 'F', '1', '1', 'system:sensitiveWord:import', '#', null, null, 103, 1, sysdate(), null, null, '');

-- 敏感词字典
insert into sys_dict_type values(17, '000000', '敏感词类别', 'sensitive_words_category', 103, 1, sysdate(), 1, sysdate(), null);
-- 敏感词字典数据，需要其他类别自行添加
insert into sys_dict_data values(70, '000000', 99, '其他', 'other', 'sensitive_words_category', null, 'primary', null, 'N', 103, 1, sysdate(), 1, sysdate(), null);
-- 管理员权限关联自行添加
