package cn.yanque.models.studentFront.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 学员端课程成绩汇总响应。
 *
 * <p>课程综合成绩按当前业务规则计算：
 * 作业平均分占 40%，考试平均分占 60%。
 * 如果某一类暂时没有成绩，该部分按 0 参与计算。</p>
 */
@Data
@Schema(description = "学员端课程成绩汇总响应")
public class StudentScoreOverviewRes {

    /** 课程综合成绩：作业平均分 * 40% + 考试平均分 * 60%。 */
    @Schema(description = "课程综合成绩")
    private BigDecimal compositeScore = BigDecimal.ZERO;

    /** 已批改作业平均分。没有已批改作业时为 0。 */
    @Schema(description = "作业平均分")
    private BigDecimal homeworkAverageScore = BigDecimal.ZERO;

    /** 已出分考试平均分。没有已出分考试时为 0。 */
    @Schema(description = "考试平均分")
    private BigDecimal examAverageScore = BigDecimal.ZERO;

    /** 最高单项成绩，作业和考试一起比较。 */
    @Schema(description = "最高单项成绩")
    private BigDecimal bestScore = BigDecimal.ZERO;

    /** 已出成绩数量，等于作业成绩数量加考试成绩数量。 */
    @Schema(description = "已出成绩数量")
    private Integer publishedScoreCount = 0;

    /** 作业成绩明细。 */
    @Schema(description = "作业成绩明细")
    private List<StudentScoreRes> homeworkScores = new ArrayList<>();

    /** 考试成绩明细。 */
    @Schema(description = "考试成绩明细")
    private List<StudentScoreRes> examScores = new ArrayList<>();
}
