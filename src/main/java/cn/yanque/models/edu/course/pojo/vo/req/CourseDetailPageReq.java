package cn.yanque.models.edu.course.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 课程详情分页查询请求参数
 *
 * 设计思路：
 * 1. 通过courseId筛选指定课程的所有详情
 * 2. pageNum和pageSize用于分页，避免一次性加载所有数据
 * 3. 默认值通过字段初始化设置，前端不传时使用默认值
 */
@Data
@Schema(description = "课程详情分页请求")
public class CourseDetailPageReq {

    /** 课程ID，筛选指定课程的详情列表 */
    @Schema(description = "课程ID")
    private Long courseId;

    /** 页码，从1开始，默认第1页 */
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    /** 每页条数，默认10条 */
    @Schema(description = "每页条数", defaultValue = "10")
    private Integer pageSize = 10;
}
