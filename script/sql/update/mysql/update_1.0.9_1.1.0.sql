-- 新增字典回显风格
ALTER TABLE sys_dict_data
    ADD COLUMN tag_style varchar(50) NULL COMMENT '回显风格' AFTER list_class;
UPDATE sys_dict_data SET list_class='text' WHERE list_class = 'default';
UPDATE sys_dict_data SET list_class='primary', tag_style='light-outline' WHERE dict_type = 'sys_grant_type';

insert into sys_dict_data values(58, '000000', 11, '助通短信', 'ZHUTONG', 'sys_message_supplier_type', null, 'primary', '', 'N', 103, 1, sysdate(), 1, sysdate(), null);

ALTER TABLE sys_message_log
DROP COLUMN error_code,
DROP COLUMN error_message,
DROP COLUMN biz_id,
DROP COLUMN message,
ADD COLUMN response_body varchar(1000) NULL COMMENT '返回主体消息' AFTER is_success;

ALTER TABLE sys_logininfor
    ADD COLUMN client_key VARCHAR(32)  NULL DEFAULT NULL COMMENT '客户端' AFTER status,
    ADD COLUMN device_type VARCHAR(32) NULL DEFAULT NULL COMMENT '设备类型' AFTER client_key;

UPDATE sys_dict_data SET list_class='primary' WHERE dict_type = 'sys_device_type';

-- ----------------------------
-- 租户套餐和菜单关联表
-- ----------------------------
CREATE TABLE sys_tenant_package_menu  (
  package_id    bigint NOT NULL COMMENT '租户套餐id',
  menu_id       bigint NOT NULL COMMENT '菜单id',
  PRIMARY KEY (package_id, menu_id) USING BTREE
) ENGINE = InnoDB COMMENT = '租户套餐和菜单关联表';

insert into sys_tenant_package_menu select t1.package_id, t2.menu_id from sys_menu t2 join sys_tenant_package t1 on FIND_IN_SET(t2.menu_id, REPLACE(t1.menu_ids, ', ', ','));

alter table sys_tenant_package drop column menu_ids;
