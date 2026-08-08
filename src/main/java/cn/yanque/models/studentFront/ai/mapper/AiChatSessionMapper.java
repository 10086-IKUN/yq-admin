package cn.yanque.models.studentFront.ai.mapper;

import cn.yanque.models.studentFront.ai.pojo.entity.AiChatSessionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper

/**
 * AI 问答会话表访问接口。
 *
 * <p>所有学生端读取都带 studentId 条件，避免不同学生之间串会话。</p>
 */
public interface AiChatSessionMapper {

    int insert(AiChatSessionEntity entity);

    AiChatSessionEntity selectByIdAndStudentId(@Param("id") Long id, @Param("studentId") Long studentId);

    List<AiChatSessionEntity> selectActiveByStudentId(@Param("studentId") Long studentId);

    int updateTitle(@Param("id") Long id, @Param("studentId") Long studentId, @Param("title") String title);

    int updateStatus(@Param("id") Long id, @Param("studentId") Long studentId, @Param("status") String status);

    /**
     * 重新统计会话消息数和最后消息时间。
     */
    int refreshStats(@Param("id") Long id);

    int updateCompression(@Param("id") Long id,
                          @Param("studentId") Long studentId,
                          @Param("expectedCompressedUntilMessageId") Long expectedCompressedUntilMessageId,
                          @Param("summary") String summary,
                          @Param("summaryTokenCount") Integer summaryTokenCount,
                          @Param("compressedUntilMessageId") Long compressedUntilMessageId);
}
