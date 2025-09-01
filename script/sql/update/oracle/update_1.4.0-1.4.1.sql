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
DROP TABLE sys_file;
CREATE TABLE sys_file (
  file_id NUMBER(20) NOT NULL,
  tenant_id NVARCHAR2(20),
  url NVARCHAR2(512) NOT NULL,
  "size" NUMBER(20),
  filename NVARCHAR2(256),
  original_filename NVARCHAR2(256),
  base_path NVARCHAR2(256),
  path NVARCHAR2(256),
  ext NVARCHAR2(32),
  content_type NVARCHAR2(128),
  storage_config_id NUMBER(20),
  th_url NVARCHAR2(512),
  th_filename NVARCHAR2(256),
  th_size NUMBER(20),
  th_content_type NVARCHAR2(128),
  object_id NVARCHAR2(32),
  object_type NVARCHAR2(32),
  metadata NCLOB,
  user_metadata NCLOB,
  th_metadata NCLOB,
  th_user_metadata NCLOB,
  attr NCLOB,
  file_acl NVARCHAR2(32),
  th_file_acl NVARCHAR2(32),
  hash_info NCLOB,
  upload_id NVARCHAR2(128),
  upload_status NUMBER(11),
  file_category_id NUMBER(20) NOT NULL,
  user_type NVARCHAR2(20) NOT NULL,
  is_lock NUMBER(4) NOT NULL,
  create_dept NUMBER(20),
  update_by NUMBER(20),
  create_by NUMBER(20),
  update_time DATE,
  create_time DATE
);
ALTER TABLE sys_file ADD PRIMARY KEY (file_id);
CREATE INDEX idx_filename ON sys_file (filename ASC);
COMMENT ON COLUMN sys_file.file_id IS '文件id';
COMMENT ON COLUMN sys_file.tenant_id IS '租户编号';
COMMENT ON COLUMN sys_file.url IS '文件访问地址';
COMMENT ON COLUMN sys_file."size" IS '文件大小，单位字节';
COMMENT ON COLUMN sys_file.filename IS '文件名称';
COMMENT ON COLUMN sys_file.original_filename IS '原始文件名';
COMMENT ON COLUMN sys_file.base_path IS '基础存储路径';
COMMENT ON COLUMN sys_file.path IS '存储路径';
COMMENT ON COLUMN sys_file.ext IS '文件扩展名';
COMMENT ON COLUMN sys_file.content_type IS 'MIME类型';
COMMENT ON COLUMN sys_file.storage_config_id IS '存储配置id';
COMMENT ON COLUMN sys_file.th_url IS '缩略图访问路径';
COMMENT ON COLUMN sys_file.th_filename IS '缩略图名称';
COMMENT ON COLUMN sys_file.th_size IS '缩略图大小，单位字节';
COMMENT ON COLUMN sys_file.th_content_type IS '缩略图MIME类型';
COMMENT ON COLUMN sys_file.object_id IS '文件所属对象id';
COMMENT ON COLUMN sys_file.object_type IS '文件所属对象类型，例如用户头像，评价图片';
COMMENT ON COLUMN sys_file.metadata IS '文件元数据';
COMMENT ON COLUMN sys_file.user_metadata IS '文件用户元数据';
COMMENT ON COLUMN sys_file.th_metadata IS '缩略图元数据';
COMMENT ON COLUMN sys_file.th_user_metadata IS '缩略图用户元数据';
COMMENT ON COLUMN sys_file.attr IS '附加属性';
COMMENT ON COLUMN sys_file.file_acl IS '文件ACL';
COMMENT ON COLUMN sys_file.th_file_acl IS '缩略图文件ACL';
COMMENT ON COLUMN sys_file.hash_info IS '哈希信息';
COMMENT ON COLUMN sys_file.upload_id IS '上传ID，仅在手动分片上传时使用';
COMMENT ON COLUMN sys_file.upload_status IS '上传状态，仅在手动分片上传时使用，1：初始化完成，2：上传完成';
COMMENT ON COLUMN sys_file.file_category_id IS '分类id';
COMMENT ON COLUMN sys_file.user_type IS '用户类型';
COMMENT ON COLUMN sys_file.is_lock IS '是否锁定状态';
COMMENT ON COLUMN sys_file.create_dept IS '创建部门';
COMMENT ON COLUMN sys_file.update_by IS '更新人';
COMMENT ON COLUMN sys_file.create_by IS '上传人';
COMMENT ON COLUMN sys_file.update_time IS '更新时间';
COMMENT ON COLUMN sys_file.create_time IS '创建时间';
COMMENT ON TABLE sys_file IS '文件记录表';

-- ----------------------------
-- 文件分类表
-- ----------------------------
DROP TABLE sys_file_category;
CREATE TABLE sys_file_category (
  file_category_id NUMBER(20) NOT NULL,
  category_name NVARCHAR2(255) NOT NULL,
  parent_id NUMBER(20) NOT NULL,
  category_path NCLOB NOT NULL,
  "level" NUMBER(11) NOT NULL,
  order_num NUMBER(11) NOT NULL,
  login_type NVARCHAR2(20) NOT NULL,
  create_by NUMBER(20) NOT NULL,
  update_time DATE,
  create_time DATE NOT NULL
);
ALTER TABLE sys_file_category ADD PRIMARY KEY (file_category_id);
CREATE INDEX idx_user ON sys_file_category (create_by ASC, login_type ASC, order_num ASC);
COMMENT ON COLUMN sys_file_category.file_category_id IS '文件分类id';
COMMENT ON COLUMN sys_file_category.category_name IS '分类名称';
COMMENT ON COLUMN sys_file_category.parent_id IS '父级分类id';
COMMENT ON COLUMN sys_file_category.category_path IS '分类路径';
COMMENT ON COLUMN sys_file_category."level" IS '层级';
COMMENT ON COLUMN sys_file_category.order_num IS '显示顺序';
COMMENT ON COLUMN sys_file_category.login_type IS '用户类型';
COMMENT ON COLUMN sys_file_category.create_by IS '上传人';
COMMENT ON COLUMN sys_file_category.update_time IS '更新时间';
COMMENT ON COLUMN sys_file_category.create_time IS '创建时间';
COMMENT ON TABLE sys_file_category IS '文件分类表';

-- ----------------------------
-- 文件分片信息表，仅在手动分片上传时使用
-- ----------------------------
DROP TABLE sys_file_part;
CREATE TABLE sys_file_part (
  file_part_id NUMBER(20) NOT NULL,
  storage_config_id NUMBER(20),
  upload_id NVARCHAR2(128),
  e_tag NVARCHAR2(255),
  part_number NUMBER(11),
  part_size NUMBER(20),
  hash_info NCLOB,
  create_dept NUMBER(20),
  update_by NUMBER(20),
  create_by NUMBER(20),
  update_time DATE,
  create_time DATE
);
ALTER TABLE sys_file_part ADD PRIMARY KEY (file_part_id);
COMMENT ON COLUMN sys_file_part.file_part_id IS '分片id';
COMMENT ON COLUMN sys_file_part.storage_config_id IS '存储配置id';
COMMENT ON COLUMN sys_file_part.upload_id IS '上传ID，仅在手动分片上传时使用';
COMMENT ON COLUMN sys_file_part.e_tag IS '分片 ETag';
COMMENT ON COLUMN sys_file_part.part_number IS '分片号。每一个上传的分片都有一个分片号，一般情况下取值范围是1~10000';
COMMENT ON COLUMN sys_file_part.part_size IS '文件大小，单位字节';
COMMENT ON COLUMN sys_file_part.hash_info IS '哈希信息';
COMMENT ON COLUMN sys_file_part.create_dept IS '创建部门';
COMMENT ON COLUMN sys_file_part.update_by IS '更新人';
COMMENT ON COLUMN sys_file_part.create_by IS '上传人';
COMMENT ON COLUMN sys_file_part.update_time IS '更新时间';
COMMENT ON COLUMN sys_file_part.create_time IS '创建时间';
COMMENT ON TABLE sys_file_part IS '文件分片信息表，仅在手动分片上传时使用';

-- ----------------------------
-- 存储配置
-- ----------------------------
DROP TABLE sys_storage_config;
CREATE TABLE sys_storage_config (
  storage_config_id NUMBER(20) NOT NULL,
  tenant_id NVARCHAR2(20),
  name NVARCHAR2(255) NOT NULL,
  platform NVARCHAR2(50) NOT NULL,
  weight NUMBER(11) NOT NULL,
  status NUMBER(4) NOT NULL,
  config_json NCLOB NOT NULL,
  request_mode NVARCHAR2(255),
  del_flag NCHAR(1),
  create_dept NUMBER(20),
  create_by NUMBER(20),
  create_time DATE,
  update_by NUMBER(20),
  update_time DATE,
  remark NVARCHAR2(500)
);
ALTER TABLE sys_storage_config ADD PRIMARY KEY (storage_config_id);
COMMENT ON COLUMN sys_storage_config.storage_config_id IS '主建';
COMMENT ON COLUMN sys_storage_config.tenant_id IS '租户编号';
COMMENT ON COLUMN sys_storage_config.name IS '配置名称';
COMMENT ON COLUMN sys_storage_config.platform IS '平台';
COMMENT ON COLUMN sys_storage_config.weight IS '负载均衡权重';
COMMENT ON COLUMN sys_storage_config.status IS '启用状态';
COMMENT ON COLUMN sys_storage_config.config_json IS '配置json';
COMMENT ON COLUMN sys_storage_config.request_mode IS '请求模式 proxy：代理转发请求 direct：源地址重定向请求 direct_signature：预签名重定向请求';
COMMENT ON COLUMN sys_storage_config.del_flag IS '删除标志';
COMMENT ON COLUMN sys_storage_config.create_dept IS '创建部门';
COMMENT ON COLUMN sys_storage_config.create_by IS '创建者';
COMMENT ON COLUMN sys_storage_config.create_time IS '创建时间';
COMMENT ON COLUMN sys_storage_config.update_by IS '更新者';
COMMENT ON COLUMN sys_storage_config.update_time IS '更新时间';
COMMENT ON COLUMN sys_storage_config.remark IS '备注';
COMMENT ON TABLE sys_storage_config IS '存储配置';

UPDATE sys_menu SET menu_name = '文件管理（旧）' WHERE menu_id = 1510;

INSERT INTO sys_menu VALUES (1899, '文件管理', 1, 10, 'storage', NULL, NULL, NULL, 0, 1, 'M', '1', '1', NULL, 'cloud', NULL, NULL, 103, 1, sysdate, 1, sysdate, '');
INSERT INTO sys_menu VALUES (1900, '存储配置', 1899, 1, 'storageConfig', 'system/storageConfig/index', 'StorageConfig', '', 0, 1, 'C', '1', '1', 'system:storageConfig:list', 'server', NULL, NULL, 103, 1, sysdate, 1, sysdate, '存储配置菜单');
INSERT INTO sys_menu VALUES (1901, '存储配置查询', 1900, 1, '#', '', NULL, '', 0, 1, 'F', '1', '1', 'system:storageConfig:query', '#', NULL, NULL, 103, 1, sysdate, NULL, NULL, '');
INSERT INTO sys_menu VALUES (1902, '存储配置新增', 1900, 2, '#', '', NULL, '', 0, 1, 'F', '1', '1', 'system:storageConfig:add', '#', NULL, NULL, 103, 1, sysdate, NULL, NULL, '');
INSERT INTO sys_menu VALUES (1903, '存储配置修改', 1900, 3, '#', '', NULL, '', 0, 1, 'F', '1', '1', 'system:storageConfig:edit', '#', NULL, NULL, 103, 1, sysdate, NULL, NULL, '');
INSERT INTO sys_menu VALUES (1904, '存储配置删除', 1900, 4, '#', '', NULL, '', 0, 1, 'F', '1', '1', 'system:storageConfig:remove', '#', NULL, NULL, 103, 1, sysdate, NULL, NULL, '');
INSERT INTO sys_menu VALUES (1905, '存储配置导出', 1900, 5, '#', '', NULL, '', 0, 1, 'F', '1', '1', 'system:storageConfig:export', '#', NULL, NULL, 103, 1, sysdate, NULL, NULL, '');
INSERT INTO sys_menu VALUES (1910, '我的文件', 1899, 2, 'myFile', 'system/myFile/index', 'MyFile', NULL, 0, 1, 'C', '1', '1', '', 'folder-open', NULL, NULL, 103, 1, sysdate, 1, sysdate, '');
INSERT INTO sys_menu VALUES (1920, '文件管理', 1899, 3, 'file', 'system/file/index', 'File', NULL, 0, 1, 'C', '1', '1', 'system:file:list', 'sd-card-1', NULL, NULL, 103, 1, sysdate, 1, sysdate, '');
INSERT INTO sys_menu VALUES (1921, '文件查询', 1920, 1, '', NULL, NULL, NULL, 0, 1, 'F', '1', '1', 'system:file:query', '#', NULL, NULL, 103, 1, sysdate, 1, sysdate, '');
INSERT INTO sys_menu VALUES (1922, '文件修改', 1920, 2, '', NULL, NULL, NULL, 0, 1, 'F', '1', '1', 'system:file:edit', '#', NULL, NULL, 103, 1, sysdate, 1, sysdate, '');
INSERT INTO sys_menu VALUES (1923, '文件删除', 1920, 3, '', NULL, NULL, NULL, 0, 1, 'F', '1', '1', 'system:file:remove', '#', NULL, NULL, 103, 1, sysdate, 1, sysdate, '');
INSERT INTO sys_menu VALUES (1924, '文件加锁解锁', 1920, 4, '', NULL, NULL, NULL, 0, 1, 'F', '1', '1', 'system:file:lock', '#', NULL, NULL, 103, 1, sysdate, 1, sysdate, '');
INSERT INTO sys_menu VALUES (1925, '文件下载', 1920, 5, '', NULL, NULL, NULL, 0, 1, 'F', '1', '1', 'system:file:download', '#', NULL, NULL, 103, 1, sysdate, 1, sysdate, '');
INSERT INTO sys_menu VALUES (1926, '文件上传', 1920, 6, '', NULL, NULL, NULL, 0, 1, 'F', '1', '1', 'system:file:upload', '#', NULL, NULL, 103, 1, sysdate, 1, sysdate, '');


INSERT INTO sys_dict_type VALUES (18, '000000', '业务状态', 'wf_business_status', 103, 1, sysdate, NULL, NULL, '业务状态列表');
INSERT INTO sys_dict_type VALUES (19, '000000', '表单类型', 'wf_form_type', 103, 1, sysdate, NULL, NULL, '表单类型列表');
INSERT INTO sys_dict_type VALUES (20, '000000', '任务状态', 'wf_task_status', 103, 1, sysdate, NULL, NULL, '任务状态');
INSERT INTO sys_dict_type VALUES (21, '000000', '存储配置请求模式', 'sys_storage_request_mode', 103, 1, sysdate, 1, sysdate, '存储配置');

INSERT INTO sys_dict_data VALUES (71, '000000', 1, '已撤销', 'cancel', 'wf_business_status', '', 'danger', '', 'N', 103, 1, sysdate, NULL, NULL, '已撤销');
INSERT INTO sys_dict_data VALUES (72, '000000', 2, '草稿', 'draft', 'wf_business_status', '', 'default', '', 'N', 103, 1, sysdate, NULL, NULL, '草稿');
INSERT INTO sys_dict_data VALUES (73, '000000', 3, '待审核', 'waiting', 'wf_business_status', '', 'primary', '', 'N', 103, 1, sysdate, NULL, NULL, '待审核');
INSERT INTO sys_dict_data VALUES (74, '000000', 4, '已完成', 'finish', 'wf_business_status', '', 'success', '', 'N', 103, 1, sysdate, NULL, NULL, '已完成');
INSERT INTO sys_dict_data VALUES (75, '000000', 5, '已作废', 'invalid', 'wf_business_status', '', 'danger', '', 'N', 103, 1, sysdate, NULL, NULL, '已作废');
INSERT INTO sys_dict_data VALUES (76, '000000', 6, '已退回', 'back', 'wf_business_status', '', 'danger', '', 'N', 103, 1, sysdate, NULL, NULL, '已退回');
INSERT INTO sys_dict_data VALUES (77, '000000', 7, '已终止', 'termination', 'wf_business_status', '', 'danger', '', 'N', 103, 1, sysdate, NULL, NULL, '已终止');
INSERT INTO sys_dict_data VALUES (78, '000000', 1, '自定义表单', 'static', 'wf_form_type', '', 'success', '', 'N', 103, 1, sysdate, NULL, NULL, '自定义表单');
INSERT INTO sys_dict_data VALUES (79, '000000', 2, '动态表单', 'dynamic', 'wf_form_type', '', 'primary', '', 'N', 103, 1, sysdate, NULL, NULL, '动态表单');
INSERT INTO sys_dict_data VALUES (80, '000000', 1, '撤销', 'cancel', 'wf_task_status', '', 'danger', '', 'N', 103, 1, sysdate, NULL, NULL, '撤销');
INSERT INTO sys_dict_data VALUES (81, '000000', 2, '通过', 'pass', 'wf_task_status', '', 'success', '', 'N', 103, 1, sysdate, NULL, NULL, '通过');
INSERT INTO sys_dict_data VALUES (82, '000000', 3, '待审核', 'waiting', 'wf_task_status', '', 'primary', '', 'N', 103, 1, sysdate, NULL, NULL, '待审核');
INSERT INTO sys_dict_data VALUES (83, '000000', 4, '作废', 'invalid', 'wf_task_status', '', 'danger', '', 'N', 103, 1, sysdate, NULL, NULL, '作废');
INSERT INTO sys_dict_data VALUES (84, '000000', 5, '退回', 'back', 'wf_task_status', '', 'danger', '', 'N', 103, 1, sysdate, NULL, NULL, '退回');
INSERT INTO sys_dict_data VALUES (85, '000000', 6, '终止', 'termination', 'wf_task_status', '', 'danger', '', 'N', 103, 1, sysdate, NULL, NULL, '终止');
INSERT INTO sys_dict_data VALUES (86, '000000', 7, '转办', 'transfer', 'wf_task_status', '', 'primary', '', 'N', 103, 1, sysdate, NULL, NULL, '转办');
INSERT INTO sys_dict_data VALUES (87, '000000', 8, '委托', 'depute', 'wf_task_status', '', 'primary', '', 'N', 103, 1, sysdate, NULL, NULL, '委托');
INSERT INTO sys_dict_data VALUES (88, '000000', 9, '抄送', 'copy', 'wf_task_status', '', 'primary', '', 'N', 103, 1, sysdate, NULL, NULL, '抄送');
INSERT INTO sys_dict_data VALUES (89, '000000', 10, '加签', 'sign', 'wf_task_status', '', 'primary', '', 'N', 103, 1, sysdate, NULL, NULL, '加签');
INSERT INTO sys_dict_data VALUES (90, '000000', 11, '减签', 'sign_off', 'wf_task_status', '', 'danger', '', 'N', 103, 1, sysdate, NULL, NULL, '减签');
INSERT INTO sys_dict_data VALUES (91, '000000', 11, '超时', 'timeout', 'wf_task_status', '', 'danger', '', 'N', 103, 1, sysdate, NULL, NULL, '超时');
INSERT INTO sys_dict_data VALUES (100, '000000', 0, '代理转发请求', 'proxy', 'sys_storage_request_mode', NULL, 'primary', NULL, 'N', 103, 1, sysdate, 1, sysdate, NULL);
INSERT INTO sys_dict_data VALUES (101, '000000', 1, '源地址重定向请求', 'direct', 'sys_storage_request_mode', NULL, 'primary', NULL, 'N', 103, 1, sysdate, 1, sysdate, NULL);
INSERT INTO sys_dict_data VALUES (102, '000000', 2, '预签名重定向请求', 'direct_signature', 'sys_storage_request_mode', NULL, 'primary', NULL, 'N', 103, 1, sysdate, 1, sysdate, NULL);
