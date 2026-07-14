CREATE TABLE IF NOT EXISTS knowledge_base (
    id bigint PRIMARY KEY AUTO_INCREMENT COMMENT '知识库ID',
    knowledge_base_name varchar(100) NOT NULL COMMENT '知识库名称',
    description varchar(500) NULL COMMENT '描述',
    status varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/INACTIVE',
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_knowledge_base_name (knowledge_base_name)
) COMMENT '知识库';

CREATE TABLE IF NOT EXISTS knowledge_document (
    id bigint PRIMARY KEY AUTO_INCREMENT COMMENT '文档ID',
    knowledge_base_id bigint NOT NULL COMMENT '知识库ID',
    document_name varchar(200) NOT NULL COMMENT '文档名称',
    object_key varchar(500) NOT NULL COMMENT 'TOS对象Key',
    file_size bigint NULL COMMENT '文件大小',
    version varchar(50) NULL COMMENT '版本号',
    index_status varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '入库状态：PENDING/INDEXING/INDEXED/FAILED',
    chunk_count int NULL COMMENT '切片数量',
    vector_dim int NULL COMMENT '向量维度',
    error_message varchar(1000) NULL COMMENT '失败原因',
    indexed_at datetime NULL COMMENT '入库完成时间',
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_knowledge_document_base (knowledge_base_id),
    KEY idx_knowledge_document_status (index_status)
) COMMENT '知识库文档';

INSERT INTO knowledge_base (knowledge_base_name, description, status, created_at, updated_at)
SELECT '默认知识库', '默认知识库', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM knowledge_base
    WHERE knowledge_base_name = '默认知识库'
);

INSERT INTO sys_permission
(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT 0, 'menu:knowledge', '知识库', 'MENU', NULL, 600, '知识库管理菜单', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_permission
    WHERE permission_code = 'menu:knowledge'
);

SET @knowledge_parent_id = (
    SELECT id
    FROM sys_permission
    WHERE permission_code = 'menu:knowledge'
    LIMIT 1
);

INSERT INTO sys_permission
(parent_id, permission_code, permission_name, permission_type, api_path, sort_num, description, status, created_at, updated_at)
SELECT @knowledge_parent_id, code, name, 'API', path, sort_num, description, 'ACTIVE', NOW(), NOW()
FROM (
    SELECT 'knowledge:base:view' AS code, '分页/详情查询知识库' AS name, '/yq-admin/api/knowledgeBases/**' AS path, 601 AS sort_num, '查询知识库和文档列表' AS description
    UNION ALL SELECT 'knowledge:base:create', '新增知识库', '/yq-admin/api/knowledgeBases', 602, '新增知识库'
    UNION ALL SELECT 'knowledge:base:update', '修改知识库', '/yq-admin/api/knowledgeBases/*', 603, '修改知识库'
    UNION ALL SELECT 'knowledge:base:delete', '删除知识库', '/yq-admin/api/knowledgeBases/*', 604, '删除知识库'
    UNION ALL SELECT 'knowledge:document:index', '知识库文档入库', '/yq-admin/api/knowledgeBases/*/documents/index', 605, '上传文档并入库'
    UNION ALL SELECT 'knowledge:document:view', '查询知识库文档', '/yq-admin/api/knowledgeDocuments/**', 606, '查询文档下载地址和切块'
    UNION ALL SELECT 'knowledge:document:delete', '删除知识库文档', '/yq-admin/api/knowledgeDocuments/*', 607, '删除知识库文档记录'
    UNION ALL SELECT 'knowledge:search', '知识库语义检索', '/yq-admin/api/knowledgeSearch/search', 608, '知识库语义检索'
) item
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_permission p
    WHERE p.permission_code = item.code
);
