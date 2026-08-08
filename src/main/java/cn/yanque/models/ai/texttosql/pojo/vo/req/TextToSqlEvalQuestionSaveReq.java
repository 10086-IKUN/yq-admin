package cn.yanque.models.ai.texttosql.pojo.vo.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TextToSqlEvalQuestionSaveReq {

    /** 评测问题，评测任务会用它发起一次真实 Text-to-SQL 流程。 */
    @NotBlank(message = "问题不能为空")
    @Size(max = 1000, message = "问题不能超过1000字")
    private String question;

    /** 业务标识，例如 order。 */
    private String businessId;
    /** 业务展示名称，例如订单支付。 */
    private String businessName;
    /** 评测目标：ROUTING/SQL_GENERATION/CLARIFICATION/END_TO_END 等。 */
    private String evalTarget;
    /** 样本场景：NORMAL/BOUNDARY/REGRESSION/AMBIGUOUS/NEGATIVE。 */
    private String sampleCategory;
    /** 判断标准列表，客观 State/SQL 断言和主观 ANSWER 标准都放这里。 */
    private List<TextToSqlEvalAssertionReq> assertions;
    /** 人工判断说明，不参与自动判定。 */
    private String judgeNote;
    /** 整理备注。 */
    private String remark;
    /** 样本状态：DRAFT/ACTIVE/DISABLED。 */
    private String status;
}

