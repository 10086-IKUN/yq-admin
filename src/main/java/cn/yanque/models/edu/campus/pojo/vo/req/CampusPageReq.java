package cn.yanque.models.edu.campus.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "分页查询校区请求")
public class CampusPageReq {

    @Schema(description = "关键词（校区名称）")
    private String keyword;

    @Schema(description = "状态，1启用，0禁用")
    private Integer status;

    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数最小为1")
    @Schema(description = "每页条数", defaultValue = "10")
    private Integer pageSize = 10;
}
