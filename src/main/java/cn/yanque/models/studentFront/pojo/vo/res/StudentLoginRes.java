package cn.yanque.models.studentFront.pojo.vo.res;

import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.pojo.entity.StuPermissionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "学生端登录响应")

/**
 * StudentLoginRes 响应结果对象。
 *
 * <p>用于把业务层处理后的数据整理成前端需要的展示结构。</p>
 */
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
