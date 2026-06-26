-- ============================================================
-- 燕雀教育在线考试模块
-- 说明：
-- 1. 只支持线上考试。
-- 2. 每名学员对每场考试只能产生一条考试记录。
-- 3. 交卷后不可再次作答，不提供补考。
-- 4. 成绩在老师发布答案，或考试截止后对学员可见。
-- ============================================================

CREATE TABLE IF NOT EXISTS `exam_question` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `question_type` varchar(20) NOT NULL COMMENT '题型：SINGLE/MULTIPLE/JUDGE/FILL/SHORT',
  `question_stem` text NOT NULL COMMENT '题干',
  `options_json` json DEFAULT NULL COMMENT '选择题选项JSON数组',
  `correct_answer` text DEFAULT NULL COMMENT '标准答案，多选题使用逗号分隔',
  `answer_analysis` text DEFAULT NULL COMMENT '答案解析',
  `difficulty` varchar(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '难度：EASY/MEDIUM/HARD',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1启用，0停用',
  `created_by` bigint unsigned NOT NULL COMMENT '创建老师ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_question_type` (`question_type`) USING BTREE,
  KEY `idx_question_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考试题库表';

CREATE TABLE IF NOT EXISTS `exam_paper` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `paper_name` varchar(200) NOT NULL COMMENT '试卷名称',
  `description` varchar(500) DEFAULT NULL COMMENT '试卷说明',
  `duration_minutes` int NOT NULL COMMENT '考试时长，单位分钟',
  `total_score` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '试卷总分',
  `pass_score` decimal(6,2) NOT NULL DEFAULT '60.00' COMMENT '及格分',
  `status` varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT/ENABLED/DISABLED',
  `created_by` bigint unsigned NOT NULL COMMENT '创建老师ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_paper_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考试试卷表';

CREATE TABLE IF NOT EXISTS `exam_paper_question` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `paper_id` bigint unsigned NOT NULL COMMENT '试卷ID',
  `question_id` bigint unsigned NOT NULL COMMENT '题目ID',
  `question_score` decimal(6,2) NOT NULL COMMENT '本题分值',
  `sort_num` int NOT NULL DEFAULT '0' COMMENT '题目顺序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_paper_question` (`paper_id`,`question_id`) USING BTREE,
  KEY `idx_paper_question_question` (`question_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='试卷题目关联表';

CREATE TABLE IF NOT EXISTS `exam_schedule` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '考试发布ID',
  `paper_id` bigint unsigned NOT NULL COMMENT '试卷ID',
  `class_id` bigint unsigned NOT NULL COMMENT '参加考试的班级ID',
  `exam_name` varchar(200) NOT NULL COMMENT '考试名称',
  `start_time` datetime NOT NULL COMMENT '考试开始时间',
  `end_time` datetime NOT NULL COMMENT '考试截止时间',
  `status` varchar(20) NOT NULL DEFAULT 'PUBLISHED' COMMENT '状态：PUBLISHED/CLOSED/CANCELLED',
  `answer_published` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否由老师提前发布答案',
  `answer_publish_time` datetime DEFAULT NULL COMMENT '答案发布时间',
  `created_by` bigint unsigned NOT NULL COMMENT '发布老师ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_exam_schedule_class` (`class_id`) USING BTREE,
  KEY `idx_exam_schedule_time` (`start_time`,`end_time`) USING BTREE,
  KEY `idx_exam_schedule_paper` (`paper_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考试发布表';

CREATE TABLE IF NOT EXISTS `exam_attempt` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '考试记录ID',
  `schedule_id` bigint unsigned NOT NULL COMMENT '考试发布ID',
  `student_id` bigint unsigned NOT NULL COMMENT '学员ID',
  `student_no` varchar(64) NOT NULL COMMENT '学号快照',
  `student_name_snapshot` varchar(100) NOT NULL COMMENT '学员姓名快照',
  `start_time` datetime NOT NULL COMMENT '开始答题时间',
  `submit_time` datetime DEFAULT NULL COMMENT '交卷时间',
  `status` varchar(20) NOT NULL DEFAULT 'IN_PROGRESS' COMMENT '状态：IN_PROGRESS/SUBMITTED/REVIEWED',
  `objective_score` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '客观题得分',
  `subjective_score` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '主观题得分',
  `total_score` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '总得分',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_schedule_student` (`schedule_id`,`student_id`) USING BTREE,
  KEY `idx_attempt_student` (`student_id`) USING BTREE,
  KEY `idx_attempt_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学员考试记录表';

CREATE TABLE IF NOT EXISTS `exam_answer` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '答题记录ID',
  `attempt_id` bigint unsigned NOT NULL COMMENT '考试记录ID',
  `paper_question_id` bigint unsigned NOT NULL COMMENT '试卷题目关联ID',
  `question_id` bigint unsigned NOT NULL COMMENT '题目ID',
  `answer_content` text DEFAULT NULL COMMENT '学员答案',
  `is_correct` tinyint(1) DEFAULT NULL COMMENT '客观题是否正确，主观题为空',
  `score` decimal(6,2) DEFAULT NULL COMMENT '本题得分',
  `review_comment` varchar(500) DEFAULT NULL COMMENT '老师批改意见',
  `review_teacher_id` bigint unsigned DEFAULT NULL COMMENT '批改老师ID',
  `review_time` datetime DEFAULT NULL COMMENT '批改时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_attempt_question` (`attempt_id`,`paper_question_id`) USING BTREE,
  KEY `idx_answer_attempt` (`attempt_id`) USING BTREE,
  KEY `idx_answer_question` (`question_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学员逐题答案表';

-- 管理端权限数据。若权限码已经存在，则不会重复插入。
INSERT INTO `sys_permission`
(`parent_id`, `permission_code`, `permission_name`, `permission_type`, `api_path`, `sort_num`, `description`, `status`, `created_at`, `updated_at`)
SELECT 106, 'menu:teaching:exam', '考试管理', 'MENU', NULL, 8, '教学管理下的考试管理菜单', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `permission_code` = 'menu:teaching:exam');

SET @exam_menu_id = (SELECT id FROM `sys_permission` WHERE permission_code = 'menu:teaching:exam' LIMIT 1);

INSERT INTO `sys_permission`
(`parent_id`, `permission_code`, `permission_name`, `permission_type`, `api_path`, `sort_num`, `description`, `status`, `created_at`, `updated_at`)
SELECT @exam_menu_id, 'exam:view', '查看考试', 'API', '/api/exams/**', 1, '查看题库、试卷、考试与阅卷数据', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `permission_code` = 'exam:view');

INSERT INTO `sys_permission`
(`parent_id`, `permission_code`, `permission_name`, `permission_type`, `api_path`, `sort_num`, `description`, `status`, `created_at`, `updated_at`)
SELECT @exam_menu_id, 'exam:manage', '管理考试', 'API', '/api/exams/**', 2, '维护题库、试卷、考试发布和阅卷', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `permission_code` = 'exam:manage');

INSERT INTO `sys_permission`
(`parent_id`, `permission_code`, `permission_name`, `permission_type`, `api_path`, `sort_num`, `description`, `status`, `created_at`, `updated_at`)
SELECT @exam_menu_id, 'button:exam:create', '新增考试内容按钮', 'BUTTON', NULL, 1, '新增题目、试卷和考试按钮', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `permission_code` = 'button:exam:create');

INSERT INTO `sys_permission`
(`parent_id`, `permission_code`, `permission_name`, `permission_type`, `api_path`, `sort_num`, `description`, `status`, `created_at`, `updated_at`)
SELECT @exam_menu_id, 'button:exam:update', '编辑考试内容按钮', 'BUTTON', NULL, 2, '编辑题目、试卷和考试按钮', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `permission_code` = 'button:exam:update');

INSERT INTO `sys_permission`
(`parent_id`, `permission_code`, `permission_name`, `permission_type`, `api_path`, `sort_num`, `description`, `status`, `created_at`, `updated_at`)
SELECT @exam_menu_id, 'button:exam:review', '考试阅卷按钮', 'BUTTON', NULL, 3, '查看考试记录、批改主观题和发布答案', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `permission_code` = 'button:exam:review');

-- 超级管理员默认获得考试模块全部权限，其他角色仍由权限管理页面按需分配。
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`, `created_at`)
SELECT role.id, permission.id, NOW()
FROM `sys_role` role
JOIN `sys_permission` permission
  ON permission.permission_code IN (
      'menu:teaching:exam',
      'exam:view',
      'exam:manage',
      'button:exam:create',
      'button:exam:update',
      'button:exam:review'
  )
WHERE role.role_code = 'SUPER_ADMIN'
  AND NOT EXISTS (
      SELECT 1
      FROM `sys_role_permission` relation
      WHERE relation.role_id = role.id
        AND relation.permission_id = permission.id
  );
