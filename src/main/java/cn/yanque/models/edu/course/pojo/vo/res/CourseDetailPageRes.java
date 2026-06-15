package cn.yanque.models.edu.course.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 课程详情分页响应
 *
 * 设计思路：
 * 1. 比DetailRes少一个updatedAt字段，因为列表页通常不需要显示更新时间
 * 2. 这是性能优化：减少不必要的数据传输
 * 3. 实际项目中，列表响应和详情响应分开定义是常见做法
 */
@Data
@Schema(description = "课程详情分页响应")
public class CourseDetailPageRes {

    @Schema(description = "课程详情ID")
    private Long id;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "所属阶段")
    private String stageName;

    @Schema(description = "第几天课")
    private Integer dayNum;

    @Schema(description = "课程内容")
    private String courseContent;

    /** 创建时间，用于列表展示 */
    @Schema(description = "创建时间")
    private Date createdAt;
}
