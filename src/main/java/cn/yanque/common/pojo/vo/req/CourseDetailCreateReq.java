package cn.yanque.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建课程详情的请求参数
 *
 * 设计思路：
 * 1. 使用JSR 380注解进行参数校验（@NotNull、@NotBlank、@Min）
 * 2. @Valid注解触发校验，校验失败会抛出MethodArgumentNotValidException
 * 3. 与Entity的区别：Req只包含前端需要传递的字段，不包含id、时间戳等自动生成的字段
 */
@Data
@Schema(description = "创建课程详情请求")
public class CourseDetailCreateReq {

    /** 课程ID，必须指定属于哪个课程 */
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID")
    private Long courseId;

    /** 所属阶段名称，不能为空字符串 */
    @NotBlank(message = "所属阶段不能为空")
    @Schema(description = "所属阶段")
    private String stageName;

    /** 第几天课，最小值为1 */
    @NotNull(message = "天数不能为空")
    @Min(value = 1, message = "天数最小为1")
    @Schema(description = "第几天课")
    private Integer dayNum;

    /** 课程内容，不能为空字符串 */
    @NotBlank(message = "课程内容不能为空")
    @Schema(description = "课程内容")
    private String courseContent;
}
