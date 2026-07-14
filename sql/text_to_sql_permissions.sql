INSERT INTO sys_permission
(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT 0, 'menu:text-to-sql', '智能取数', 'MENU', NULL, 650, 'Text-to-SQL 数据查询工作台', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_permission
    WHERE permission_code = 'menu:text-to-sql'
);

SET @text_to_sql_parent_id = (
    SELECT id
    FROM sys_permission
    WHERE permission_code = 'menu:text-to-sql'
    LIMIT 1
);

INSERT INTO sys_permission
(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT @text_to_sql_parent_id, 'text-to-sql:query', '生成只读 SQL', 'API',
       '/yq-admin/api/textToSql/**', 651, '提交数据查询并补充查询条件', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_permission
    WHERE permission_code = 'text-to-sql:query'
);
