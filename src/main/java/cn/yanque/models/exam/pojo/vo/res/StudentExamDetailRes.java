package cn.yanque.models.exam.pojo.vo.res;

import cn.yanque.models.exam.pojo.entity.ExamAnswerEntity;
import cn.yanque.models.exam.pojo.entity.ExamAttemptEntity;
import cn.yanque.models.exam.pojo.entity.ExamScheduleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 学员端考试详情响应。
 */
@Data
@Schema(description = "学员端考试详情")
public class StudentExamDetailRes {

    /** 考试发布信息。 */
    @Schema(description = "考试信息")
    private ExamScheduleEntity exam;

    /** 当前学员的考试记录，未开始时为空。 */
    @Schema(description = "考试记录")
    private ExamAttemptEntity attempt;

    /** 当前试卷的逐题答案。未开始时为空。 */
    @Schema(description = "答题列表")
    private List<ExamAnswerEntity> answers;
}
