-- 修改sys_oss表结构
alter table sys_oss
add column content_type varchar(255) null comment '内容类型' after size,
add column oss_category_id bigint not null default 0 comment '分类id' after content_type,
add column user_type varchar(20) not null default '' comment '用户类型' after oss_category_id,
add column is_lock tinyint(1) not null default 0 comment '是否锁定状态' after user_type;
alter table sys_oss
add index idx_user(create_by, user_type, create_time) comment '用户索引',
add index idx_oss_category_id(oss_category_id) comment '分类索引';

-- ----------------------------
-- OSS分类表
-- ----------------------------
drop table if exists sys_oss_category;
create table sys_oss_category  (
  oss_category_id   bigint(20)      not null                    comment 'oss分类id',
  category_name     varchar(255)    not null                    comment '分类名称',
  parent_id         bigint(20)      not null                    comment '父级分类id',
  category_path     varchar(2000)   not null                    comment '分类路径',
  level             int(11)         not null                    comment '层级',
  order_num         int(11)         not null                    comment '显示顺序',
  user_type         varchar(20)     not null                    comment '用户类型',
  create_by         bigint(20)      not null                    comment '上传人',
  update_time       datetime        null        default null    comment '更新时间',
  create_time       datetime        not null                    comment '创建时间',
  primary key (oss_category_id) using btree,
  index idx_user(create_by, user_type, order_num) using btree
) engine = innodb comment = 'oss分类表';

-- 系统菜单
insert into sys_menu values('1531', '我的文件', '1510', 4, 'ossCategory', 'system/ossCategory/index', NULL, 0, 1, 'C', '1', '1', 'system:ossCategory:list', 'folder-open', 103, 1, sysdate(), 1, sysdate(), '我的文件菜单');
insert into sys_menu values('1532', 'OSS分类查询', '1531', 1, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:ossCategory:query', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1533', 'OSS分类新增', '1531', 2, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:ossCategory:add', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1534', 'OSS分类修改', '1531', 3, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:ossCategory:edit', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1535', 'OSS分类删除', '1531', 4, '#', '', NULL, 0, 1, 'F', '1', '1', 'system:ossCategory:remove', '#', 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1604', '文件修改', '118', 5, '', NULL, NULL, 0, 1, 'F', '1', '1', 'system:oss:edit', '#', 103, 1, sysdate(), 1, sysdate(), '');
update sys_menu set icon = 'tree-square-dot' where menu_id = 103;
update sys_menu set icon = 'book' where menu_id = 105;
update sys_menu set icon = 'backup' where menu_id = 118;
