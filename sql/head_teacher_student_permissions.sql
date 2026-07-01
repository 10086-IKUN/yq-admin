-- Grant student tag and visit permissions to the head_teacher role.
-- This script is idempotent.

SET @students_parent_id = (
    SELECT id
    FROM sys_permission
    WHERE permission_code IN ('menu:students', 'student:menu', 'student:view')
    ORDER BY id ASC
    LIMIT 1
);

SET @students_parent_id = IFNULL(@students_parent_id, 0);

INSERT INTO sys_permission
(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT @students_parent_id, 'student-tag:list', 'Student Tag List', 'API', '/api/student-tag/**', 310, 'Head teacher can view student tags', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE permission_code = 'student-tag:list');

INSERT INTO sys_permission
(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT @students_parent_id, 'student-tag:confirm', 'Student Tag Confirm', 'BUTTON', '/api/student-tag/*/confirm', 311, 'Head teacher can confirm student tags', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE permission_code = 'student-tag:confirm');

INSERT INTO sys_permission
(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT @students_parent_id, 'student-visit:list', 'Student Visit List', 'API', '/api/student-visit/**', 320, 'Head teacher can view student visits', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE permission_code = 'student-visit:list');

INSERT INTO sys_permission
(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT @students_parent_id, 'student-visit:submit', 'Student Visit Submit', 'BUTTON', '/api/student-visit', 321, 'Head teacher can submit student visits', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE permission_code = 'student-visit:submit');

INSERT INTO sys_role_permission (role_id, permission_id, created_at)
SELECT r.id, p.id, NOW()
FROM sys_role r
JOIN sys_permission p ON p.permission_code IN (
    'student-tag:list',
    'student-tag:confirm',
    'student-visit:list',
    'student-visit:submit'
)
WHERE r.role_code = 'head_teacher'
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_permission rp
      WHERE rp.role_id = r.id
        AND rp.permission_id = p.id
  );
