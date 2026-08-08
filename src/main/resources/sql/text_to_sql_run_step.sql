create table if not exists text_to_sql_run_step (
    id bigint primary key auto_increment comment '主键ID',
    run_id bigint not null comment '运行记录ID',
    conversation_id varchar(64) not null comment '会话ID',
    step_index int not null comment '步骤序号',
    node_name varchar(256) null comment '本步骤执行节点，可能为并行节点组合',
    source varchar(64) null comment 'LangGraph checkpoint source',
    lang_graph_step int null comment 'LangGraph 内部 step',
    next_nodes json null comment '下一节点',
    state_snapshot_json json null comment '该步骤状态快照',
    checkpoint_created_at varchar(64) null comment 'LangGraph checkpoint 创建时间',
    saved_at datetime not null default current_timestamp comment '保存时间',
    key idx_text_to_sql_run_step_run_id (run_id),
    key idx_text_to_sql_run_step_conversation_id (conversation_id),
    key idx_text_to_sql_run_step_node_name (node_name)
) comment 'Text-to-SQL LangGraph 状态历史';

