package cn.yanque.models.edu.student.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "分页查询学员请求")
public class StudentPageReq {

    @Schema(description = "关键词（姓名/学号/手机号）")
    private String keyword;

    @Schema(description = "学习方式", allowableValues = {"ONLINE", "OFFLINE"})
    private String studyMode;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "产品ID")
    private Long productId;

    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数最小为1")
    @Schema(description = "每页条数", defaultValue = "10")
    private Integer pageSize = 10;
}
