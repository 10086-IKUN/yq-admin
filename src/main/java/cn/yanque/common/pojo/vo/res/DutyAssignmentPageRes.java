package cn.yanque.common.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;

/**
 * 值班安排分页响应
 */
@Data
@Schema(description = "值班安排分页响应")
public class DutyAssignmentPageRes {

    @Schema(description = "值班安排ID")
    private Long id;

    @Schema(description = "值班日期")
    private Date dutyDate;

    @Schema(description = "值班类型")
    private String dutyType;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "开始时间")
    private Time startTime;

    @Schema(description = "结束时间")
    private Time endTime;

    @Schema(description = "值班老师ID")
    private Long teacherId;

    @Schema(description = "值班老师姓名")
    private String teacherName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private Date createdAt;
}
