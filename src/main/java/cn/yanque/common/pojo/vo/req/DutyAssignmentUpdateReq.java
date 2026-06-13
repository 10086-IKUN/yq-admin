package cn.yanque.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新值班安排请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "更新值班安排请求")
public class DutyAssignmentUpdateReq extends DutyAssignmentCreateReq {

    @NotNull(message = "ID不能为空")
    @Schema(description = "值班安排ID")
    private Long id;
}
