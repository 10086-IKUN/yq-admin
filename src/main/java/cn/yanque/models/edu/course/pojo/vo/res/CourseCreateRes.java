package cn.yanque.models.edu.course.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建课程响应参数
 */
@Data
@Schema(description = "创建课程响应")
public class CourseCreateRes {

    @Schema(description = "课程ID")
    private Long id;
}
