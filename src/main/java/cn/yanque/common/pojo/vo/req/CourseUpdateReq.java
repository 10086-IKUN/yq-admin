package cn.yanque.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改课程请求")
public class CourseUpdateReq extends CourseCreateReq {

    @Schema(description = "课程ID")
    private Long id;
}
