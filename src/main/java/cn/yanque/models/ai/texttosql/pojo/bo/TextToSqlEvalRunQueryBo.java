package cn.yanque.models.ai.texttosql.pojo.bo;

import lombok.Data;

@Data
public class TextToSqlEvalRunQueryBo {

    /** 任务名称关键词。 */
    private String keyword;
    /** 任务状态：RUNNING/COMPLETED/WAITING_CLARIFICATION/FAILED。 */
    private String status;
}

