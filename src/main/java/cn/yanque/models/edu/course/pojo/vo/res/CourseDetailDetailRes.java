package cn.yanque.models.edu.course.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 课程详情详情响应
 *
 * 设计思路：
 * 1. 包含所有字段，用于查看单条记录的完整信息
 * 2. 与Entity字段基本一致，但实际项目中可能需要隐藏敏感字段
 * 3. 前端弹窗编辑时，会先调用详情接口获取完整数据填充表单
 */
@Data
@Schema(description = "课程详情详情响应")
public class CourseDetailDetailRes {

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

    /** 创建时间，用于展示给用户 */
    @Schema(description = "创建时间")
    private Date createdAt;

    /** 更新时间，用于展示给用户 */
    @Schema(description = "更新时间")
    private Date updatedAt;
}
