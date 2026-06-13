package cn.yanque.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Date;

/**
 * 值班安排分页查询请求参数
 */
@Data
@Schema(description = "值班安排分页请求")
public class DutyAssignmentPageReq {

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "值班类型")
    private String dutyType;

    @Schema(description = "开始日期")
    private Date startDate;

    @Schema(description = "结束日期")
    private Date endDate;

    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", defaultValue = "10")
    private Integer pageSize = 10;
}
