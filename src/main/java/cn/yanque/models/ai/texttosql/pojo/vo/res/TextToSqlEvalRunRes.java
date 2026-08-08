package cn.yanque.models.ai.texttosql.pojo.vo.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TextToSqlEvalRunRes {

    /** 评测任务ID。 */
    private Long id;
    /** 评测任务名称。 */
    private String name;
    /** 任务状态：RUNNING/COMPLETED/WAITING_CLARIFICATION/FAILED。 */
    private String status;
    /** 本次任务选择的样本总数。 */
    private Integer sampleCount;
    /** 通过数量。 */
    private Integer passCount;
    /** 失败数量，包含待澄清样本。 */
    private Integer failCount;
    /** 通过率，百分比形式。 */
    private BigDecimal passRate;
    /** 评测范围 JSON。 */
    private String scopeJson;
    /** 失败类型汇总 JSON。 */
    private String summaryJson;
    /** 任务级异常信息。 */
    private String errorMessage;
    /** 开始时间。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date startedAt;
    /** 完成时间或第一轮结束时间。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date finishedAt;
    /** 创建人ID。 */
    private Long createdBy;
    /** 创建时间。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createdAt;
    /** 更新时间。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updatedAt;
}

