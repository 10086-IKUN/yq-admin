CREATE TABLE IF NOT EXISTS prompt_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    active_version_id BIGINT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_prompt_template_code (code),
    KEY idx_prompt_template_active_version (active_version_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS prompt_template_version (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id BIGINT NOT NULL,
    version_no INT NOT NULL,
    content TEXT NOT NULL,
    remark VARCHAR(500) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_prompt_template_version (template_id, version_no),
    KEY idx_prompt_template_version_template (template_id),
    CONSTRAINT fk_prompt_version_template FOREIGN KEY (template_id) REFERENCES prompt_template(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO sys_permission
(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT 0, 'menu:ai:prompt', '提示词管理', 'MENU', '/yq-admin/api/promptTemplates/**', 652,
       '管理 AI 提示词及版本', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE permission_code='menu:ai:prompt');

INSERT INTO sys_role_permission(role_id, permission_id, created_at)
SELECT r.id, p.id, NOW() FROM sys_role r JOIN sys_permission p ON p.permission_code='menu:ai:prompt'
WHERE r.role_code='SUPER_ADMIN'
  AND NOT EXISTS (SELECT 1 FROM sys_role_permission rp WHERE rp.role_id=r.id AND rp.permission_id=p.id);
