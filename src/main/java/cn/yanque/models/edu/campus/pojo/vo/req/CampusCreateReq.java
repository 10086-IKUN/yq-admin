package cn.yanque.models.edu.campus.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "创建校区请求")

/**
 * CampusCreateReq 请求参数对象。
 *
 * <p>用于承载前端提交到后端的表单或查询条件，字段含义由对应控制器和业务服务消费。</p>
 */
public class CampusCreateReq {

    @NotBlank(message = "校区名称不能为空")
    @Schema(description = "校区名称")
    private String campusName;

    @NotNull(message = "负责人用户ID不能为空")
    @Schema(description = "负责人用户ID")
    private Long principalUserId;

    @NotBlank(message = "校区地址不能为空")
    @Schema(description = "校区地址")
    private String address;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "备注")
    private String remark;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态，1启用，0禁用")
    private Integer status;
}
