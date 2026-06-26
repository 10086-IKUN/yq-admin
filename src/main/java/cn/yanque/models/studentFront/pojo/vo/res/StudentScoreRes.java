package cn.yanque.models.studentFront.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学员成绩明细响应。
 *
 * <p>成绩明细同时承载作业成绩和考试成绩。
 * 前端通过 scoreType 区分来源，再展示不同的名称和反馈。</p>
 */
@Data
@Schema(description = "学员成绩明细响应")
public class StudentScoreRes {

    /** 成绩来源类型：HOMEWORK 作业，EXAM 考试。 */
    @Schema(description = "成绩来源类型", allowableValues = {"HOMEWORK", "EXAM"})
    private String scoreType;

    /** 成绩项名称，例如作业标题或考试名称。 */
    @Schema(description = "成绩项名称")
    private String title;

    /** 课程名称。 */
    @Schema(description = "课程名称")
    private String courseName;

    /** 实际得分。 */
    @Schema(description = "成绩")
    private BigDecimal score;

    /** 满分。作业暂时按 100 分制，考试使用试卷总分。 */
    @Schema(description = "满分")
    private BigDecimal fullScore;

    /** 成绩产生时间：作业取批改时间，考试取交卷时间。 */
    @Schema(description = "成绩产生时间")
    private LocalDateTime scoreTime;

    /** 状态说明，例如“已批改”“已出分”。 */
    @Schema(description = "状态")
    private String status;

    /** 老师评语或备注。 */
    @Schema(description = "备注")
    private String remark;
}
