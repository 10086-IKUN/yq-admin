package cn.yanque.models.edu.student.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "学员详情响应")
public class StudentDetailRes {

    @Schema(description = "学员ID")
    private Long id;

    @Schema(description = "学号")
    private String studentCode;

    @Schema(description = "姓名")
    private String studentName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "毕业届数")
    private Integer graduationSession;

    @Schema(description = "学校")
    private String school;

    @Schema(description = "学历")
    private String education;

    @Schema(description = "学习方式", allowableValues = {"ONLINE", "OFFLINE"})
    private String studyMode;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "加入时间")
    private Date joinTime;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "更新时间")
    private Date updatedAt;
}
