package cn.yanque.common.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建课程响应")
public class CourseCreateRes {

    @Schema(description = "课程ID")
    private Long id;
}
