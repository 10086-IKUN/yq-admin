package cn.yanque.common.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 课表分页查询响应VO
 */
@Data
@Schema(description = "课表分页查询响应")
public class SchedulePageRes {

    @Schema(description = "课表ID")
    private Long id;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程详情ID")
    private Long courseDetailId;

    @Schema(description = "课程第几天")
    private Integer courseDayNum;

    @Schema(description = "排课日期")
    private Date scheduleDate;

    @Schema(description = "排课类型：CLASS/SELF_STUDY/REST")
    private String scheduleType;

    @Schema(description = "课程内容")
    private String courseContent;

    @Schema(description = "课程阶段")
    private String stageName;

    @Schema(description = "授课老师ID")
    private Long teacherId;

    @Schema(description = "授课老师姓名")
    private String teacherName;

    @Schema(description = "创建时间")
    private Date createdAt;
}
