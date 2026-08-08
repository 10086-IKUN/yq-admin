-- 日志检索与 AI 诊断权限。重复执行不会创建重复权限。

INSERT INTO sys_permission
    (parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT 0, 'menu:log-diagnosis', '日志诊断', 'MENU', NULL, 655,
       '按请求 GUID 检索火山日志并调用 AI 分析', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM sys_permission WHERE permission_code = 'menu:log-diagnosis'
);

SET @log_diagnosis_parent_id = (
    SELECT id FROM sys_permission WHERE permission_code = 'menu:log-diagnosis' LIMIT 1
);

INSERT INTO sys_permission
    (parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT @log_diagnosis_parent_id, 'api:log-diagnosis:context', '读取日志上下文', 'API',
       '/yq-admin/api/log-diagnosis/context', 656, '读取指定 GUID 的结构化日志上下文',
       'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM sys_permission WHERE permission_code = 'api:log-diagnosis:context'
);

INSERT INTO sys_permission
    (parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT @log_diagnosis_parent_id, 'api:log-diagnosis:analyze', '执行 AI 日志诊断', 'API',
       '/yq-admin/api/log-diagnosis/analyze', 657, '对指定 GUID 的日志和相关代码进行 AI 诊断',
       'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM sys_permission WHERE permission_code = 'api:log-diagnosis:analyze'
);

INSERT INTO sys_permission
    (parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT @log_diagnosis_parent_id, 'api:log-analysis:search', '检索火山日志', 'API',
       '/yq-admin/api/log-analysis/search', 658, '按条件直接检索火山 TLS 日志',
       'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM sys_permission WHERE permission_code = 'api:log-analysis:search'
);

-- 同步既有权限的展示信息，确保脚本升级或重复执行时也能修正名称与路径。
UPDATE sys_permission
SET parent_id = 0,
    permission_name = '日志诊断',
    permission_type = 'MENU',
    api_path = NULL,
    sort_num = 655,
    description = '按请求 GUID 检索火山日志并调用 AI 分析',
    status = 'ACTIVE',
    updated_at = NOW()
WHERE permission_code = 'menu:log-diagnosis';

UPDATE sys_permission
SET parent_id = @log_diagnosis_parent_id,
    permission_name = '读取日志上下文',
    permission_type = 'API',
    api_path = '/yq-admin/api/log-diagnosis/context',
    sort_num = 656,
    description = '读取指定 GUID 的结构化日志上下文',
    status = 'ACTIVE',
    updated_at = NOW()
WHERE permission_code = 'api:log-diagnosis:context';

UPDATE sys_permission
SET parent_id = @log_diagnosis_parent_id,
    permission_name = '执行 AI 日志诊断',
    permission_type = 'API',
    api_path = '/yq-admin/api/log-diagnosis/analyze',
    sort_num = 657,
    description = '对指定 GUID 的日志和相关代码进行 AI 诊断',
    status = 'ACTIVE',
    updated_at = NOW()
WHERE permission_code = 'api:log-diagnosis:analyze';

UPDATE sys_permission
SET parent_id = @log_diagnosis_parent_id,
    permission_name = '检索火山日志',
    permission_type = 'API',
    api_path = '/yq-admin/api/log-analysis/search',
    sort_num = 658,
    description = '按条件直接检索火山 TLS 日志',
    status = 'ACTIVE',
    updated_at = NOW()
WHERE permission_code = 'api:log-analysis:search';

INSERT INTO sys_role_permission (role_id, permission_id, created_at)
SELECT role.id, permission.id, NOW()
FROM sys_role role
JOIN sys_permission permission
  ON permission.permission_code IN (
      'menu:log-diagnosis',
      'api:log-diagnosis:context',
      'api:log-diagnosis:analyze',
      'api:log-analysis:search'
  )
WHERE role.role_code = 'SUPER_ADMIN'
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_permission existing
      WHERE existing.role_id = role.id
        AND existing.permission_id = permission.id
  );

INSERT INTO sys_config (`k`, `v`)
SELECT 'volcengine.tls.default.topic.id', ''
WHERE NOT EXISTS (
    SELECT 1 FROM sys_config WHERE `k` = 'volcengine.tls.default.topic.id'
);
