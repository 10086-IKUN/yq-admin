package cn.yanque.models.edu.course.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "分页查询课程请求")
public class CoursePageReq {

    @Schema(description = "关键词（课程名称）")
    private String keyword;

    @Schema(description = "状态，1启用，0禁用", allowableValues = {"0", "1"})
    private Integer status;

    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数最小为1")
    @Schema(description = "每页条数", defaultValue = "10")
    private Integer pageSize = 10;
}
