package cn.yanque.models.ai.texttosql.pojo.bo;

import lombok.Data;

@Data
public class TextToSqlRunQueryBo {

    private String keyword;
    private String conversationId;
    private String sourceType;
    private String questionType;
    private String businessId;
    private String status;
    private String sqlAction;
    private Boolean interrupted;
    private String feedbackResult;
}

