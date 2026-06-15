package cn.yanque.models.edu.student.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除学员响应")
public class StudentDeleteRes {

    @Schema(description = "学员ID")
    private Long id;
}
