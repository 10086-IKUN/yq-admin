create table if not exists text_to_sql_feedback (
    id bigint primary key auto_increment comment '主键ID',
    run_id bigint not null comment 'Text-to-SQL 运行记录ID',
    conversation_id varchar(64) not null comment 'LangGraph thread_id / 会话ID',
    feedback_result varchar(32) not null comment 'CORRECT/INCORRECT',
    error_type varchar(64) null comment 'ROUTE/BUSINESS/SKILL/SQL/RESULT/ANALYSIS/CLARIFICATION/OTHER',
    comment text null comment '反馈说明',
    created_by bigint null comment '反馈用户ID',
    created_at datetime not null default current_timestamp comment '创建时间',
    key idx_text_to_sql_feedback_run_id (run_id),
    key idx_text_to_sql_feedback_conversation_id (conversation_id),
    key idx_text_to_sql_feedback_result (feedback_result),
    key idx_text_to_sql_feedback_created_at (created_at)
) comment 'Text-to-SQL 人工反馈记录';

