package cn.yanque.models.studentFront.ai.biz;

import cn.yanque.models.studentFront.ai.pojo.vo.req.AiChatCreateSessionReq;
import cn.yanque.models.studentFront.ai.pojo.vo.req.AiChatSendReq;
import cn.yanque.models.studentFront.ai.pojo.vo.res.AiChatMessageRes;
import cn.yanque.models.studentFront.ai.pojo.vo.res.AiChatCompressionRes;
import cn.yanque.models.studentFront.ai.pojo.vo.res.AiChatSessionRes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


/**
 * 学生端 AI 问答业务接口。
 *
 * <p>控制器只依赖这一层；会话、消息和流式问答的编排细节由实现类处理。</p>
 */
public interface StudentAiChatBiz {

    List<AiChatSessionRes> listSessions(Long studentId);

    AiChatSessionRes createSession(Long studentId, String studentCode, AiChatCreateSessionReq req);

    List<AiChatMessageRes> listMessages(Long sessionId, Long studentId);

    void deleteSession(Long sessionId, Long studentId);

    AiChatCompressionRes compressSession(Long sessionId, Long studentId);

    /**
     * 发起一次流式问答。
     *
     * <p>返回的 SseEmitter 会持续向前端发送 session、message_start、chunk、done 或 error 事件。</p>
     */
    SseEmitter stream(Long studentId, String studentCode, AiChatSendReq req);
}
