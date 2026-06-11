package cn.yanque.common.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "班级分页响应")
public class ClassPageRes {

    @Schema(description = "班级ID")
    private Long id;

    @Schema(description = "班级期数")
    private Integer classTerm;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "班主任用户ID")
    private Long headTeacherId;

    @Schema(description = "班级状态", allowableValues = {"WAITING", "TEACHING", "FINISHED"})
    private String classStatus;

    @Schema(description = "开班时间")
    private Date startTime;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "班级人数")
    private Integer studentCount;

    @Schema(description = "创建时间")
    private Date createdAt;
}
