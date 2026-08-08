package cn.yanque.models.ai.prompt.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.ai.prompt.pojo.PromptTemplateReq;
import cn.yanque.models.ai.prompt.pojo.PromptTemplateRes;
import cn.yanque.models.ai.prompt.pojo.PromptVersionReq;
import cn.yanque.models.ai.prompt.pojo.PromptVersionRes;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PromptTemplateService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public PageResult<PromptTemplateRes> page(String code, String name, String status, Integer pageNum, Integer pageSize) {
        int current = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null ? 10 : Math.min(Math.max(pageSize, 1), 100);
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> args = new ArrayList<>();
        if (hasText(code)) {
            where.append(" AND t.code LIKE ?");
            args.add("%" + code.trim() + "%");
        }
        if (hasText(name)) {
            where.append(" AND t.name LIKE ?");
            args.add("%" + name.trim() + "%");
        }
        if (hasText(status)) {
            where.append(" AND t.status = ?");
            args.add(normalizeStatus(status));
        }

        Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM prompt_template t" + where, Long.class, args.toArray());
        List<Object> pageArgs = new ArrayList<>(args);
        pageArgs.add(size);
        pageArgs.add((current - 1) * size);
        String sql = templateSelect() + where + " ORDER BY t.id DESC LIMIT ? OFFSET ?";
        List<PromptTemplateRes> records = jdbcTemplate.query(sql, this::mapTemplate, pageArgs.toArray());
        return new PageResult<>(total == null ? 0L : total, current, size, records);
    }

    public PromptTemplateRes detail(Long id) {
        List<PromptTemplateRes> rows = jdbcTemplate.query(templateSelect() + " WHERE t.id = ?", this::mapTemplate, id);
        if (rows.isEmpty()) {
            throw new BusinessException(404, "提示词模板不存在");
        }
        return rows.get(0);
    }

    public PromptTemplateRes activeByCode(String code) {
        List<PromptTemplateRes> rows = jdbcTemplate.query(
                templateSelect() + " WHERE t.code = ? AND t.status = 'ACTIVE' AND v.status = 'ACTIVE'",
                this::mapTemplate,
                code
        );
        if (rows.isEmpty() || !hasText(rows.get(0).getActiveContent())) {
            throw new BusinessException(404, "未找到已生效的提示词：" + code);
        }
        return rows.get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long create(PromptTemplateReq req) {
        if (!hasText(req.getContent())) {
            throw new BusinessException(400, "初始提示词内容不能为空");
        }
        String code = req.getCode().trim();
        validateContent(code, req.getContent());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        try {
            Long templateId = insertAndReturnKey(
                    "INSERT INTO prompt_template(code,name,description,active_version_id,status,created_at,updated_at) VALUES(?,?,?,NULL,?,?,?)",
                    code, req.getName().trim(), blankToNull(req.getDescription()), normalizeStatus(req.getStatus()), now, now
            );
            Long versionId = insertAndReturnKey(
                    "INSERT INTO prompt_template_version(template_id,version_no,content,remark,status,created_at,updated_at) VALUES(?,1,?,?, 'ACTIVE',?,?)",
                    templateId, req.getContent().trim(), blankToNull(req.getRemark()), now, now
            );
            jdbcTemplate.update("UPDATE prompt_template SET active_version_id=?, updated_at=? WHERE id=?", versionId, now, templateId);
            return templateId;
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(409, "提示词编码已存在");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Long update(Long id, PromptTemplateReq req) {
        detail(id);
        try {
            jdbcTemplate.update(
                    "UPDATE prompt_template SET code=?,name=?,description=?,status=?,updated_at=NOW() WHERE id=?",
                    req.getCode().trim(), req.getName().trim(), blankToNull(req.getDescription()), normalizeStatus(req.getStatus()), id
            );
            return id;
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(409, "提示词编码已存在");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Long delete(Long id) {
        detail(id);
        jdbcTemplate.update("UPDATE prompt_template SET active_version_id=NULL WHERE id=?", id);
        jdbcTemplate.update("DELETE FROM prompt_template_version WHERE template_id=?", id);
        jdbcTemplate.update("DELETE FROM prompt_template WHERE id=?", id);
        return id;
    }

    public List<PromptVersionRes> versions(Long templateId) {
        detail(templateId);
        return jdbcTemplate.query(
                "SELECT id,template_id,version_no,content,remark,status,created_at,updated_at FROM prompt_template_version WHERE template_id=? ORDER BY version_no DESC",
                this::mapVersion,
                templateId
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createVersion(Long templateId, PromptVersionReq req) {
        PromptTemplateRes template = detail(templateId);
        validateContent(template.getCode(), req.getContent());
        Integer next = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(version_no),0)+1 FROM prompt_template_version WHERE template_id=?",
                Integer.class,
                templateId
        );
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return insertAndReturnKey(
                "INSERT INTO prompt_template_version(template_id,version_no,content,remark,status,created_at,updated_at) VALUES(?,?,?,?, 'INACTIVE',?,?)",
                templateId, next, req.getContent().trim(), blankToNull(req.getRemark()), now, now
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public Long activate(Long templateId, Long versionId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM prompt_template_version WHERE id=? AND template_id=?",
                Integer.class,
                versionId,
                templateId
        );
        if (count == null || count == 0) {
            throw new BusinessException(404, "提示词版本不存在");
        }
        jdbcTemplate.update("UPDATE prompt_template_version SET status='INACTIVE',updated_at=NOW() WHERE template_id=?", templateId);
        jdbcTemplate.update("UPDATE prompt_template_version SET status='ACTIVE',updated_at=NOW() WHERE id=?", versionId);
        jdbcTemplate.update("UPDATE prompt_template SET active_version_id=?,status='ACTIVE',updated_at=NOW() WHERE id=?", versionId, templateId);
        return versionId;
    }

    private String templateSelect() {
        return "SELECT t.id,t.code,t.name,t.description,t.active_version_id,t.status,t.created_at,t.updated_at," +
                "v.version_no active_version_no,v.content active_content FROM prompt_template t " +
                "LEFT JOIN prompt_template_version v ON v.id=t.active_version_id";
    }

    private PromptTemplateRes mapTemplate(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        PromptTemplateRes res = new PromptTemplateRes();
        res.setId(rs.getLong("id"));
        res.setCode(rs.getString("code"));
        res.setName(rs.getString("name"));
        res.setDescription(rs.getString("description"));
        long activeVersionId = rs.getLong("active_version_id");
        res.setActiveVersionId(rs.wasNull() ? null : activeVersionId);
        int activeVersionNo = rs.getInt("active_version_no");
        res.setActiveVersionNo(rs.wasNull() ? null : activeVersionNo);
        res.setActiveContent(rs.getString("active_content"));
        res.setStatus(rs.getString("status"));
        res.setCreatedAt(format(rs.getTimestamp("created_at")));
        res.setUpdatedAt(format(rs.getTimestamp("updated_at")));
        return res;
    }

    private PromptVersionRes mapVersion(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        PromptVersionRes res = new PromptVersionRes();
        res.setId(rs.getLong("id"));
        res.setTemplateId(rs.getLong("template_id"));
        res.setVersionNo(rs.getInt("version_no"));
        res.setContent(rs.getString("content"));
        res.setRemark(rs.getString("remark"));
        res.setStatus(rs.getString("status"));
        res.setCreatedAt(format(rs.getTimestamp("created_at")));
        res.setUpdatedAt(format(rs.getTimestamp("updated_at")));
        return res;
    }

    private Long insertAndReturnKey(String sql, Object... args) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int index = 0; index < args.length; index++) {
                statement.setObject(index + 1, args[index]);
            }
            return statement;
        }, holder);
        if (holder.getKey() == null) {
            throw new BusinessException(500, "新增提示词数据失败");
        }
        return holder.getKey().longValue();
    }

    private String normalizeStatus(String status) {
        String value = hasText(status) ? status.trim().toUpperCase(Locale.ROOT) : "ACTIVE";
        if (!"ACTIVE".equals(value) && !"INACTIVE".equals(value)) {
            throw new BusinessException(400, "状态只能是 ACTIVE 或 INACTIVE");
        }
        return value;
    }

    private void validateContent(String code, String content) {
        boolean requiresMessages = "ai_chat_system".equals(code) || code.startsWith("text_to_sql_");
        if (!requiresMessages && !content.trim().startsWith("[")) {
            return;
        }
        try {
            Object parsed = JSON.parse(content);
            if (!(parsed instanceof JSONArray messages) || messages.isEmpty()) {
                throw new IllegalArgumentException();
            }
            for (Object item : messages) {
                if (!(item instanceof JSONObject message)) {
                    throw new IllegalArgumentException();
                }
                String role = message.getString("role");
                String messageContent = message.getString("content");
                if (!("system".equals(role) || "human".equals(role) || "user".equals(role)
                        || "assistant".equals(role) || "ai".equals(role) || "placeholder".equals(role))
                        || !hasText(messageContent)) {
                    throw new IllegalArgumentException();
                }
            }
        } catch (Exception exception) {
            throw new BusinessException(400, "提示词消息格式无效，每项必须包含合法的 role 和 content");
        }
    }

    private String blankToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String format(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime().toString();
    }
}
