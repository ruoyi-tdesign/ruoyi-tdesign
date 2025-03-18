ALTER TABLE sys_dept ADD dept_category VARCHAR(100) DEFAULT NULL COMMENT '部门类别编码' AFTER dept_name;
ALTER TABLE sys_post ADD dept_id BIGINT(20) NOT NULL COMMENT '部门id' AFTER tenant_id, ADD post_category VARCHAR(100) DEFAULT NULL COMMENT '岗位类别编码' AFTER post_code;
UPDATE sys_post SET dept_id = 100;
UPDATE sys_post SET dept_id = 103 where post_id = 1;
delete from sys_menu where menu_id in (120,114);
insert into sys_menu values('120',  '任务调度中心',  '2',   '6',  'snailjob',     'monitor/snailjob/index', null,        '', 0, 1, 'C', '1', '1', 'monitor:snailjob:list', 'video',           null, '!getProperty(''snail-job.enabled'')', 103, 1, sysdate(), null, null, 'SnailJob控制台菜单');
UPDATE sys_menu SET menu_name = '文件管理' WHERE menu_id = 1510;

-- 删除消息类型、消息支持平台字典
delete from sys_dict_type where dict_type in ('sys_message_type', 'sys_message_supplier_type');
delete from sys_dict_data where dict_type in ('sys_message_type', 'sys_message_supplier_type');

ALTER TABLE sys_oss_config DROP COLUMN create_bucket;

ALTER TABLE sys_social ALTER COLUMN tenant_id SET DEFAULT '000000';

ALTER TABLE sys_oper_log
MODIFY COLUMN oper_param varchar(4000) NULL DEFAULT '' COMMENT '请求参数' AFTER oper_location,
MODIFY COLUMN json_result varchar(4000) NULL DEFAULT '' COMMENT '返回参数' AFTER oper_param,
MODIFY COLUMN error_msg varchar(4000) NULL DEFAULT '' COMMENT '错误消息' AFTER status;
