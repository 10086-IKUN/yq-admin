package cn.yanque.models.ai.texttosql.pojo.vo.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class TextToSqlRunRes {

    private Long id;
    private String conversationId;
    private String sourceType;
    private String originalQuestion;
    private String finalQuestion;
    private String questionType;
    private String businessId;
    private String businessName;
    private String routeReason;
    private String status;
    private Boolean interrupted;
    private String sqlAction;
    private String generatedSql;
    private String executedSql;
    private String sqlGenerationReason;
    private String executionError;
    private String usedTables;
    private String usedFields;
    private String missingInfo;
    private String queryColumns;
    private String queryRowsSample;
    private Integer queryRowCount;
    private String analysisSummary;
    private String analysisJson;
    private String finalAnswer;
    private String stateSnapshotJson;
    private String feedbackResult;
    private String feedbackErrorType;
    private String feedbackComment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date feedbackAt;

    private Long evalQuestionId;

    private Long createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updatedAt;

    private List<TextToSqlRunStepRes> steps = new ArrayList<>();
}

