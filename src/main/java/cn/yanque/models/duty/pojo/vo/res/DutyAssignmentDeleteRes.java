package cn.yanque.models.duty.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 删除值班安排响应
 */
@Data
@Schema(description = "删除值班安排响应")
public class DutyAssignmentDeleteRes {

    @Schema(description = "值班安排ID")
    private Long id;
}
