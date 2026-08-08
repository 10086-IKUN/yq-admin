package cn.yanque.models.ai.texttosql.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TextToSqlEvalRunResultEntity {

    /** 评测结果ID。 */
    private Long id;
    /** 所属评测任务ID。 */
    private Long evalRunId;
    /** 对应评测样本ID。 */
    private Long evalQuestionId;
    /** 本次实际运行的问题文本，冗余保存便于结果列表展示。 */
    private String question;
    /** 样本所属业务标识，冗余保存便于筛选和排查。 */
    private String businessId;
    /** 样本评测目标，冗余保存便于分析不同能力的表现。 */
    private String evalTarget;
    /** 样本场景，冗余保存便于区分正常/边界/回归样本。 */
    private String sampleCategory;
    /** 本次评测运行使用的会话ID，可关联 source_type=EVAL 的运行记录。 */
    private String conversationId;
    /** 是否通过自动判定。 */
    private Boolean passed;
    /** 失败类型，例如 TABLE_SELECTION_ERROR、CLARIFICATION_REQUIRED。 */
    private String failureType;
    /** 失败原因，保存具体期望值和实际值差异。 */
    private String failureReason;
    /** 本次运行得到的实际 State JSON。 */
    private String actualStateJson;
    /** 本次运行实际生成或执行的 SQL。 */
    private String actualSql;
    /** 本次运行最终回答。 */
    private String actualAnswer;
    /** SQL 执行或 Text-to-SQL 运行中的错误信息。 */
    private String executionError;
    /** 本条样本累计运行耗时，澄清继续时会叠加。 */
    private Long durationMs;
    /** 结果创建时间。 */
    private Date createdAt;
}

