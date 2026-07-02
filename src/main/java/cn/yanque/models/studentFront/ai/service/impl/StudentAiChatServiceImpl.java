package cn.yanque.models.studentFront.ai.service.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.studentFront.ai.mapper.AiChatMessageMapper;
import cn.yanque.models.studentFront.ai.mapper.AiChatSessionMapper;
import cn.yanque.models.studentFront.ai.pojo.entity.AiChatMessageEntity;
import cn.yanque.models.studentFront.ai.pojo.entity.AiChatSessionEntity;
import cn.yanque.models.studentFront.ai.service.StudentAiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service

/**
 * AI 问答会话和消息的数据服务。
 *
 * <p>所有会话读取都带 studentId 校验，避免学生通过猜 sessionId 访问他人的对话。</p>
 */
public class StudentAiChatServiceImpl implements StudentAiChatService {

    @Autowired
    private AiChatSessionMapper aiChatSessionMapper;

    @Autowired
    private AiChatMessageMapper aiChatMessageMapper;

    @Override
    public List<AiChatSessionEntity> listSessions(Long studentId) {
        return aiChatSessionMapper.selectActiveByStudentId(studentId);
    }

    @Override
    public AiChatSessionEntity createSession(Long studentId, String studentCode, String title) {
        LocalDateTime now = LocalDateTime.now();
        AiChatSessionEntity entity = new AiChatSessionEntity();
        entity.setStudentId(studentId);
        entity.setStudentCode(studentCode);
        entity.setTitle(normalizeTitle(title));
        entity.setStatus("ACTIVE");
        entity.setMessageCount(0);
        entity.setLastMessageAt(now);
        entity.setSummaryTokenCount(0);
        entity.setArchiveStatus("NONE");
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        aiChatSessionMapper.insert(entity);
        return entity;
    }

    @Override
    public AiChatSessionEntity getSession(Long sessionId, Long studentId) {
        // 会话必须同时按 sessionId 和 studentId 查询，防止学生通过猜 ID 读取他人对话。
        AiChatSessionEntity entity = aiChatSessionMapper.selectByIdAndStudentId(sessionId, studentId);
        if (entity == null) {
            throw new BusinessException(404, "对话不存在");
        }
        return entity;
    }

    @Override
    public void deleteSession(Long sessionId, Long studentId) {
        int rows = aiChatSessionMapper.updateStatus(sessionId, studentId, "DELETED");
        if (rows == 0) {
            throw new BusinessException(404, "对话不存在");
        }
    }

    @Override
    public List<AiChatMessageEntity> listMessages(Long sessionId, Long studentId) {
        getSession(sessionId, studentId);
        return aiChatMessageMapper.selectBySessionIdAndStudentId(sessionId, studentId);
    }

    @Override
    public List<AiChatMessageEntity> listRecentHistory(Long sessionId, Long studentId, Integer limit) {
        getSession(sessionId, studentId);
        return aiChatMessageMapper.selectRecentHistory(sessionId, studentId, limit);
    }

    @Override
    public AiChatMessageEntity addMessage(AiChatMessageEntity entity) {
        // 入库前补齐默认值，和 ai_chat_message 表的默认约定保持一致。
        if (entity.getContentType() == null) {
            entity.setContentType("markdown");
        }
        if (entity.getTokenCount() == null) {
            entity.setTokenCount(0);
        }
        if (entity.getCompressed() == null) {
            entity.setCompressed(false);
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        aiChatMessageMapper.insert(entity);
        return entity;
    }

    @Override
    public void refreshSessionStats(Long sessionId) {
        aiChatSessionMapper.refreshStats(sessionId);
    }

    private String normalizeTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "新对话";
        }
        String value = title.trim().replaceAll("\\s+", " ");
        return value.length() > 40 ? value.substring(0, 40) : value;
    }
}
