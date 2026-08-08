package cn.yanque.models.ai.texttosql.pojo.vo.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class TextToSqlEvalRunResultRes {

    /** 评测结果ID。 */
    private Long id;
    /** 所属评测任务ID。 */
    private Long evalRunId;
    /** 对应评测样本ID。 */
    private Long evalQuestionId;
    /** 本次评测问题。 */
    private String question;
    /** 业务标识。 */
    private String businessId;
    /** 评测目标。 */
    private String evalTarget;
    /** 样本场景。 */
    private String sampleCategory;
    /** 本次评测运行会话ID，可关联 EVAL 运行记录。 */
    private String conversationId;
    /** 是否通过。 */
    private Boolean passed;
    /** 失败类型。 */
    private String failureType;
    /** 失败原因。 */
    private String failureReason;
    /** 本次运行得到的实际 State JSON。 */
    private String actualStateJson;
    /** 本次运行实际 SQL。 */
    private String actualSql;
    /** 本次运行最终回答。 */
    private String actualAnswer;
    /** 执行错误信息。 */
    private String executionError;
    /** 累计耗时毫秒。 */
    private Long durationMs;
    /** 创建时间。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createdAt;
}

