-- OSS配置新增自动创建桶字段 update by 2023-07-05
ALTER TABLE sys_oss_config ADD COLUMN create_bucket tinyint(1) NOT NULL DEFAULT '0' COMMENT '创建桶（1=是,0=否）' AFTER status;

-- 代码生成新增排序字段 update by 2023-07-07
ALTER TABLE gen_table_column ADD COLUMN is_sort char(1) NULL COMMENT '是否排序字段（1是）' AFTER is_detail;
-- 新增部门、菜单导出 update by 2023-07-07
insert into sys_menu values('1117', '菜单导出', '102', '5',  '', '', '', 0, 1, 'F', '1', '1', 'system:menu:export',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1121', '部门导出', '103', '5',  '', '', '', 0, 1, 'F', '1', '1', 'system:dept:export',         '#', 103, 1, sysdate(), null, null, '');
