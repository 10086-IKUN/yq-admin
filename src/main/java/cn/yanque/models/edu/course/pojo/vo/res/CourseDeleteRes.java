package cn.yanque.models.edu.course.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除课程响应")
public class CourseDeleteRes {

    @Schema(description = "课程ID")
    private Long id;
}
