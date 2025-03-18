-- ----------------------------
-- 第三方平台授权表
-- ----------------------------
create table sys_social
(
    id                 bigint           not null        comment '主键',
    user_id            bigint           not null        comment '用户ID',
    tenant_id          varchar(20)      default '000000' comment '租户id',
    auth_id            varchar(255)     not null        comment '平台+平台唯一id',
    source             varchar(255)     not null        comment '用户来源',
    open_id            varchar(255)     default null    comment '平台编号唯一id',
    user_name          varchar(30)      not null        comment '登录账号',
    nick_name          varchar(30)      default ''      comment '用户昵称',
    email              varchar(255)     default ''      comment '用户邮箱',
    avatar             varchar(500)     default ''      comment '头像地址',
    access_token       varchar(255)     not null        comment '用户的授权令牌',
    expire_in          int              default null    comment '用户的授权令牌的有效期，部分平台可能没有',
    refresh_token      varchar(255)     default null    comment '刷新令牌，部分平台可能没有',
    access_code        varchar(255)     default null    comment '平台的授权信息，部分平台可能没有',
    union_id           varchar(255)     default null    comment '用户的 unionid',
    scope              varchar(255)     default null    comment '授予的权限，部分平台可能没有',
    token_type         varchar(255)     default null    comment '个别平台的授权信息，部分平台可能没有',
    id_token           varchar(2000)    default null    comment 'id token，部分平台可能没有',
    mac_algorithm      varchar(255)     default null    comment '小米平台用户的附带属性，部分平台可能没有',
    mac_key            varchar(255)     default null    comment '小米平台用户的附带属性，部分平台可能没有',
    code               varchar(255)     default null    comment '用户的授权code，部分平台可能没有',
    oauth_token        varchar(255)     default null    comment 'Twitter平台用户的附带属性，部分平台可能没有',
    oauth_token_secret varchar(255)     default null    comment 'Twitter平台用户的附带属性，部分平台可能没有',
    create_dept        bigint(20)                       comment '创建部门',
    create_by          bigint(20)                       comment '创建者',
    create_time        datetime                         comment '创建时间',
    update_by          bigint(20)                       comment '更新者',
    update_time        datetime                         comment '更新时间',
    del_flag           char(1)          default '0'     comment '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id)
) engine=innodb comment = '社会化关系表';

-- ----------------------------
-- 租户表
-- ----------------------------
create table sys_tenant
(
    id                bigint(20)    not null        comment 'id',
    tenant_id         varchar(20)   not null        comment '租户编号',
    contact_user_name varchar(20)                   comment '联系人',
    contact_phone     varchar(20)                   comment '联系电话',
    company_name      varchar(50)                   comment '企业名称',
    license_number    varchar(30)                   comment '统一社会信用代码',
    address           varchar(200)                  comment '地址',
    intro             varchar(200)                  comment '企业简介',
    domain            varchar(200)                  comment '域名',
    remark            varchar(200)                  comment '备注',
    package_id        bigint(20)                    comment '租户套餐编号',
    expire_time       datetime                      comment '过期时间',
    account_count     int           default -1      comment '用户数量（-1不限制）',
    status            char(1)       default '1'     comment '租户状态（1正常 0停用）',
    del_flag          char(1)       default '0'     comment '删除标志（0代表存在 1代表删除）',
    create_dept       bigint(20)                    comment '创建部门',
    create_by         bigint(20)                    comment '创建者',
    create_time       datetime                      comment '创建时间',
    update_by         bigint(20)                    comment '更新者',
    update_time       datetime                      comment '更新时间',
    primary key (id)
) engine=innodb comment = '租户表';


-- ----------------------------
-- 初始化-租户表数据
-- ----------------------------

insert into sys_tenant values(1, '000000', '管理组', '15888888888', 'XXX有限公司', null, null, '多租户通用后台管理管理系统', null, null, null, null, -1, '1', '0', 103, 1, sysdate(), null, null);


-- ----------------------------
-- 租户套餐表
-- ----------------------------
create table sys_tenant_package (
    package_id              bigint(20)     not null    comment '租户套餐id',
    package_name            varchar(20)                comment '套餐名称',
    menu_ids                varchar(3000)              comment '关联菜单id',
    remark                  varchar(200)               comment '备注',
    menu_check_strictly     tinyint(1)     default 1   comment '菜单树选择项是否关联显示',
    status                  char(1)        default '1' comment '状态（1正常 0停用）',
    del_flag                char(1)        default '0' comment '删除标志（0代表存在 1代表删除）',
    create_dept             bigint(20)                 comment '创建部门',
    create_by               bigint(20)                 comment '创建者',
    create_time             datetime                   comment '创建时间',
    update_by               bigint(20)                 comment '更新者',
    update_time             datetime                   comment '更新时间',
    primary key (package_id)
) engine=innodb comment = '租户套餐表';

-- ----------------------------
-- 租户套餐和菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_tenant_package_menu;
CREATE TABLE sys_tenant_package_menu  (
  package_id    bigint NOT NULL COMMENT '租户套餐id',
  menu_id       bigint NOT NULL COMMENT '菜单id',
  PRIMARY KEY (package_id, menu_id) USING BTREE
) ENGINE = InnoDB COMMENT = '租户套餐和菜单关联表';


-- ----------------------------
-- 1、部门表
-- ----------------------------
create table sys_dept (
    dept_id           bigint(20)      not null                   comment '部门id',
    tenant_id         varchar(20)     default '000000'           comment '租户编号',
    parent_id         bigint(20)      default 0                  comment '父部门id',
    ancestors         varchar(500)    default ''                 comment '祖级列表',
    dept_name         varchar(30)     default ''                 comment '部门名称',
    dept_category     varchar(100)    default null               comment '部门类别编码',
    order_num         int(4)          default 0                  comment '显示顺序',
    leader            bigint(20)      default null               comment '负责人',
    phone             varchar(11)     default null               comment '联系电话',
    email             varchar(50)     default null               comment '邮箱',
    status            char(1)         default '1'                comment '部门状态（1正常 0停用）',
    del_flag          char(1)         default '0'                comment '删除标志（0代表存在 1代表删除）',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    primary key (dept_id)
) engine=innodb comment = '部门表';

-- ----------------------------
-- 初始化-部门表数据
-- ----------------------------

insert into sys_dept values(100, '000000', 0,   '0',          'XXX科技',   null,0, null, '15888888888', 'xxx@qq.com', '1', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(101, '000000', 100, '0,100',      '深圳总公司', null,1, null, '15888888888', 'xxx@qq.com', '1', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(102, '000000', 100, '0,100',      '长沙分公司', null,2, null, '15888888888', 'xxx@qq.com', '1', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(103, '000000', 101, '0,100,101',  '研发部门',   null,1, 1,    '15888888888', 'xxx@qq.com', '1', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(104, '000000', 101, '0,100,101',  '市场部门',   null,2, null, '15888888888', 'xxx@qq.com', '1', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(105, '000000', 101, '0,100,101',  '测试部门',   null,3, null, '15888888888', 'xxx@qq.com', '1', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(106, '000000', 101, '0,100,101',  '财务部门',   null,4, null, '15888888888', 'xxx@qq.com', '1', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(107, '000000', 101, '0,100,101',  '运维部门',   null,5, null, '15888888888', 'xxx@qq.com', '1', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(108, '000000', 102, '0,100,102',  '市场部门',   null,1, null, '15888888888', 'xxx@qq.com', '1', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(109, '000000', 102, '0,100,102',  '财务部门',   null,2, null, '15888888888', 'xxx@qq.com', '1', '0', 103, 1, sysdate(), null, null);


-- ----------------------------
-- 2、用户信息表
-- ----------------------------
create table sys_user (
    user_id           bigint(20)      not null                   comment '用户ID',
    tenant_id         varchar(20)     default '000000'           comment '租户编号',
    dept_id           bigint(20)      default null               comment '部门ID',
    user_name         varchar(30)     not null                   comment '用户账号',
    nick_name         varchar(30)     not null                   comment '用户昵称',
    user_type         varchar(10)     default 'sys_user'         comment '用户类型（sys_user系统用户）',
    email             varchar(50)     default ''                 comment '用户邮箱',
    phonenumber       varchar(11)     default ''                 comment '手机号码',
    sex               char(1)         default '0'                comment '用户性别（0男 1女 2未知）',
    avatar            bigint(20)                                 comment '头像地址',
    password          varchar(100)    default ''                 comment '密码',
    status            char(1)         default '1'                comment '帐号状态（1正常 0停用）',
    del_flag          char(1)         default '0'                comment '删除标志（0代表存在 1代表删除）',
    login_ip          varchar(128)    default ''                 comment '最后登录IP',
    login_date        datetime                                   comment '最后登录时间',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    remark            varchar(500)    default null               comment '备注',
    primary key (user_id)
) engine=innodb comment = '用户信息表';

-- ----------------------------
-- 初始化-用户信息表数据
-- ----------------------------
insert into sys_user values(1, '000000', 103, 'admin', '管理员', 'sys_user', 'xxx@163.com', '15888888888', '1', null, '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '1', '0', '127.0.0.1', sysdate(), 103, 1, sysdate(), null, null, '管理员');


-- ----------------------------
-- 3、岗位信息表
-- ----------------------------
create table sys_post
(
    post_id       bigint(20)      not null                   comment '岗位ID',
    tenant_id     varchar(20)     default '000000'           comment '租户编号',
    dept_id       bigint(20)      not null                   comment '部门id',
    post_code     varchar(64)     not null                   comment '岗位编码',
    post_category varchar(100)    default null               comment '岗位类别编码',
    post_name     varchar(50)     not null                   comment '岗位名称',
    post_sort     int(4)          not null                   comment '显示顺序',
    status        char(1)         not null                   comment '状态（1正常 0停用）',
    create_dept   bigint(20)      default null               comment '创建部门',
    create_by     bigint(20)      default null               comment '创建者',
    create_time   datetime                                   comment '创建时间',
    update_by     bigint(20)      default null               comment '更新者',
    update_time   datetime                                   comment '更新时间',
    remark        varchar(500)    default null               comment '备注',
    primary key (post_id)
) engine=innodb comment = '岗位信息表';

-- ----------------------------
-- 初始化-岗位信息表数据
-- ----------------------------
insert into sys_post values(1, '000000', 103, 'ceo',  null, '董事长',    1, '1', 103, 1, sysdate(), null, null, '');
insert into sys_post values(2, '000000', 100, 'se',   null, '项目经理',  2, '1', 103, 1, sysdate(), null, null, '');
insert into sys_post values(3, '000000', 100, 'hr',   null, '人力资源',  3, '1', 103, 1, sysdate(), null, null, '');
insert into sys_post values(4, '000000', 100, 'user', null, '普通员工',  4, '1', 103, 1, sysdate(), null, null, '');


-- ----------------------------
-- 4、角色信息表
-- ----------------------------
create table sys_role (
    role_id              bigint(20)      not null                   comment '角色ID',
    tenant_id            varchar(20)     default '000000'           comment '租户编号',
    role_name            varchar(30)     not null                   comment '角色名称',
    role_key             varchar(100)    not null                   comment '角色权限字符串',
    role_sort            int(4)          not null                   comment '显示顺序',
    data_scope           char(1)         default '1'                comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
    menu_check_strictly  tinyint(1)      default 1                  comment '菜单树选择项是否关联显示',
    dept_check_strictly  tinyint(1)      default 1                  comment '部门树选择项是否关联显示',
    status               char(1)         not null                   comment '角色状态（1正常 0停用）',
    del_flag             char(1)         default '0'                comment '删除标志（0代表存在 1代表删除）',
    create_dept          bigint(20)      default null               comment '创建部门',
    create_by            bigint(20)      default null               comment '创建者',
    create_time          datetime                                   comment '创建时间',
    update_by            bigint(20)      default null               comment '更新者',
    update_time          datetime                                   comment '更新时间',
    remark               varchar(500)    default null               comment '备注',
    primary key (role_id)
) engine=innodb comment = '角色信息表';

-- ----------------------------
-- 初始化-角色信息表数据
-- ----------------------------
insert into sys_role values(1, '000000', '超级管理员',  'superadmin',  1, 1, 1, 1, '1', '0', 103, 1, sysdate(), null, null, '超级管理员');
insert into sys_role values(2, '000000', '普通角色',    'common', 2, 2, 1, 1, '1', '0', 103, 1, sysdate(), null, null, '普通角色');


-- ----------------------------
-- 5、菜单权限表
-- ----------------------------
create table sys_menu (
    menu_id           bigint(20)      not null                   comment '菜单ID',
    menu_name         varchar(50)     not null                   comment '菜单名称',
    parent_id         bigint(20)      default 0                  comment '父菜单ID',
    order_num         int(4)          default 0                  comment '显示顺序',
    path              varchar(200)    default ''                 comment '路由地址',
    component         varchar(255)    default null               comment '组件路径',
    component_name    varchar(255)    default null               comment '组件名称',
    query_param       varchar(255)    default null               comment '路由参数',
    is_frame          int(1)          default 0                  comment '是否为外链（1是 0否）',
    is_cache          int(1)          default 1                  comment '是否缓存（1缓存 0不缓存）',
    menu_type         char(1)         default ''                 comment '菜单类型（M目录 C菜单 F按钮）',
    visible           char(1)         default 1                  comment '显示状态（1显示 0隐藏）',
    status            char(1)         default 1                  comment '菜单状态（1正常 0停用）',
    perms             varchar(100)    default null               comment '权限标识',
    icon              varchar(100)    default '#'                comment '菜单图标',
    hidden_expression varchar(255)    default null               comment '隐藏表达式',
    shop_expression   varchar(255)    default null               comment '停用表达式',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    remark            varchar(500)    default ''                 comment '备注',
    primary key (menu_id)
) engine=innodb comment = '菜单权限表';

-- ----------------------------
-- 初始化-菜单信息表数据
-- ----------------------------
-- 一级菜单
insert into sys_menu values('1', '系统管理', '0', '11', 'system',           null, null, '', 0, 1, 'M', '1', '1', '', 'setting',    null, null, 103, 1, sysdate(), null, null, '系统管理目录');
insert into sys_menu values('6', '租户管理', '0', '12', 'tenant',           null, null, '', 0, 1, 'M', '1', '1', '', 'chart-bar',  null, '!getProperty(\'tenant.enable\')', 103, 1, sysdate(), null, null, '租户管理目录');
insert into sys_menu values('2', '系统监控', '0', '13', 'monitor',          null, null, '', 0, 1, 'M', '1', '1', '', 'chart',      null, null, 103, 1, sysdate(), null, null, '系统监控目录');
insert into sys_menu values('3', '系统工具', '0', '14', 'tool',             null, null, '', 0, 1, 'M', '1', '1', '', 'tools',      null, 'getProperty(\'spring.profiles.active\') != \'dev\'', 103, 1, sysdate(), null, null, '系统工具目录');
insert into sys_menu values('4', 'PLUS官网', '0', '15', 'https://gitee.com/yixiacoco/ruoyi-tdesign', null, null, '', 1, 1, 'M', '1', '1', '', 'link',    null, 'getProperty(\'spring.profiles.active\') != \'dev\'', 103, 1, sysdate(), null, null, 'ruoyi-tdesign官网地址');

-- 二级菜单
insert into sys_menu values('100',  '用户管理',     '1',   '1', 'user',             'system/user/index','User',            '', 0, 1, 'C', '1', '1', 'system:user:list',            'user',          null, null, 103, 1, sysdate(), null, null, '用户管理菜单');
insert into sys_menu values('101',  '角色管理',     '1',   '2', 'role',             'system/role/index','Role',            '', 0, 1, 'C', '1', '1', 'system:role:list',            'user-safety',       null, null, 103, 1, sysdate(), null, null, '角色管理菜单');
insert into sys_menu values('102',  '菜单管理',     '1',   '3', 'menus',             'system/menu/index','Menu',            '', 0, 1, 'C', '1', '1', 'system:menu:list',            'bulletpoint',    null, null, 103, 1, sysdate(), null, null, '菜单管理菜单');
insert into sys_menu values('103',  '部门管理',     '1',   '4', 'dept',             'system/dept/index','Dept',            '', 0, 1, 'C', '1', '1', 'system:dept:list',            'tree-square-dot',          null, null, 103, 1, sysdate(), null, null, '部门管理菜单');
insert into sys_menu values('104',  '岗位管理',     '1',   '5', 'post',             'system/post/index','Post',            '', 0, 1, 'C', '1', '1', 'system:post:list',            'user-avatar',          null, null, 103, 1, sysdate(), null, null, '岗位管理菜单');
insert into sys_menu values('105',  '字典管理',     '1',   '6', 'dict',             'system/dict/index','Dict',            '', 0, 1, 'C', '1', '1', 'system:dict:list',            'book',          null, null, 103, 1, sysdate(), null, null, '字典管理菜单');
insert into sys_menu values('106',  '参数设置',     '1',   '7', 'sysConfig',           'system/config/index','Config',          '', 0, 1, 'C', '1', '1', 'system:config:list',          'edit',          null, null, 103, 1, sysdate(), null, null, '参数设置菜单');
insert into sys_menu values('107',  '通知公告',     '1',   '8', 'notice',           'system/notice/index','Notice',          '', 0, 1, 'C', '1', '1', 'system:notice:list',          'mail',       null, null, 103, 1, sysdate(), null, null, '通知公告菜单');
insert into sys_menu values('108',  '日志管理',     '1',   '9', 'log',              '',                             null,   '', 0, 1, 'M', '1', '1', '',                            'root-list',    null, null, 103, 1, sysdate(), null, null, '日志管理菜单');
insert into sys_menu values('109',  '在线用户',     '2',   '1', 'online',           'monitor/online/index','Online',         '', 0, 1, 'C', '1', '1', 'monitor:online:list',         'user-talk',        null, null, 103, 1, sysdate(), null, null, '在线用户菜单');
insert into sys_menu values('113',  '缓存监控',     '2',   '5', 'cache',            'monitor/cache/index','Cache',          '', 0, 1, 'C', '1', '1', 'monitor:cache:list',          'layers',         null, null, 103, 1, sysdate(), null, null, '缓存监控菜单');
insert into sys_menu values('115',  '代码生成',     '3',   '2', 'gen',              'tool/gen/index','Gen',               '', 0, 1, 'C', '1', '1', 'tool:gen:list',               'code',          null, null, 103, 1, sysdate(), null, null, '代码生成菜单');
insert into sys_menu values('121',  '租户管理',     '6',   '1', 'tenant',           'system/tenant/index','Tenant',          '', 0, 1, 'C', '1', '1', 'system:tenant:list',          'bulletpoint',          null, null, 103, 1, sysdate(), null, null, '租户管理菜单');
insert into sys_menu values('122',  '租户套餐管理',  '6',   '2', 'tenantPackage',    'system/tenantPackage/index','TenantPackage',   '', 0, 1, 'C', '1', '1', 'system:tenantPackage:list',   'edit-1',          null, null, 103, 1, sysdate(), null, null, '租户套餐管理菜单');
insert into sys_menu values('123',  '客户端管理',   '1',   '12', 'client',           'system/client/index','Client',          '', 0, 1, 'C', '1', '1', 'system:client:list',          'internet', null, null, 103, 1, sysdate(), null, null, '客户端管理菜单');
insert into sys_menu values('124',  '敏感词',      '1', '13', 'sensitiveWord', 'system/sensitiveWord/index', 'SensitiveWord', '', 0, 1, 'C', '1', '1', 'system:sensitiveWord:list', 'book-open', null, null, 103, 1, sysdate(), 1, sysdate(), '敏感词菜单');

-- springboot-admin监控
insert into sys_menu values('117',  'Admin监控',   '2',   '5',  'Admin',            'monitor/admin/index','Admin',         '', 0, 1, 'C', '1', '1', 'monitor:admin:list',           'dashboard',     null, '!getProperty(\'spring.boot.admin.client.enabled\')', 103, 1, sysdate(), null, null, 'Admin监控菜单');
-- oss菜单
insert into sys_menu values('1510', '文件管理', '1', '10', 'store', null, null, null, 0, 1, 'M', '1', '1', null, 'cloud', null, null, 103, 1, sysdate(), 1, null, '');
-- snail-job server控制台
insert into sys_menu values('120',  '任务调度中心',  '2',   '6',  'snailjob',     'monitor/snailjob/index', null,        '', 0, 1, 'C', '1', '1', 'monitor:snailjob:list', 'video',           null, '!getProperty(''snail-job.enabled'')', 103, 1, sysdate(), null, null, 'SnailJob控制台菜单');

-- 三级菜单
insert into sys_menu values('500',  '操作日志', '108', '1', 'operlog',    'monitor/operlog/index','Operlog',    '', 0, 1, 'C', '1', '1', 'monitor:operlog:list',    'edit-1',          null, null, 103, 1, sysdate(), null, null, '操作日志菜单');
insert into sys_menu values('501',  '登录日志', '108', '2', 'logininfor', 'monitor/logininfor/index','Logininfor', '', 0, 1, 'C', '1', '1', 'monitor:logininfor:list', 'swap',    null, null, 103, 1, sysdate(), null, null, '登录日志菜单');
insert into sys_menu values('1500', 'OSS配置管理', '1510', '1', 'ossConfig', 'system/ossConfig/index','OssConfig', '', 0, 1, 'C', '1', '1', 'system:ossConfig:list', 'server', null, null, 103, 1, sysdate(), 1, null, '');
insert into sys_menu values('118',  '文件管理','1510','2', 'oss','system/oss/index','Oss','', 0, 1, 'C', '1', '1', 'system:oss:list', 'backup',null, null, 103, 1, sysdate(), null, null, '文件管理菜单');
insert into sys_menu values('1521', 'OSS处理规则', '1510', '3', 'ossRule', 'system/ossRule/index','OssRule', null, 0, 1, 'C', '1', '1', 'system:ossRule:list', 'chevron-right-double', null, null, 103, 1, sysdate(), 1, null, 'OSS处理规则菜单');
insert into sys_menu values('1531', '我的文件', '1510', 4, 'ossCategory', 'system/ossCategory/index','OssCategory', NULL, 0, 1, 'C', '1', '1', 'system:ossCategory:list', 'folder-open', null, null, 103, 1, sysdate(), 1, sysdate(), '我的文件菜单');
-- 用户管理按钮
insert into sys_menu values('1001', '用户查询', '100', '1',  '', '', '','', 0, 1, 'F', '1', '1', 'system:user:query',          '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1002', '用户新增', '100', '2',  '', '', '','', 0, 1, 'F', '1', '1', 'system:user:add',            '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1003', '用户修改', '100', '3',  '', '', '','', 0, 1, 'F', '1', '1', 'system:user:edit',           '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1004', '用户删除', '100', '4',  '', '', '','', 0, 1, 'F', '1', '1', 'system:user:remove',         '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1005', '用户导出', '100', '5',  '', '', '','', 0, 1, 'F', '1', '1', 'system:user:export',         '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1006', '用户导入', '100', '6',  '', '', '','', 0, 1, 'F', '1', '1', 'system:user:import',         '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1007', '重置密码', '100', '7',  '', '', '','', 0, 1, 'F', '1', '1', 'system:user:resetPwd',       '#', null, null, 103, 1, sysdate(), null, null, '');
-- 角色管理按钮
insert into sys_menu values('1008', '角色查询', '101', '1',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:role:query',          '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1009', '角色新增', '101', '2',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:role:add',            '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1010', '角色修改', '101', '3',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:role:edit',           '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1011', '角色删除', '101', '4',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:role:remove',         '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1012', '角色导出', '101', '5',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:role:export',         '#', null, null, 103, 1, sysdate(), null, null, '');
-- 菜单管理按钮
insert into sys_menu values('1013', '菜单查询', '102', '1',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:menu:query',          '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1014', '菜单新增', '102', '2',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:menu:add',            '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1015', '菜单修改', '102', '3',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:menu:edit',           '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1016', '菜单删除', '102', '4',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:menu:remove',         '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1117', '菜单导出', '102', '5',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:menu:export',         '#', null, null, 103, 1, sysdate(), null, null, '');
-- 部门管理按钮
insert into sys_menu values('1017', '部门查询', '103', '1',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:dept:query',          '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1018', '部门新增', '103', '2',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:dept:add',            '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1019', '部门修改', '103', '3',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:dept:edit',           '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1020', '部门删除', '103', '4',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:dept:remove',         '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1121', '部门导出', '103', '5',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:dept:export',         '#', null, null, 103, 1, sysdate(), null, null, '');
-- 岗位管理按钮
insert into sys_menu values('1021', '岗位查询', '104', '1',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:post:query',          '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1022', '岗位新增', '104', '2',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:post:add',            '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1023', '岗位修改', '104', '3',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:post:edit',           '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1024', '岗位删除', '104', '4',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:post:remove',         '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1025', '岗位导出', '104', '5',  '', '', '', '', 0, 1, 'F', '1', '1', 'system:post:export',         '#', null, null, 103, 1, sysdate(), null, null, '');
-- 字典管理按钮
insert into sys_menu values('1026', '字典查询', '105', '1', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:dict:query',          '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1027', '字典新增', '105', '2', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:dict:add',            '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1028', '字典修改', '105', '3', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:dict:edit',           '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1029', '字典删除', '105', '4', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:dict:remove',         '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1030', '字典导出', '105', '5', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:dict:export',         '#', null, null, 103, 1, sysdate(), null, null, '');
-- 参数设置按钮
insert into sys_menu values('1031', '参数查询', '106', '1', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:config:query',        '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1032', '参数新增', '106', '2', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:config:add',          '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1033', '参数修改', '106', '3', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:config:edit',         '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1034', '参数删除', '106', '4', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:config:remove',       '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1035', '参数导出', '106', '5', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:config:export',       '#', null, null, 103, 1, sysdate(), null, null, '');
-- 通知公告按钮
insert into sys_menu values('1036', '公告查询', '107', '1', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:notice:query',        '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1037', '公告新增', '107', '2', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:notice:add',          '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1038', '公告修改', '107', '3', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:notice:edit',         '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1039', '公告删除', '107', '4', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:notice:remove',       '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1140', '公告导出', '107', '5', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:notice:export',       '#', null, null, 103, 1, sysdate(), null, null, '');
-- 操作日志按钮
insert into sys_menu values('1040', '操作查询', '500', '1', '#', '', '', '', 0, 1, 'F', '1', '1', 'monitor:operlog:query',      '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1041', '操作删除', '500', '2', '#', '', '', '', 0, 1, 'F', '1', '1', 'monitor:operlog:remove',     '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1042', '日志导出', '500', '4', '#', '', '', '', 0, 1, 'F', '1', '1', 'monitor:operlog:export',     '#', null, null, 103, 1, sysdate(), null, null, '');
-- 登录日志按钮
insert into sys_menu values('1043', '登录查询', '501', '1', '#', '', '', '', 0, 1, 'F', '1', '1', 'monitor:logininfor:query',   '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1044', '登录删除', '501', '2', '#', '', '', '', 0, 1, 'F', '1', '1', 'monitor:logininfor:remove',  '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1045', '日志导出', '501', '3', '#', '', '', '', 0, 1, 'F', '1', '1', 'monitor:logininfor:export',  '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1050', '账户解锁', '501', '4', '#', '', '', '', 0, 1, 'F', '1', '1', 'monitor:logininfor:unlock',  '#', null, null, 103, 1, sysdate(), null, null, '');
-- 在线用户按钮
insert into sys_menu values('1046', '在线查询', '109', '1', '#', '', '', '', 0, 1, 'F', '1', '1', 'monitor:online:query',       '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1047', '批量强退', '109', '2', '#', '', '', '', 0, 1, 'F', '1', '1', 'monitor:online:batchLogout', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1048', '单条强退', '109', '3', '#', '', '', '', 0, 1, 'F', '1', '1', 'monitor:online:forceLogout', '#', null, null, 103, 1, sysdate(), null, null, '');
-- 代码生成按钮
insert into sys_menu values('1055', '生成查询', '115', '1', '#', '', '', '', 0, 1, 'F', '1', '1', 'tool:gen:query',             '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1056', '生成修改', '115', '2', '#', '', '', '', 0, 1, 'F', '1', '1', 'tool:gen:edit',              '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1057', '生成删除', '115', '3', '#', '', '', '', 0, 1, 'F', '1', '1', 'tool:gen:remove',            '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1058', '导入代码', '115', '2', '#', '', '', '', 0, 1, 'F', '1', '1', 'tool:gen:import',            '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1059', '预览代码', '115', '4', '#', '', '', '', 0, 1, 'F', '1', '1', 'tool:gen:preview',           '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1060', '生成代码', '115', '5', '#', '', '', '', 0, 1, 'F', '1', '1', 'tool:gen:code',              '#', null, null, 103, 1, sysdate(), null, null, '');
-- oss相关按钮
insert into sys_menu values('1600', '文件查询', '118', '1', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:oss:query',        '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1601', '文件上传', '118', '2', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:oss:upload',       '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1602', '文件下载', '118', '3', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:oss:download',     '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1603', '文件删除', '118', '4', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:oss:remove',       '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1604', '文件修改', '118', 5, '', NULL, NULL, NULL, 0, 1, 'F', '1', '1', 'system:oss:edit', '#', null, null, 103, 1, sysdate(), 1, sysdate(), '');
insert into sys_menu values('1501', '配置添加', '1500', '1', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:ossConfig:add',          '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1502', '配置编辑', '1500', '2', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:ossConfig:edit',          '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1503', '配置删除', '1500', '3', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:ossConfig:remove',         '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1522', 'OSS处理规则查询', '1521', '1', '#', '', '', null, 0, 1, 'F', '1', '1', 'system:ossRule:query', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1523', 'OSS处理规则新增', '1521', '2', '#', '', '', null, 0, 1, 'F', '1', '1', 'system:ossRule:add', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1524', 'OSS处理规则修改', '1521', '3', '#', '', '', null, 0, 1, 'F', '1', '1', 'system:ossRule:edit', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1525', 'OSS处理规则删除', '1521', '4', '#', '', '', null, 0, 1, 'F', '1', '1', 'system:ossRule:remove', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1526', 'OSS处理规则导出', '1521', '5', '#', '', '', null, 0, 1, 'F', '1', '1', 'system:ossRule:export', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1532', 'OSS分类查询', '1531', 1, '#', '', '', NULL, 0, 1, 'F', '1', '1', 'system:ossCategory:query', '#', null, null, 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1533', 'OSS分类新增', '1531', 2, '#', '', '', NULL, 0, 1, 'F', '1', '1', 'system:ossCategory:add', '#', null, null, 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1534', 'OSS分类修改', '1531', 3, '#', '', '', NULL, 0, 1, 'F', '1', '1', 'system:ossCategory:edit', '#', null, null, 103, 1, sysdate(), NULL, NULL, '');
insert into sys_menu values('1535', 'OSS分类删除', '1531', 4, '#', '', '', NULL, 0, 1, 'F', '1', '1', 'system:ossCategory:remove', '#', null, null, 103, 1, sysdate(), NULL, NULL, '');
-- 租户管理相关按钮
insert into sys_menu values('1606', '租户查询', '121', '1', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:tenant:query',   '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1607', '租户新增', '121', '2', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:tenant:add',     '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1608', '租户修改', '121', '3', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:tenant:edit',    '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1609', '租户删除', '121', '4', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:tenant:remove',  '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1610', '租户导出', '121', '5', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:tenant:export',  '#', null, null, 103, 1, sysdate(), null, null, '');
-- 租户套餐管理相关按钮
insert into sys_menu values('1611', '租户套餐查询', '122', '1', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:tenantPackage:query',   '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1612', '租户套餐新增', '122', '2', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:tenantPackage:add',     '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1613', '租户套餐修改', '122', '3', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:tenantPackage:edit',    '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1614', '租户套餐删除', '122', '4', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:tenantPackage:remove',  '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1615', '租户套餐导出', '122', '5', '#', '', '', '', 0, 1, 'F', '1', '1', 'system:tenantPackage:export',  '#', null, null, 103, 1, sysdate(), null, null, '');
-- 客户端管理按钮
insert into sys_menu values('1061', '客户端管理查询', '123', '1',  '#', '', '', '', 0, 1, 'F', '1', '1', 'system:client:query',        '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1062', '客户端管理新增', '123', '2',  '#', '', '', '', 0, 1, 'F', '1', '1', 'system:client:add',          '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1063', '客户端管理修改', '123', '3',  '#', '', '', '', 0, 1, 'F', '1', '1', 'system:client:edit',         '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1064', '客户端管理删除', '123', '4',  '#', '', '', '', 0, 1, 'F', '1', '1', 'system:client:remove',       '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1065', '客户端管理导出', '123', '5',  '#', '', '', '', 0, 1, 'F', '1', '1', 'system:client:export',       '#', null, null, 103, 1, sysdate(), null, null, '');
-- 租户应用管理表
insert into sys_menu values('1701', '租户应用管理',    '6',    3, 'tenantApp','system/tenantApp/index','TenantApp', null, 0, 1, 'C', '1', '1', 'system:tenantApp:list', 'app', null, null, 103, 1, sysdate(), 1, sysdate(), '租户应用管理菜单');
insert into sys_menu values('1702', '租户应用管理查询', '1701', 1, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:tenantApp:query', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1703', '租户应用管理新增', '1701', 2, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:tenantApp:add', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1704', '租户应用管理修改', '1701', 3, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:tenantApp:edit', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1705', '租户应用管理删除', '1701', 4, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:tenantApp:remove', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1706', '租户应用管理导出', '1701', 5, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:tenantApp:export', '#', null, null, 103, 1, sysdate(), null, null, '');
-- 敏感词
insert into sys_menu values ('1710', '敏感词查询', '124', 1, '#', '', NULL, '', 0, 1, 'F', '1', '1', 'system:sensitiveWord:query', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1711', '敏感词新增', '124', 2, '#', '', null, '', 0, 1, 'F', '1', '1', 'system:sensitiveWord:add', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1712', '敏感词修改', '124', 3, '#', '', null, '', 0, 1, 'F', '1', '1', 'system:sensitiveWord:edit', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1713', '敏感词删除', '124', 4, '#', '', null, '', 0, 1, 'F', '1', '1', 'system:sensitiveWord:remove', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1714', '敏感词导出', '124', 5, '#', '', null, '', 0, 1, 'F', '1', '1', 'system:sensitiveWord:export', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('1715', '敏感词导入', '124', 6, '#', '', null, '', 0, 1, 'F', '1', '1', 'system:sensitiveWord:import', '#', null, null, 103, 1, sysdate(), null, null, '');

-- 消息管理
insert into sys_menu values('1801', '消息管理', '1', 11, 'messageManage', null, null, null, 0, 1, 'M', '1', '1', null, 'chat', null, null, 103, 1, sysdate(), 1, sysdate(), '');
-- 消息配置
insert into sys_menu values('1802', '消息配置',    '1801', 1, 'messageConfig', 'system/messageConfig/index','MessageConfig', null, 0, 1, 'C', '1', '1', 'system:messageConfig:list', 'tools', null, null, 103, 1, sysdate(), 1, sysdate(), '消息配置菜单');
insert into sys_menu values('1803', '消息配置查询', '1802', 1, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageConfig:query', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1804', '消息配置新增', '1802', 2, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageConfig:add', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1805', '消息配置修改', '1802', 3, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageConfig:edit', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1806', '消息配置删除', '1802', 4, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageConfig:remove', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1807', '消息配置导出', '1802', 5, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageConfig:export', '#', null, null, 103, 1, sysdate(), null, null, '');
-- 消息常量
insert into sys_menu values('1810', '消息常量',     '1801', 2, 'messageKey', 'system/messageKey/index','MessageKey', null, 0, 1, 'C', '1', '1', 'system:messageKey:list', 'root-list', null, null, 103, 1, sysdate(), 1, sysdate(), '消息常量菜单');
insert into sys_menu values('1811', '消息常量查询', '1810', 1, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageKey:query', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1812', '消息常量新增', '1810', 2, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageKey:add', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1813', '消息常量修改', '1810', 3, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageKey:edit', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1814', '消息常量删除', '1810', 4, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageKey:remove', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1815', '消息常量导出', '1810', 5, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageKey:export', '#', null, null, 103, 1, sysdate(), null, null, '');
-- 消息模板
insert into sys_menu values('1820', '消息模板',    '1801', 3, 'messageTemplate', 'system/messageTemplate/index','MessageTemplate', null, 0, 1, 'C', '1', '1', 'system:messageTemplate:list', 'relativity', null, null, 103, 1, sysdate(), 1, sysdate(), '消息模板菜单');
insert into sys_menu values('1821', '消息模板查询', '1820', 1, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageTemplate:query', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1822', '消息模板新增', '1820', 2, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageTemplate:add', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1823', '消息模板修改', '1820', 3, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageTemplate:edit', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1824', '消息模板删除', '1820', 4, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageTemplate:remove', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1825', '消息模板导出', '1820', 5, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageTemplate:export', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1826', '发送测试消息', '1820', 6, '', null, null, null, 0, 1, 'F', '1', '1', 'system:messageTemplate:test', '#', null, null, 103, 1, sysdate(), 1, sysdate(), '');
-- 消息发送记录
insert into sys_menu values('1830', '消息发送记录',    '1801', 4, 'messageLog', 'system/messageLog/index','MessageLog', null, 0, 1, 'C', '1', '1', 'system:messageLog:list', 'history', null, null, 103, 1, sysdate(), 1, sysdate(), '消息发送记录菜单');
insert into sys_menu values('1831', '消息发送记录查询', '1830', 1, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageLog:query', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1832', '消息发送记录删除', '1830', 4, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageLog:remove', '#', null, null, 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1833', '消息发送记录导出', '1830', 5, '#', '', '', null, 0, 1, 'F', '1', '1', 'system:messageLog:export', '#', null, null, 103, 1, sysdate(), null, null, '');


-- ----------------------------
-- 6、用户和角色关联表  用户N-1角色
-- ----------------------------
create table sys_user_role (
    user_id   bigint(20) not null comment '用户ID',
    role_id   bigint(20) not null comment '角色ID',
    primary key(user_id, role_id)
) engine=innodb comment = '用户和角色关联表';

-- ----------------------------
-- 初始化-用户和角色关联表数据
-- ----------------------------
insert into sys_user_role values ('1', '1');


-- ----------------------------
-- 7、角色和菜单关联表  角色1-N菜单
-- ----------------------------
create table sys_role_menu (
    role_id   bigint(20) not null comment '角色ID',
    menu_id   bigint(20) not null comment '菜单ID',
    primary key(role_id, menu_id)
) engine=innodb comment = '角色和菜单关联表';

-- ----------------------------
-- 初始化-角色和菜单关联表数据
-- ----------------------------
insert into sys_role_menu values ('2', '1');
insert into sys_role_menu values ('2', '2');
insert into sys_role_menu values ('2', '3');
insert into sys_role_menu values ('2', '4');
insert into sys_role_menu values ('2', '100');
insert into sys_role_menu values ('2', '101');
insert into sys_role_menu values ('2', '102');
insert into sys_role_menu values ('2', '103');
insert into sys_role_menu values ('2', '104');
insert into sys_role_menu values ('2', '105');
insert into sys_role_menu values ('2', '106');
insert into sys_role_menu values ('2', '107');
insert into sys_role_menu values ('2', '108');
insert into sys_role_menu values ('2', '109');
insert into sys_role_menu values ('2', '110');
insert into sys_role_menu values ('2', '111');
insert into sys_role_menu values ('2', '112');
insert into sys_role_menu values ('2', '113');
insert into sys_role_menu values ('2', '114');
insert into sys_role_menu values ('2', '115');
insert into sys_role_menu values ('2', '116');
insert into sys_role_menu values ('2', '500');
insert into sys_role_menu values ('2', '501');
insert into sys_role_menu values ('2', '1000');
insert into sys_role_menu values ('2', '1001');
insert into sys_role_menu values ('2', '1002');
insert into sys_role_menu values ('2', '1003');
insert into sys_role_menu values ('2', '1004');
insert into sys_role_menu values ('2', '1005');
insert into sys_role_menu values ('2', '1006');
insert into sys_role_menu values ('2', '1007');
insert into sys_role_menu values ('2', '1008');
insert into sys_role_menu values ('2', '1009');
insert into sys_role_menu values ('2', '1010');
insert into sys_role_menu values ('2', '1011');
insert into sys_role_menu values ('2', '1012');
insert into sys_role_menu values ('2', '1013');
insert into sys_role_menu values ('2', '1014');
insert into sys_role_menu values ('2', '1015');
insert into sys_role_menu values ('2', '1016');
insert into sys_role_menu values ('2', '1017');
insert into sys_role_menu values ('2', '1018');
insert into sys_role_menu values ('2', '1019');
insert into sys_role_menu values ('2', '1020');
insert into sys_role_menu values ('2', '1021');
insert into sys_role_menu values ('2', '1022');
insert into sys_role_menu values ('2', '1023');
insert into sys_role_menu values ('2', '1024');
insert into sys_role_menu values ('2', '1025');
insert into sys_role_menu values ('2', '1026');
insert into sys_role_menu values ('2', '1027');
insert into sys_role_menu values ('2', '1028');
insert into sys_role_menu values ('2', '1029');
insert into sys_role_menu values ('2', '1030');
insert into sys_role_menu values ('2', '1031');
insert into sys_role_menu values ('2', '1032');
insert into sys_role_menu values ('2', '1033');
insert into sys_role_menu values ('2', '1034');
insert into sys_role_menu values ('2', '1035');
insert into sys_role_menu values ('2', '1036');
insert into sys_role_menu values ('2', '1037');
insert into sys_role_menu values ('2', '1038');
insert into sys_role_menu values ('2', '1039');
insert into sys_role_menu values ('2', '1040');
insert into sys_role_menu values ('2', '1041');
insert into sys_role_menu values ('2', '1042');
insert into sys_role_menu values ('2', '1043');
insert into sys_role_menu values ('2', '1044');
insert into sys_role_menu values ('2', '1045');
insert into sys_role_menu values ('2', '1050');
insert into sys_role_menu values ('2', '1046');
insert into sys_role_menu values ('2', '1047');
insert into sys_role_menu values ('2', '1048');
insert into sys_role_menu values ('2', '1055');
insert into sys_role_menu values ('2', '1056');
insert into sys_role_menu values ('2', '1057');
insert into sys_role_menu values ('2', '1058');
insert into sys_role_menu values ('2', '1059');
insert into sys_role_menu values ('2', '1060');
insert into sys_role_menu values ('2', '1061');
insert into sys_role_menu values ('2', '1062');
insert into sys_role_menu values ('2', '1063');
insert into sys_role_menu values ('2', '1064');
insert into sys_role_menu values ('2', '1065');

-- ----------------------------
-- 8、角色和部门关联表  角色1-N部门
-- ----------------------------
create table sys_role_dept (
    role_id   bigint(20) not null comment '角色ID',
    dept_id   bigint(20) not null comment '部门ID',
    primary key(role_id, dept_id)
) engine=innodb comment = '角色和部门关联表';

-- ----------------------------
-- 9、用户与岗位关联表  用户1-N岗位
-- ----------------------------
create table sys_user_post
(
    user_id   bigint(20) not null comment '用户ID',
    post_id   bigint(20) not null comment '岗位ID',
    primary key (user_id, post_id)
) engine=innodb comment = '用户与岗位关联表';

-- ----------------------------
-- 初始化-用户与岗位关联表数据
-- ----------------------------
insert into sys_user_post values ('1', '1');

-- ----------------------------
-- 10、操作日志记录
-- ----------------------------
create table sys_oper_log (
    oper_id           bigint(20)      not null                   comment '日志主键',
    tenant_id         varchar(20)     default '000000'           comment '租户编号',
    user_id           bigint          default null               comment '用户id',
    title             varchar(50)     default ''                 comment '模块标题',
    business_type     int(2)          default 0                  comment '业务类型（0其它 1新增 2修改 3删除）',
    method            varchar(100)    default ''                 comment '方法名称',
    request_method    varchar(10)     default ''                 comment '请求方式',
    operator_type     int(1)          default 0                  comment '操作类别（0其它 1后台用户 2手机端用户）',
    oper_name         varchar(50)     default ''                 comment '操作人员',
    dept_name         varchar(50)     default ''                 comment '部门名称',
    oper_url          varchar(255)    default ''                 comment '请求URL',
    oper_ip           varchar(128)    default ''                 comment '主机地址',
    oper_location     varchar(255)    default ''                 comment '操作地点',
    oper_param        varchar(5000)   default ''                 comment '请求参数',
    json_result       varchar(5000)   default ''                 comment '返回参数',
    status            int(1)          default 0                  comment '操作状态（1正常 0异常）',
    error_msg         varchar(5000)   default ''                 comment '错误消息',
    oper_time         datetime                                   comment '操作时间',
    cost_time         bigint(20)      default 0                  comment '消耗时间',
    primary key (oper_id),
    key idx_sys_oper_log_bt (business_type),
    key idx_sys_oper_log_s  (status),
    key idx_sys_oper_log_ot (oper_time)
) engine=innodb comment = '操作日志记录';


-- ----------------------------
-- 11、字典类型表
-- ----------------------------
create table sys_dict_type
(
    dict_id          bigint(20)      not null                   comment '字典主键',
    tenant_id        varchar(20)     default '000000'           comment '租户编号',
    dict_name        varchar(100)    default ''                 comment '字典名称',
    dict_type        varchar(100)    default ''                 comment '字典类型',
    create_dept      bigint(20)      default null               comment '创建部门',
    create_by        bigint(20)      default null               comment '创建者',
    create_time      datetime                                   comment '创建时间',
    update_by        bigint(20)      default null               comment '更新者',
    update_time      datetime                                   comment '更新时间',
    remark           varchar(500)    default null               comment '备注',
    primary key (dict_id),
    unique (tenant_id, dict_type)
) engine=innodb comment = '字典类型表';

insert into sys_dict_type values(1,  '000000', '用户性别', 'sys_user_sex',        103, 1, sysdate(), null, null, '用户性别列表');
insert into sys_dict_type values(2,  '000000', '显隐状态', 'sys_show_hide',       103, 1, sysdate(), null, null, '菜单状态列表');
insert into sys_dict_type values(3,  '000000', '正常状态', 'sys_normal_disable',  103, 1, sysdate(), null, null, '系统开关列表');
insert into sys_dict_type values(6,  '000000', '系统是否', 'sys_yes_no',          103, 1, sysdate(), null, null, '系统是否列表');
insert into sys_dict_type values(7,  '000000', '通知类型', 'sys_notice_type',     103, 1, sysdate(), null, null, '通知类型列表');
insert into sys_dict_type values(8,  '000000', '通知状态', 'sys_notice_status',   103, 1, sysdate(), null, null, '通知状态列表');
insert into sys_dict_type values(9,  '000000', '操作类型', 'sys_oper_type',       103, 1, sysdate(), null, null, '操作类型列表');
insert into sys_dict_type values(10, '000000', '成功状态', 'sys_common_status',   103, 1, sysdate(), null, null, '登录状态列表');
insert into sys_dict_type values(11, '000000', '应用类型', 'sys_app_type', 103, 1, sysdate(), 1, sysdate(), '应用管理列表');
insert into sys_dict_type values(14, '000000', '消息模板类型', 'sys_message_template_mode', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_dict_type values(15, '000000', '授权类型', 'sys_grant_type',     103, 1, sysdate(), null, null, '认证授权类型');
insert into sys_dict_type values(16, '000000', '设备类型', 'sys_device_type',    103, 1, sysdate(), null, null, '客户端设备类型');
insert into sys_dict_type values(17, '000000', '敏感词类别', 'sensitive_words_category', 103, 1, sysdate(), 1, sysdate(), null);


-- ----------------------------
-- 12、字典数据表
-- ----------------------------
create table sys_dict_data
(
    dict_code        bigint(20)      not null                   comment '字典编码',
    tenant_id        varchar(20)     default '000000'           comment '租户编号',
    dict_sort        int(4)          default 0                  comment '字典排序',
    dict_label       varchar(100)    default ''                 comment '字典标签',
    dict_value       varchar(100)    default ''                 comment '字典键值',
    dict_type        varchar(100)    default ''                 comment '字典类型',
    css_class        varchar(100)    default null               comment '样式属性（其他样式扩展）',
    list_class       varchar(100)    default null               comment '表格回显样式',
    tag_style        varchar(50)     default null               comment '回显风格',
    is_default       char(1)         default 'N'                comment '是否默认（Y是 N否）',
    create_dept      bigint(20)      default null               comment '创建部门',
    create_by        bigint(20)      default null               comment '创建者',
    create_time      datetime                                   comment '创建时间',
    update_by        bigint(20)      default null               comment '更新者',
    update_time      datetime                                   comment '更新时间',
    remark           varchar(500)    default null               comment '备注',
    primary key (dict_code)
) engine=innodb comment = '字典数据表';

insert into sys_dict_data values(1, '000000', 1,  '男',       '0',       'sys_user_sex',        '',   '', '',        'Y', 103, 1, sysdate(), null, null, '性别男');
insert into sys_dict_data values(2, '000000', 2,  '女',       '1',       'sys_user_sex',        '',   '', '',        'N', 103, 1, sysdate(), null, null, '性别女');
insert into sys_dict_data values(3, '000000', 3,  '未知',     '2',       'sys_user_sex',        '',   '', '',        'N', 103, 1, sysdate(), null, null, '性别未知');
insert into sys_dict_data values(4, '000000', 1,  '显示',     '1',       'sys_show_hide',       '',   'primary', '', 'Y', 103, 1, sysdate(), null, null, '显示状态');
insert into sys_dict_data values(5, '000000', 2,  '隐藏',     '0',       'sys_show_hide',       '',   'danger', '',  'N', 103, 1, sysdate(), null, null, '隐藏状态');
insert into sys_dict_data values(6, '000000', 1,  '正常',     '1',       'sys_normal_disable',  '',   'primary', '', 'Y', 103, 1, sysdate(), null, null, '正常状态');
insert into sys_dict_data values(7, '000000', 2,  '停用',     '0',       'sys_normal_disable',  '',   'danger', '',  'N', 103, 1, sysdate(), null, null, '停用状态');
insert into sys_dict_data values(12, '000000', 1,  '是',       'Y',       'sys_yes_no',          '',   'primary', '', 'Y', 103, 1, sysdate(), null, null, '系统默认是');
insert into sys_dict_data values(13, '000000', 2,  '否',       'N',       'sys_yes_no',          '',   'danger', '',  'N', 103, 1, sysdate(), null, null, '系统默认否');
insert into sys_dict_data values(14, '000000', 1,  '通知',     '1',       'sys_notice_type',     '',   'warning', '', 'Y', 103, 1, sysdate(), null, null, '通知');
insert into sys_dict_data values(15, '000000', 2,  '公告',     '2',       'sys_notice_type',     '',   'success', '', 'N', 103, 1, sysdate(), null, null, '公告');
insert into sys_dict_data values(16, '000000', 1,  '正常',     '1',       'sys_notice_status',   '',   'primary', '', 'Y', 103, 1, sysdate(), null, null, '正常状态');
insert into sys_dict_data values(17, '000000', 2,  '关闭',     '0',       'sys_notice_status',   '',   'danger', '',  'N', 103, 1, sysdate(), null, null, '关闭状态');
insert into sys_dict_data values(29, '000000', 99, '其他',     '0',       'sys_oper_type',       '',   'primary', '', 'N', 103, 1, sysdate(), null, null, '其他操作');
insert into sys_dict_data values(18, '000000', 1,  '新增',     '1',       'sys_oper_type',       '',   'primary', '', 'N', 103, 1, sysdate(), null, null, '新增操作');
insert into sys_dict_data values(19, '000000', 2,  '修改',     '2',       'sys_oper_type',       '',   'primary', '', 'N', 103, 1, sysdate(), null, null, '修改操作');
insert into sys_dict_data values(20, '000000', 3,  '删除',     '3',       'sys_oper_type',       '',   'danger', '',  'N', 103, 1, sysdate(), null, null, '删除操作');
insert into sys_dict_data values(21, '000000', 4,  '授权',     '4',       'sys_oper_type',       '',   'primary', '', 'N', 103, 1, sysdate(), null, null, '授权操作');
insert into sys_dict_data values(22, '000000', 5,  '导出',     '5',       'sys_oper_type',       '',   'warning', '', 'N', 103, 1, sysdate(), null, null, '导出操作');
insert into sys_dict_data values(23, '000000', 6,  '导入',     '6',       'sys_oper_type',       '',   'warning', '', 'N', 103, 1, sysdate(), null, null, '导入操作');
insert into sys_dict_data values(24, '000000', 7,  '强退',     '7',       'sys_oper_type',       '',   'danger', '',  'N', 103, 1, sysdate(), null, null, '强退操作');
insert into sys_dict_data values(25, '000000', 8,  '生成代码', '8',       'sys_oper_type',       '',   'warning', '', 'N', 103, 1, sysdate(), null, null, '生成操作');
insert into sys_dict_data values(26, '000000', 9,  '清空数据', '9',       'sys_oper_type',       '',   'danger', '',  'N', 103, 1, sysdate(), null, null, '清空操作');
insert into sys_dict_data values(27, '000000', 1,  '成功',     '1',       'sys_common_status',   '',   'primary', '', 'N', 103, 1, sysdate(), null, null, '成功状态');
insert into sys_dict_data values(28, '000000', 2,  '失败',     '0',       'sys_common_status',   '',   'danger', '',  'N', 103, 1, sysdate(), null, null, '失败状态');
insert into sys_dict_data values(30, '000000', 0, '域名', 'DOMAIN', 'sys_app_type', null, 'primary', '', 'N', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_dict_data values(31, '000000', 1, '微信小程序', 'WX_XCX', 'sys_app_type', null, 'primary', '', 'N', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_dict_data values(32, '000000', 2, '微信公众号', 'WX_GZH', 'sys_app_type', null, 'primary', '', 'N', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_dict_data values(33, '000000', 3, 'APP', 'APP', 'sys_app_type', null, 'primary', '', 'N', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_dict_data values(47, '000000', 0, '模板ID', 'TEMPLATE_ID', 'sys_message_template_mode', null, 'primary', '', 'N', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_dict_data values(48, '000000', 1, '模板内容', 'TEMPLATE_CONTENT', 'sys_message_template_mode', null, 'primary', '', 'N', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_dict_data values(49, '000000', 0,  '密码认证', 'password',   'sys_grant_type',   '',   'primary', 'light-outline', 'N', 103, 1, sysdate(), null, null, '密码认证');
insert into sys_dict_data values(50, '000000', 0,  '短信认证', 'sms',        'sys_grant_type',   '',   'primary', 'light-outline', 'N', 103, 1, sysdate(), null, null, '短信认证');
insert into sys_dict_data values(51, '000000', 0,  '邮件认证', 'email',      'sys_grant_type',   '',   'primary', 'light-outline', 'N', 103, 1, sysdate(), null, null, '邮件认证');
insert into sys_dict_data values(52, '000000', 0,  '小程序认证', 'xcx',      'sys_grant_type',   '',   'primary', 'light-outline', 'N', 103, 1, sysdate(), null, null, '小程序认证');
insert into sys_dict_data values(53, '000000', 0,  '三方登录认证', 'social', 'sys_grant_type',   '',   'primary', 'light-outline', 'N', 103, 1, sysdate(), null, null, '三方登录认证');
insert into sys_dict_data values(54, '000000', 0,  'PC',    'pc',         'sys_device_type',     '',   'primary', '', 'N', 103, 1, sysdate(), null, null, 'PC');
insert into sys_dict_data values(55, '000000', 0,  '安卓', 'android',     'sys_device_type',     '',   'primary', '', 'N', 103, 1, sysdate(), null, null, '安卓');
insert into sys_dict_data values(56, '000000', 0,  'iOS', 'ios',          'sys_device_type',     '',   'primary', '', 'N', 103, 1, sysdate(), null, null, 'iOS');
insert into sys_dict_data values(57, '000000', 0,  '小程序', 'xcx',       'sys_device_type',     '',   'primary', '', 'N', 103, 1, sysdate(), null, null, '小程序');
insert into sys_dict_data values(70, '000000', 99, '其他', 'other', 'sensitive_words_category', null, 'primary', null, 'N', 103, 1, sysdate(), 1, sysdate(), null);



-- ----------------------------
-- 13、参数配置表
-- ----------------------------
create table sys_config (
    config_id         bigint(20)      not null                   comment '参数主键',
    tenant_id         varchar(20)     default '000000'           comment '租户编号',
    config_name       varchar(100)    default ''                 comment '参数名称',
    config_key        varchar(100)    default ''                 comment '参数键名',
    config_value      text            default null               comment '参数键值',
    config_type       char(1)         default 'N'                comment '系统内置（Y是 N否）',
    is_global         tinyint(1)      not null                   comment '是否是全局配置 1是 0否',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    remark            varchar(500)    default null               comment '备注',
    primary key (config_id)
) engine=innodb comment = '参数配置表';

insert into sys_config values(1, '000000', '主框架页-默认皮肤样式名称',     'sys.index.skinName',            'skin-blue',     'Y', 0, 103, 1, sysdate(), null, null, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow' );
insert into sys_config values(2, '000000', '用户管理-账号初始密码',        'sys.user.initPassword',         '123456',        'Y', 0, 103, 1, sysdate(), null, null, '初始化密码 123456' );
insert into sys_config values(3, '000000', '主框架页-侧边栏主题',          'sys.index.sideTheme',           'theme-dark',    'Y', 0, 103, 1, sysdate(), null, null, '深色主题theme-dark，浅色主题theme-light' );
insert into sys_config values(5, '000000', '账号自助-是否开启用户注册功能',  'sys.account.registerUser',      'false',         'Y', 1, 103, 1, sysdate(), null, null, '是否开启注册用户功能（true开启，false关闭）');
insert into sys_config values(11, '000000', 'OSS预览列表资源开关',         'sys.oss.previewListResource',   'true',          'Y', 1, 103, 1, sysdate(), null, null, 'true:开启, false:关闭');


-- ----------------------------
-- 14、系统访问记录
-- ----------------------------
create table sys_logininfor (
    info_id        bigint(20)     not null                  comment '访问ID',
    tenant_id      varchar(20)    default '000000'          comment '租户编号',
    user_id        bigint         default null              comment '用户id',
    user_name      varchar(50)    default ''                comment '用户账号',
    client_key     varchar(32)    default ''                comment '客户端',
    device_type    varchar(32)    default ''                comment '设备类型',
    ipaddr         varchar(128)   default ''                comment '登录IP地址',
    login_location varchar(255)   default ''                comment '登录地点',
    browser        varchar(50)    default ''                comment '浏览器类型',
    os             varchar(50)    default ''                comment '操作系统',
    status         char(1)        default '1'               comment '登录状态（1成功 0失败）',
    msg            varchar(255)   default ''                comment '提示消息',
    login_time     datetime                                 comment '访问时间',
    primary key (info_id),
    key idx_sys_logininfor_s  (status),
    key idx_sys_logininfor_lt (login_time)
) engine=innodb comment = '系统访问记录';


-- ----------------------------
-- 17、通知公告表
-- ----------------------------
create table sys_notice (
    notice_id         bigint(20)      not null                   comment '公告ID',
    tenant_id         varchar(20)     default '000000'           comment '租户编号',
    notice_title      varchar(50)     not null                   comment '公告标题',
    notice_type       char(1)         not null                   comment '公告类型（1通知 2公告）',
    notice_content    longblob        default null               comment '公告内容',
    status            char(1)         default '1'                comment '公告状态（1正常 0关闭）',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    remark            varchar(255)    default null               comment '备注',
    primary key (notice_id)
) engine=innodb comment = '通知公告表';

-- ----------------------------
-- 初始化-公告信息表数据
-- ----------------------------
insert into sys_notice values('1', '000000', '温馨提醒：2018-07-01 新版本发布啦', '2', '新版本内容', '1', 103, 1, sysdate(), null, null, '管理员');
insert into sys_notice values('2', '000000', '维护通知：2018-07-01 系统凌晨维护', '1', '维护内容',   '1', 103, 1, sysdate(), null, null, '管理员');


-- ----------------------------
-- 18、代码生成业务表
-- ----------------------------
create table gen_table (
    table_id          bigint(20)      not null                   comment '编号',
    data_name         varchar(200)    default ''                 comment '数据源名称',
    table_name        varchar(200)    default ''                 comment '表名称',
    table_comment     varchar(500)    default ''                 comment '表描述',
    class_name        varchar(100)    default ''                 comment '实体类名称',
    tpl_category      varchar(200)    default 'crud'             comment '使用的模板（crud单表操作 tree树表操作）',
    package_name      varchar(100)                               comment '生成包路径',
    module_name       varchar(30)                                comment '生成模块名',
    business_name     varchar(30)                                comment '生成业务名',
    function_name     varchar(50)                                comment '生成功能名',
    function_author   varchar(50)                                comment '生成功能作者',
    gen_type          char(1)         default '0'                comment '生成代码方式（0zip压缩包 1自定义路径）',
    gen_path          varchar(200)    default '/'                comment '生成路径（不填默认项目路径）',
    options           varchar(1000)                              comment '其它生成选项',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    remark            varchar(500)    default null               comment '备注',
    primary key (table_id)
) engine=innodb comment = '代码生成业务表';


-- ----------------------------
-- 19、代码生成业务表字段
-- ----------------------------
create table gen_table_column (
    column_id         bigint(20)      not null                   comment '编号',
    table_id          bigint(20)                                 comment '归属表编号',
    column_name       varchar(200)                               comment '列名称',
    column_comment    varchar(500)                               comment '列描述',
    column_type       varchar(100)                               comment '列类型',
    java_type         varchar(500)                               comment 'JAVA类型',
    java_field        varchar(200)                               comment 'JAVA字段名',
    is_pk             char(1)                                    comment '是否主键（1是）',
    is_increment      char(1)                                    comment '是否自增（1是）',
    is_required       char(1)                                    comment '是否必填（1是）',
    is_insert         char(1)                                    comment '是否为插入字段（1是）',
    is_edit           char(1)                                    comment '是否编辑字段（1是）',
    is_list           char(1)                                    comment '是否列表字段（1是）',
    is_query          char(1)                                    comment '是否查询字段（1是）',
    is_detail         char(1)                                    comment '是否详情字段（1是）',
    is_sort           char(1)                                    comment '是否排序字段（1是）',
    query_type        varchar(200)    default 'EQ'               comment '查询方式（等于、不等于、大于、小于、范围）',
    html_type         varchar(200)                               comment '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
    dict_type         varchar(200)    default ''                 comment '字典类型',
    sort              int                                        comment '排序',
    create_dept       bigint(20)      default null               comment '创建部门',
    create_by         bigint(20)      default null               comment '创建者',
    create_time       datetime                                   comment '创建时间',
    update_by         bigint(20)      default null               comment '更新者',
    update_time       datetime                                   comment '更新时间',
    primary key (column_id)
) engine=innodb comment = '代码生成业务表字段';

-- ----------------------------
-- OSS对象存储表
-- ----------------------------
create table sys_oss (
    oss_id          bigint(20)   not null                   comment '对象存储主键',
    tenant_id       varchar(20)           default '000000'  comment '租户编号',
    file_name       varchar(255) not null default ''        comment '文件名',
    original_name   varchar(255) not null default ''        comment '原名',
    file_suffix     varchar(10)  not null default ''        comment '文件后缀名',
    url             varchar(500) not null                   comment 'URL地址',
    size            bigint(20)            default null      comment '字节长度',
    content_type    varchar(255) null     default null      comment '内容类型',
    oss_category_id bigint       not null default 0         comment '分类id',
    user_type       varchar(20)  not null default ''        comment '用户类型',
    is_lock         tinyint(1)   not null default 0         comment '是否锁定状态',
    create_dept     bigint(20)            default null      comment '创建部门',
    create_time     datetime              default null      comment '创建时间',
    create_by       bigint(20)            default null      comment '上传人',
    update_time     datetime              default null      comment '更新时间',
    update_by       bigint(20)            default null      comment '更新人',
    service         varchar(20)  not null default 'minio'   comment '服务商',
    primary key (oss_id),
    index idx_oss_category_id(oss_category_id) using btree comment '分类索引',
    index idx_user(create_by, user_type, create_time) using btree comment '用户索引'
) engine=innodb comment ='OSS对象存储表';

-- ----------------------------
-- OSS分类表
-- ----------------------------
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

-- ----------------------------
-- OSS对象存储动态配置表
-- ----------------------------
create table sys_oss_config (
    oss_config_id   bigint(20)    not null                  comment '主键',
    tenant_id       varchar(20)             default '000000'comment '租户编号',
    config_key      varchar(20)   not null  default ''      comment '配置key',
    access_key      varchar(255)            default ''      comment 'accessKey',
    secret_key      varchar(255)            default ''      comment '秘钥',
    bucket_name     varchar(255)            default ''      comment '桶名称',
    prefix          varchar(255)            default ''      comment '前缀',
    endpoint        varchar(255)            default ''      comment '访问站点',
    domain          varchar(255)            default ''      comment '自定义域名',
    is_https        char(1)                 default 'N'     comment '是否https（Y=是,N=否）',
    region          varchar(255)            default ''      comment '域',
    access_policy   char(1)       not null  default '1'     comment '桶权限类型(0=private 1=public 2=custom)',
    status          char(1)                 default '0'     comment '是否默认（1=是,0=否）',
    ext1            varchar(255)            default ''      comment '扩展字段',
    create_dept     bigint(20)              default null    comment '创建部门',
    create_by       bigint(20)              default null    comment '创建者',
    create_time     datetime                default null    comment '创建时间',
    update_by       bigint(20)              default null    comment '更新者',
    update_time     datetime                default null    comment '更新时间',
    remark          varchar(500)            default null    comment '备注',
    primary key (oss_config_id)
) engine=innodb comment='对象存储配置表';

insert into sys_oss_config values (1, '000000', 'minio',  'ruoyi',            'ruoyi123',        'ruoyi',             '', '127.0.0.1:9000',                '','N', '',             '1' ,'1', 0,'', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_oss_config values (2, '000000', 'qiniu',  'XXXXXXXXXXXXXXX',  'XXXXXXXXXXXXXXX', 'ruoyi',             '', 's3-cn-north-1.qiniucs.com',     '','N', '',             '1' ,'0', 0,'', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_oss_config values (3, '000000', 'aliyun', 'XXXXXXXXXXXXXXX',  'XXXXXXXXXXXXXXX', 'ruoyi',             '', 'oss-cn-beijing.aliyuncs.com',   '','N', '',             '1' ,'0', 0,'', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_oss_config values (4, '000000', 'qcloud', 'XXXXXXXXXXXXXXX',  'XXXXXXXXXXXXXXX', 'ruoyi-1250000000',  '', 'cos.ap-beijing.myqcloud.com',   '','N', 'ap-beijing',   '1' ,'0', 0,'', 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_oss_config values (5, '000000', 'image',  'ruoyi',            'ruoyi123',        'ruoyi',             'image', '127.0.0.1:9000',           '','N', '',             '1' ,'0', 0,'', 103, 1, sysdate(), 1, sysdate(), null);

-- ----------------------------
-- 系统授权表
-- ----------------------------
create table sys_client (
    id                  bigint(20)    not null            comment 'id',
    client_id           varchar(64)   default null        comment '客户端id',
    client_key          varchar(32)   default null        comment '客户端key',
    client_secret       varchar(255)  default null        comment '客户端秘钥',
    grant_type          varchar(255)  default null        comment '授权类型',
    device_type         varchar(32)   default null        comment '设备类型',
    active_timeout      int(11)       default 1800        comment 'token活跃超时时间',
    timeout             int(11)       default 604800      comment 'token固定超时',
    status              char(1)       default '1'         comment '状态（1正常 0停用）',
    del_flag            char(1)       default '0'         comment '删除标志（0代表存在 1代表删除）',
    create_dept         bigint(20)    default null        comment '创建部门',
    create_by           bigint(20)    default null        comment '创建者',
    create_time         datetime      default null        comment '创建时间',
    update_by           bigint(20)    default null        comment '更新者',
    update_time         datetime      default null        comment '更新时间',
    primary key (id)
) engine=innodb comment='系统授权表';

insert into sys_client values (1, 'e5cd7e4891bf95d1d19206ce24a7b32e', 'pc', 'pc123', 'password,social', 'pc', 1800, 604800, 1, 0, 103, 1, sysdate(), 1, sysdate());
insert into sys_client values (2, '428a8310cd442757ae699df5d894f051', 'app', 'app123', 'password,sms,social', 'android', 1800, 604800, 1, 0, 103, 1, sysdate(), 1, sysdate());

-- ----------------------------
-- OSS处理规则表
-- ----------------------------
create table sys_oss_rule  (
    oss_rule_id   bigint       not null                  comment 'oss规则id',
    tenant_id     varchar(20)  null     default '000000' comment '租户编号',
    rule_name     varchar(255) not null                  comment '规则名称（例如：80x80，则字段名称将输出字段名_80x80）',
    domain        varchar(255) not null                  comment '匹配域名',
    mime_type     varchar(255) not null                  comment '媒体类型（规则对匹配的媒体类型生效）',
    rule          varchar(255) not null                  comment '规则',
    is_overwrite  char(1)      not null                  comment '是否覆盖默认字段值',
    is_default    char(1)      not null                  comment '是否默认（不指定规则时，默认输出的规则）',
    status        char(1)      not null                  comment '启用状态',
    rule_sort     int          not null default 0        comment '规则顺序',
    create_dept   bigint       null     default null     comment '创建部门',
    create_by     bigint       null     default null     comment '创建者',
    create_time   datetime     null     default null     comment '创建时间',
    update_by     bigint       null     default null     comment '更新者',
    update_time   datetime     null     default null     comment '更新时间',
    remark        varchar(500) null     default null     comment '备注',
    primary key (oss_rule_id) using btree
) engine = innodb comment = 'OSS处理规则表';

insert into sys_oss_rule values (1, '000000', '180x180', 'oss-cn-beijing.aliyuncs.com', 'image', '#{#url}?x-oss-process=image/auto-orient,1/resize,m_lfit,w_180/quality,q_90', 'N', 'N', '1', 0, 103, 1, sysdate(), 1, sysdate(), null);
insert into sys_oss_rule values (2, '000000', '800x800', 'oss-cn-beijing.aliyuncs.com', 'image', '#{#url}?x-oss-process=image/auto-orient,1/resize,m_lfit,w_800/quality,q_90', 'N', 'N', '1', 1, 103, 1, sysdate(), 1, sysdate(), null);

-- ----------------------------
-- 租户应用管理表
-- ----------------------------
create table sys_tenant_app  (
    appid         bigint          not null                        comment '应用id',
    tenant_id     varchar(20)     null        default '000000'    comment '租户编号',
    app_type      varchar(20)     not null                        comment '应用类型',
    app_key       varchar(50)     not null                        comment '应用key',
    app_name      varchar(255)    not null                        comment '应用名称',
    create_dept   bigint          null        default null        comment '创建部门',
    create_by     bigint          null        default null        comment '创建者',
    create_time   datetime        null        default null        comment '创建时间',
    update_by     bigint          null        default null        comment '更新者',
    update_time   datetime        null        default null        comment '更新时间',
    remark        varchar(500)    null        default null        comment '备注',
    primary key (appid) using btree
) engine = innodb comment = '租户应用管理表';

-- ----------------------------
-- 消息配置表
-- ----------------------------
create table sys_message_config  (
    message_config_id bigint(20)      not null                    comment '消息设置id',
    title             varchar(255)    not null                    comment '标题',
    message_type      varchar(20)     not null                    comment '消息类型 SMS、MAIL',
    supplier_type     varchar(20)     not null                    comment '支持平台标识',
    config_json       text            null                        comment '配置json',
    status            tinyint(1)      not null                    comment '状态（1正常 0停用）',
    remark            varchar(500)    null        default null    comment '备注',
    create_dept       bigint(20)      null        default null    comment '创建部门',
    update_by         bigint(20)      null        default null    comment '更新者',
    create_by         bigint(20)      null        default null    comment '创建者',
    update_time       datetime        null        default null    comment '更新时间',
    create_time       datetime        null        default null    comment '创建时间',
    primary key (message_config_id) using btree,
    index idx_message_type(message_type, status) using btree
) engine = innodb comment = '消息配置表';

-- ----------------------------
-- 消息常量表
-- ----------------------------
create table sys_message_key  (
    message_key_id    bigint(20)      not null                    comment '消息key主键',
    name              varchar(255)    not null                    comment '消息名称',
    message_key       varchar(50)     not null                    comment '消息key',
    remark            varchar(500)    null        default null    comment '备注',
    create_dept       bigint(20)      null        default null    comment '创建部门',
    update_by         bigint(20)      null        default null    comment '更新者',
    create_by         bigint(20)      null        default null    comment '创建者',
    update_time       datetime        null        default null    comment '更新时间',
    create_time       datetime        null        default null    comment '创建时间',
    primary key (message_key_id) using btree
) engine = innodb comment = '消息常量表';

-- ----------------------------
-- 消息发送记录表
-- ----------------------------
create table sys_message_log  (
    message_log_id        bigint(20)      not null                    comment '消息发送记录id',
    message_template_id   bigint(20)      null        default null    comment '消息模板id',
    message_key           varchar(50)     not null                    comment '消息key',
    message_template_name varchar(255)    null        default null    comment '模板名称',
    message_type          varchar(20)     not null                    comment '消息类型 SMS、MAIL',
    template_mode         varchar(20)     null                        comment '模板类型 模板id模式、内容模式',
    account               varchar(255)    not null                    comment '发送账号',
    title                 varchar(255)    null        default null    comment '标题',
    template_id           varchar(100)    null        default null    comment '模板ID',
    content               text            null                        comment '发送内容',
    message_config_title  varchar(255)    null        default null    comment '消息配置标题',
    supplier_type         varchar(20)     null        default null    comment '平台标识',
    is_success            tinyint(1)      null        default null    comment '是否成功',
    response_body         varchar(1000)   null        default null    comment '返回主体消息',
    log_time              datetime        not null                    comment '记录时间',
    cost_time             bigint(20)      null                        comment '消耗时间',
    primary key (message_log_id) using btree,
    index idx_message_template_id(message_template_id) using btree
) engine = innodb comment = '消息发送记录表';

-- ----------------------------
-- 消息模板表
-- ----------------------------
create table sys_message_template  (
    message_template_id   bigint(20)      not null                    comment '消息模板id',
    template_name         varchar(255)    not null                    comment '模板名称',
    message_config_id     bigint(20)      not null                    comment '消息配置id',
    message_key_id        bigint          not null                    COMMENT '消息key主键',
    message_key           varchar(50)     not null                    comment '消息key',
    message_type          varchar(20)     not null                    comment '消息类型 SMS、MAIL',
    template_mode         varchar(20)     not null                    comment '模板类型 模板id模式、内容模式',
    title                 varchar(255)    null        default null    comment '标题',
    signature             varchar(100)    null        default null    comment '签名',
    template_id           varchar(100)    null        default null    comment '模板id',
    content               text            null                        comment '内容',
    vars_json             text            null                        comment '输入变量',
    status                tinyint(1)      not null                    comment '状态（1正常 0停用）',
    remark                varchar(500)    null        default null    comment '备注',
    create_dept           bigint(20)      null        default null    comment '创建部门',
    update_by             bigint(20)      null        default null    comment '更新者',
    create_by             bigint(20)      null        default null    comment '创建者',
    update_time           datetime        null        default null    comment '更新时间',
    create_time           datetime        null        default null    comment '创建时间',
    primary key (message_template_id) using btree,
    index idx_message_key_id(message_key_id) using btree,
    index idx_message_key(message_key, message_type, status) using btree
) engine = innodb comment = '消息模板表';

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
