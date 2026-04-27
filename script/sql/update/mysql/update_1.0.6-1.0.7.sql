-- 消息模板发送记录耗时时间
ALTER TABLE sys_message_log ADD COLUMN cost_time bigint NULL DEFAULT null COMMENT '消耗时间' AFTER log_time;
