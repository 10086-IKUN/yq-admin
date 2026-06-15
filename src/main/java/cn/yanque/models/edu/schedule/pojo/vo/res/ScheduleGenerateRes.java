package cn.yanque.models.edu.schedule.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 课表生成响应VO
 */
@Data
@Schema(description = "课表生成响应")
public class ScheduleGenerateRes {

    @Schema(description = "生成的课表记录数")
    private Integer count;
}
