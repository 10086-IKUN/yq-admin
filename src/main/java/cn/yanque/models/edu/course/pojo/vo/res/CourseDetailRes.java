package cn.yanque.models.edu.course.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "课程详情响应")
public class CourseDetailRes {

    @Schema(description = "课程ID")
    private Long id;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程天数")
    private Integer courseDays;

    @Schema(description = "飞书资料路径")
    private String feishuMaterialPath;

    @Schema(description = "状态，1启用，0禁用")
    private Integer status;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "更新时间")
    private Date updatedAt;
}
