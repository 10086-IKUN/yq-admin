-- 将 Navicat 导出的初始化数据调整为当前提示词中心的发布规则。
ALTER TABLE prompt_template_version
    ALTER COLUMN status SET DEFAULT 'INACTIVE';

-- 每个模板只能有一个生效版本，以主表 active_version_id 为准。
UPDATE prompt_template_version SET status = 'INACTIVE';
UPDATE prompt_template_version v
JOIN prompt_template t ON t.active_version_id = v.id
SET v.status = 'ACTIVE';

INSERT INTO sys_permission
(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT 0, 'menu:ai:prompt', '提示词管理', 'MENU', '/yq-admin/api/promptTemplates/**', 652,
       '管理 AI 提示词及版本', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE permission_code = 'menu:ai:prompt');

INSERT INTO sys_role_permission(role_id, permission_id, created_at)
SELECT r.id, p.id, NOW()
FROM sys_role r
JOIN sys_permission p ON p.permission_code = 'menu:ai:prompt'
WHERE r.role_code = 'SUPER_ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_permission rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
