ALTER TABLE sys_dept ADD (dept_category varchar2(100) DEFAULT NULL);
COMMENT ON COLUMN sys_dept.dept_category IS '部门类别编码';
ALTER TABLE sys_post ADD (dept_id number(20) NOT NULL);
COMMENT ON COLUMN sys_post.dept_id IS '部门id';
ALTER TABLE sys_post ADD (post_category VARCHAR2(100) DEFAULT NULL);
COMMENT ON COLUMN sys_post.post_category IS '岗位类别编码';
UPDATE sys_post SET dept_id = 100;
UPDATE sys_post SET dept_id = 103 where post_id = 1;
delete from sys_menu where menu_id in (120,114);
insert into sys_menu values('120',  '任务调度中心',  '2',    '6', 'snailjob',           'monitor/snailjob/index', null,        '', 0, 1, 'C', '1', '1', 'monitor:snailjob:list', 'video',           null, '!getProperty(''snail-job.enabled'')', 103, 1, sysdate, null, null, 'snailjob控制台菜单');
UPDATE sys_menu SET menu_name = '文件管理' WHERE menu_id = 1510;

-- 删除消息类型、消息支持平台字典
delete from sys_dict_type where dict_type in ('sys_message_type', 'sys_message_supplier_type');
delete from sys_dict_data where dict_type in ('sys_message_type', 'sys_message_supplier_type');
