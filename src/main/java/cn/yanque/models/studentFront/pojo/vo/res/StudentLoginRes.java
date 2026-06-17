package cn.yanque.models.studentFront.pojo.vo.res;

import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.pojo.entity.StuPermissionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "学生端登录响应")
public class StudentLoginRes {

    @Schema(description = "Token")
    private String token;

    @Schema(description = "签名密钥")
    private String signSecret;

    @Schema(description = "学员信息")
    private EduStudentEntity studentInfo;

    @Schema(description = "权限列表")
    private List<StuPermissionEntity> permissions;
}
