package cn.yanque.models.edu.schedule.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

/**
 * 课表生成请求VO
 */
@Data
@Schema(description = "课表生成请求")
public class ScheduleGenerateReq {

    @NotNull(message = "开班时间不能为空")
    @Schema(description = "开班时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private Date startDate;

    @NotNull(message = "阶段老师分配不能为空")
    @Schema(description = "阶段老师分配，key=阶段名称，value=老师ID")
    private Map<String, Long> stageTeachers;
}
