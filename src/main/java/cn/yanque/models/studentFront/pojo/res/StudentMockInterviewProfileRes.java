package cn.yanque.models.studentFront.pojo.res;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "学生模拟面试画像状态")
public class StudentMockInterviewProfileRes {

    @Schema(description = "是否已上传简历")
    private Boolean hasResume;

    @Schema(description = "简历文件名")
    private String resumeFileName;

    @Schema(description = "简历解析状态")
    private String resumeParseStatus;

    @Schema(description = "简历解析失败原因")
    private String resumeParseErrorMessage;

    @Schema(description = "简历解析完成时间")
    private Date resumeParsedAt;

    @Schema(description = "最近一次模拟面试会话")
    private MockInterviewSessionRes latestSession;

    @Data
    @Schema(description = "模拟面试会话")
    public static class MockInterviewSessionRes {

        @Schema(description = "会话ID")
        private Long id;

        @Schema(description = "画像生成状态")
        private String profileStatus;

        @Schema(description = "画像生成失败原因")
        private String profileErrorMessage;

        @Schema(description = "画像生成时间")
        private Date profileGeneratedAt;

        @Schema(description = "会话状态")
        private String status;

        @Schema(description = "实时语音会话ID")
        private String voiceSessionId;

        @Schema(description = "面试开始时间")
        private Date startedAt;

        @Schema(description = "面试结束时间")
        private Date finishedAt;

        @Schema(description = "本次模拟面试画像")
        private JSONObject profile;
    }
}

