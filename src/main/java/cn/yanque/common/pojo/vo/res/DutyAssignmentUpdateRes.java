package cn.yanque.common.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新值班安排响应
 */
@Data
@Schema(description = "更新值班安排响应")
public class DutyAssignmentUpdateRes {

    @Schema(description = "值班安排ID")
    private Long id;
}
