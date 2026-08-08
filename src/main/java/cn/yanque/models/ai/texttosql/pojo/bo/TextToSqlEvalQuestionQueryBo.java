package cn.yanque.models.ai.texttosql.pojo.bo;

import lombok.Data;

@Data
public class TextToSqlEvalQuestionQueryBo {

    /** 关键词，匹配问题、回答、反馈说明、备注等文本。 */
    private String keyword;
    /** 业务标识过滤，例如 order。 */
    private String businessId;
    /** 评测目标过滤，例如 SQL_GENERATION、CLARIFICATION。 */
    private String evalTarget;
    /** 样本场景过滤，例如 NORMAL、REGRESSION。 */
    private String sampleCategory;
    /** 样本来源过滤：MANUAL/RUN_HISTORY/FEEDBACK。 */
    private String sourceType;
    /** 来源反馈结果过滤：CORRECT/INCORRECT。 */
    private String feedbackResult;
    /** 样本状态过滤：DRAFT/ACTIVE/DISABLED。 */
    private String status;
}

