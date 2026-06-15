package cn.yanque.models.edu.campus.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改校区响应")
public class CampusUpdateRes {

    @Schema(description = "校区ID")
    private Long id;
}
