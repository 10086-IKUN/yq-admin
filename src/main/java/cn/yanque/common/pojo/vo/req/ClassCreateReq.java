package cn.yanque.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * 创建班级请求参数
 */
@Data
@Schema(description = "创建班级请求")
public class ClassCreateReq {

    @NotNull(message = "班级期数不能为空")
    @Schema(description = "班级期数")
    private Integer classTerm;

    @NotNull(message = "校区ID不能为空")
    @Schema(description = "校区ID")
    private Long campusId;

    @NotNull(message = "班主任用户ID不能为空")
    @Schema(description = "班主任用户ID")
    private Long headTeacherId;

    @NotNull(message = "班级状态不能为空")
    @Schema(description = "班级状态", allowableValues = {"WAITING", "TEACHING", "FINISHED"})
    private String classStatus;

    @NotNull(message = "开班时间不能为空")
    @Schema(description = "开班时间")
    private Date startTime;

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID")
    private Long courseId;
}
