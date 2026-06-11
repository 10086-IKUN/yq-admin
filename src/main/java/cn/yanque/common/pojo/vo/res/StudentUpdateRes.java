package cn.yanque.common.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改学员响应")
public class StudentUpdateRes {

    @Schema(description = "学员ID")
    private Long id;
}
