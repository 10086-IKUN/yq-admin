package cn.yanque.models.ai.texttosql.pojo.vo.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class TextToSqlEvalQuestionPageReq {

    /** 关键词，匹配问题、回答、反馈说明、备注等文本。 */
    private String keyword;
    /** 业务标识过滤。 */
    private String businessId;
    /** 评测目标过滤。 */
    private String evalTarget;
    /** 样本场景过滤。 */
    private String sampleCategory;
    /** 样本来源过滤：MANUAL/RUN_HISTORY/FEEDBACK。 */
    private String sourceType;
    /** 来源反馈结果过滤：CORRECT/INCORRECT。 */
    private String feedbackResult;
    /** 样本状态过滤：DRAFT/ACTIVE/DISABLED。 */
    private String status;

    /** 页码，从1开始。 */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    /** 每页条数，最多200。 */
    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 200, message = "每页条数不能大于200")
    private Integer pageSize = 10;
}

