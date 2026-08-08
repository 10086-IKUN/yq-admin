package cn.yanque.models.studentFront.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.studentFront.pojo.req.StudentMockInterviewCreateReq;
import cn.yanque.models.studentFront.pojo.res.StudentMockInterviewProfileRes;
import cn.yanque.models.studentFront.pojo.res.StudentMockInterviewVoiceStartRes;
import cn.yanque.models.studentFront.service.StudentMockInterviewService;
import cn.yanque.common.annotation.SkipPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/student/mock-interview")
@SkipPermission
@Tag(name = "StudentMockInterviewController", description = "学生端模拟面试")
public class StudentMockInterviewController {

    @Autowired
    private StudentMockInterviewService studentMockInterviewService;

    @GetMapping("profile")
    @Operation(description = "查询简历解析状态和最近一次模拟面试")
    public ApiResponse<StudentMockInterviewProfileRes> getProfile(@RequestAttribute("studentId") Long studentId) {
        return ApiResponse.success(studentMockInterviewService.getProfile(studentId));
    }

    @PostMapping("sessions")
    @Operation(description = "创建一场模拟面试并生成本场面试画像")
    public ApiResponse<StudentMockInterviewProfileRes> createSession(@RequestAttribute("studentId") Long studentId,
                                                                     @Valid @RequestBody StudentMockInterviewCreateReq req) {
        return ApiResponse.success(studentMockInterviewService.createSession(studentId, req));
    }

    @PostMapping("sessions/{sessionId}/voice/start")
    @Operation(description = "开始本场模拟面试实时语音会话")
    public ApiResponse<StudentMockInterviewVoiceStartRes> startVoice(@RequestAttribute("studentId") Long studentId,
                                                                     @PathVariable Long sessionId) {
        return ApiResponse.success(studentMockInterviewService.startVoice(studentId, sessionId));
    }

    @PostMapping("sessions/{sessionId}/voice/finish")
    @Operation(description = "结束本场模拟面试实时语音会话")
    public ApiResponse<StudentMockInterviewProfileRes> finishVoice(@RequestAttribute("studentId") Long studentId,
                                                                   @PathVariable Long sessionId) {
        return ApiResponse.success(studentMockInterviewService.finishVoice(studentId, sessionId));
    }
}
