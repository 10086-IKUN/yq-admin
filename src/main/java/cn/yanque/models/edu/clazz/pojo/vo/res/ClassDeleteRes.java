package cn.yanque.models.edu.clazz.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除班级响应")
public class ClassDeleteRes {

    @Schema(description = "班级ID")
    private Long id;
}
