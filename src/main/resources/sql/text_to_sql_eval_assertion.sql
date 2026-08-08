create table if not exists text_to_sql_eval_assertion (
    id bigint primary key auto_increment comment '主键ID',
    eval_question_id bigint not null comment '评测样本ID',
    actual_key varchar(128) null comment '实际取值key，例如 business_id、used_tables、final_answer',
    operator varchar(32) not null comment '判断方式：EQ/CONTAINS/NOT_CONTAINS/EXISTS/NOT_EMPTY/REGEX/SEMANTIC',
    expected_value text null comment '客观判断期望值',
    required tinyint(1) not null default 1 comment '是否必过',
    weight decimal(5,2) not null default 1.00 comment '权重，预留给评分制',
    failure_type varchar(64) null comment '失败归因',
    reference_answer text null comment '主观判断参考答案',
    key_points text null comment '主观判断必须覆盖要点，按行分隔',
    forbidden_points text null comment '主观判断禁止出现内容，按行分隔',
    min_score int null comment '主观判断最低通过分',
    sort_order int not null default 0 comment '展示顺序',
    remark varchar(512) null comment '规则说明',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    key idx_text_to_sql_eval_assertion_question_id (eval_question_id)
) comment 'Text-to-SQL 评测判断标准';

