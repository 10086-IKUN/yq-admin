package cn.yanque.models.ai.texttosql.pojo.vo.req;

import lombok.Data;

@Data
public class TextToSqlEvalRunPageReq {

    /** 任务名称关键词。 */
    private String keyword;
    /** 任务状态：RUNNING/COMPLETED/WAITING_CLARIFICATION/FAILED。 */
    private String status;
    /** 页码，从1开始。 */
    private Integer pageNum = 1;
    /** 每页条数。 */
    private Integer pageSize = 10;
}

