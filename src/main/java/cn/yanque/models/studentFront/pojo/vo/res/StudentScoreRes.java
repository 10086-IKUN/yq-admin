package cn.yanque.models.studentFront.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 学员成绩响应
 */
@Data
@Schema(description = "学员成绩响应")
public class StudentScoreRes {

    /** 课程名称 */
    @Schema(description = "课程名称")
    private String courseName;

    /** 成绩 */
    @Schema(description = "成绩")
    private Integer score;

    /** 等级 */
    @Schema(description = "等级")
    private String grade;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
