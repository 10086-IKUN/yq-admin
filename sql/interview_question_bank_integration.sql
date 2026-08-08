-- 面试复盘题库增量升级：兼容已有 interview_review_task 历史链路。
ALTER TABLE interview_review_record
    ADD COLUMN question_status VARCHAR(32) NOT NULL DEFAULT 'NONE' COMMENT 'NONE/PROCESSING/DONE/FAILED' AFTER error_message,
    ADD COLUMN question_extracted_count INT NOT NULL DEFAULT 0 AFTER question_status,
    ADD COLUMN question_created_count INT NOT NULL DEFAULT 0 AFTER question_extracted_count,
    ADD COLUMN question_merged_count INT NOT NULL DEFAULT 0 AFTER question_created_count,
    ADD COLUMN question_fail_reason VARCHAR(1000) NULL AFTER question_merged_count;

ALTER TABLE interview_question_bank
    ADD COLUMN audit_status VARCHAR(32) NOT NULL DEFAULT 'PUBLISHED' COMMENT 'PENDING/PUBLISHED/REJECTED' AFTER source_count,
    ADD COLUMN vector_status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/INDEXED/FAILED' AFTER audit_status,
    ADD COLUMN vector_id VARCHAR(100) NULL AFTER vector_status,
    ADD COLUMN confidence DECIMAL(5,4) NULL AFTER vector_id,
    ADD COLUMN first_seen_at DATETIME NULL AFTER confidence,
    ADD COLUMN last_seen_at DATETIME NULL AFTER first_seen_at,
    ADD COLUMN last_source_review_id BIGINT NULL AFTER last_source_task_id;

ALTER TABLE interview_question_source
    MODIFY COLUMN task_id BIGINT NULL COMMENT '旧版来源面试复盘任务 ID',
    ADD COLUMN review_record_id BIGINT NULL COMMENT '新版来源面试复盘记录 ID' AFTER task_id,
    ADD COLUMN confidence DECIMAL(5,4) NULL AFTER improvement_suggestion,
    ADD COLUMN source_hash CHAR(64) NULL AFTER confidence,
    ADD UNIQUE KEY uk_interview_question_review_source (question_id, review_record_id, source_hash),
    ADD KEY idx_interview_question_source_review (review_record_id);

INSERT INTO sys_permission(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT p.id, 'interview-question:view', '查看面试题库', 'API', '/api/interview-questions', 3, '查询面试问题和来源', 'ACTIVE', NOW(), NOW()
FROM sys_permission p WHERE p.permission_code='menu:interview-review'
AND NOT EXISTS (SELECT 1 FROM sys_permission WHERE permission_code='interview-question:view');

INSERT INTO sys_permission(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT p.id, 'interview-question:manage', '管理面试题库', 'API', '/api/interview-questions/**', 4, '审核、重试和发布面试问题', 'ACTIVE', NOW(), NOW()
FROM sys_permission p WHERE p.permission_code='menu:interview-review'
AND NOT EXISTS (SELECT 1 FROM sys_permission WHERE permission_code='interview-question:manage');

INSERT INTO sys_role_permission(role_id, permission_id, created_at)
SELECT r.id,p.id,NOW() FROM sys_role r JOIN sys_permission p
ON p.permission_code IN ('interview-question:view','interview-question:manage')
WHERE r.role_code='SUPER_ADMIN'
AND NOT EXISTS (SELECT 1 FROM sys_role_permission rp WHERE rp.role_id=r.id AND rp.permission_id=p.id);
