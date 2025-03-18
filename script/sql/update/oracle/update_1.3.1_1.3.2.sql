-- OSS表增加url_style字段
ALTER TABLE sys_oss_config ADD (url_style char(1) DEFAULT '0');
comment on column sys_oss_config.url_style is 'URL风格（0=虚拟托管风格，1=路径风格）';

-- 旧数据迁移，包含 aliyun、obs、qcloud、qiniu 等改为虚拟托管，其余改为路径风格
UPDATE sys_oss_config
SET url_style =
    CASE
        WHEN endpoint LIKE '%aliyun%' OR endpoint LIKE '%obs%' OR endpoint LIKE '%qcloud%' OR endpoint LIKE '%qiniu%' THEN '0'
        ELSE '1'
    END;

-- 添加字典类型
insert into sys_dict_type values(18, '000000', 'OSS的URL风格', 'oss_url_style', 103, 1, sysdate, 1, sysdate, 'https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/userguide/VirtualHosting.html#VirtualHostingControl');

-- 添加字典数据
insert into sys_dict_data values(71, '000000', 1,  '拟托管风格', '0', 'oss_url_style',  '',   '', '',  'Y', 104, 1, sysdate, 1, sysdate, '示例：<bucket-name>.s3.region-code.amazonaws.com');
insert into sys_dict_data values(72, '000000', 2,  '路径风格',   '1', 'oss_url_style',  '',   '', '',  'N', 104, 1, sysdate, 1, sysdate, '示例：s3.region-code.amazonaws.com/<bucket-name>');
