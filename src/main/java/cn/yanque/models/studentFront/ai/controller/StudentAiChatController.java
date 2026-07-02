package cn.yanque.models.studentFront.ai.controller;

import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.ai.biz.StudentAiChatBiz;
import cn.yanque.models.studentFront.ai.pojo.vo.req.AiChatCreateSessionReq;
import cn.yanque.models.studentFront.ai.pojo.vo.req.AiChatSendReq;
import cn.yanque.models.studentFront.ai.pojo.vo.res.AiChatMessageRes;
import cn.yanque.models.studentFront.ai.pojo.vo.res.AiChatSessionRes;
import cn.yanque.models.studentFront.util.StudentAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/student/ai-chat")
@SkipPermission
@Tag(name = "StudentAiChatController", description = "学生端 AI 问答")

/**
 * 学生端 AI 问答接口。
 *
 * <p>负责会话列表、消息列表、删除会话和 SSE 流式问答入口；学生身份统一从请求上下文读取。</p>
 */
public class StudentAiChatController {

    @Autowired
    private StudentAiChatBiz studentAiChatBiz;

    @GetMapping("/sessions")
    @Operation(description = "查询 AI 问答会话列表")
    public ApiResponse<List<AiChatSessionRes>> listSessions(HttpServletRequest request) {
        Long studentId = StudentAuthUtil.getStudentId(request);
        return ApiResponse.success(studentAiChatBiz.listSessions(studentId));
    }

    @PostMapping("/sessions")
    @Operation(description = "创建 AI 问答会话")
    public ApiResponse<AiChatSessionRes> createSession(@RequestBody(required = false) AiChatCreateSessionReq req,
                                                       HttpServletRequest request) {
        EduStudentEntity student = StudentAuthUtil.getStudent(request);
        return ApiResponse.success(studentAiChatBiz.createSession(student.getId(), student.getStudentCode(), req));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    @Operation(description = "查询 AI 问答消息列表")
    public ApiResponse<List<AiChatMessageRes>> listMessages(@PathVariable Long sessionId, HttpServletRequest request) {
        Long studentId = StudentAuthUtil.getStudentId(request);
        return ApiResponse.success(studentAiChatBiz.listMessages(sessionId, studentId));
    }

    @DeleteMapping("/sessions/{sessionId}")
    @Operation(description = "删除 AI 问答会话")
    public ApiResponse<Void> deleteSession(@PathVariable Long sessionId, HttpServletRequest request) {
        Long studentId = StudentAuthUtil.getStudentId(request);
        studentAiChatBiz.deleteSession(sessionId, studentId);
        return ApiResponse.success();
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(description = "发送 AI 问答消息")
    public SseEmitter stream(@Valid @RequestBody AiChatSendReq req, HttpServletRequest request) {
        EduStudentEntity student = StudentAuthUtil.getStudent(request);
        return studentAiChatBiz.stream(student.getId(), student.getStudentCode(), req);
    }
}
