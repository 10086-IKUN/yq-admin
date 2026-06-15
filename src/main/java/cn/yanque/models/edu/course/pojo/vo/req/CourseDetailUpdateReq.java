package cn.yanque.models.edu.course.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新课程详情的请求参数
 *
 * 设计思路：
 * 1. 继承CreateReq，复用所有字段的校验规则
 * 2. 额外添加id字段，用于指定要更新哪条记录
 * 3. 这种继承模式在项目中很常见：CreateReq定义基础字段，UpdateReq在此基础上加id
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "更新课程详情请求")
public class CourseDetailUpdateReq extends CourseDetailCreateReq {

    /** 课程详情ID，必填，指定要更新的记录 */
    @NotNull(message = "ID不能为空")
    @Schema(description = "课程详情ID")
    private Long id;
}
