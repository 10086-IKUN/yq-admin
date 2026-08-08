package cn.yanque.models.studentFront.service;

import cn.yanque.models.studentFront.pojo.res.StudentMockInterviewProfileRes;
import cn.yanque.models.studentFront.pojo.req.StudentMockInterviewCreateReq;
import cn.yanque.models.studentFront.pojo.res.StudentMockInterviewVoiceStartRes;

public interface StudentMockInterviewService {

    StudentMockInterviewProfileRes getProfile(Long studentId);

    StudentMockInterviewProfileRes createSession(Long studentId, StudentMockInterviewCreateReq req);

    StudentMockInterviewVoiceStartRes startVoice(Long studentId, Long sessionId);

    StudentMockInterviewProfileRes finishVoice(Long studentId, Long sessionId);
}

