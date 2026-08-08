package cn.yanque.models.ai.texttosql.pojo.vo.req;

import lombok.Data;

@Data
public class TextToSqlEvalRunResultPageReq {

    /** 是否通过过滤。 */
    private Boolean passed;
    /** 失败类型过滤，例如 CLARIFICATION_REQUIRED。 */
    private String failureType;
    /** 关键词，匹配问题、失败原因、实际回答等文本。 */
    private String keyword;
    /** 页码，从1开始。 */
    private Integer pageNum = 1;
    /** 每页条数。 */
    private Integer pageSize = 10;
}

