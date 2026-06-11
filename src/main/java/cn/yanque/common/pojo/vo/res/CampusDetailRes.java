package cn.yanque.common.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "校区详情响应")
public class CampusDetailRes {

    @Schema(description = "校区ID")
    private Long id;

    @Schema(description = "校区名称")
    private String campusName;

    @Schema(description = "负责人用户ID")
    private Long principalUserId;

    @Schema(description = "校区地址")
    private String address;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态，1启用，0禁用")
    private Integer status;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "更新时间")
    private Date updatedAt;
}
