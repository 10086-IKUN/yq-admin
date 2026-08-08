package cn.yanque.models.ai.texttosql.pojo.vo.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TextToSqlEvalQuestionRes {

    /** 评测样本ID。 */
    private Long id;
    /** 样本问题。 */
    private String question;
    /** 业务标识，例如 order。 */
    private String businessId;
    /** 业务展示名称，例如订单支付。 */
    private String businessName;
    /** 评测目标。 */
    private String evalTarget;
    /** 样本场景。 */
    private String sampleCategory;
    /** 样本来源：MANUAL/RUN_HISTORY/FEEDBACK。 */
    private String sourceType;
    /** 来源运行记录ID。 */
    private Long sourceRunId;
    /** 来源运行会话ID。 */
    private String sourceConversationId;
    /** 来源运行生成 SQL。 */
    private String generatedSql;
    /** 来源运行执行 SQL。 */
    private String executedSql;
    /** 来源运行最终回答。 */
    private String finalAnswer;
    /** 来源反馈结果。 */
    private String feedbackResult;
    /** 来源反馈错误类型。 */
    private String feedbackErrorType;
    /** 来源反馈说明。 */
    private String feedbackComment;
    /** 判断标准列表。 */
    private List<TextToSqlEvalAssertionRes> assertions;
    /** 人工判断说明。 */
    private String judgeNote;
    /** 整理备注。 */
    private String remark;
    /** 样本状态：DRAFT/ACTIVE/DISABLED。 */
    private String status;
    /** 创建人ID。 */
    private Long createdBy;

    /** 创建时间。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createdAt;

    /** 更新时间。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updatedAt;
}

