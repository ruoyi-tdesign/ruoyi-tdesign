UPDATE sys_menu SET path = 'workflow', icon = 'tree-round-dot-vertical' WHERE menu_id = 11616;
UPDATE sys_menu SET path = 'task', icon = 'task' WHERE menu_id = 11618;
UPDATE sys_menu SET path = 'taskWaiting', icon = 'task-visible' WHERE menu_id = 11619;
UPDATE sys_menu SET path = 'processDefinition', icon = 'system-components' WHERE menu_id = 11620;
UPDATE sys_menu SET path = 'processInstance', icon = 'tree-round-dot' WHERE menu_id = 11621;
UPDATE sys_menu SET path = 'category', icon = 'app' WHERE menu_id = 11622;
UPDATE sys_menu SET path = 'myDocument', icon = 'send' WHERE menu_id = 11629;
UPDATE sys_menu SET path = 'monitor', icon = 'chart-analytics' WHERE menu_id = 11630;
UPDATE sys_menu SET path = 'allTaskWaiting', icon = 'task-visible' WHERE menu_id = 11631;
UPDATE sys_menu SET path = 'taskFinish', icon = 'task-checked' WHERE menu_id = 11632;
UPDATE sys_menu SET path = 'taskCopyList', icon = 'user-arrow-left' WHERE menu_id = 11633;

-- ----------------------------
-- 文件记录表
-- ----------------------------
DROP TABLE IF EXISTS sys_file;
CREATE TABLE sys_file  (
  file_id bigint NOT NULL COMMENT '文件id',
  tenant_id varchar(20) NULL DEFAULT '000000' COMMENT '租户编号',
  url varchar(512) NOT NULL COMMENT '文件访问地址',
  size bigint NULL DEFAULT NULL COMMENT '文件大小，单位字节',
  filename varchar(256) NULL DEFAULT NULL COMMENT '文件名称',
  original_filename varchar(256) NULL DEFAULT NULL COMMENT '原始文件名',
  base_path varchar(256) NULL DEFAULT NULL COMMENT '基础存储路径',
  path varchar(256) NULL DEFAULT NULL COMMENT '存储路径',
  ext varchar(32) NULL DEFAULT NULL COMMENT '文件扩展名',
  content_type varchar(128) NULL DEFAULT NULL COMMENT 'MIME类型',
  storage_config_id bigint NULL DEFAULT NULL COMMENT '存储配置id',
  th_url varchar(512) NULL DEFAULT NULL COMMENT '缩略图访问路径',
  th_filename varchar(256) NULL DEFAULT NULL COMMENT '缩略图名称',
  th_size bigint NULL DEFAULT NULL COMMENT '缩略图大小，单位字节',
  th_content_type varchar(128) NULL DEFAULT NULL COMMENT '缩略图MIME类型',
  object_id varchar(32) NULL DEFAULT NULL COMMENT '文件所属对象id',
  object_type varchar(32) NULL DEFAULT NULL COMMENT '文件所属对象类型，例如用户头像，评价图片',
  metadata text NULL COMMENT '文件元数据',
  user_metadata text NULL COMMENT '文件用户元数据',
  th_metadata text NULL COMMENT '缩略图元数据',
  th_user_metadata text NULL COMMENT '缩略图用户元数据',
  attr text NULL COMMENT '附加属性',
  file_acl varchar(32) NULL DEFAULT NULL COMMENT '文件ACL',
  th_file_acl varchar(32) NULL DEFAULT NULL COMMENT '缩略图文件ACL',
  hash_info text NULL COMMENT '哈希信息',
  upload_id varchar(128) NULL DEFAULT NULL COMMENT '上传ID，仅在手动分片上传时使用',
  upload_status int NULL DEFAULT NULL COMMENT '上传状态，仅在手动分片上传时使用，1：初始化完成，2：上传完成',
  file_category_id bigint NOT NULL DEFAULT 0 COMMENT '分类id',
  user_type varchar(20) NOT NULL DEFAULT '' COMMENT '用户类型',
  is_lock tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否锁定状态',
  create_dept bigint NULL DEFAULT NULL COMMENT '创建部门',
  update_by bigint NULL DEFAULT NULL COMMENT '更新人',
  create_by bigint NULL DEFAULT NULL COMMENT '上传人',
  update_time datetime NULL DEFAULT NULL COMMENT '更新时间',
  create_time datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (file_id) USING BTREE,
  INDEX idx_filename(filename ASC) USING BTREE
) ENGINE = InnoDB COMMENT = '文件记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 文件分类表
-- ----------------------------
DROP TABLE IF EXISTS sys_file_category;
CREATE TABLE sys_file_category  (
  file_category_id bigint NOT NULL COMMENT '文件分类id',
  category_name varchar(255) NOT NULL COMMENT '分类名称',
  parent_id bigint NOT NULL COMMENT '父级分类id',
  category_path varchar(2000) NOT NULL COMMENT '分类路径',
  level int NOT NULL COMMENT '层级',
  order_num int NOT NULL COMMENT '显示顺序',
  login_type varchar(20) NOT NULL COMMENT '用户类型',
  create_by bigint NOT NULL COMMENT '上传人',
  update_time datetime NULL DEFAULT NULL COMMENT '更新时间',
  create_time datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (file_category_id) USING BTREE,
  INDEX idx_user(create_by ASC, login_type ASC, order_num ASC) USING BTREE
) ENGINE = InnoDB COMMENT = '文件分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 文件分片信息表，仅在手动分片上传时使用
-- ----------------------------
DROP TABLE IF EXISTS sys_file_part;
CREATE TABLE sys_file_part  (
  file_part_id bigint NOT NULL COMMENT '分片id',
  storage_config_id bigint NULL DEFAULT NULL COMMENT '存储配置id',
  upload_id varchar(128) NULL DEFAULT NULL COMMENT '上传ID，仅在手动分片上传时使用',
  e_tag varchar(255) NULL DEFAULT NULL COMMENT '分片 ETag',
  part_number int NULL DEFAULT NULL COMMENT '分片号。每一个上传的分片都有一个分片号，一般情况下取值范围是1~10000',
  part_size bigint NULL DEFAULT NULL COMMENT '文件大小，单位字节',
  hash_info text NULL COMMENT '哈希信息',
  create_dept bigint NULL DEFAULT NULL COMMENT '创建部门',
  update_by bigint NULL DEFAULT NULL COMMENT '更新人',
  create_by bigint NULL DEFAULT NULL COMMENT '上传人',
  update_time datetime NULL DEFAULT NULL COMMENT '更新时间',
  create_time datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (file_part_id) USING BTREE
) ENGINE = InnoDB COMMENT = '文件分片信息表，仅在手动分片上传时使用' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 存储配置
-- ----------------------------
DROP TABLE IF EXISTS sys_storage_config;
CREATE TABLE sys_storage_config  (
  storage_config_id bigint NOT NULL COMMENT '主建',
  tenant_id varchar(20) NULL DEFAULT '000000' COMMENT '租户编号',
  name varchar(255) NOT NULL COMMENT '配置名称',
  platform varchar(50) NOT NULL COMMENT '平台',
  weight int NOT NULL COMMENT '负载均衡权重',
  status tinyint(1) NOT NULL COMMENT '启用状态',
  config_json varchar(2000) NOT NULL COMMENT '配置json',
  request_mode varchar(255) NULL DEFAULT NULL COMMENT '请求模式 proxy：代理转发请求 direct：源地址重定向请求 direct_signature：预签名重定向请求',
  del_flag char(1) NULL DEFAULT '0' COMMENT '删除标志',
  create_dept bigint NULL DEFAULT NULL COMMENT '创建部门',
  create_by bigint NULL DEFAULT NULL COMMENT '创建者',
  create_time datetime NULL DEFAULT NULL COMMENT '创建时间',
  update_by bigint NULL DEFAULT NULL COMMENT '更新者',
  update_time datetime NULL DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (storage_config_id) USING BTREE
) ENGINE = InnoDB COMMENT = '存储配置' ROW_FORMAT = Dynamic;

UPDATE sys_menu SET menu_name = '文件管理（旧）' WHERE menu_id = 1510;

INSERT INTO sys_menu VALUES (1899, '文件管理', 1, 10, 'storage', NULL, NULL, NULL, 0, 1, 'M', '1', '1', NULL, 'cloud', NULL, NULL, 103, 1, sysdate(), 1, sysdate(), '');
INSERT INTO sys_menu VALUES (1900, '存储配置', 1899, 1, 'storageConfig', 'system/storageConfig/index', 'StorageConfig', '', 0, 1, 'C', '1', '1', 'system:storageConfig:list', 'server', NULL, NULL, 103, 1, sysdate(), 1, sysdate(), '存储配置菜单');
INSERT INTO sys_menu VALUES (1901, '存储配置查询', 1900, 1, '#', '', NULL, '', 0, 1, 'F', '1', '1', 'system:storageConfig:query', '#', NULL, NULL, 103, 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES (1902, '存储配置新增', 1900, 2, '#', '', NULL, '', 0, 1, 'F', '1', '1', 'system:storageConfig:add', '#', NULL, NULL, 103, 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES (1903, '存储配置修改', 1900, 3, '#', '', NULL, '', 0, 1, 'F', '1', '1', 'system:storageConfig:edit', '#', NULL, NULL, 103, 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES (1904, '存储配置删除', 1900, 4, '#', '', NULL, '', 0, 1, 'F', '1', '1', 'system:storageConfig:remove', '#', NULL, NULL, 103, 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES (1905, '存储配置导出', 1900, 5, '#', '', NULL, '', 0, 1, 'F', '1', '1', 'system:storageConfig:export', '#', NULL, NULL, 103, 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES (1910, '我的文件', 1899, 2, 'myFile', 'system/myFile/index', 'MyFile', NULL, 0, 1, 'C', '1', '1', '', 'folder-open', NULL, NULL, 103, 1, sysdate(), 1, sysdate(), '');
INSERT INTO sys_menu VALUES (1920, '文件管理', 1899, 3, 'file', 'system/file/index', 'File', NULL, 0, 1, 'C', '1', '1', 'system:file:list', 'sd-card-1', NULL, NULL, 103, 1, sysdate(), 1, sysdate(), '');
INSERT INTO sys_menu VALUES (1921, '文件查询', 1920, 1, '', NULL, NULL, NULL, 0, 1, 'F', '1', '1', 'system:file:query', '#', NULL, NULL, 103, 1, sysdate(), 1, sysdate(), '');
INSERT INTO sys_menu VALUES (1922, '文件修改', 1920, 2, '', NULL, NULL, NULL, 0, 1, 'F', '1', '1', 'system:file:edit', '#', NULL, NULL, 103, 1, sysdate(), 1, sysdate(), '');
INSERT INTO sys_menu VALUES (1923, '文件删除', 1920, 3, '', NULL, NULL, NULL, 0, 1, 'F', '1', '1', 'system:file:remove', '#', NULL, NULL, 103, 1, sysdate(), 1, sysdate(), '');
INSERT INTO sys_menu VALUES (1924, '文件加锁解锁', 1920, 4, '', NULL, NULL, NULL, 0, 1, 'F', '1', '1', 'system:file:lock', '#', NULL, NULL, 103, 1, sysdate(), 1, sysdate(), '');
INSERT INTO sys_menu VALUES (1925, '文件下载', 1920, 5, '', NULL, NULL, NULL, 0, 1, 'F', '1', '1', 'system:file:download', '#', NULL, NULL, 103, 1, sysdate(), 1, sysdate(), '');
INSERT INTO sys_menu VALUES (1926, '文件上传', 1920, 6, '', NULL, NULL, NULL, 0, 1, 'F', '1', '1', 'system:file:upload', '#', NULL, NULL, 103, 1, sysdate(), 1, sysdate(), '');

INSERT INTO sys_dict_type VALUES (18, '000000', '业务状态', 'wf_business_status', 103, 1, sysdate(), NULL, NULL, '业务状态列表');
INSERT INTO sys_dict_type VALUES (19, '000000', '表单类型', 'wf_form_type', 103, 1, sysdate(), NULL, NULL, '表单类型列表');
INSERT INTO sys_dict_type VALUES (20, '000000', '任务状态', 'wf_task_status', 103, 1, sysdate(), NULL, NULL, '任务状态');
INSERT INTO sys_dict_type VALUES (21, '000000', '存储配置请求模式', 'sys_storage_request_mode', 103, 1, sysdate(), 1, sysdate(), '存储配置');

INSERT INTO sys_dict_data VALUES (71, '000000', 1, '已撤销', 'cancel', 'wf_business_status', '', 'danger', '', 'N', 103, 1, sysdate(), NULL, NULL, '已撤销');
INSERT INTO sys_dict_data VALUES (72, '000000', 2, '草稿', 'draft', 'wf_business_status', '', 'default', '', 'N', 103, 1, sysdate(), NULL, NULL, '草稿');
INSERT INTO sys_dict_data VALUES (73, '000000', 3, '待审核', 'waiting', 'wf_business_status', '', 'primary', '', 'N', 103, 1, sysdate(), NULL, NULL, '待审核');
INSERT INTO sys_dict_data VALUES (74, '000000', 4, '已完成', 'finish', 'wf_business_status', '', 'success', '', 'N', 103, 1, sysdate(), NULL, NULL, '已完成');
INSERT INTO sys_dict_data VALUES (75, '000000', 5, '已作废', 'invalid', 'wf_business_status', '', 'danger', '', 'N', 103, 1, sysdate(), NULL, NULL, '已作废');
INSERT INTO sys_dict_data VALUES (76, '000000', 6, '已退回', 'back', 'wf_business_status', '', 'danger', '', 'N', 103, 1, sysdate(), NULL, NULL, '已退回');
INSERT INTO sys_dict_data VALUES (77, '000000', 7, '已终止', 'termination', 'wf_business_status', '', 'danger', '', 'N', 103, 1, sysdate(), NULL, NULL, '已终止');
INSERT INTO sys_dict_data VALUES (78, '000000', 1, '自定义表单', 'static', 'wf_form_type', '', 'success', '', 'N', 103, 1, sysdate(), NULL, NULL, '自定义表单');
INSERT INTO sys_dict_data VALUES (79, '000000', 2, '动态表单', 'dynamic', 'wf_form_type', '', 'primary', '', 'N', 103, 1, sysdate(), NULL, NULL, '动态表单');
INSERT INTO sys_dict_data VALUES (80, '000000', 1, '撤销', 'cancel', 'wf_task_status', '', 'danger', '', 'N', 103, 1, sysdate(), NULL, NULL, '撤销');
INSERT INTO sys_dict_data VALUES (81, '000000', 2, '通过', 'pass', 'wf_task_status', '', 'success', '', 'N', 103, 1, sysdate(), NULL, NULL, '通过');
INSERT INTO sys_dict_data VALUES (82, '000000', 3, '待审核', 'waiting', 'wf_task_status', '', 'primary', '', 'N', 103, 1, sysdate(), NULL, NULL, '待审核');
INSERT INTO sys_dict_data VALUES (83, '000000', 4, '作废', 'invalid', 'wf_task_status', '', 'danger', '', 'N', 103, 1, sysdate(), NULL, NULL, '作废');
INSERT INTO sys_dict_data VALUES (84, '000000', 5, '退回', 'back', 'wf_task_status', '', 'danger', '', 'N', 103, 1, sysdate(), NULL, NULL, '退回');
INSERT INTO sys_dict_data VALUES (85, '000000', 6, '终止', 'termination', 'wf_task_status', '', 'danger', '', 'N', 103, 1, sysdate(), NULL, NULL, '终止');
INSERT INTO sys_dict_data VALUES (86, '000000', 7, '转办', 'transfer', 'wf_task_status', '', 'primary', '', 'N', 103, 1, sysdate(), NULL, NULL, '转办');
INSERT INTO sys_dict_data VALUES (87, '000000', 8, '委托', 'depute', 'wf_task_status', '', 'primary', '', 'N', 103, 1, sysdate(), NULL, NULL, '委托');
INSERT INTO sys_dict_data VALUES (88, '000000', 9, '抄送', 'copy', 'wf_task_status', '', 'primary', '', 'N', 103, 1, sysdate(), NULL, NULL, '抄送');
INSERT INTO sys_dict_data VALUES (89, '000000', 10, '加签', 'sign', 'wf_task_status', '', 'primary', '', 'N', 103, 1, sysdate(), NULL, NULL, '加签');
INSERT INTO sys_dict_data VALUES (90, '000000', 11, '减签', 'sign_off', 'wf_task_status', '', 'danger', '', 'N', 103, 1, sysdate(), NULL, NULL, '减签');
INSERT INTO sys_dict_data VALUES (91, '000000', 11, '超时', 'timeout', 'wf_task_status', '', 'danger', '', 'N', 103, 1, sysdate(), NULL, NULL, '超时');
INSERT INTO sys_dict_data VALUES (100, '000000', 0, '代理转发请求', 'proxy', 'sys_storage_request_mode', NULL, 'primary', NULL, 'N', 103, 1, sysdate(), 1, sysdate(), NULL);
INSERT INTO sys_dict_data VALUES (101, '000000', 1, '源地址重定向请求', 'direct', 'sys_storage_request_mode', NULL, 'primary', NULL, 'N', 103, 1, sysdate(), 1, sysdate(), NULL);
INSERT INTO sys_dict_data VALUES (102, '000000', 2, '预签名重定向请求', 'direct_signature', 'sys_storage_request_mode', NULL, 'primary', NULL, 'N', 103, 1, sysdate(), 1, sysdate(), NULL);
