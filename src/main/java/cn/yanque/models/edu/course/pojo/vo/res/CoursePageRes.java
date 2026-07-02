package cn.yanque.models.edu.course.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "课程分页响应")

/**
 * CoursePageRes 响应结果对象。
 *
 * <p>用于把业务层处理后的数据整理成前端需要的展示结构。</p>
 */
public class CoursePageRes {

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
}
