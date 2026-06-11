package cn.yanque.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "分页查询班级请求")
public class ClassPageReq {

    @Schema(description = "关键词（班级期数）")
    private String keyword;

    @Schema(description = "班级状态", allowableValues = {"WAITING", "TEACHING", "FINISHED"})
    private String classStatus;

    @Schema(description = "校区ID")
    private Long campusId;

    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数最小为1")
    @Schema(description = "每页条数", defaultValue = "10")
    private Integer pageSize = 10;
}
