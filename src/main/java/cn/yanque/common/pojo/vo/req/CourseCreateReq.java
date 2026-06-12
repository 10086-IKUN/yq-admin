package cn.yanque.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建课程请求参数
 */
@Data
@Schema(description = "创建课程请求")
public class CourseCreateReq {

    @NotBlank(message = "课程名称不能为空")
    @Schema(description = "课程名称")
    private String courseName;

    @NotNull(message = "课程天数不能为空")
    @Min(value = 1, message = "课程天数最小为1")
    @Schema(description = "课程天数")
    private Integer courseDays;

    @NotBlank(message = "飞书资料路径不能为空")
    @Schema(description = "飞书资料路径")
    private String feishuMaterialPath;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态，1启用，0禁用", allowableValues = {"0", "1"})
    private Integer status;
}
