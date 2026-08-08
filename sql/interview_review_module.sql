CREATE TABLE IF NOT EXISTS interview_review_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '面试复盘记录 ID',
    student_id BIGINT NOT NULL COMMENT '学生 ID',
    company_name VARCHAR(120) NULL COMMENT '面试公司',
    interview_role VARCHAR(120) NULL COMMENT '面试岗位',
    interview_time DATETIME NOT NULL COMMENT '面试时间',
    audio_object_key VARCHAR(500) NOT NULL COMMENT 'OSS objectKey',
    audio_file_name VARCHAR(255) NOT NULL COMMENT '录音文件名',
    audio_file_size BIGINT NULL COMMENT '录音大小（字节）',
    resume_text LONGTEXT NULL COMMENT '用于 AI 复盘的简历文本',
    review_remark VARCHAR(1000) NULL COMMENT '学生或老师备注',
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/TRANSCRIBING/ANALYZING/DONE/FAILED',
    transcript_task_id VARCHAR(100) NULL COMMENT '豆包 ASR 任务 ID',
    transcript_dialogue_json LONGTEXT NULL COMMENT '清洗后的说话人对话 JSON',
    report_json LONGTEXT NULL COMMENT 'AI 面试复盘报告 JSON',
    error_message VARCHAR(1000) NULL COMMENT '处理失败原因',
    started_at DATETIME NULL,
    completed_at DATETIME NULL,
    created_by BIGINT NULL COMMENT '管理端创建人 ID，学生自助上传时为空',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_interview_student (student_id), KEY idx_interview_status (status), KEY idx_interview_time (interview_time)
) COMMENT='面试录音转写与 AI 复盘记录';

INSERT INTO sys_permission(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT 0, 'menu:interview-review', '面试复盘', 'MENU', NULL, 450, '面试录音与 AI 复盘管理', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE permission_code='menu:interview-review');
SET @interview_menu_id=(SELECT id FROM sys_permission WHERE permission_code='menu:interview-review' LIMIT 1);
INSERT INTO sys_permission(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT @interview_menu_id, 'interview-review:view', '查看面试复盘', 'API', '/api/interview-reviews', 1, '分页和详情查询', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE permission_code='interview-review:view');
INSERT INTO sys_permission(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT @interview_menu_id, 'interview-review:manage', '管理面试复盘', 'API', '/api/interview-reviews/**', 2, '创建和重试面试复盘', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE permission_code='interview-review:manage');
INSERT INTO sys_role_permission(role_id, permission_id, created_at)
SELECT r.id,p.id,NOW() FROM sys_role r JOIN sys_permission p ON p.permission_code IN ('menu:interview-review','interview-review:view','interview-review:manage')
WHERE r.role_code='SUPER_ADMIN' AND NOT EXISTS (SELECT 1 FROM sys_role_permission rp WHERE rp.role_id=r.id AND rp.permission_id=p.id);
