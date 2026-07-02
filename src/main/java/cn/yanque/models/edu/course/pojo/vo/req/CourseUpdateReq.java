package cn.yanque.models.edu.course.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改课程请求")

/**
 * CourseUpdateReq 请求参数对象。
 *
 * <p>用于承载前端提交到后端的表单或查询条件，字段含义由对应控制器和业务服务消费。</p>
 */
public class CourseUpdateReq extends CourseCreateReq {

    @Schema(description = "课程ID")
    private Long id;
}
