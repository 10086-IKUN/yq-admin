package cn.yanque.common.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新课程详情响应
 *
 * 设计思路：
 * 1. 与创建响应结构相同，只返回被更新记录的ID
 * 2. 这种一致性设计让前端处理更简单
 */
@Data
@Schema(description = "更新课程详情响应")
public class CourseDetailUpdateRes {

    /** 被更新的课程详情ID */
    @Schema(description = "课程详情ID")
    private Long id;
}
