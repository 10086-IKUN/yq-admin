package cn.yanque.models.mockinterview.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MockInterviewSessionEntity {

    private Long id;

    private Long studentId;

    private String resumeObjectKey;

    private String resumeFileName;

    private String resumeTextSnapshot;

    private String profileJson;

    private String profileStatus;

    private String profileErrorMessage;

    private Date profileGeneratedAt;

    private String voiceSessionId;

    private String status;

    private Date startedAt;

    private Date finishedAt;

    private Date createdAt;

    private Date updatedAt;
}

