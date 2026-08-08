package cn.yanque.models.ai.texttosql.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TextToSqlRunEntity {

    /** 运行记录ID。 */
    private Long id;
    /** LangGraph thread_id / 会话ID。 */
    private String conversationId;
    /** 基于历史反馈重新运行时关联的父运行记录ID。 */
    private Long parentRunId;
    /** 运行来源：USER 表示真实用户提问，EVAL 表示评测任务产生。 */
    private String sourceType;
    /** 用户原始问题。 */
    private String originalQuestion;
    /** 澄清补充后合并出的最终问题。 */
    private String finalQuestion;
    /** 问题类型，例如 chat/query/update/unclear。 */
    private String questionType;
    /** 业务标识，例如 order。 */
    private String businessId;
    /** 业务展示名称。 */
    private String businessName;
    /** 路由原因。 */
    private String routeReason;
    /** 运行状态：RUNNING/INTERRUPTED/COMPLETED/FAILED。 */
    private String status;
    /** 是否曾经中断等待澄清。 */
    private Boolean interrupted;
    /** SQL 动作：generate_sql/need_metric/need_schema/need_clarification 等。 */
    private String sqlAction;
    /** 模型生成的 SQL。 */
    private String generatedSql;
    /** 实际执行的 SQL。 */
    private String executedSql;
    /** SQL 生成原因。 */
    private String sqlGenerationReason;
    /** SQL 执行错误或运行错误。 */
    private String executionError;
    /** 使用到的表，JSON 字符串。 */
    private String usedTables;
    /** 使用到的字段，JSON 字符串。 */
    private String usedFields;
    /** 缺失信息，JSON 字符串。 */
    private String missingInfo;
    /** 查询结果字段，JSON 字符串。 */
    private String queryColumns;
    /** 查询结果样例，JSON 字符串。 */
    private String queryRowsSample;
    /** 查询结果行数。 */
    private Integer queryRowCount;
    /** 分析摘要。 */
    private String analysisSummary;
    /** 完整分析结果 JSON。 */
    private String analysisJson;
    /** 最终回答。 */
    private String finalAnswer;
    /** 最终 State 快照 JSON。 */
    private String stateSnapshotJson;
    /** State 快照文本兜底字段。 */
    private String stateSnapshotText;
    /** 最新反馈结果：CORRECT/INCORRECT。 */
    private String feedbackResult;
    /** 最新反馈错误类型。 */
    private String feedbackErrorType;
    /** 最新反馈说明。 */
    private String feedbackComment;
    /** 最新反馈时间。 */
    private Date feedbackAt;
    /** 已加入的评测样本ID，仅分页查询时通过子查询填充。 */
    private Long evalQuestionId;
    /** 创建人ID。 */
    private Long createdBy;
    /** 创建时间。 */
    private Date createdAt;
    /** 更新时间。 */
    private Date updatedAt;
}

