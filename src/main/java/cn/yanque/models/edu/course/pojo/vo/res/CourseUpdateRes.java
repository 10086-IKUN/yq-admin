package cn.yanque.models.edu.course.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改课程响应")
public class CourseUpdateRes {

    @Schema(description = "课程ID")
    private Long id;
}
