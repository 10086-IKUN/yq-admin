package cn.yanque.common.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除校区响应")
public class CampusDeleteRes {

    @Schema(description = "校区ID")
    private Long id;
}
