package cn.yanque.models.ai.texttosql.pojo.bo;

import lombok.Data;

@Data
public class TextToSqlEvalRunResultQueryBo {

    /** 所属评测任务ID，结果分页必须带上。 */
    private Long evalRunId;
    /** 是否通过过滤。 */
    private Boolean passed;
    /** 失败类型过滤，例如 TABLE_SELECTION_ERROR。 */
    private String failureType;
    /** 关键词，匹配问题、失败原因、实际回答等文本。 */
    private String keyword;
}

