create table if not exists text_to_sql_eval_run (
    id bigint primary key auto_increment comment '主键ID',
    name varchar(128) not null comment '评测任务名称',
    status varchar(32) not null comment 'RUNNING/COMPLETED/WAITING_CLARIFICATION/FAILED',
    sample_count int not null default 0 comment '样本数量',
    pass_count int not null default 0 comment '通过数量',
    fail_count int not null default 0 comment '失败数量',
    pass_rate decimal(7,2) not null default 0.00 comment '通过率，百分比',
    scope_json json null comment '评测范围',
    summary_json json null comment '失败原因汇总',
    error_message text null comment '任务错误信息',
    started_at datetime null comment '开始时间',
    finished_at datetime null comment '完成时间',
    created_by bigint null comment '创建人ID',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    key idx_text_to_sql_eval_run_status (status),
    key idx_text_to_sql_eval_run_created_at (created_at)
) comment 'Text-to-SQL 评测任务';

create table if not exists text_to_sql_eval_run_result (
    id bigint primary key auto_increment comment '主键ID',
    eval_run_id bigint not null comment '评测任务ID',
    eval_question_id bigint not null comment '评测样本ID',
    question text not null comment '评测问题',
    business_id varchar(64) null comment '业务标识',
    eval_target varchar(64) null comment '评测目标',
    sample_category varchar(64) null comment '样本场景',
    conversation_id varchar(128) null comment '本次运行会话ID',
    passed tinyint(1) not null default 0 comment '是否通过',
    failure_type varchar(64) null comment '失败类型',
    failure_reason text null comment '失败原因',
    actual_state_json json null comment '实际 State',
    actual_sql text null comment '实际 SQL',
    actual_answer text null comment '实际回答',
    execution_error text null comment '执行错误',
    duration_ms bigint null comment '运行耗时毫秒',
    created_at datetime not null default current_timestamp comment '创建时间',
    key idx_text_to_sql_eval_run_result_run_id (eval_run_id),
    key idx_text_to_sql_eval_run_result_question_id (eval_question_id),
    key idx_text_to_sql_eval_run_result_passed (passed),
    key idx_text_to_sql_eval_run_result_failure_type (failure_type)
) comment 'Text-to-SQL 评测任务结果';

