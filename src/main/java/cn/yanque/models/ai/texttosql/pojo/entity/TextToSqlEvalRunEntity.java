package cn.yanque.models.ai.texttosql.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TextToSqlEvalRunEntity {

    /** 评测任务ID。 */
    private Long id;
    /** 评测任务名称。 */
    private String name;
    /** 任务状态：RUNNING/COMPLETED/WAITING_CLARIFICATION/FAILED。 */
    private String status;
    /** 本次任务选择的样本总数。 */
    private Integer sampleCount;
    /** 自动判定通过的样本数。 */
    private Integer passCount;
    /** 自动判定失败的样本数，包含待澄清样本。 */
    private Integer failCount;
    /** 通过率，百分比形式，例如 85.50 表示 85.50%。 */
    private BigDecimal passRate;
    /** 评测范围 JSON，例如本次选择的 evalQuestionIds。 */
    private String scopeJson;
    /** 失败类型汇总 JSON，例如 {"TABLE_SELECTION_ERROR":2}。 */
    private String summaryJson;
    /** 任务级异常信息，只有 FAILED 时通常有值。 */
    private String errorMessage;
    /** 任务开始时间。 */
    private Date startedAt;
    /** 任务结束时间；WAITING_CLARIFICATION 表示第一轮执行结束但仍需补充。 */
    private Date finishedAt;
    /** 创建人ID。 */
    private Long createdBy;
    /** 创建时间。 */
    private Date createdAt;
    /** 更新时间。 */
    private Date updatedAt;
}

