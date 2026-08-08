package cn.yanque.models.studentFront.ai.mapper;

import cn.yanque.models.studentFront.ai.pojo.entity.AiChatMessageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper

/**
 * AI 问答消息表访问接口。
 *
 * <p>用于保存学生和助手的完整消息，并按会话读取历史上下文。</p>
 */
public interface AiChatMessageMapper {

    int insert(AiChatMessageEntity entity);

    List<AiChatMessageEntity> selectBySessionIdAndStudentId(@Param("sessionId") Long sessionId,
                                                            @Param("studentId") Long studentId);

    /**
     * 查询最近的对话历史，供下一轮问答作为模型上下文。
     */
    List<AiChatMessageEntity> selectRecentHistory(@Param("sessionId") Long sessionId,
                                                   @Param("studentId") Long studentId,
                                                   @Param("limit") Integer limit);

    List<AiChatMessageEntity> selectUncompressedHistory(@Param("sessionId") Long sessionId,
                                                        @Param("studentId") Long studentId);

    int markCompressedThrough(@Param("sessionId") Long sessionId,
                              @Param("studentId") Long studentId,
                              @Param("messageId") Long messageId);
}
