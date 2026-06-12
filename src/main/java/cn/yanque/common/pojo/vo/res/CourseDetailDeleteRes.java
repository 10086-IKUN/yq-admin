package cn.yanque.common.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 删除课程详情响应
 *
 * 设计思路：
 * 1. 返回被删除记录的ID，用于前端确认删除操作
 * 2. 也可以设计为不返回任何数据（返回void），取决于业务需求
 */
@Data
@Schema(description = "删除课程详情响应")
public class CourseDetailDeleteRes {

    /** 被删除的课程详情ID */
    @Schema(description = "课程详情ID")
    private Long id;
}
