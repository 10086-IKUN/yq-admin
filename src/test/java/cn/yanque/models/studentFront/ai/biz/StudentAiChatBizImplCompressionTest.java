package cn.yanque.models.studentFront.ai.biz;

import cn.yanque.models.studentFront.ai.biz.impl.StudentAiChatBizImpl;
import cn.yanque.models.studentFront.ai.config.AiChatProperties;
import cn.yanque.models.studentFront.ai.pojo.entity.AiChatMessageEntity;
import cn.yanque.models.studentFront.ai.pojo.entity.AiChatSessionEntity;
import cn.yanque.models.studentFront.ai.pojo.vo.res.AiChatCompressionRes;
import cn.yanque.models.studentFront.ai.service.AiChatPythonClient;
import cn.yanque.models.studentFront.ai.service.StudentAiChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StudentAiChatBizImplCompressionTest {

    private StudentAiChatService chatService;
    private AiChatPythonClient pythonClient;
    private StudentAiChatBizImpl biz;

    @BeforeEach
    void setUp() {
        chatService = mock(StudentAiChatService.class);
        pythonClient = mock(AiChatPythonClient.class);
        AiChatProperties properties = new AiChatProperties();
        properties.setCompressionKeepRecentMessages(4);
        properties.setCompressionBatchSize(40);

        biz = new StudentAiChatBizImpl();
        ReflectionTestUtils.setField(biz, "studentAiChatService", chatService);
        ReflectionTestUtils.setField(biz, "aiChatPythonClient", pythonClient);
        ReflectionTestUtils.setField(biz, "properties", properties);
    }

    @Test
    void manualCompressionKeepsRecentMessagesAndCompressesCompleteTurns() throws Exception {
        AiChatSessionEntity session = new AiChatSessionEntity();
        session.setId(33L);
        session.setStudentId(12L);
        session.setSummaryTokenCount(0);

        List<AiChatMessageEntity> messages = buildMessages(10);
        when(chatService.getSession(33L, 12L)).thenReturn(session);
        when(chatService.listUncompressedHistory(33L, 12L)).thenReturn(messages);
        when(pythonClient.summarize(eq(12L), eq(33L), eq(null), any())).thenReturn("【稳定信息】\n学生正在学习 AI");
        when(chatService.applyCompression(eq(33L), eq(12L), eq(null), any(), any(), eq(6L))).thenReturn(6);

        AiChatCompressionRes result = biz.compressSession(33L, 12L);

        assertTrue(result.getCompressed());
        assertEquals(6, result.getCompressedMessageCount());
        assertEquals(4, result.getKeptRecentMessageCount());
        assertEquals(6L, result.getCompressedUntilMessageId());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AiChatMessageEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(pythonClient).summarize(eq(12L), eq(33L), eq(null), captor.capture());
        assertEquals(6, captor.getValue().size());
        assertEquals("assistant", captor.getValue().get(5).getRole());
    }

    private List<AiChatMessageEntity> buildMessages(int count) {
        List<AiChatMessageEntity> messages = new ArrayList<>();
        for (int index = 1; index <= count; index++) {
            AiChatMessageEntity message = new AiChatMessageEntity();
            message.setId((long) index);
            message.setSessionId(33L);
            message.setStudentId(12L);
            message.setRole(index % 2 == 1 ? "user" : "assistant");
            message.setContent("消息 " + index);
            if ("assistant".equals(message.getRole())) {
                message.setFinishReason("stop");
            }
            messages.add(message);
        }
        return messages;
    }
}
