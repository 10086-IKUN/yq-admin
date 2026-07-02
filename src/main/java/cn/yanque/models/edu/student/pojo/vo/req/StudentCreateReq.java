package cn.yanque.models.edu.student.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "创建学员请求")

/**
 * StudentCreateReq 请求参数对象。
 *
 * <p>用于承载前端提交到后端的表单或查询条件，字段含义由对应控制器和业务服务消费。</p>
 */
public class StudentCreateReq {

    @NotBlank(message = "学号不能为空")
    @Schema(description = "学号")
    private String studentCode;

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "姓名")
    private String studentName;

    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "登录密码", defaultValue = "123456")
    private String password;

    @NotNull(message = "毕业届数不能为空")
    @Schema(description = "毕业届数")
    private Integer graduationSession;

    @NotBlank(message = "学校不能为空")
    @Schema(description = "学校")
    private String school;

    @NotBlank(message = "学历不能为空")
    @Schema(description = "学历")
    private String education;

    @Schema(description = "学习方式，由所选产品自动带入", allowableValues = {"ONLINE", "OFFLINE"})
    private String studyMode;

    @Schema(description = "班级ID，线下学员使用")
    private Long classId;

    @NotNull(message = "产品ID不能为空")
    @Schema(description = "产品ID")
    private Long productId;

    @NotNull(message = "加入时间不能为空")
    @Schema(description = "加入时间")
    private Date joinTime;
}
