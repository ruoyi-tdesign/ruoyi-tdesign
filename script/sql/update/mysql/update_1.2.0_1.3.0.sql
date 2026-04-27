insert into sys_dict_data values(61, '000000', 14, '七牛云短信', 'QI_NIU', 'sys_message_supplier_type', null, 'primary', null, 'N', 103, 1, sysdate(), 1, sysdate(), null);

alter table sys_menu
add column hidden_expression varchar(255) null comment '隐藏表达式' after icon,
add column shop_expression varchar(255) null comment '停用表达式' after hidden_expression;

UPDATE sys_menu SET shop_expression = 'getProperty(\'spring.profiles.active\') != \'dev\'' WHERE menu_id = 3;
UPDATE sys_menu SET shop_expression = 'getProperty(\'spring.profiles.active\') != \'dev\'' WHERE menu_id = 4;
UPDATE sys_menu SET shop_expression = '!getProperty(\'tenant.enable\')' WHERE menu_id = 6;
UPDATE sys_menu SET shop_expression = '!getProperty(\'spring.boot.admin.client.enabled\')' WHERE menu_id = 117;
UPDATE sys_menu SET shop_expression = '!getProperty(\'powerjob.worker.enabled\')' WHERE menu_id = 120;

ALTER TABLE sys_oss_rule
ADD COLUMN rule_sort int NOT NULL DEFAULT 0 COMMENT '规则顺序' AFTER status;

ALTER TABLE sys_logininfor
ADD COLUMN user_id bigint NULL COMMENT '用户id' AFTER tenant_id;

ALTER TABLE sys_oper_log
ADD COLUMN user_id bigint NULL COMMENT '用户id' AFTER tenant_id;

-- 如果启用PowerJob则执行以下脚本
-- Upgrade PowerJob SQL FROM 4.3.7 to 4.3.8
-- ----------------------------
-- Table change for pj_job_info
-- ----------------------------
-- alter table pj_job_info add dispatch_strategy_config varchar(255) comment 'dispatch_strategy_config' default null;
-- alter table pj_job_info add advanced_runtime_config varchar(255) comment 'advanced_runtime_config' default null;
