package cn.yanque.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 课表分页查询请求VO
 */
@Data
@Schema(description = "课表分页查询请求")
public class SchedulePageReq {

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "排课类型：CLASS/SELF_STUDY/REST")
    private String scheduleType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "开始日期")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "结束日期")
    private Date endDate;

    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数最小为1")
    @Schema(description = "每页条数", defaultValue = "10")
    private Integer pageSize = 10;
}
