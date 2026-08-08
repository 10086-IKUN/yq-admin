package cn.yanque.models.studentFront.ai.service;

import cn.yanque.models.studentFront.ai.mapper.AiChatMessageMapper;
import cn.yanque.models.studentFront.ai.mapper.AiChatSessionMapper;
import cn.yanque.models.studentFront.ai.service.impl.StudentAiChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StudentAiChatServiceImplCompressionTest {

    private AiChatSessionMapper sessionMapper;
    private AiChatMessageMapper messageMapper;
    private StudentAiChatServiceImpl service;

    @BeforeEach
    void setUp() {
        sessionMapper = mock(AiChatSessionMapper.class);
        messageMapper = mock(AiChatMessageMapper.class);
        service = new StudentAiChatServiceImpl();
        ReflectionTestUtils.setField(service, "aiChatSessionMapper", sessionMapper);
        ReflectionTestUtils.setField(service, "aiChatMessageMapper", messageMapper);
    }

    @Test
    void appliesSummaryBeforeMarkingMessagesCompressed() {
        when(sessionMapper.updateCompression(33L, 12L, 100L, "摘要", 20, 126L)).thenReturn(1);
        when(messageMapper.markCompressedThrough(33L, 12L, 126L)).thenReturn(18);

        int compressed = service.applyCompression(33L, 12L, 100L, "摘要", 20, 126L);

        assertEquals(18, compressed);
        verify(messageMapper).markCompressedThrough(33L, 12L, 126L);
    }

    @Test
    void optimisticLockConflictDoesNotMarkMessages() {
        when(sessionMapper.updateCompression(33L, 12L, 100L, "摘要", 20, 126L)).thenReturn(0);

        int compressed = service.applyCompression(33L, 12L, 100L, "摘要", 20, 126L);

        assertEquals(-1, compressed);
        verify(messageMapper, never()).markCompressedThrough(33L, 12L, 126L);
    }
}
