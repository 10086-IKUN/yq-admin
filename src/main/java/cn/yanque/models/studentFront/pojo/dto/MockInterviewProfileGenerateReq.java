package cn.yanque.models.studentFront.pojo.dto;

import lombok.Data;

@Data
public class MockInterviewProfileGenerateReq {

    private Long studentId;

    private String studentName;

    private String education;

    private Integer gradeYear;

    private String school;

    private String major;

    private String resumeText;
}

