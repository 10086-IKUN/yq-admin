package cn.yanque.models.ai.texttosql.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TextToSqlFeedbackEntity {

    /** 反馈ID。 */
    private Long id;
    /** 被反馈的运行记录ID。 */
    private Long runId;
    /** 被反馈的运行会话ID。 */
    private String conversationId;
    /** 反馈结果：CORRECT/INCORRECT。 */
    private String feedbackResult;
    /** 错误类型：ROUTE/BUSINESS/SKILL/SQL/RESULT/ANALYSIS/CLARIFICATION/OTHER。 */
    private String errorType;
    /** 反馈说明，可作为后续整理评测样本的人工依据。 */
    private String comment;
    /** 反馈人ID。 */
    private Long createdBy;
    /** 反馈时间。 */
    private Date createdAt;
}

