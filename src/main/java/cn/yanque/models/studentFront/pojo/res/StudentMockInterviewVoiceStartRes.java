package cn.yanque.models.studentFront.pojo.res;

import lombok.Data;

@Data
public class StudentMockInterviewVoiceStartRes {

    private String voiceSessionId;

    private Long interviewSessionId;

    private String status;

    private String provider;

    private Boolean connected;

    private String message;

    private String promptPreview;
}

