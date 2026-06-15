package cn.yanque.models.duty.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;

/**
 * 创建值班安排请求参数
 */
@Data
@Schema(description = "创建值班安排请求")
public class DutyAssignmentCreateReq {

    @NotNull(message = "值班日期不能为空")
    @Schema(description = "值班日期")
    private Date dutyDate;

    @NotBlank(message = "值班类型不能为空")
    @Schema(description = "值班类型 CLASS_NIGHT_DUTY/GLOBAL_NIGHT_DUTY")
    private String dutyType;

    @NotNull(message = "班级ID不能为空")
    @Schema(description = "班级ID，全局值班填0")
    private Long classId;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间")
    private Time startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间")
    private Time endTime;

    @NotNull(message = "值班老师ID不能为空")
    @Schema(description = "值班老师ID")
    private Long teacherId;

    @Schema(description = "备注")
    private String remark;
}
