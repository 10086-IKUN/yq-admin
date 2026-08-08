package cn.yanque.models.ai.texttosql.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TextToSqlEvalQuestionEntity {

    /** 评测样本ID。 */
    private Long id;
    /** 样本问题，评测时会作为用户原始问题发起 Text-to-SQL 流程。 */
    private String question;
    /** 期望或来源运行识别出的业务标识，例如 order/student/teaching。 */
    private String businessId;
    /** 业务展示名称，例如订单支付。 */
    private String businessName;
    /** 评测目标：ROUTING/SQL_GENERATION/CLARIFICATION/END_TO_END 等。 */
    private String evalTarget;
    /** 样本场景：NORMAL/BOUNDARY/REGRESSION/AMBIGUOUS/NEGATIVE。 */
    private String sampleCategory;
    /** 样本来源：MANUAL/RUN_HISTORY/FEEDBACK。 */
    private String sourceType;
    /** 来源运行记录ID，从运行记录加入样本时保存。 */
    private Long sourceRunId;
    /** 来源运行会话ID，方便回溯原始 graph。 */
    private String sourceConversationId;
    /** 来源运行生成的 SQL，仅用于人工整理样本时参考。 */
    private String generatedSql;
    /** 来源运行实际执行的 SQL，仅用于人工整理样本时参考。 */
    private String executedSql;
    /** 来源运行最终回答，仅用于人工整理样本时参考。 */
    private String finalAnswer;
    /** 来源人工反馈结果：CORRECT/INCORRECT。 */
    private String feedbackResult;
    /** 来源人工反馈错误类型，例如 SQL/CLARIFICATION/RESULT。 */
    private String feedbackErrorType;
    /** 来源人工反馈说明。 */
    private String feedbackComment;
    /** 人工判断说明，用来解释这条样本为什么这样判，不直接参与自动判断。 */
    private String judgeNote;
    /** 样本整理备注。 */
    private String remark;
    /** 样本状态：DRAFT 候选，ACTIVE 正式，DISABLED 停用。 */
    private String status;
    /** 创建人ID。 */
    private Long createdBy;
    /** 创建时间。 */
    private Date createdAt;
    /** 更新时间。 */
    private Date updatedAt;
}

