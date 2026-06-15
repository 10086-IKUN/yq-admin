package cn.yanque.models.edu.course.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建课程详情响应
 *
 * 设计思路：
 * 1. 只返回新创建记录的ID，这是RESTful API的常见做法
 * 2. 前端可以根据ID进行后续操作（如编辑、删除）
 * 3. 返回最小必要数据，减少网络传输
 */
@Data
@Schema(description = "创建课程详情响应")
public class CourseDetailCreateRes {

    /** 新创建的课程详情ID */
    @Schema(description = "课程详情ID")
    private Long id;
}
