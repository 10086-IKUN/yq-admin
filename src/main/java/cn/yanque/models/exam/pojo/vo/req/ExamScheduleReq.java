package cn.yanque.models.exam.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 发布或编辑考试的请求对象。
 */
@Data
@Schema(description = "考试发布请求")
public class ExamScheduleReq {

    /** 使用的试卷 ID。 */
    @NotNull(message = "试卷不能为空")
    @Schema(description = "试卷ID")
    private Long paperId;

    /** 参加考试的班级 ID。 */
    @NotNull(message = "班级不能为空")
    @Schema(description = "班级ID")
    private Long classId;

    /** 考试名称。 */
    @NotBlank(message = "考试名称不能为空")
    @Schema(description = "考试名称")
    private String examName;

    /** 开始时间。 */
    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /** 截止时间。 */
    @NotNull(message = "截止时间不能为空")
    @Schema(description = "截止时间")
    private LocalDateTime endTime;
}
