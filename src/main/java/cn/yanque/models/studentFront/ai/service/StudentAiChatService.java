package cn.yanque.models.studentFront.ai.service;

import cn.yanque.models.studentFront.ai.pojo.entity.AiChatMessageEntity;
import cn.yanque.models.studentFront.ai.pojo.entity.AiChatSessionEntity;

import java.util.List;


/**
 * AI 问答数据服务接口。
 *
 * <p>封装会话和消息表的读写规则，业务编排层不直接操作 Mapper。</p>
 */
public interface StudentAiChatService {

    List<AiChatSessionEntity> listSessions(Long studentId);

    AiChatSessionEntity createSession(Long studentId, String studentCode, String title);

    AiChatSessionEntity getSession(Long sessionId, Long studentId);

    void deleteSession(Long sessionId, Long studentId);

    List<AiChatMessageEntity> listMessages(Long sessionId, Long studentId);

    /**
     * 读取最近若干条历史消息，作为下一轮模型调用的上下文。
     */
    List<AiChatMessageEntity> listRecentHistory(Long sessionId, Long studentId, Integer limit);

    List<AiChatMessageEntity> listUncompressedHistory(Long sessionId, Long studentId);

    AiChatMessageEntity addMessage(AiChatMessageEntity entity);

    void refreshSessionStats(Long sessionId);

    int applyCompression(Long sessionId,
                         Long studentId,
                         Long expectedCompressedUntilMessageId,
                         String summary,
                         Integer summaryTokenCount,
                         Long compressedUntilMessageId);
}
