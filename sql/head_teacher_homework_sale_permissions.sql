-- Grant homework operation permissions and sale read-only permissions to head_teacher.
-- This script is idempotent.

SET @homework_parent_id = (
    SELECT id
    FROM sys_permission
    WHERE permission_code = 'menu:teaching:homework'
    LIMIT 1
);

SET @sale_parent_id = (
    SELECT id
    FROM sys_permission
    WHERE permission_code IN ('menu:sale', 'sale:menu')
    ORDER BY id ASC
    LIMIT 1
);

SET @homework_parent_id = IFNULL(@homework_parent_id, 0);
SET @sale_parent_id = IFNULL(@sale_parent_id, 0);

INSERT INTO sys_permission
(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT 0, 'menu:teaching:homework', 'Homework Menu', 'MENU', NULL, 400, 'Show homework menu', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE permission_code = 'menu:teaching:homework');

SET @homework_parent_id = (
    SELECT id
    FROM sys_permission
    WHERE permission_code = 'menu:teaching:homework'
    LIMIT 1
);

INSERT INTO sys_permission
(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT @homework_parent_id, code, name, type, path, sort_num, description, 'ACTIVE', NOW(), NOW()
FROM (
    SELECT 'homework:view' AS code, 'Homework View' AS name, 'API' AS type, '/api/homeworkAssignment/**' AS path, 401 AS sort_num, 'View homework assignments' AS description
    UNION ALL SELECT 'homework:create', 'Homework Create', 'API', '/api/homeworkAssignment', 402, 'Create homework assignments'
    UNION ALL SELECT 'homework:update', 'Homework Update', 'API', '/api/homeworkAssignment/**', 403, 'Update or delete homework assignments'
    UNION ALL SELECT 'homework:status', 'Homework Status', 'API', '/api/homeworkAssignment/*/close', 404, 'Close homework assignments'
    UNION ALL SELECT 'homework:answer', 'Homework Answer', 'API', '/api/homeworkAssignment/*/answer', 405, 'Publish homework answers'
    UNION ALL SELECT 'homework:submissions', 'Homework Submissions', 'API', '/api/homeworkAssignment/*/submissions', 406, 'View and review homework submissions'
    UNION ALL SELECT 'button:homework:create', 'Homework Button Create', 'BUTTON', NULL, 421, 'Show create homework button'
    UNION ALL SELECT 'button:homework:update', 'Homework Button Update', 'BUTTON', NULL, 422, 'Show update homework button'
    UNION ALL SELECT 'button:homework:delete', 'Homework Button Delete', 'BUTTON', NULL, 423, 'Show delete homework button'
    UNION ALL SELECT 'button:homework:toggle-status', 'Homework Button Status', 'BUTTON', NULL, 424, 'Show close homework button'
    UNION ALL SELECT 'button:homework:answer-manage', 'Homework Button Answer', 'BUTTON', NULL, 425, 'Show answer button'
    UNION ALL SELECT 'button:homework:submissions', 'Homework Button Submissions', 'BUTTON', NULL, 426, 'Show submissions button'
) item
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_permission p
    WHERE p.permission_code = item.code
);

INSERT INTO sys_permission
(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT @sale_parent_id, code, name, 'API', path, sort_num, description, 'ACTIVE', NOW(), NOW()
FROM (
    SELECT 'sale:product:list' AS code, 'Sale Product List' AS name, '/api/sale/products/**' AS path, 501 AS sort_num, 'View sale products' AS description
    UNION ALL SELECT 'sale:order:list', 'Sale Order List', '/api/sale/orders/**', 502, 'View sale orders'
) item
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_permission p
    WHERE p.permission_code = item.code
);

INSERT INTO sys_role_permission (role_id, permission_id, created_at)
SELECT r.id, p.id, NOW()
FROM sys_role r
JOIN sys_permission p ON p.permission_code IN (
    'menu:teaching:homework',
    'homework:view',
    'homework:create',
    'homework:update',
    'homework:status',
    'homework:answer',
    'homework:submissions',
    'button:homework:create',
    'button:homework:update',
    'button:homework:delete',
    'button:homework:toggle-status',
    'button:homework:answer-manage',
    'button:homework:submissions',
    'sale:product:list',
    'sale:order:list'
)
WHERE r.role_code = 'head_teacher'
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_permission rp
      WHERE rp.role_id = r.id
        AND rp.permission_id = p.id
  );
