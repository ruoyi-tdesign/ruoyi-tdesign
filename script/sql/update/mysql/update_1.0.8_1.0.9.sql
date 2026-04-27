-- 新增菜单
insert into sys_menu values('1140', '公告导出', '107', '5', '#', '', '', 0, 1, 'F', '1', '1', 'system:notice:export',       '#', 103, 1, sysdate(), null, null, '');

-- 更新设备类型
update sys_client set device_type = 'android' where device_type = 'app';
alter table sys_dict_type drop column status;
alter table sys_dict_data drop column status;
delete from sys_dict_data where dict_type = 'sys_device_type';
insert into sys_dict_data values(54, '000000', 0,  'PC',    'pc',         'sys_device_type',     '',   'default', 'N', 103, 1, sysdate(), null, null, 'PC');
insert into sys_dict_data values(55, '000000', 0,  '安卓', 'android',     'sys_device_type',     '',   'default', 'N', 103, 1, sysdate(), null, null, '安卓');
insert into sys_dict_data values(56, '000000', 0,  'iOS', 'ios',          'sys_device_type',     '',   'default', 'N', 103, 1, sysdate(), null, null, 'iOS');
insert into sys_dict_data values(57, '000000', 0,  '小程序', 'xcx',       'sys_device_type',     '',   'default', 'N', 103, 1, sysdate(), null, null, '小程序');

-- 变更设备leader字段类型
update sys_dept set leader = null;
alter table sys_dept modify leader bigint null comment '负责人';
update sys_dept set leader = 1 where dept_id = 103;
